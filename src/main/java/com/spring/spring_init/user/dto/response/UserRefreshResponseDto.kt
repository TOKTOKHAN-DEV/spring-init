package com.spring.spring_init.user.dto.response

data class UserRefreshResponseDto(
    val accessToken: String,
    val refreshToken: String
)
