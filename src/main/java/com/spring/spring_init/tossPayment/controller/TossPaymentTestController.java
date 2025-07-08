package com.spring.spring_init.tossPayment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.spring_init.common.dto.ResponseDTO;
import com.spring.spring_init.tossPayment.dto.request.CancelPaymentRequestDto;
import com.spring.spring_init.tossPayment.dto.response.CancelPaymentResponseDto;
import com.spring.spring_init.tossPayment.service.TossPaymentClient;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/test/payment")
@RequiredArgsConstructor
public class TossPaymentTestController {
	private final TossPaymentClient tossPaymentClient;
	
	// 결제 페이지
	@GetMapping
	public String paymentPage() {
		return "test/payment";
	}
	
	// 결제 성공 페이지
	@GetMapping("/success")
	public String paymentSuccess(
		@RequestParam String paymentKey,
		@RequestParam String orderId,
		@RequestParam Long amount
	) {
		return "test/success";
	}
	
	// 결제 실패 페이지
	@GetMapping("/fail")
	public String paymentFail(
		@RequestParam String message,
		@RequestParam String code
	) {
		return "test/fail";
	}
	
	// 결제 취소 테스트
	@PostMapping("/cancel")
	public ResponseEntity<ResponseDTO<CancelPaymentResponseDto>> cancelPayment(
		@RequestParam String paymentKey,
		@Validated @RequestBody CancelPaymentRequestDto requestDto
	) {
		return ResponseEntity.ok(
			ResponseDTO.<CancelPaymentResponseDto>builder()
				.statusCode(HttpStatus.OK.value())
				.message("SUCCESS")
				.data(tossPaymentClient.requestCancel(paymentKey, requestDto))
				.build()
		);
	}
}
