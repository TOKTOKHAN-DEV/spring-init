package com.spring.spring_init.tossPayment.entity;

public enum TossRefundStatus {
	NONE("NONE", "환불 요청이 없는 상태입니다."),
	PENDING("PENDING", "환불을 처리 중인 상태입니다."),
	FAILED("FAILED", "환불에 실패한 상태입니다."),
	PARTIAL_FAILED("PARTIAL_FAILED", "부분 환불에 실패한 상태입니다."),
	COMPLETED("COMPLETED", "환불이 완료된 상태입니다."),
	
	;
	
	private final String code;
	private final String description;
	
	TossRefundStatus(String code, String description) {
		this.code = code;
		this.description = description;
	}
}
