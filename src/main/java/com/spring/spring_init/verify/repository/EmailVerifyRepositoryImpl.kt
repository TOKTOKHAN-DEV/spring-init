package com.spring.spring_init.verify.repository

import com.spring.spring_init.common.security.jwt.TokenProvider
import com.spring.spring_init.verify.entity.EmailVerifier
import com.spring.spring_init.verify.entity.EmailVerifyPurpose
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class EmailVerifyRepositoryImpl(
    private val emailVerifyJpaRepository: EmailVerifyJpaRepository,
    private val tokenProvider: TokenProvider
) : EmailVerifyRepository {

    override fun save(emailVerifier: EmailVerifier): EmailVerifier {
        return emailVerifyJpaRepository.save(emailVerifier)
    }

    override fun findByEmailAndCodeAndPurposeOrderByCreatedAtDesc(
        email: String,
        code: String,
        emailVerifyPurpose: EmailVerifyPurpose
    ): Optional<EmailVerifier> {
        return emailVerifyJpaRepository.findByEmailAndCodeAndPurposeOrderByCreatedAtDesc(
            email,
            code,
            emailVerifyPurpose
        )
    }

    override fun findByEmailAndToken(
        email: String,
        emailToken: String
    ): Optional<EmailVerifier> {
        return emailVerifyJpaRepository.findByEmailAndToken(email, emailToken)
    }

    override fun deleteByEmailAndPurpose(email: String, emailVerifyPurpose: EmailVerifyPurpose) {
        emailVerifyJpaRepository.deleteByEmailAndPurpose(email, emailVerifyPurpose)
    }

    override fun findByEmailAndTokenAndPurpose(
        email: String,
        token: String,
        emailVerifyPurpose: EmailVerifyPurpose
    ): Optional<EmailVerifier> {
        return emailVerifyJpaRepository.findByEmailAndTokenAndPurpose(
            email,
            token,
            emailVerifyPurpose
        )
    }

    override fun findFirstByEmailAndPurposeOrderByCreatedAtDesc(
        email: String,
        purpose: EmailVerifyPurpose
    ): Optional<EmailVerifier> {
        return emailVerifyJpaRepository.findFirstByEmailAndPurposeOrderByCreatedAtDesc(
            email,
            purpose
        )
    }
}
