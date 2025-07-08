package com.spring.spring_init.tossPayment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CheckPaymentStatusResponseDto {
	private String email;
	private Boolean isPaid;
}
