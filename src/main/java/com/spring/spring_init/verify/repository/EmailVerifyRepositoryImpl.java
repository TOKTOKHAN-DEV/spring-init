package com.spring.spring_init.verify.repository;

import com.spring.spring_init.common.security.jwt.TokenProvider;
import com.spring.spring_init.verify.entity.EmailVerifier;
import com.spring.spring_init.verify.entity.EmailVerifyPurpose;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailVerifyRepositoryImpl implements EmailVerifyRepository {

    private final EmailVerifyJpaRepository emailVerifyJpaRepository;
    private final TokenProvider tokenProvider;


    @Override
    public EmailVerifier save(final EmailVerifier emailVerifier) {
        return emailVerifyJpaRepository.save(emailVerifier);
    }

    @Override
    public Optional<EmailVerifier> findByEmailAndCodeAndPurposeOrderByCreatedAtDesc(
        final String email,
        final String code,
        final EmailVerifyPurpose emailVerifyPurpose
    ) {
        return emailVerifyJpaRepository.findByEmailAndCodeAndPurposeOrderByCreatedAtDesc(
            email,
            code,
            emailVerifyPurpose
        );
    }

    @Override
    public Optional<EmailVerifier> findByEmailAndToken(
        final String email,
        final String emailToken
    ) {
        return emailVerifyJpaRepository.findByEmailAndToken(email, emailToken);
    }

    @Override
    public void deleteByEmailAndPurpose(String email, EmailVerifyPurpose emailVerifyPurpose) {
        emailVerifyJpaRepository.deleteByEmailAndPurpose(email, emailVerifyPurpose);
    }

    @Override
    public Optional<EmailVerifier> findByEmailAndTokenAndPurpose(
        final String email,
        final String token,
        final EmailVerifyPurpose emailVerifyPurpose
    ) {
        return emailVerifyJpaRepository.findByEmailAndTokenAndPurpose(
            email,
            token,
            emailVerifyPurpose
        );
    }

    @Override
    public Optional<EmailVerifier> findFirstByEmailAndPurposeOrderByCreatedAtDesc(
        final String email,
        final EmailVerifyPurpose purpose
    ) {
        return emailVerifyJpaRepository.findFirstByEmailAndPurposeOrderByCreatedAtDesc(
            email,
            purpose
        );
    }
}
