package com.spring.spring_init.verify.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class VerifyEmailConfirmRequest(
    @field:NotNull(message = "email 필수 값 입니다")
    @Schema(description = "이메일")
    val email: String,

    @field:NotNull(message = "code 필수 값 입니다")
    @Schema(description = "이메일 인증 코드")
    val code: String
)
