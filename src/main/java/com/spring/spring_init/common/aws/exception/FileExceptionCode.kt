package com.spring.spring_init.common.aws.exception;

import com.spring.spring_init.common.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FileExceptionCode implements BaseErrorCode {
    FAIL_UPLOAD_FILE(HttpStatus.BAD_REQUEST, "FAIL_UPLOAD_FILE", "파일 업로드에 실패했습니다"),

    ;


    private final HttpStatus httpStatusCode;

    private final String code;

    private final String message;

    FileExceptionCode(HttpStatus httpStatusCode, String code, String message) {
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
