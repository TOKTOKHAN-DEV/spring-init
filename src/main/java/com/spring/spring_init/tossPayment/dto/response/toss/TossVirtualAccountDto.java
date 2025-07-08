package com.spring.spring_init.tossPayment.dto.response.toss;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TossVirtualAccountDto {
	@Schema(
		description = "가상계좌 타입 / 일반, 고정 중 하나",
		example = "일반",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String accountType;
	
	@Schema(
		description = "발급된 가상계좌 번호",
		maxLength = 20,
		example = "123-456-7890",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String accountNumber;
	
	@Schema(
		description = "가상계좌 은행 코드",
		example = "01",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String bankCode;
	
	@Schema(
		description = "가상계좌를 발급한 구매자명",
		maxLength = 100,
		example = "홍길동",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String customerName;
	
	@Schema(
		description = "입금 기한 / yyyy-MM-dd'T'HH:mm:ss ISO 8601 형식",
		example = "2023-10-01T12:00:00Z",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private LocalDateTime dueDate;
	
	@Schema(
		description = """
			환불 처리 상태
			- NONE: 환불 요청이 없는 상태입니다.
			- PENDING: 환불을 처리 중인 상태입니다.
			- FAILED: 환불에 실패한 상태입니다.
			- PARTIAL_FAILED: 부분 환불에 실패한 상태입니다.
			- COMPLETED: 환불이 완료된 상태입니다.
			""",
		example = "NONE",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String refundStatus;
	
	@Schema(
		description = "가상계좌 만료 여부",
		example = "false",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private Boolean expired;
	
	@Schema(
		description = """
			정산상태
			- INCOMPLETED: 정산이 완료되지 않은 상태입니다.
			- COMPLETED: 정산이 완료된 상태입니다.
			""",
		example = "INCOMPLETED",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String settlementStatus;
}
