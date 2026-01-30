package com.spring.spring_init.common.security.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthExceptionCode {
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_ACCESS", "인증이 필요한 접근입니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근이 거부되었습니다"),
    TOKEN_EXPIRED(HttpStatus.valueOf(444), "TOKEN_EXPIRED", "토큰이 만료되었습니다");

    private final HttpStatus httpStatusCode;

    private final String code;

    private final String message;

    AuthExceptionCode(HttpStatus httpStatusCode, String code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
    }
}
