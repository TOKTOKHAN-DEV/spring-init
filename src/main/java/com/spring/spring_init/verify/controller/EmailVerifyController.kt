package com.spring.spring_init.verify.controller;

import com.spring.spring_init.common.dto.ResponseDTO;
import com.spring.spring_init.verify.dto.request.VerifyEmailConfirmRequest;
import com.spring.spring_init.verify.dto.request.VerifyEmailRequest;
import com.spring.spring_init.verify.dto.request.VerifyPasswordResetRequest;
import com.spring.spring_init.verify.dto.response.VerifyEmailConfirmResponse;
import com.spring.spring_init.verify.dto.response.VerifyEmailResponse;
import com.spring.spring_init.verify.service.EmailVerifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/verifier")
public class EmailVerifyController implements EmailVerifyApi {

    private final EmailVerifyService emailVerifyService;

    /**
     * 이메일 검증 및 인증번호 메일 전송
     */
    @PostMapping("/email")
    public ResponseEntity<ResponseDTO<VerifyEmailResponse>> verifyEmail(
        @Validated @RequestBody VerifyEmailRequest request
    ) {
        return ResponseEntity.ok(
            ResponseDTO.<VerifyEmailResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(emailVerifyService.verifyEmailAndSendEmail(request))
                .build()
        );
    }

    /**
     * 이메일 인증 확인
     */
    @PostMapping("/email-confirm")
    public ResponseEntity<ResponseDTO<VerifyEmailConfirmResponse>> verifyEmailConfirm(
        @Validated @RequestBody VerifyEmailConfirmRequest request
    ) {
        return ResponseEntity.ok(
            ResponseDTO.<VerifyEmailConfirmResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(emailVerifyService.verifyEmailConfirm(request))
                .build()
        );
    }

    /**
     * 비밀번호 재설정 링크 검증
     */
    @PostMapping("/password-reset")
    public ResponseEntity<ResponseDTO<Void>> verifyPasswordReset(
        @Validated @RequestBody VerifyPasswordResetRequest request
    ) {
        emailVerifyService.verifyPasswordReset(request);

        return ResponseEntity.ok(
            ResponseDTO.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("SUCCESS")
                .build()
        );
    }
}
