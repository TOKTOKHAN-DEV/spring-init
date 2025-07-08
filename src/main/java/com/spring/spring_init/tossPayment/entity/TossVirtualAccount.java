package com.spring.spring_init.tossPayment.entity;

import java.time.LocalDateTime;

import com.spring.spring_init.tossPayment.dto.response.toss.TossVirtualAccountDto;
import com.spring.spring_init.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "toss_virtual_account")
public class TossVirtualAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name = "virtual_account_id", unique = true, nullable = false)
	private Long virtualAccountId;
	
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(name = "account_number", nullable = false)
	private String accountNumber;
	
	@Column(name = "bank_code", nullable = false)
	private String bankCode;
	
	@Column(name = "customer_name", nullable = false)
	private String customerName;
	
	@Column(name = "due_date", nullable = false)
	private LocalDateTime dueDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "refund_status", nullable = false)
	private TossRefundStatus refundStatus;
	
	@Column(name = "expired", nullable = false)
	private Boolean expired;
	
	public TossVirtualAccount(TossVirtualAccountDto responseDto, User user) {
		this.user = user;
		this.accountNumber = responseDto.getAccountNumber();
		this.bankCode = responseDto.getBankCode();
		this.customerName = responseDto.getCustomerName();
		this.dueDate = responseDto.getDueDate();
		this.refundStatus = TossRefundStatus.valueOf(responseDto.getRefundStatus());
		this.expired = responseDto.getExpired();
	}
}
