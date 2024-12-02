package com.spring.spring_init.verify.service;

import com.spring.spring_init.common.exception.CommonException;
import com.spring.spring_init.user.entity.User;
import com.spring.spring_init.user.exception.UserExceptionCode;
import com.spring.spring_init.user.repository.UserRepository;
import com.spring.spring_init.verify.dto.request.VerifyEmailConfirmRequest;
import com.spring.spring_init.verify.dto.request.VerifyEmailRequest;
import com.spring.spring_init.verify.dto.request.VerifyPasswordResetRequest;
import com.spring.spring_init.verify.dto.response.VerifyEmailConfirmResponse;
import com.spring.spring_init.verify.dto.response.VerifyEmailResponse;
import com.spring.spring_init.verify.entity.EmailVerifier;
import com.spring.spring_init.verify.entity.EmailVerifyPurpose;
import com.spring.spring_init.verify.exception.EmailVerifyExceptionCode;
import com.spring.spring_init.verify.repository.EmailVerifyRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailVerifyService {

    private final EmailVerifyRepository emailVerifyRepository;
    private final MailSender mailSender;
    private final UserRepository userRepository;
    private final EmailTokenGenerator emailTokenGenerator;

    @Value("${common.email-validation-limit-time-minutes}")
    private Long emailValidationLimitTime;


    /**
     * 이메일 검증 및 인증번호 메일 전송
     */
    @Transactional
    public VerifyEmailResponse verifyEmailAndSendEmail(final VerifyEmailRequest request) {
        checkIfEmailExists(request.getEmail());

        String code = emailTokenGenerator.generateVerificationCode();
        String token = emailTokenGenerator.generateVerificationToken(request.getEmail(), code);

        mailSender.sendEmail(
            request.getEmail(),
            code,
            token,
            null,
            EmailVerifyPurpose.EMAIL_VALIDATION);

        EmailVerifier emailVerifier = new EmailVerifier(
            request.getEmail(),
            code,
            token,
            EmailVerifyPurpose.EMAIL_VALIDATION
        );
        emailVerifyRepository.save(emailVerifier);
        return new VerifyEmailResponse(request.getEmail());
    }

    /**
     * 이메일 인증 확인
     */
    public VerifyEmailConfirmResponse verifyEmailConfirm(final VerifyEmailConfirmRequest request) {

        EmailVerifier findedEmailVerifier =
            emailVerifyRepository.findByEmailAndCodeAndPurposeOrderByCreatedAtDesc(
                request.getEmail(),
                request.getCode(),
                EmailVerifyPurpose.EMAIL_VALIDATION
            ).orElseThrow(() ->
                new CommonException(
                    EmailVerifyExceptionCode.NOT_MATCH_CODE.getCode(),
                    EmailVerifyExceptionCode.NOT_MATCH_CODE.getMessage()
                )
            );
        if (isOverValidationTimeLimit(findedEmailVerifier.getCreatedAt())) {
            throw new CommonException(
                EmailVerifyExceptionCode.TIME_OVER.getCode(),
                EmailVerifyExceptionCode.TIME_OVER.getMessage()
            );
        }
        return new VerifyEmailConfirmResponse(findedEmailVerifier.getToken());
    }

    /**
     * 비밀번호 재설정 링크 검증
     */
    public void verifyPasswordReset(final VerifyPasswordResetRequest request) {
        Long userId = emailTokenGenerator.decodeUidByUserId(request.getUid());

        User user = userRepository.findById(userId).orElseThrow(() ->
            new CommonException(
                UserExceptionCode.NOT_FOUND_USER.getCode(),
                UserExceptionCode.NOT_FOUND_USER.getMessage()
            )
        );

        //Token 검증
        EmailVerifier emailVerifier =
            emailVerifyRepository.findByEmailAndTokenAndPurpose(
                user.getEmail(),
                request.getToken(),
                EmailVerifyPurpose.RESET_PASSWORD
            ).orElseThrow(() ->
                new CommonException(
                    EmailVerifyExceptionCode.INVALID_TOKEN.getCode(),
                    EmailVerifyExceptionCode.INVALID_TOKEN.getMessage()
                )
            );

        //마지막 토큰인지 검증
        EmailVerifier firstByEmailAndPurpose =
            emailVerifyRepository.findFirstByEmailAndPurposeOrderByCreatedAtDesc(
                user.getEmail(),
                EmailVerifyPurpose.RESET_PASSWORD
            ).orElseThrow(() ->
                new CommonException(
                    EmailVerifyExceptionCode.INVALID_TOKEN.getCode(),
                    EmailVerifyExceptionCode.INVALID_TOKEN.getMessage()
                )
            );

        if (!emailVerifier.equals(firstByEmailAndPurpose)) {
            throw new CommonException(
                EmailVerifyExceptionCode.INVALID_TOKEN.getCode(),
                EmailVerifyExceptionCode.INVALID_TOKEN.getMessage()
            );
        }

        //인증 시간 검증
        if (isOverValidationTimeLimit(firstByEmailAndPurpose.getCreatedAt())) {
            throw new CommonException(
                EmailVerifyExceptionCode.TIME_OVER.getCode(),
                EmailVerifyExceptionCode.TIME_OVER.getMessage()
            );
        }
    }

    //인증 제한시간 초과 여부(초과: true)
    private boolean isOverValidationTimeLimit(LocalDateTime createTime) {
        return LocalDateTime.now().isAfter(createTime.plusMinutes(emailValidationLimitTime));
    }

    //중복된 이메일인지 검증(회원정보에 존해자는 이메일 일 시 예외 반환)
    private void checkIfEmailExists(final String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new CommonException(
                UserExceptionCode.EXIST_EMAIL.getCode(),
                UserExceptionCode.EXIST_EMAIL.getMessage()
            );
        });
    }
}
