package com.spring.spring_init.tossPayment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.spring_init.tossPayment.entity.TossVirtualAccount;

public interface TossVirtualAccountJpaRepository extends JpaRepository<TossVirtualAccount, Long> {
}
