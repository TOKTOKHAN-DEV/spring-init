package com.spring.spring_init.tossPayment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.spring_init.common.dto.ResponseDTO;
import com.spring.spring_init.common.security.user.UserDetailsImpl;
import com.spring.spring_init.tossPayment.dto.request.ConfirmPaymentRequestDto;
import com.spring.spring_init.tossPayment.dto.request.SaveAmountRequestDto;
import com.spring.spring_init.tossPayment.dto.response.ConfirmPaymentResponseDto;
import com.spring.spring_init.tossPayment.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@RequestMapping("/v1/payment")

@RestController
@RequiredArgsConstructor
@Slf4j
public class TossPaymentController implements TossPaymentApi {
	private final PaymentService paymentService;
	
	// 결제 금액 임시 저장
	@PostMapping("/save-amount/")
	public ResponseEntity<ResponseDTO<Void>> savePaymentAmount(
		@Validated @RequestBody SaveAmountRequestDto requestDto
	) {
		paymentService.savePaymentAmount(requestDto);
		return ResponseEntity.ok(
			ResponseDTO.<Void>builder()
				.statusCode(HttpStatus.OK.value())
				.message("SUCCESS")
				.build()
		);
	}
	
	// 결제 승인 요청
	@PostMapping("/confirm/")
	public ResponseEntity<ResponseDTO<ConfirmPaymentResponseDto>> confirmPayment(
		@Validated @RequestBody ConfirmPaymentRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		return ResponseEntity.ok(
			ResponseDTO.<ConfirmPaymentResponseDto>builder()
				.statusCode(HttpStatus.OK.value())
				.message("SUCCESS")
				.data(paymentService.confirmPayment(requestDto, userDetails))
				.build()
		);
	}
}
