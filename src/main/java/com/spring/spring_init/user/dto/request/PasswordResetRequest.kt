package com.spring.spring_init.user.dto.request

import jakarta.validation.constraints.NotNull

data class PasswordResetRequest(
    @field:NotNull(message = "email은 필수 입니다.")
    val email: String
)
