package com.spring.spring_init.user.exception;

import com.spring.spring_init.common.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserExceptionCode implements BaseErrorCode {
    EXIST_EMAIL(HttpStatus.BAD_REQUEST, "EXIST_EMAIL", "중복된 email 입니다"),
    UNVERIFIED_EMAIL(HttpStatus.BAD_REQUEST, "UNVERIFIED_EMAIL", "인증되지 않은 이메일입니다"),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "PASSWORD_MISMATCH", "비밀번호 확인란이 일치하지 않습니다"),
    LOGIN_FAIL(HttpStatus.BAD_REQUEST, "LOGIN_FAIL", "아이디나 비밀번호가 틀렸습니다"),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "NOT_FOUND_USER", "회원을 찾을 수 없습니다"),
    NOT_MATCH_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "NOT_MATCH_CURRENT_PASSWORD",
        "비밀번호를 다시 확인해주세요"),
    NOT_MATCH_CHANGE_PASSWORD(HttpStatus.BAD_REQUEST, "NOT_MATCH_CHANGE_PASSWORD",
        "비밀번호가 일치하지 않습니다"),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, "SAME_PASSWORD", "기존 비밀번호와 같을 수 없습니다"),
    NOT_MATCH_USER(HttpStatus.BAD_REQUEST, "NOT_MATCH_USER", "회원 정보가 일치하지 않습니다");

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
