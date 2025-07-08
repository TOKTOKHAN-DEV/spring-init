package com.spring.spring_init.tossPayment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyAmountRequestDto {
	@Schema(name = "orderId", description = "주문 ID")
	@NotNull(message = "orderId 필수 값 입니다.")
	private String orderId;
	
	@Schema(name = "amount", description = "결제 금액")
	@NotNull(message = "amount 필수 값 입니다.")
	private Long amount;
}
