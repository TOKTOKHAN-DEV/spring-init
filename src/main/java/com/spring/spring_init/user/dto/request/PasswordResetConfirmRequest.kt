package com.spring.spring_init.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class PasswordResetConfirmRequest(
    @field:NotNull(message = "password 는 필수 값 입니다.")
    @Schema(description = "비밀번호")
    val password: String,

    @field:NotNull(message = "passwordConfirm 는 필수 값 입니다.")
    @Schema(description = "비밀전호 확인")
    val passwordConfirm: String,

    @field:NotNull(message = "uid 는 필수 값 입니다.")
    val uid: String,

    @field:NotNull(message = "token 는 필수 값 입니다.")
    val token: String
)
