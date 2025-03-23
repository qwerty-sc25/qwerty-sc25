package qwerty.chaekit.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import qwerty.chaekit.global.response.ApiErrorResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // TODO: 추후에 모든 CustomException을 ENUM으로 통일시키는 것 고려할것
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleBadRequestException(BadRequestException ex) {
        log.info("Bad request: {}", ex.getMessage());
        return ApiErrorResponse.of(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleGenericException(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return ApiErrorResponse.of("500", "Internal Server Error: " + ex.getMessage());
    }
}