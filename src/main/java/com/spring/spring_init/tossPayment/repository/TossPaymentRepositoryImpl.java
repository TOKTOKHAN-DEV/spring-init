package com.spring.spring_init.tossPayment.repository;

import org.springframework.stereotype.Repository;

import com.spring.spring_init.tossPayment.entity.TossPayment;

import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class TossPaymentRepositoryImpl implements TossPaymentRepository {

	private final TossPaymentJpaRepository tossPaymentJpaRepository;
	
	@Override
	public TossPayment save(TossPayment tossPayment) {
		return tossPaymentJpaRepository.save(tossPayment);
	}
}
