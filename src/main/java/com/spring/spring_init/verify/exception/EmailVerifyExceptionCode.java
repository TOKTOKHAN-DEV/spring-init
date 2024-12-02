package com.spring.spring_init.verify.exception;

import com.spring.spring_init.common.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum EmailVerifyExceptionCode implements BaseErrorCode {
    NOT_MATCH_CODE(HttpStatus.BAD_REQUEST, "NOT_MATCH_CODE", "인증번호가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "INVALID_TOKEN", "유효하지 않은 token 입니다."),
    TIME_OVER(HttpStatus.BAD_REQUEST, "TIME_OVER", "인증 시간이 초과되었습니다.");

    private final HttpStatus httpStatusCode;

    private final String code;

    private final String message;

    EmailVerifyExceptionCode(HttpStatus httpStatusCode, String code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatusCode;
    }
}
