package com.spring.spring_init.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class PasswordChangeRequestDto(
    @Schema(name = "currentPassword", description = "기존 비밀번호")
    @field:NotNull(message = "currentPassword 필수 값 입니다.")
    val currentPassword: String,

    @Schema(name = "password", description = "새로운 비밀번호")
    @field:NotNull(message = "password 필수 값 입니다.")
    val password: String,

    @Schema(name = "passwordConfirm", description = "새로운 비밀번호 확인")
    @field:NotNull(message = "passwordConfirm 필수 값 입니다.")
    val passwordConfirm: String
)
