package com.spring.spring_init.tossPayment.dto.response.toss;

import java.time.ZonedDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TossCancelDto {
	@Schema(
		description = "결제를 취소한 금액",
		example = "10000",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private Long cancelAmount;
	
	@Schema(
		description = "결제 취소 사유",
		example = "고객 요청에 의한 취소",
		maxLength = 200,
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String cancelReason;
	
	@Schema(
		description = "취소된 금액 중 면세 금액",
		example = "5000",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private Long taxFreeAmount;
	
	@Schema(
		description = "취소된 금액 중 과세 제외 금액(컵 보증금 등)",
		example = "5000",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private Long taxExemptionAmount;
	
	@Schema(
		description = "결제 취소 후 환불 가능한 금액",
		example = "5000",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private Long refundableAmount;
	
	@Schema(
		description = "간편결제 서비스의 포인트, 쿠폰, 즉시할인과 같은 적립식 결제수단에서 취소된 금액",
		example = "5000",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private Long easyPayDiscountAmount;
	
	@Schema(
		description = "결제 취소 날짜와 시간 정보 / yyyy-MM-dd'T'HH:mm:ss±hh:mm ISO 8601 형식",
		example = "2023-10-01T12:00:00Z",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private ZonedDateTime canceledAt;
	
	@Schema(
		description = "취소 건의 키값",
		example = "cancelId",
		maxLength = 64,
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String transactionKey;
	
	@Schema(
		description = "취소 건의 현금영수증 키값",
		example = "receiptId",
		maxLength = 200,
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	private String receiptKey;
	
	@Schema(
		description = "결제 취소 상태 / DONE이면 결제가 성공적으로 취소된 상태",
		example = "DONE",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String cancelStatus;
	
	@Schema(
		description = "결제 취소 요청 ID / 비동기 결제에만 적용되는 특수 값 / 일반결제, 자동결제(빌링), 페이팔 해외결제에서는 항상 null",
		example = "cancelRequestId",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	private String cancelRequestId;
}
