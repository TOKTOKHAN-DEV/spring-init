package com.spring.spring_init.common.exception;

import com.spring.spring_init.common.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonExceptionCode implements BaseErrorCode {
    FIELD_ERROR(org.springframework.http.HttpStatus.BAD_REQUEST, "FIELD_ERROR", "Field error");

    private final HttpStatus httpStatusCode;

    private final String code;

    private final String message;

    CommonExceptionCode(HttpStatus httpStatusCode, String code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatusCode;
    }
}
