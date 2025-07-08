package com.spring.spring_init.tossPayment.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.spring.spring_init.tossPayment.dto.request.SaveAmountRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "toss_payment_verify_amount")
public class TossPaymentVerifyAmount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "verify_id", unique = true, nullable = false)
	private Long verifyId;
	
	@Column(name = "order_id", unique = true, nullable = false)
	private String orderId;
	
	@Column(name = "amount", nullable = false)
	private Long amount;
	
	@CreatedDate
	@Column(name = "created_at", nullable = false, columnDefinition = "timestamptz")
	private LocalDateTime createdAt;
	
	public TossPaymentVerifyAmount(SaveAmountRequestDto requestDto) {
		this.orderId = requestDto.getOrderId();
		this.amount = requestDto.getAmount();
	}
}
