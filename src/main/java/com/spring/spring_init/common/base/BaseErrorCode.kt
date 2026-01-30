package com.spring.spring_init.common.base

import org.springframework.http.HttpStatus

interface BaseErrorCode {
    val code: String
    val message: String
    val httpStatus: HttpStatus
}
