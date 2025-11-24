package qwerty.chaekit.service.ebook.credit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import qwerty.chaekit.dto.ebook.credit.payment.CreditPaymentReadyRequest;
import qwerty.chaekit.dto.external.kakaopay.KakaoPayApproveResponse;
import qwerty.chaekit.dto.external.kakaopay.KakaoPayCancelResponse;
import qwerty.chaekit.dto.external.kakaopay.KakaoPayReadyResponse;
import qwerty.chaekit.global.constant.CreditProduct;
import qwerty.chaekit.global.enums.ErrorCode;
import qwerty.chaekit.global.exception.BadRequestException;
import qwerty.chaekit.global.properties.KakaoPayProperties;
import qwerty.chaekit.global.security.resolver.UserToken;
import qwerty.chaekit.service.ebook.credit.exception.PaymentCancelFailedException;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoPayService {
    private static final String KAKAO_PAY_READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
    private static final String KAKAO_PAY_CANCEL_URL = "https://open-api.kakaopay.com/online/v1/payment/cancel";
    private static final String KAKAO_PAY_APPROVE_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";
    private static final String REDIS_TID_KEY_PREFIX = "kakao:tid:";
    private static final String REDIS_ORDER_ID_KEY_PREFIX = "kakao:orderId:";
    private static final Duration REDIS_KEY_EXPIRATION = Duration.ofMinutes(10);

    private final RestTemplate restTemplate;
    private final KakaoPayProperties kakaoPayProperties;
    private final RedisTemplate<String, String> redisTemplate;

    public String requestKakaoPay(UserToken userToken, CreditPaymentReadyRequest request) {
        Long creditProductId = request.creditProductId();
        CreditProduct product = findCreditProductById(creditProductId);
        String orderId = UUID.randomUUID().toString();

        HttpEntity<Map<String, String>> httpEntity = createKakaoPayRequest(userToken, product, orderId);
        ResponseEntity<KakaoPayReadyResponse> response = restTemplate.postForEntity(
                KAKAO_PAY_READY_URL, httpEntity, KakaoPayReadyResponse.class
        );

        return handleKakaoPayResponse(response, userToken.userId(), orderId);
    }

    private CreditProduct findCreditProductById(Long creditProductId) {
        return Arrays.stream(CreditProduct.values())
                .filter(p -> p.id == creditProductId)
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_CREDIT_PRODUCT_ID));
    }

    private HttpEntity<Map<String, String>> createKakaoPayRequest(UserToken userToken, CreditProduct product,
                                                                            String orderId) {
        HttpHeaders headers = createKakaoPayHeaders();
        Map<String, String> body = createKakaoPayRequestBody(userToken, product, orderId);

        log.info("[KakaoPay Create Request] Headers: {}", headers);
        log.info("[KakaoPay Create Request] Body: {}", body);
        return new HttpEntity<>(body, headers);
    }

    private Map<String, String> createKakaoPayRequestBody(UserToken userToken, CreditProduct product,
                                                                    String orderId) {
        Map<String, String> body = new HashMap<>();
        body.put("cid", kakaoPayProperties.cid());
        body.put("partner_order_id", orderId);
        body.put("partner_user_id", String.valueOf(userToken.userId()));
        body.put("item_name", product.getName());
        body.put("item_code", String.valueOf(product.id));
        body.put("quantity", "1");
        body.put("total_amount", String.valueOf(product.price));
        body.put("tax_free_amount", "0");

        String redirectBaseUrl = kakaoPayProperties.redirectBaseUrl();
        body.put("approval_url", redirectBaseUrl + "/credits/payment/success");
        body.put("cancel_url", redirectBaseUrl + "/credits/payment/cancel");
        body.put("fail_url", redirectBaseUrl + "/credits/payment/fail");

        return body;
    }

    private String handleKakaoPayResponse(ResponseEntity<KakaoPayReadyResponse> response, Long userId,
                                          String orderId) {
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String tid = response.getBody().tid();

            // save tid, orderId to Redis
            String tidKey = REDIS_TID_KEY_PREFIX + userId;
            redisTemplate.opsForValue().set(tidKey, tid, REDIS_KEY_EXPIRATION);

            String orderIdKey = REDIS_ORDER_ID_KEY_PREFIX + tid;
            redisTemplate.opsForValue().set(orderIdKey, orderId, REDIS_KEY_EXPIRATION);

            return response.getBody().next_redirect_pc_url(); // pc only
        }
        throw new IllegalStateException("카카오페이 요청 실패");
    }

    public void cancelKakaoPayPayment(String tid, long amount) {
        HttpEntity<Map<String, String>> httpEntity = createKakaoPayCancelRequest(tid, amount);

        ResponseEntity<KakaoPayCancelResponse> response = restTemplate.postForEntity(
                KAKAO_PAY_CANCEL_URL,
                httpEntity,
                KakaoPayCancelResponse.class
        );

        if (!response.getStatusCode().is2xxSuccessful()
                || response.getBody() == null
                || !response.getBody().status().equals("CANCEL_PAYMENT")) {
            throw new PaymentCancelFailedException("카카오페이 환불 처리가 실패했습니다.");
        }
    }

    private HttpEntity<Map<String, String>> createKakaoPayCancelRequest(String tid, long amount) {
        HttpHeaders headers = createKakaoPayHeaders();
        Map<String, String> body = createKakaoPayCancelRequestBody(tid, amount);
        return new HttpEntity<>(body, headers);
    }

    private Map<String, String> createKakaoPayCancelRequestBody(String tid, long amount) {
        Map<String, String> body = new HashMap<>();
        body.put("cid", kakaoPayProperties.cid());
        body.put("tid", tid);
        body.put("cancel_amount", String.valueOf(amount));
        body.put("cancel_tax_free_amount", "0");

        return body;
    }

    public KakaoPayApproveResponse approveKakaoPayPayment(Long userId, String pgToken) {
        String tid = loadTidFromRedis(userId);
        String orderId = loadOrderIdFromRedis(tid);
        HttpEntity<Map<String, String>> httpEntity = createKakaoPayApproveRequest(userId, tid, orderId, pgToken);

        ResponseEntity<KakaoPayApproveResponse> response = restTemplate.postForEntity(
                KAKAO_PAY_APPROVE_URL,
                httpEntity,
                KakaoPayApproveResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        throw new IllegalStateException("카카오페이 결제 승인 실패");
    }

    private String loadTidFromRedis(Long userId) {
        String redisKey = REDIS_TID_KEY_PREFIX + userId;
        String tid = redisTemplate.opsForValue().get(redisKey);
        if (tid == null) {
            throw new BadRequestException(ErrorCode.INVALID_PAYMENT_SESSION);
        }
        redisTemplate.delete(redisKey);
        return tid;
    }

    private String loadOrderIdFromRedis(String tid) {
        String orderIdKey = REDIS_ORDER_ID_KEY_PREFIX + tid;
        String orderId = redisTemplate.opsForValue().get(orderIdKey);
        if (orderId == null) {
            throw new BadRequestException(ErrorCode.INVALID_PAYMENT_SESSION);
        }
        redisTemplate.delete(orderIdKey);
        return orderId;
    }

    private HttpEntity<Map<String, String>> createKakaoPayApproveRequest(Long userId, String tid,
                                                                                   String orderId, String pgToken) {
        HttpHeaders headers = createKakaoPayHeaders();
        Map<String, String> body = new HashMap<>();
        body.put("cid", kakaoPayProperties.cid());
        body.put("tid", tid);
        body.put("partner_order_id", orderId);
        body.put("partner_user_id", String.valueOf(userId));
        body.put("pg_token", pgToken);

        return new HttpEntity<>(body, headers);
    }

    // ==== common methods ====
    private HttpHeaders createKakaoPayHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + kakaoPayProperties.secretKey());
        return headers;
    }
}
