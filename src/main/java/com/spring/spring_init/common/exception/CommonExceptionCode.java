package com.spring.spring_init.common.exception;

import com.spring.spring_init.common.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonExceptionCode implements BaseErrorCode {
    FIELD_ERROR(HttpStatus.BAD_REQUEST, "FIELD_ERROR", "Field error"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 에러가 발생하였습니다");

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
