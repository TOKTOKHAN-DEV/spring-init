package com.spring.spring_init.tossPayment.dto.response;

import java.time.ZonedDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmPaymentResponseDto {
	@Schema(
		description = """
			- 결제의 키값 / 결제 고유 식별자 역할을 합니다
			- 최대 200자
			- 결제 데이터 관리를 위해 반드시 저장해야 합니다
			""",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String paymentKey;
	
	@Schema(
		description = """
			- 결제 타입 정보
			-  NORMAL(일반결제), BILLING(자동결제), BRANDPAY(브랜드페이) 중 하나
			"""
	)
	private String type;
	
	@Schema(
		description = """
			- 주문번호 / 각 주문을 구분하기 위한 고유한 값
			- 결제 요청에서 내 상점이 직접 생성한 영문 대소문자, 숫자, 특수문자 -, _로 이루어진 6자 이상 64자 이하의 문자열
			- 결제 데이터 관리를 위해 반드시 저장해야 합니다
			""",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String orderId;
	
	@Schema(
		description = """
			- 구매 상품
			- 최대 100자
			""",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	private String orderName;
	
	@Schema(
		description = """
			- 총 결제 금액
			""",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private Long totalAmount;
	
	@Schema(
		description = """
			- 결제 처리 상태
			- READY / IN_PROGRESS / WAITING_FOR_DEPOSIT / DONE / CANCELED / PARTIAL_CANCELED / ABORTED / EXPIRED 중 하나
			""",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String status;
	
	@Schema(
		description = """
			- 결제 요청이 일어난 날짜와 시간 정보
			- `yyyy-MM-dd'T'HH:mm:ss±hh:mm` ISO 8601 형식
			""",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private ZonedDateTime requestedAt;
	
	@Schema(
		description = """
			- 결제 승인이 일어난 날짜와 시간 정보
			- `yyyy-MM-dd'T'HH:mm:ss±hh:mm` ISO 8601 형식
			""",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	private ZonedDateTime approvedAt;
	
	public ConfirmPaymentResponseDto(TossPaymentDto confirmDto) {
		this.paymentKey = confirmDto.getPaymentKey();
		this.type = confirmDto.getType();
		this.orderId = confirmDto.getOrderId();
		this.orderName = confirmDto.getOrderName();
		this.totalAmount = confirmDto.getTotalAmount();
		this.status = confirmDto.getStatus();
		this.requestedAt = confirmDto.getRequestedAt();
		this.approvedAt = confirmDto.getApprovedAt();
	}
}
