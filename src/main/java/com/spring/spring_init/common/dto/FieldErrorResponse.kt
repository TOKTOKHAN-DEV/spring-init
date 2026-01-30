package com.spring.spring_init.common.dto

import com.fasterxml.jackson.annotation.JsonView
import com.spring.spring_init.common.exception.CustomJsonView
import io.swagger.v3.oas.annotations.media.Schema

data class FieldErrorResponse(
    @Schema(name = "필드 이름")
    @JsonView(CustomJsonView.Hidden::class)
    val filedName: String,

    @Schema(name = "필드 에러 이유")
    @JsonView(CustomJsonView.Hidden::class)
    val reason: String
)
