package com.spring.spring_init.tossPayment.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.spring.spring_init.tossPayment.entity.TossPaymentVerifyAmount;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TossPaymentVerifyAmountRepositoryImpl implements TossPaymentVerifyAmountRepository {
	private final TossPaymentVerifyAmountJpaRepository tossPaymentVerifyAmountJpaRepository;
	
	@Override
	public TossPaymentVerifyAmount save(TossPaymentVerifyAmount tossPaymentVerifyAmount) {
		return tossPaymentVerifyAmountJpaRepository.save(tossPaymentVerifyAmount);
	}
	
	@Override
	public Optional<TossPaymentVerifyAmount> findByOrderId(String orderId) {
		return tossPaymentVerifyAmountJpaRepository.findByOrderId(orderId);
	}
	
	@Override
	public void delete(TossPaymentVerifyAmount amount) {
		tossPaymentVerifyAmountJpaRepository.delete(amount);
	}
}
