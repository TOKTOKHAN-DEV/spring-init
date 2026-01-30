package com.spring.spring_init.common.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonView
import com.spring.spring_init.common.exception.CustomJsonView
import io.swagger.v3.oas.annotations.media.Schema

@Schema(title = "API 응답 - 실패 및 에러")
data class ErrorResponseDTO(
    @JsonView(CustomJsonView.Common::class)
    @Schema(description = "에러 코드", example = "ERROR_CODE")
    val errorCode: String,

    @JsonView(CustomJsonView.Common::class)
    @Schema(description = "에러 메시지", example = "에러 이유")
    val message: String,

    @JsonView(CustomJsonView.Hidden::class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "필드 에러")
    val fieldErrors: List<FieldErrorResponse>? = null
)
