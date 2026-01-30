package com.spring.spring_init.verify.entity;

import com.spring.spring_init.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity(name = "email_verifier")
public class EmailVerifier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_verifier_id", nullable = false)
    private Long emailVerifierId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "code")
    private String code;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "purpose")
    @Enumerated(EnumType.STRING)
    private EmailVerifyPurpose purpose;

    public EmailVerifier(
        final String email,
        final String code,
        final String token,
        final EmailVerifyPurpose purpose
    ) {
        this.email = email;
        this.code = code;
        this.token = token;
        this.purpose = purpose;
    }
}
