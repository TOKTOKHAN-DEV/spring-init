package com.spring.spring_init.verify.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull

data class VerifyEmailRequest(
    @field:Email
    @field:NotNull(message = "email은 필수 값 입니다.")
    val email: String
)
