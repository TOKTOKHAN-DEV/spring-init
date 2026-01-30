package com.spring.spring_init.common.exception

class CommonException(
    val code: String,
    message: String
) : RuntimeException(message)
