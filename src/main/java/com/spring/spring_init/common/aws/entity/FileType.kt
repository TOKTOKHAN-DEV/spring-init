package com.spring.spring_init.common.aws.entity

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "파일 타입")
enum class FileType(
    val description: String
) {
    IMAGE("이미지 그래픽 데이터(i.e. jpeg, png, gif, apng, etc.)"),
    AUDIO("오디오/음악 데이터(i.e. mpeg, vorbis, etc.)"),
    TEXT("텍스트 데이터(i.e. plain, csv, html, etc.)"),
    VIDEO("비디오 데이터(i.e. mp4, webm, etc.)"),
    APPLICATION("이진 데이터(i.e. pdf, zip, pkcs8, etc.)")
}
