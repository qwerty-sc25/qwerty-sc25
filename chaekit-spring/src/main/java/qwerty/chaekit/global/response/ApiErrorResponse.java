package qwerty.chaekit.global.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API 성공 응답을 감싸는 클래스")
public record ApiErrorResponse(
        boolean isSuccessful,
        String errorCode,
        String errorMessage
) {
    public static ApiErrorResponse of(String errorCode, String errorMessage) {
        return new ApiErrorResponse(false, errorCode, errorMessage);
    }
}