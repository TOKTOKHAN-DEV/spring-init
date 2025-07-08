package com.spring.spring_init.tossPayment.dto.response;

import java.time.ZonedDateTime;
import java.util.List;

import com.spring.spring_init.tossPayment.dto.response.toss.TossCancelDto;
import com.spring.spring_init.tossPayment.dto.response.toss.TossCheckoutDto;
import com.spring.spring_init.tossPayment.dto.response.toss.TossEasyPayDto;
import com.spring.spring_init.tossPayment.dto.response.toss.TossReceiptDto;
import com.spring.spring_init.tossPayment.dto.response.toss.TossVirtualAccountDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * TODO
 *  - 참고 : https://docs.tosspayments.com/reference#payment-%EA%B0%9D%EC%B2%B4
 * 	- 받아야 하는 결제 정보가 있으면 추가해서 사용하세요.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TossPaymentDto {
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
			- 결제 요청 시 전달한 상점 고유 ID
			- 토스페이먼츠에서 발급
			- 최대 14자
			""",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String mid;
	
	@Schema(
		description = """
			- 결제할 때 사용한 통화
			""",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String currency;
	
	@Schema(
		description = """
			- 결제 수단
			- 카드, 가상계좌, 간편결제, 휴대폰, 계좌이체, 문화상품권, 도서문화상품권, 게임문화상품권 중 하나
			- 결제 요청 시 전달한 값과 동일합니다
			""",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	private String method;
	
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
	
	@Schema(
		description = """
			- 발행된 영수증 정보
			""",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	private TossReceiptDto receipt;
	
	@Schema(
		description = """
			- 결제창 정보
			""",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	private TossCheckoutDto checkout;
	
	@Schema(
		description = """
			- 간편결제 정보
			- 구매자가 선택한 결제수단에 따라 amount, discountAmount가 달라집니다.
			""",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	private TossEasyPayDto easyPay;
	
	@Schema(
		description = """
			- 결제한 국가
			- ISO 3166-1 alpha-2 형식
			"""
	)
	private String country;
	
	@Schema(
		description = """
			- 가상계좌로 결제하면 제공되는 가상계좌 관련 정보
			""",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	private TossVirtualAccountDto virtualAccount;
	
	@Schema(
		description = """
			- 결제 취소 이력
			""",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	private List<TossCancelDto> cancels;
	
	@Schema(
		description = """
			- 웹훅을 검증하는 최대 50자 값
			""",
		maxLength = 50,
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String secret;
}
