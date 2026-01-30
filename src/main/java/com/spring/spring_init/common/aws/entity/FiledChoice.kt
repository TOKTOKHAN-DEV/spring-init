package com.spring.spring_init.common.aws.entity

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "파일 경로")
enum class FiledChoice(
    val value: String,
    val description: String
) {
    // TODO : 적절한 파일 경로 추가
    USER("user", "유저 프로필 사진");

    companion object {
        fun of(value: String): FiledChoice {
            return entries.find { it.value == value }
                ?: throw IllegalArgumentException("Unknown FiledChoice value: $value")
        }
    }
}
