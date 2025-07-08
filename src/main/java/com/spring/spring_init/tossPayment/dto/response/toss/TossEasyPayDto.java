package com.spring.spring_init.tossPayment.dto.response.toss;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TossEasyPayDto {
	private String provider;
	private Long amount;
	private Long discountAmount;
}
