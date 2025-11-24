package qwerty.chaekit.service.ebook.credit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qwerty.chaekit.domain.ebook.credit.payment.CreditPaymentTransactionRepository;
import qwerty.chaekit.domain.ebook.credit.wallet.CreditWalletRepository;
import qwerty.chaekit.dto.ebook.credit.CreditProductInfoResponse;
import qwerty.chaekit.dto.ebook.credit.CreditTransactionResponse;
import qwerty.chaekit.dto.ebook.credit.CreditWalletResponse;
import qwerty.chaekit.dto.ebook.credit.payment.CreditPaymentApproveResponse;
import qwerty.chaekit.dto.ebook.credit.payment.CreditPaymentReadyRequest;
import qwerty.chaekit.dto.external.kakaopay.KakaoPayApproveResponse;
import qwerty.chaekit.dto.page.PageResponse;
import qwerty.chaekit.global.constant.CreditProduct;
import qwerty.chaekit.global.security.resolver.UserToken;
import qwerty.chaekit.service.ebook.credit.exception.PaymentCancelFailedException;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditService {
    private final KakaoPayService kakaoPayService;
    private final CreditPaymentTransactionRepository creditPaymentTransactionRepository;
    private final CreditWalletRepository creditWalletRepository;
    private final PaymentApprovalProcessor paymentApprovalProcessor;

    @Transactional(readOnly = true)
    public List<CreditProductInfoResponse> getCreditProductList() {
        return Arrays.stream(CreditProduct.values())
                .map(creditProduct -> CreditProductInfoResponse.builder()
                        .id(creditProduct.getId())
                        .creditAmount(creditProduct.getCreditAmount())
                        .price(creditProduct.getPrice())
                        .build()
                ).toList();
    }

    public String requestKakaoPay(UserToken userToken, CreditPaymentReadyRequest request) {
        return kakaoPayService.requestKakaoPay(userToken, request);
    }

    public CreditPaymentApproveResponse approveKakaoPayPayment(UserToken userToken, String pgToken) {
        Long userId = userToken.userId();

        // 카카오페이 결제 승인
        KakaoPayApproveResponse response = kakaoPayService.approveKakaoPayPayment(userId, pgToken);


        // 트랜잭션 내 처리(PaymentApprovalProcessor 로 이동 필요)
        try {
            paymentApprovalProcessor.finalizePayment(response, userId);
        } catch (Exception ex) {
            // 예외 발생 시 결제 취소 및 에러 처리
            handlePaymentFailureAndCancel(ex, response);
        }

        CreditProduct creditProduct = CreditProduct.getCreditProduct(Integer.parseInt(response.item_code()));

        return CreditPaymentApproveResponse.builder()
                .orderId(response.partner_order_id())
                .creditProductId(creditProduct.getId())
                .creditProductName(creditProduct.getName())
                .paymentMethod(response.payment_method_type())
                .paymentAmount(response.amount().total())
                .approvedAt(response.approved_at())
                .build();
    }

    private void handlePaymentFailureAndCancel(Exception ex, KakaoPayApproveResponse response) {
        String tid = response.tid();
        try {
            kakaoPayService.cancelKakaoPayPayment(tid, response.amount().total());
            log.info("카카오페이 결제 취소 완료: tid={} 이유={}", tid, ex.getMessage());
            throw new RuntimeException("시스템 오류로 결제가 자동 환불되었습니다.", ex);
        } catch (PaymentCancelFailedException cancelEx) {
            log.error("카카오페이 결제 취소 실패: tid={}, 이유={}", tid, cancelEx.getMessage());
            throw new RuntimeException("결제 취소에 실패했습니다. 고객센터에 문의해주세요.", ex);
        }
    }

    @Transactional(readOnly = true)
    public CreditWalletResponse getMyWallet(UserToken userToken) {
        return creditWalletRepository.findByUser_Id(userToken.userId())
                .map(wallet -> CreditWalletResponse.builder()
                        .walletId(wallet.getId())
                        .balance(wallet.getBalance())
                        .build()
                ).orElseThrow(() -> new IllegalStateException("Credit Wallet not found"));
    }

    @Transactional(readOnly = true)
    public PageResponse<CreditTransactionResponse> getMyWalletTransactions(UserToken userToken, Pageable pageable) {
        Page<CreditTransactionResponse> result = creditPaymentTransactionRepository.getCreditTransactionsByWallet_User_Id(
                userToken.userId(), pageable
        ).map(transaction -> CreditTransactionResponse.builder()
                .orderId(transaction.getOrderId())
                .productId(transaction.getCreditProductId())
                .productName(transaction.getCreditProductName())
                .type(transaction.getTransactionType())
                .creditAmount(transaction.getCreditAmount())
                .paymentAmount(transaction.getPaymentAmount())
                .approvedAt(transaction.getApprovedAt())
                .build()
        );

        return PageResponse.of(result);
    }
}
