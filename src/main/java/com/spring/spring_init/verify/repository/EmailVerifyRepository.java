package com.spring.spring_init.verify.repository;


import com.spring.spring_init.verify.entity.EmailVerifier;
import com.spring.spring_init.verify.entity.EmailVerifyPurpose;
import java.util.Optional;

public interface EmailVerifyRepository {

    EmailVerifier save(final EmailVerifier emailVerifier);


    Optional<EmailVerifier> findByEmailAndCodeAndPurposeOrderByCreatedAtDesc(
        final String email,
        final String code,
        final EmailVerifyPurpose emailVerifyPurpose
    );

    Optional<EmailVerifier> findByEmailAndToken(
        final String email,
        final String emailToken
    );

    void deleteByEmailAndPurpose(
        final String email,
        final EmailVerifyPurpose emailVerifyPurpose
    );

    Optional<EmailVerifier> findByEmailAndTokenAndPurpose(
        final String email,
        final String token,
        final EmailVerifyPurpose emailVerifyPurpose
    );

    Optional<EmailVerifier> findFirstByEmailAndPurposeOrderByCreatedAtDesc(
        final String email,
        final EmailVerifyPurpose emailVerifyPurpose
    );
}
