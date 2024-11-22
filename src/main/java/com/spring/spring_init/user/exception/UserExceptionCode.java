package com.spring.spring_init.user.exception;

import com.spring.spring_init.common.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserExceptionCode implements BaseErrorCode {
    EXIST_USERNAME(HttpStatus.BAD_REQUEST, "EXIST_USERNAME", "중복된 username 입니다"),
    LOGIN_FAIL(HttpStatus.BAD_REQUEST, "LOGIN_FAIL", "아이디나 비밀번호가 틀렸습니다");

    private final HttpStatus httpStatusCode;

    private final String code;

    private final String message;

    UserExceptionCode(HttpStatus httpStatusCode, String code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatusCode;
    }
}
