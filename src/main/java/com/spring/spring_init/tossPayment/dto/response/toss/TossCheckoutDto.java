package com.spring.spring_init.tossPayment.dto.response.toss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TossCheckoutDto {
	@Schema(
		description = "결제창이 열리는 주소",
		example = "https://api.tosspayments.com/v1/checkout/checkoutId",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String url;
}
