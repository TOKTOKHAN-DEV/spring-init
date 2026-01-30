package com.spring.spring_init.verify.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class VerifyPasswordRequestDto(
    @field:NotNull(message = "password 필수 값 입니다")
    @Schema(description = "비밀번호")
    val password: String
)
