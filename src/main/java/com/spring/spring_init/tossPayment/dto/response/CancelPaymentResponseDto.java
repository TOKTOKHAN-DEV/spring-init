package com.spring.spring_init.tossPayment.dto.response;

import java.time.ZonedDateTime;
import java.util.List;

import com.spring.spring_init.tossPayment.dto.response.toss.TossCancelDto;
import com.spring.spring_init.tossPayment.dto.response.toss.TossCheckoutDto;
import com.spring.spring_init.tossPayment.dto.response.toss.TossReceiptDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CancelPaymentResponseDto {
	private String paymentKey;
	private String orderId;
	private String orderName;
	private String currency;
	private Long totalAmount;
	private String method;
	private String status;
	private TossReceiptDto receipt;
	private TossCheckoutDto checkout;
	private ZonedDateTime requestedAt;
	private ZonedDateTime approvedAt;
	private TossCheckoutDto easyPay;
	private List<TossCancelDto> cancels;
	private String secret;
}
