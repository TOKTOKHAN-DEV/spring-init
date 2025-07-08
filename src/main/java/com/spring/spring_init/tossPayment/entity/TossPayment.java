package com.spring.spring_init.tossPayment.entity;

import java.time.ZonedDateTime;

import com.spring.spring_init.tossPayment.dto.response.ConfirmPaymentResponseDto;
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
@Table(name = "toss_payment")
public class TossPayment {
	/* TODO
	 * - 저장할 내용에 대해서 고민해보아야 함
	 * - 현재는 가장 기본적인 것들만 저장하고 있음
	 */
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id", unique = true, nullable = false)
	private Long paymentId;
	
	@Column(name = "user_id", nullable = false)
	private Long user;
	
	@Column(name = "toss_payment_key", nullable = false)
	private String tossPaymentKey;
	
	@Column(name = "toss_order_id", nullable = false)
	private String tossOrderId;
	
	@Column(name = "order_name", nullable = false)
	private String orderName;
	
	@Column(name = "currency", nullable = false)
	private String currency;
	
	@Column(name = "total_amount", nullable = false)
	private Long totalAmount;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false)
	private TossPaymentMethod paymentMethod;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false)
	private TossPaymentStatus paymentStatus;
	
	@OneToOne
	@JoinColumn(name = "virtual_account_id")
	private TossVirtualAccount virtualAccount;
	
	@Column(name = "receipt_url", nullable = false)
	private String receiptUrl;
	
	@Column(name = "checkout_url")
	private String checkoutUrl;
	
	@Column(name = "requested_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private ZonedDateTime requestedAt;
	
	@Column(name = "approved_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private ZonedDateTime approvedAt;
	
	@Column(name = "secret")
	private String secret;
	
	public TossPayment(ConfirmPaymentResponseDto responseDto, User user) {
		this.user = user.getUserId();
		this.tossPaymentKey = responseDto.getPaymentKey();
		this.tossOrderId = responseDto.getOrderId();
		this.orderName = responseDto.getOrderName();
		this.currency = responseDto.getCurrency();
		this.totalAmount = responseDto.getTotalAmount();
		this.paymentMethod = TossPaymentMethod.valueOf(responseDto.getMethod());
		this.paymentStatus = TossPaymentStatus.valueOf(responseDto.getStatus());
		this.receiptUrl = responseDto.getReceipt().getUrl();
		this.checkoutUrl = responseDto.getCheckout().getUrl();
		this.requestedAt = responseDto.getRequestedAt();
		this.approvedAt = responseDto.getApprovedAt();
		this.secret = responseDto.getSecret();
	}
	
	public void updateStatus(TossPaymentStatus status) {
		this.paymentStatus = status;
	}
}
