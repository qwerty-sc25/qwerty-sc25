package qwerty.chaekit.global.response.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import qwerty.chaekit.global.response.ApiErrorResponse;
import qwerty.chaekit.global.response.ApiSuccessResponse;

/**
 * 모든 컨트롤러의 응답을 가로채어, 성공 응답을 { isSuccessful: true, data: ... } 형식으로 자동 래핑하는 역할을 합니다.
 * - 컨트롤러에서 순수 DTO만 반환해도, 이 클래스가 응답을 ApiSuccessResponse로 감싸줍니다.
 * - 이미 감싸진 응답(ApiSuccessResponse 또는 ApiErrorResponse)은 중복으로 감싸지 않습니다.
 * - 예외 응답(@ExceptionHandler)은 ResponseBodyAdvice에 적용되지 않기 때문에 별도로 처리됩니다.
 * 예:
 * {
 *   "isSuccessful": true,
 *   "data": {
 *     "username": "testuser",
 *     "nickname": "테스트 유저"
 *   }
 * }
 */

@RestControllerAdvice
@RequiredArgsConstructor
public class ApiResponseWrapperAdvice implements ResponseBodyAdvice<Object> {
    private final ObjectMapper objectMapper;
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        String path = request.getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-resources") || path.startsWith("/swagger-ui")) {
            return body;
        }

        if (body instanceof ApiSuccessResponse<?> || body instanceof ApiErrorResponse) {
            return body;
        }

        if (body instanceof String) {
            try {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return objectMapper.writeValueAsString(ApiSuccessResponse.of(body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON 직렬화 실패", e);
            }
        }

        return ApiSuccessResponse.of(body);
    }
}