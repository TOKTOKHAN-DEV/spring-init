package com.spring.spring_init.verify.controller;

import com.spring.spring_init.common.apidocs.ApiExceptionExplanation;
import com.spring.spring_init.common.apidocs.ApiResponseExplanations;
import com.spring.spring_init.common.dto.ResponseDTO;
import com.spring.spring_init.user.exception.UserExceptionCode;
import com.spring.spring_init.verify.dto.request.VerifyEmailConfirmRequest;
import com.spring.spring_init.verify.dto.request.VerifyEmailRequest;
import com.spring.spring_init.verify.dto.request.VerifyPasswordResetRequest;
import com.spring.spring_init.verify.dto.response.VerifyEmailConfirmResponse;
import com.spring.spring_init.verify.dto.response.VerifyEmailResponse;
import com.spring.spring_init.verify.exception.EmailVerifyExceptionCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[Email Verifier]")
public interface EmailVerifyApi {

    @Operation(summary = "이메일 검증 및 인증 번호 메일 전송")
    @ApiResponseExplanations(
        errors = {
            @ApiExceptionExplanation(
                name = "이미 존재하는 이메일",
                value = UserExceptionCode.class,
                constant = "EXIST_EMAIL"
            )
        }
    )
    ResponseEntity<ResponseDTO<VerifyEmailResponse>> verifyEmail(
        @Validated @RequestBody VerifyEmailRequest request
    );

    @Operation(summary = "이메일 인증 확인")
    @ApiResponseExplanations(
        errors = {
            @ApiExceptionExplanation(
                name = "인증번호가 일치하지 않습니다.(인증 코드 입력 시 모든 오류 공통)",
                value = EmailVerifyExceptionCode.class,
                constant = "NOT_MATCH_CODE"
            )
        }
    )
    ResponseEntity<ResponseDTO<VerifyEmailConfirmResponse>> verifyEmailConfirm(
        @Validated @RequestBody VerifyEmailConfirmRequest request
    );

    @Operation(summary = "비밀전호 재설정 링크 검증", description = "메일로 받은 재설정 링크 중 마지막 링크(uid, token)인지 검증")
    @ApiResponseExplanations(
        errors = {
            @ApiExceptionExplanation(
                name = "해당 User가 존재하지 않음",
                value = UserExceptionCode.class,
                constant = "NOT_FOUND_USER"
            ),
            @ApiExceptionExplanation(
                name = "토큰이 유효하지 않은 모든 경우",
                value = EmailVerifyExceptionCode.class,
                constant = "INVALID_TOKEN"
            ),
            @ApiExceptionExplanation(
                name = "인증 제한 시간이 초과된 경우",
                value = EmailVerifyExceptionCode.class,
                constant = "TIME_OVER"
            )
        }
    )
    ResponseEntity<ResponseDTO<Void>> verifyPasswordReset(
        @Validated @RequestBody VerifyPasswordResetRequest request
    );
}
