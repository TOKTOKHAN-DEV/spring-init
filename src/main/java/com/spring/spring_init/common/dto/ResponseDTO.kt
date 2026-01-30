package com.spring.spring_init.common.dto

import io.swagger.v3.oas.annotations.media.Schema

data class ResponseDTO<T>(
    @Schema(description = "HttpStatusCode", example = "200")
    val statusCode: Int,

    @Schema(description = "응답 메시지", example = "성공")
    val message: String,

    @Schema(description = "데이터")
    val data: T?
)
