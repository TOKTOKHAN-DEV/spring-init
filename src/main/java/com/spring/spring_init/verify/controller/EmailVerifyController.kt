package com.spring.spring_init.verify.controller

import com.spring.spring_init.common.dto.ResponseDTO
import com.spring.spring_init.verify.dto.request.VerifyEmailConfirmRequest
import com.spring.spring_init.verify.dto.request.VerifyEmailRequest
import com.spring.spring_init.verify.dto.request.VerifyPasswordResetRequest
import com.spring.spring_init.verify.dto.response.VerifyEmailConfirmResponse
import com.spring.spring_init.verify.dto.response.VerifyEmailResponse
import com.spring.spring_init.verify.service.EmailVerifyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/verifier")
class EmailVerifyController(
    private val emailVerifyService: EmailVerifyService
) : EmailVerifyApi {

    /**
     * 이메일 검증 및 인증번호 메일 전송
     */
    @PostMapping("/email")
    override fun verifyEmail(
        @Validated @RequestBody request: VerifyEmailRequest
    ): ResponseEntity<ResponseDTO<VerifyEmailResponse>> {
        return ResponseEntity.ok(
            ResponseDTO(
                statusCode = HttpStatus.OK.value(),
                message = "SUCCESS",
                data = emailVerifyService.verifyEmailAndSendEmail(request)
            )
        )
    }

    /**
     * 이메일 인증 확인
     */
    @PostMapping("/email-confirm")
    override fun verifyEmailConfirm(
        @Validated @RequestBody request: VerifyEmailConfirmRequest
    ): ResponseEntity<ResponseDTO<VerifyEmailConfirmResponse>> {
        return ResponseEntity.ok(
            ResponseDTO(
                statusCode = HttpStatus.OK.value(),
                message = "SUCCESS",
                data = emailVerifyService.verifyEmailConfirm(request)
            )
        )
    }

    /**
     * 비밀번호 재설정 링크 검증
     */
    @PostMapping("/password-reset")
    override fun verifyPasswordReset(
        @Validated @RequestBody request: VerifyPasswordResetRequest
    ): ResponseEntity<ResponseDTO<Void>> {
        emailVerifyService.verifyPasswordReset(request)

        return ResponseEntity.ok(
            ResponseDTO(
                statusCode = HttpStatus.OK.value(),
                message = "SUCCESS",
                data = null
            )
        )
    }
}
