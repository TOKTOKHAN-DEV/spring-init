package com.spring.spring_init.tossPayment.entity;

import lombok.Getter;

@Getter
public enum TossPaymentStatus {
	READY("READY", "결제 준비 중"),
	IN_PROGRESS("IN_PROGRESS", "결제 진행 중"),
	WAITING_FOR_DEPOSIT("WAITING_FOR_DEPOSIT", "입금 대기 중"),
	DONE("DONE", "결제 완료"),
	CANCELED("CANCELED", "결제 취소"),
	PARTIAL_CANCELED("PARTIAL_CANCELED", "부분 취소"),
	ABORTED("ABORTED", "결제 승인 실패"),
	EXPIRED("EXPIRED", "거래 취소")
	
	;
	
	private final String code;
	private final String description;
	
	TossPaymentStatus(String code, String description) {
		this.code = code;
		this.description = description;
	}
}
