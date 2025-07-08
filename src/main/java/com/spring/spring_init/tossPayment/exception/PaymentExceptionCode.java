package com.spring.spring_init.tossPayment.exception;

import org.springframework.http.HttpStatus;

import com.spring.spring_init.common.base.BaseErrorCode;

public enum PaymentExceptionCode implements BaseErrorCode {
	INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "INVALID_AMOUNT", "결제 금액이 유효하지 않습니다"),
	CONFIRM_PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "CONFIRM_PAYMENT_FAILED", "결제 승인에 실패했습니다"),
	CONFIRM_SERVER_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CONFIRM_SERVER_FAILED", "서버 오류로 결제 승인에 실패했습니다"),
	PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_NOT_FOUND", "결제 정보가 존재하지 않습니다"),
	INVALID_SIGNATURE(HttpStatus.FORBIDDEN, "INVALID_SIGNATURE", "서명이 유효하지 않습니다"),
	INVALID_SECRET(HttpStatus.FORBIDDEN, "INVALID_SECRET", "Secret 값이 유효하지 않습니다"),
	
	;
	
	private final HttpStatus httpStatusCode;
	private final String code;
	private final String message;
	
	PaymentExceptionCode(HttpStatus httpStatusCode, String code, String message) {
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
