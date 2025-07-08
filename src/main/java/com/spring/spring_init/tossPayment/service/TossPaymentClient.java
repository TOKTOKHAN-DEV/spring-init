package com.spring.spring_init.tossPayment.service;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.spring_init.common.exception.CommonException;
import com.spring.spring_init.tossPayment.config.TossPaymentProperties;
import com.spring.spring_init.tossPayment.dto.request.CancelPaymentRequestDto;
import com.spring.spring_init.tossPayment.dto.request.ConfirmPaymentRequestDto;
import com.spring.spring_init.tossPayment.dto.response.TossPaymentDto;
import com.spring.spring_init.tossPayment.dto.response.PaymentExceptionResponseDto;
import com.spring.spring_init.tossPayment.exception.TossPaymentCancelExceptionCode;
import com.spring.spring_init.tossPayment.exception.TossPaymentConfirmExceptionCode;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Component
public class TossPaymentClient {
	private final TossPaymentProperties props;
	private final ObjectMapper objectMapper;
	private final RestClient restClient;
	private final String paymentAuthHeader;
	private final String tossConfirmUrl;
	private final String tossCancelUrl;

	// 필요할 경우, RestClient의 기본 설정을 변경할 수 있습니다.
	private static final int CONNECTION_TIMEOUT_SECONDS = 5;
	private static final int READ_TIMEOUT_SECONDS = 30;
	
	public TossPaymentClient(TossPaymentProperties props, ObjectMapper objectMapper) {
		this.props = props;
		this.objectMapper = objectMapper;
		this.restClient = RestClient.create();
		this.paymentAuthHeader = props.getAuthorizationHeader();
		this.tossConfirmUrl = props.getTossConfirmUrl();
		this.tossCancelUrl = props.getTossCancelUrl();
	}
	
	/**
	 * 결제 승인 요청을 보냅니다.
	 * @param requestDto ConfirmPaymentRequestDto 결제 승인 요청 DTO
	 * @return TossPaymentDto 결제 승인 응답 DTO
	 */
	public TossPaymentDto requestConfirm(ConfirmPaymentRequestDto requestDto) {
		log.info("Payment request: {}", requestDto.getPaymentKey());
		
		return restClient.method(HttpMethod.POST)
			.uri(tossConfirmUrl)
			.header(HttpHeaders.AUTHORIZATION, paymentAuthHeader)
			.contentType(MediaType.APPLICATION_JSON)
			.body(requestDto)
			.retrieve()
			.onStatus(HttpStatusCode::isError, (request, response) -> {
				TossPaymentConfirmExceptionCode exceptionCode = getPaymentConfirmExceptionCode(response);
				
				throw new CommonException(
					exceptionCode.getCode(),
					exceptionCode.getMessage()
				);
			})
			.body(TossPaymentDto.class);
	}
	
	/**
	 * 결제 취소 요청을 보냅니다.
	 * @param paymentKey String 결제 키
	 * @param requestDto CancelPaymentRequestDto 결제 취소 요청 DTO
	 * @return CancelPaymentResponseDto 결제 취소 응답 DTO
	 */
	public TossPaymentDto requestCancel(
		String paymentKey,
		CancelPaymentRequestDto requestDto
	) {
		return restClient.method(HttpMethod.POST)
			.uri(tossCancelUrl.replace("{paymentKey}", paymentKey))
			.header(HttpHeaders.AUTHORIZATION, paymentAuthHeader)
			.body(requestDto)
			.retrieve()
			.onStatus(HttpStatusCode::isError, (request, response) -> {
				TossPaymentCancelExceptionCode exceptionCode = getPaymentCancelExceptionCode(response);
				
				throw new CommonException(
					exceptionCode.getCode(),
					exceptionCode.getMessage()
				);
			})
			.body(TossPaymentDto.class);
	}
	
	
	// ---------------------------- //
	
	
	// 결제 승인 에러 코드 추출
	private TossPaymentConfirmExceptionCode getPaymentConfirmExceptionCode(
		final ClientHttpResponse response
	) throws IOException {
		PaymentExceptionResponseDto responseDto =
			objectMapper.readValue(response.getBody(), PaymentExceptionResponseDto.class);
		
		return TossPaymentConfirmExceptionCode.findByCode(responseDto.getError().getCode());
	}
	
	// 결제 취소 에러 코드 추출
	private TossPaymentCancelExceptionCode getPaymentCancelExceptionCode(
		final ClientHttpResponse response
	) throws IOException {
		PaymentExceptionResponseDto responseDto =
			objectMapper.readValue(response.getBody(), PaymentExceptionResponseDto.class);
		
		return TossPaymentCancelExceptionCode.findByCode(responseDto.getError().getCode());
	}
}
