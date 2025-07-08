package com.spring.spring_init.tossPayment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import com.spring.spring_init.common.apidocs.ApiExceptionExplanation;
import com.spring.spring_init.common.apidocs.ApiResponseExplanations;
import com.spring.spring_init.common.dto.ResponseDTO;
import com.spring.spring_init.common.security.user.UserDetailsImpl;
import com.spring.spring_init.tossPayment.dto.request.ConfirmPaymentRequestDto;
import com.spring.spring_init.tossPayment.dto.request.SaveAmountRequestDto;
import com.spring.spring_init.tossPayment.dto.response.ConfirmPaymentResponseDto;
import com.spring.spring_init.tossPayment.exception.PaymentExceptionCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
	name = "[TossPayment API]",
	description = """
		## 결제 API
		- 결제 금액을 임시로 저장하고, 결제를 승인하는 플로우로 구성되어 있습니다.
		- 결제 금액 검증 후, 결제를 승인합니다.
		
		참고 : https://docs.tosspayments.com/guides/v2/payment-widget/integration
		"""
)
public interface TossPaymentApi {
	@Operation(
		summary = "1. 결제 금액 임시 저장",
		description = """
			## 결제 금액 임시 저장
			- 결제 금액을 임시로 저장합니다.
			- 결제 금액 검증을 위해 사용됩니다.
			""")
	@ApiResponseExplanations(
		errors = {
			@ApiExceptionExplanation(
				name = "결제 금액 불일치",
				value = PaymentExceptionCode.class,
				constant = "INVALID_AMOUNT"
			)
		}
	)
	ResponseEntity<ResponseDTO<Void>> savePaymentAmount(
		@Validated @RequestBody SaveAmountRequestDto requestDto
	);
	
	@Operation(
		summary = "2. 결제 승인 요청",
		description = """
			## 결제 승인 요청
			- 결제 금액 검증 후, 결제를 승인합니다.
			- 승인된 결제 정보와 가상 계좌 정보를 반환합니다.
			""")
	@ApiResponseExplanations(
		errors = {
			@ApiExceptionExplanation(
				name = "결제 금액 불일치",
				value = PaymentExceptionCode.class,
				constant = "INVALID_AMOUNT"
			),
			@ApiExceptionExplanation(
				name = "결제 승인 실패",
				value = PaymentExceptionCode.class,
				constant = "CONFIRM_PAYMENT_FAILED"
			),
			@ApiExceptionExplanation(
				name = "서버 오류로 결제 승인 실패",
				value = PaymentExceptionCode.class,
				constant = "CONFIRM_SERVER_FAILED"
			)
		}
	)
	ResponseEntity<ResponseDTO<ConfirmPaymentResponseDto>> confirmPayment(
		@Validated @RequestBody ConfirmPaymentRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	);
}
