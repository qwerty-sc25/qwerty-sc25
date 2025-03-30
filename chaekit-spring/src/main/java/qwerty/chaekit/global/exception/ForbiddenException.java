package qwerty.chaekit.global.exception;

import lombok.Getter;

@Getter
public class ForbiddenException extends RuntimeException {
    private final String errorCode;
    public ForbiddenException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
