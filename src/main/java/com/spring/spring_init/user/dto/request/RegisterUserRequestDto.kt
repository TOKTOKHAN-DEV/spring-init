package com.spring.spring_init.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class RegisterUserRequestDto(
    @field:NotNull(message = "email 필수 항목 입니다")
    val email: String,

    @field:NotNull(message = "emailToken 필수 항목 입니다")
    @Schema(description = "email verifier를 통해 얻은 token값 입니다.")
    val emailToken: String,

    @field:NotNull(message = "password 필수 항목 입니다")
    @Schema(description = "비밀번호")
    val password: String,

    @field:NotNull(message = "passwordConfirm 필수 항목 입니다")
    @Schema(description = "비밀번호 확인")
    val passwordConfirm: String,

    @field:NotNull(message = "penName 필수 항목 입니다")
    @Schema(description = "필명")
    val penName: String
)
