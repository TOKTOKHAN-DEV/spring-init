package com.spring.spring_init.common.aws.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class PresignedResponseDto(
    @Schema(
        description = "Presigned URL",
        example = "https://example-bucket.s3.amazonaws.com/presigned-url"
    )
    val url: String,

    @Schema(
        description = "추가 필드 정보",
        example = "{ \"field1\": \"value1\", \"field2\": \"value2\" }"
    )
    val fields: Map<String, String>? = null
) {
    constructor(url: String) : this(url, null)
}
