package com.spring.spring_init.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class LoginRequestDto(
    @Schema(name = "email", description = "아이디")
    @field:NotNull(message = "email 필수 값 입니다.")
    val email: String,

    @Schema(name = "password", description = "비밀번호")
    @field:NotNull(message = "password 필수 값 입니다.")
    val password: String
)
