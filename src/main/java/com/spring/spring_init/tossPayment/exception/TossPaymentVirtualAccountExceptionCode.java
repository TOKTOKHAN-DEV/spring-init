package com.spring.spring_init.tossPayment.exception;

import java.util.Arrays;

import org.springframework.http.HttpStatus;

import com.spring.spring_init.common.base.BaseErrorCode;
public enum TossPaymentVirtualAccountExceptionCode implements BaseErrorCode {
	DUPLICATED_ORDER_ID(HttpStatus.BAD_REQUEST, "DUPLICATED_ORDER_ID", "이미 승인 및 취소가 진행된 중복된 주문번호 입니다. 다른 주문번호로 진행해주세요."),
	INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
	INVALID_REGISTRATION_NUMBER_TYPE(HttpStatus.BAD_REQUEST, "INVALID_REGISTRATION_NUMBER_TYPE", "유효하지 않은 등록 번호 타입입니다."),
	INVALID_DATE(HttpStatus.BAD_REQUEST, "INVALID_DATE", "날짜 데이터가 잘못 되었습니다."),
	INVALID_BANK(HttpStatus.BAD_REQUEST, "INVALID_BANK", "유효하지 않은 은행입니다."),
	EXCEED_MAX_DUE_DATE(HttpStatus.BAD_REQUEST, "EXCEED_MAX_DUE_DATE", "가상 계좌의 최대 유효만료 기간을 초과했습니다."),
	UNAUTHORIZED_KEY(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_KEY", "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."),
	INCORRECT_BASIC_AUTH_FORMAT(HttpStatus.FORBIDDEN, "INCORRECT_BASIC_AUTH_FORMAT", "잘못된 요청입니다. ':' 를 포함해 인코딩해주세요."),
	FAILED_INTERNAL_SYSTEM_PROCESSING(HttpStatus.INTERNAL_SERVER_ERROR, "FAILED_INTERNAL_SYSTEM_PROCESSING", "내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요."),
	FAILED_DB_PROCESSING(HttpStatus.INTERNAL_SERVER_ERROR, "FAILED_DB_PROCESSING", "잘못된 요청 값으로 처리 중 DB 에러가 발생했습니다."),
	
	PAYMENT_VIRTUAL_ACCOUNT_ERROR_MISMATCH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_VIRTUAL_ACCOUNT_ERROR_MISMATCH_ERROR", "가상계좌 발급 과정에서 서버 에러가 발생했습니다.")
	
	;
	
	
	private final HttpStatus httpStatusCode;
	private final String code;
	private final String message;
	
	TossPaymentVirtualAccountExceptionCode(HttpStatus httpStatusCode, String code, String message) {
		this.httpStatusCode = httpStatusCode;
		this.code = code;
		this.message = message;
	}
	
	public static TossPaymentVirtualAccountExceptionCode findByCode(String code) {
		return Arrays.stream(values())
			.filter(v -> v.name().equals(code))
			.findAny()
			.orElse(PAYMENT_VIRTUAL_ACCOUNT_ERROR_MISMATCH_ERROR);
	}
	
	@Override
	public String getCode() {
		return "";
	}
	
	@Override
	public String getMessage() {
		return "";
	}
	
	@Override
	public HttpStatus getHttpStatus() {
		return null;
	}
}
