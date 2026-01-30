package com.spring.spring_init.verify.exception

import com.spring.spring_init.common.base.BaseErrorCode
import org.springframework.http.HttpStatus

enum class EmailVerifyExceptionCode(
    private val httpStatusCode: HttpStatus,
    override val code: String,
    override val message: String
) : BaseErrorCode {
    NOT_MATCH_CODE(HttpStatus.BAD_REQUEST, "NOT_MATCH_CODE", "인증번호가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "INVALID_TOKEN", "유효하지 않은 token 입니다."),
    TIME_OVER(HttpStatus.BAD_REQUEST, "TIME_OVER", "인증 시간이 초과되었습니다.");

    override val httpStatus: HttpStatus
        get() = httpStatusCode
}
