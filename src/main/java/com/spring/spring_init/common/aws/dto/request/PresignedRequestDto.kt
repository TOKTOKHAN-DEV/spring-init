package com.spring.spring_init.common.aws.dto.request

import com.spring.spring_init.common.aws.entity.FileType
import com.spring.spring_init.common.aws.entity.FiledChoice
import io.swagger.v3.oas.annotations.media.Schema

data class PresignedRequestDto(
    @Schema(
        description = "파일명",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "profile.jpg",
        minLength = 1
    )
    val fileName: String,

    @Schema(
        description = "파일 타입",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "IMAGE"
    )
    val fileType: FileType,

    @Schema(
        description = "파일 경로",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "USER_THUMBNAIL"
    )
    val fieldChoices: FiledChoice
)
