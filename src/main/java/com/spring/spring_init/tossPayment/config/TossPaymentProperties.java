package com.spring.spring_init.tossPayment.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class TossPaymentProperties {
	private static final String PAYMENT_AUTH_HEADER_PREFIX = "Basic ";
	private static final String BASIC_DELIMITER = ":";
	
	@Value("${toss.secret-key}")
	private String tossSecretKey;
	
	@Value("${toss.confirm-url}")
	private String tossConfirmUrl;
	
	@Value("${toss.cancel-url}")
	private String tossCancelUrl;
	
	@Value("${toss.virtual-account-url}")
	private String tossVirtualAccountUrl;
	
	/**
	 * 결제 인증 헤더를 생성합니다.
	 * - 참고 문서 : https://docs.tosspayments.com/reference/using-api/authorization
	 * - 시크릿 키 뒤에 `:`을 추가하고 base64로 인코딩하여 사용
	 * @return 결제 인증 헤더 문자열
	 */
	public String getAuthorizationHeader() {
		String auth = tossSecretKey + BASIC_DELIMITER;
		return PAYMENT_AUTH_HEADER_PREFIX +
			Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
	}
}
