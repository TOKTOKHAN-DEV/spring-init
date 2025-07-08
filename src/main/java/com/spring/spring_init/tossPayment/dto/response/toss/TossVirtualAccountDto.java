package com.spring.spring_init.tossPayment.dto.response.toss;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TossVirtualAccountDto {
	private Long virtualAccountId;
	private String accountNumber;
	private String bankCode;
	private String customerName;
	private LocalDateTime dueDate;
	private String refundStatus;
	private Boolean expired;
	private String settlementStatus;
}
