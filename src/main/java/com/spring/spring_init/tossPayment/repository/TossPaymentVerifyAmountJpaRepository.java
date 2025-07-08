package com.spring.spring_init.tossPayment.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.spring_init.tossPayment.entity.TossPaymentVerifyAmount;

public interface TossPaymentVerifyAmountJpaRepository extends JpaRepository<TossPaymentVerifyAmount, Long> {
	Optional<TossPaymentVerifyAmount> findByOrderId(String orderId);
	
	List<TossPaymentVerifyAmount> findByCreatedAtBefore(LocalDateTime createdAtBefore);
}
