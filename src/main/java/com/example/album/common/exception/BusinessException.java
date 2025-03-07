package com.example.album.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Business exception for handling application-specific errors
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.code = status.value();
    }
}
