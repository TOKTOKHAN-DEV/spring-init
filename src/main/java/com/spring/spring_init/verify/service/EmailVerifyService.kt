package com.spring.spring_init.verify.service

import com.spring.spring_init.common.exception.CommonException
import com.spring.spring_init.user.exception.UserExceptionCode
import com.spring.spring_init.user.repository.UserRepository
import com.spring.spring_init.verify.dto.request.VerifyEmailConfirmRequest
import com.spring.spring_init.verify.dto.request.VerifyEmailRequest
import com.spring.spring_init.verify.dto.request.VerifyPasswordResetRequest
import com.spring.spring_init.verify.dto.response.VerifyEmailConfirmResponse
import com.spring.spring_init.verify.dto.response.VerifyEmailResponse
import com.spring.spring_init.verify.entity.EmailVerifier
import com.spring.spring_init.verify.entity.EmailVerifyPurpose
import com.spring.spring_init.verify.exception.EmailVerifyExceptionCode
import com.spring.spring_init.verify.repository.EmailVerifyRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class EmailVerifyService(
    private val emailVerifyRepository: EmailVerifyRepository,
    private val mailSender: MailSender,
    private val userRepository: UserRepository,
    private val emailTokenGenerator: EmailTokenGenerator
) {

    @Value("\${common.email-validation-limit-time-minutes}")
    private lateinit var emailValidationLimitTime: String

    /**
     * 이메일 검증 및 인증번호 메일 전송
     */
    @Transactional
    fun verifyEmailAndSendEmail(request: VerifyEmailRequest): VerifyEmailResponse {
        checkIfEmailExists(request.email)

        val code = emailTokenGenerator.generateVerificationCode()
        val token = emailTokenGenerator.generateVerificationToken(request.email, code)

        mailSender.sendEmail(
            request.email,
            code,
            token,
            null,
            EmailVerifyPurpose.EMAIL_VALIDATION
        )

        val emailVerifier = EmailVerifier(
            email = request.email,
            code = code,
            token = token,
            purpose = EmailVerifyPurpose.EMAIL_VALIDATION
        )
        emailVerifyRepository.save(emailVerifier)
        return VerifyEmailResponse(request.email)
    }

    /**
     * 이메일 인증 확인
     */
    fun verifyEmailConfirm(request: VerifyEmailConfirmRequest): VerifyEmailConfirmResponse {

        val findedEmailVerifier = emailVerifyRepository.findByEmailAndCodeAndPurposeOrderByCreatedAtDesc(
            request.email,
            request.code,
            EmailVerifyPurpose.EMAIL_VALIDATION
        ).orElseThrow {
            CommonException(
                EmailVerifyExceptionCode.NOT_MATCH_CODE.code,
                EmailVerifyExceptionCode.NOT_MATCH_CODE.message
            )
        }

        if (isOverValidationTimeLimit(findedEmailVerifier.createdAt!!)) {
            throw CommonException(
                EmailVerifyExceptionCode.TIME_OVER.code,
                EmailVerifyExceptionCode.TIME_OVER.message
            )
        }
        return VerifyEmailConfirmResponse(findedEmailVerifier.token!!)
    }

    /**
     * 비밀번호 재설정 링크 검증
     */
    fun verifyPasswordReset(request: VerifyPasswordResetRequest) {
        val userId = emailTokenGenerator.decodeUidByUserId(request.uid)

        val user = userRepository.findById(userId).orElseThrow {
            CommonException(
                UserExceptionCode.NOT_FOUND_USER.code,
                UserExceptionCode.NOT_FOUND_USER.message
            )
        }

        //Token 검증
        val emailVerifier = emailVerifyRepository.findByEmailAndTokenAndPurpose(
            user.email,
            request.token,
            EmailVerifyPurpose.RESET_PASSWORD
        ).orElseThrow {
            CommonException(
                EmailVerifyExceptionCode.INVALID_TOKEN.code,
                EmailVerifyExceptionCode.INVALID_TOKEN.message
            )
        }

        //마지막 토큰인지 검증
        val firstByEmailAndPurpose = emailVerifyRepository.findFirstByEmailAndPurposeOrderByCreatedAtDesc(
            user.email,
            EmailVerifyPurpose.RESET_PASSWORD
        ).orElseThrow {
            CommonException(
                EmailVerifyExceptionCode.INVALID_TOKEN.code,
                EmailVerifyExceptionCode.INVALID_TOKEN.message
            )
        }

        if (emailVerifier != firstByEmailAndPurpose) {
            throw CommonException(
                EmailVerifyExceptionCode.INVALID_TOKEN.code,
                EmailVerifyExceptionCode.INVALID_TOKEN.message
            )
        }

        //인증 시간 검증
        if (isOverValidationTimeLimit(firstByEmailAndPurpose.createdAt!!)) {
            throw CommonException(
                EmailVerifyExceptionCode.TIME_OVER.code,
                EmailVerifyExceptionCode.TIME_OVER.message
            )
        }
    }

    //인증 제한시간 초과 여부(초과: true)
    private fun isOverValidationTimeLimit(createTime: LocalDateTime): Boolean {
        return LocalDateTime.now().isAfter(createTime.plusMinutes(emailValidationLimitTime.toLong()))
    }

    //중복된 이메일인지 검증(회원정보에 존해자는 이메일 일 시 예외 반환)
    private fun checkIfEmailExists(email: String) {
        userRepository.findByEmail(email).ifPresent {
            throw CommonException(
                UserExceptionCode.EXIST_EMAIL.code,
                UserExceptionCode.EXIST_EMAIL.message
            )
        }
    }
}
