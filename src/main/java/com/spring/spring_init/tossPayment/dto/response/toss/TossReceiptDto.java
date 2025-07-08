package com.spring.spring_init.tossPayment.dto.response.toss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TossReceiptDto {
	@Schema(
		description = "영수증 URL",
		example = "https://api.tosspayments.com/v1/receipts/receiptId",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String url;
}
