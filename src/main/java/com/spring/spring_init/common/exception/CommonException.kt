package com.spring.spring_init.common.exception;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

    private String code;

    public CommonException(String code, String message) {
        super(message);
        this.code = code;
    }
}
