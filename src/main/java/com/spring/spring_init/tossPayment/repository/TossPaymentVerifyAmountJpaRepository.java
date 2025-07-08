package com.spring.spring_init.tossPayment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.spring_init.tossPayment.entity.TossPaymentVerifyAmount;

public interface TossPaymentVerifyAmountJpaRepository extends JpaRepository<TossPaymentVerifyAmount, Long> {
	Optional<TossPaymentVerifyAmount> findByOrderId(String orderId);
}
