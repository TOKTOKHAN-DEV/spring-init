package com.spring.spring_init.tossPayment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentExceptionResponseDto {
	@Schema(
		description = "토스페이먼츠에서 발급하는 API 요청의 고유 식별자",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String traceId;
	
	@Schema(
		description = "에러 응답",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private PaymentErrorResponse error;
	
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PaymentErrorResponse {
		
		@Schema(
			description = "에러 코드",
			requiredMode = Schema.RequiredMode.REQUIRED
		)
		private String code;
		
		@Schema(
			description = "에러 메시지",
			requiredMode = Schema.RequiredMode.REQUIRED
		)
		private String message;
	}
}