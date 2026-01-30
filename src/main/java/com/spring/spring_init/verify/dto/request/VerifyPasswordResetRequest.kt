package com.spring.spring_init.verify.dto.request

import jakarta.validation.constraints.NotNull

data class VerifyPasswordResetRequest(
    @field:NotNull(message = "uid 필수 값 입니다.")
    val uid: String,

    @field:NotNull(message = "token 필수 값 입니다.")
    val token: String
)
