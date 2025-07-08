package com.spring.spring_init.tossPayment.repository;

import java.util.Optional;

import com.spring.spring_init.tossPayment.entity.TossPaymentVerifyAmount;

public interface TossPaymentVerifyAmountRepository {
	TossPaymentVerifyAmount save(TossPaymentVerifyAmount tossPaymentVerifyAmount);
	
	Optional<TossPaymentVerifyAmount> findByOrderId(String orderId);
	
	void delete(TossPaymentVerifyAmount amount);
}
