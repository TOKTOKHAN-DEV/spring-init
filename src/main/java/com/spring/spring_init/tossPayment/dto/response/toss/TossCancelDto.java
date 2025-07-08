package com.spring.spring_init.tossPayment.dto.response.toss;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TossCancelDto {
	private Long cancelAmount;
	private String cancelReason;
	private Long taxFreeAmount;
	private Long taxExemptionAmount;
	private Long refundableAmount;
	private Long easyPayDiscountAmount;
	private ZonedDateTime canceledAt;
	private String transactionKey;
	private String receiptKey;
	private String cancelStatus;
	private String cancelRequestId;
}
