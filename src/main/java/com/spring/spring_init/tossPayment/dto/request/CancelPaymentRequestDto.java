package com.spring.spring_init.tossPayment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CancelPaymentRequestDto {
	@Schema(
		name = "cancelReason",
		description = "결제 취소 사유를 입력합니다",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotNull(message = "cancelReason 필수 값 입니다.")
	private String cancelReason;
	
	@Schema(
		name = "cancelAmount",
		description = "취소 금액",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private Long cancelAmount;
}
