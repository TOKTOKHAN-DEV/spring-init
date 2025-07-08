package com.spring.spring_init.tossPayment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.spring_init.tossPayment.entity.TossPayment;
import com.spring.spring_init.tossPayment.entity.TossPaymentStatus;

public interface TossPaymentJpaRepository extends JpaRepository<TossPayment, Long> {
	Boolean existsByUserAndPaymentStatus(Long user, TossPaymentStatus paymentStatus);
	
	Optional<TossPayment> findByTossOrderId(String orderId);
}
