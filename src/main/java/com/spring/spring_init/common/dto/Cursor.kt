package com.spring.spring_init.common.dto

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode

data class Cursor<T>(
    @Schema(
        requiredMode = RequiredMode.REQUIRED,
        description = "cursor / 다음 요청의 cursor",
        nullable = true
    )
    val cursor: String?,

    @Schema(
        requiredMode = RequiredMode.REQUIRED,
        description = "다음 데이터 존재 여부"
    )
    val hasNext: Boolean,

    val data: List<T>
)
