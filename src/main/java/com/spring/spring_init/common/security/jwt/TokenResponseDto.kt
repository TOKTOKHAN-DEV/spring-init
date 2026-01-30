package com.spring.spring_init.common.security.jwt

data class TokenResponseDto(
    val accessToken: String,
    val refreshToken: String
)
