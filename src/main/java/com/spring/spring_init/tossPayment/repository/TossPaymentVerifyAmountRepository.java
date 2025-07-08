package com.spring.spring_init.tossPayment.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.spring.spring_init.tossPayment.entity.TossPaymentVerifyAmount;

public interface TossPaymentVerifyAmountRepository {
	TossPaymentVerifyAmount save(TossPaymentVerifyAmount tossPaymentVerifyAmount);
	
	Optional<TossPaymentVerifyAmount> findByOrderId(String orderId);
	
	void delete(TossPaymentVerifyAmount amount);
	
	List<TossPaymentVerifyAmount> findByCreatedAtBefore(LocalDateTime createdAtBefore);
	
	void deleteAll(List<TossPaymentVerifyAmount> verifyAmounts);
}
