package com.spring.spring_init.tossPayment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.spring_init.common.exception.CommonException;
import com.spring.spring_init.common.security.user.UserDetailsImpl;
import com.spring.spring_init.tossPayment.dto.request.CancelPaymentRequestDto;
import com.spring.spring_init.tossPayment.dto.request.ConfirmPaymentRequestDto;
import com.spring.spring_init.tossPayment.dto.request.SaveAmountRequestDto;
import com.spring.spring_init.tossPayment.dto.response.ConfirmPaymentResponseDto;
import com.spring.spring_init.tossPayment.dto.response.TossPaymentDto;
import com.spring.spring_init.tossPayment.entity.TossPayment;
import com.spring.spring_init.tossPayment.entity.TossPaymentCancel;
import com.spring.spring_init.tossPayment.entity.TossPaymentMethod;
import com.spring.spring_init.tossPayment.entity.TossPaymentStatus;
import com.spring.spring_init.tossPayment.entity.TossPaymentVerifyAmount;
import com.spring.spring_init.tossPayment.entity.TossVirtualAccount;
import com.spring.spring_init.tossPayment.exception.PaymentExceptionCode;
import com.spring.spring_init.tossPayment.repository.TossPaymentRepository;
import com.spring.spring_init.tossPayment.repository.TossPaymentVerifyAmountRepository;
import com.spring.spring_init.tossPayment.repository.TossVirtualAccountRepository;
import com.spring.spring_init.user.entity.User;
import com.spring.spring_init.user.exception.UserExceptionCode;
import com.spring.spring_init.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PaymentService {
	private final UserRepository userRepository;
	private final TossPaymentRepository tosspaymentRepository;
	private final TossVirtualAccountRepository tossVirtualAccountRepository;
	private final TossPaymentVerifyAmountRepository tossPaymentVerifyAmountRepository;
	private final TossPaymentClient tossPaymentClient;
	
	// STEP1. 결제 금액 임시 저장
	@Transactional
	public void savePaymentAmount(SaveAmountRequestDto requestDto) {
		tossPaymentVerifyAmountRepository.save(new TossPaymentVerifyAmount(requestDto));
	}
	
	// STEP2. 결제 승인 요청
	@Transactional
	public ConfirmPaymentResponseDto confirmPayment(
		ConfirmPaymentRequestDto requestDto,
		UserDetailsImpl userDetails
	) {
		// 결제 금액 검증
		validateAmount(requestDto);
		
		// 결제 승인 요청
		User user = getUser(userDetails);
		TossPaymentDto confirmDto =
			tossPaymentClient.requestConfirm(requestDto);
		
		saveConfirmPayment(confirmDto, user);
		saveVirtualAccount(confirmDto, user);
		updatePaymentDate(confirmDto, user);
		
		log.info("Payment request success: {}", requestDto.getPaymentKey());
		
		//TODO : 프론트와 확인해서, 프론트에 필요한 정보 보내줄 수 있도록 수정 필요
		return new ConfirmPaymentResponseDto(confirmDto);
	}
	
	// 사용자 정보 조회
	private User getUser(UserDetailsImpl userDetails) {
		return userRepository.findById(userDetails.getUserId())
			.orElseThrow(() -> new CommonException(
				UserExceptionCode.NOT_FOUND_USER.getCode(),
				UserExceptionCode.NOT_FOUND_USER.getMessage()
			));
	}
	
	// 결제 정보 저장
	private void saveConfirmPayment(
		TossPaymentDto tossPaymentDto,
		User user
	) {
		try {
			// 결제 정보 저장
			tosspaymentRepository.save(new TossPayment(tossPaymentDto, user));
		} catch (Exception e) {
			// 결제 정보 저장 실패 시, 토스 결제 취소 요청
			CancelPaymentRequestDto cancelPaymentRequestDto = new CancelPaymentRequestDto(
				TossPaymentCancel.DATABASE_ERROR.getMessage(),
				tossPaymentDto.getTotalAmount()
			);
			
			tossPaymentClient.requestCancel(tossPaymentDto.getPaymentKey(), cancelPaymentRequestDto);
			
			throw new CommonException(
				PaymentExceptionCode.CONFIRM_SERVER_FAILED.getCode(),
				PaymentExceptionCode.CONFIRM_SERVER_FAILED.getMessage()
			);
		}
	}
	
	// 가상 계좌 정보 저장
	private void saveVirtualAccount(TossPaymentDto responseDto, User user) {
		if (responseDto.getMethod().equals(TossPaymentMethod.VIRTUAL_ACCOUNT.name())) {
			tossVirtualAccountRepository.save(new TossVirtualAccount(responseDto.getVirtualAccount(), user));
		}
	}
	
	// 결제 일자 업데이트
	private static void updatePaymentDate(TossPaymentDto responseDto, User user) {
		if (responseDto.getStatus().equals(TossPaymentStatus.DONE.name())) {
			// 결제 완료 시, 사용자 결제 일자 업데이트
			// TODO : 프로젝트에 따라 구현
		}
	}
	
	// 결제 승인 금액 조회
	private void validateAmount(ConfirmPaymentRequestDto requestDto) {
		TossPaymentVerifyAmount amount =
			tossPaymentVerifyAmountRepository.findByOrderId(requestDto.getOrderId())
				.orElseThrow(() -> new CommonException(
					PaymentExceptionCode.INVALID_AMOUNT.getCode(),
					PaymentExceptionCode.INVALID_AMOUNT.getMessage()
				));
		
		if (!amount.getAmount().equals(requestDto.getAmount())) {
			throw new CommonException(
				PaymentExceptionCode.INVALID_AMOUNT.getCode(),
				PaymentExceptionCode.INVALID_AMOUNT.getMessage()
			);
		}
		
		tossPaymentVerifyAmountRepository.delete(amount);
	}
}
