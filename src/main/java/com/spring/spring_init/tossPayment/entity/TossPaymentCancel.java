package com.spring.spring_init.tossPayment.entity;

import lombok.Getter;

@Getter
public enum TossPaymentCancel {
	CHANGE_OF_MIND("CHANGE_OF_MIND", "고객 변심"),
	DATABASE_ERROR("DATABASE_ERROR", "데이터베이스 오류"),
	OTHER("OTHER", "기타 사유")
	
	;
	
	private final String code;
	private final String message;
	
	TossPaymentCancel(String code, String message) {
		this.code = code;
		this.message = message;
	}
}
