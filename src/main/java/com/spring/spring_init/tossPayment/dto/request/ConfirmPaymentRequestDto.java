package com.spring.spring_init.tossPayment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmPaymentRequestDto {
	@Schema(
		name = "paymentKey",
		requiredMode = Schema.RequiredMode.REQUIRED,
		description = """
			### 결제 키
			- 프론트에서 SDK를 사용해 결제 요청을 하고, 결제 승인 시 Toss에서 발급하는 결제 키입니다.
			- 결제 키는 각 결제에 고유한 값이고, 결제 인증이 끝나면 자동으로 발급됩니다.
			- 참고 : https://docs.tosspayments.com/guides/v2/get-started/payment-flow#paymentkey%EB%8A%94-%EC%99%9C-%ED%95%84%EC%9A%94%ED%95%9C%EA%B0%80%EC%9A%94
			"""
	)
	@NotNull(message = "paymentKey 필수 값 입니다.")
	private String paymentKey;
	
	@Schema(
		name = "orderId",
		requiredMode = Schema.RequiredMode.REQUIRED,
		description = """
			### 주문 ID
			- 결제 요청 시 프론트에서 생성하여 전달한 주문 ID입니다.
			- 주문 ID는 중복되지 않는 고유한 값이어야 합니다.
			"""
	)
	@NotNull(message = "orderId 필수 값 입니다.")
	private String orderId;
	
	@Schema(
		name = "amount",
		requiredMode = Schema.RequiredMode.REQUIRED,
		description = "결제 금액"
	)
	@NotNull(message = "amount 필수 값 입니다.")
	private Long amount;
}
