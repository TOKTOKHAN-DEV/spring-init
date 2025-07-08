package com.spring.spring_init.tossPayment.repository;

import org.springframework.stereotype.Repository;

import com.spring.spring_init.tossPayment.entity.TossVirtualAccount;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TossVirtualAccountRepositoryImpl implements TossVirtualAccountRepository{
	private final TossVirtualAccountJpaRepository tossVirtualAccountJpaRepository;
	
	@Override
	public TossVirtualAccount save(TossVirtualAccount tossVirtualAccount) {
		return tossVirtualAccountJpaRepository.save(tossVirtualAccount);
	}
}
