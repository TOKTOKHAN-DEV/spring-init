package com.spring.spring_init.verify.dto.response

import jakarta.validation.constraints.NotNull

data class VerifyEmailResponse(
    @field:NotNull(message = "email 필수 값 입니다")
    val email: String
)
