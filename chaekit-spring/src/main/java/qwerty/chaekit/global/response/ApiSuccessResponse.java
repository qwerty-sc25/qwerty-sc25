package qwerty.chaekit.global.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API 에러 응답을 감싸는 클래스")
public record ApiSuccessResponse<T>(
        boolean isSuccessful,
        T data
) {
    public static <T> ApiSuccessResponse<T> of(T data) {
        return new ApiSuccessResponse<>(true, data);
    }
}
