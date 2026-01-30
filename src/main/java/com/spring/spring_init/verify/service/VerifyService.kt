package com.spring.spring_init.verify.service

import com.spring.spring_init.common.exception.CommonException
import com.spring.spring_init.common.security.user.UserDetailsImpl
import com.spring.spring_init.user.exception.UserExceptionCode
import com.spring.spring_init.user.repository.UserRepository
import com.spring.spring_init.verify.dto.request.VerifyPasswordRequestDto
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class VerifyService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun verifyPassword(requestDto: VerifyPasswordRequestDto, userDetails: UserDetailsImpl) {
        // 사용자 정보 조회
        val user = userRepository.findById(userDetails.userId)
            .orElseThrow {
                CommonException(
                    UserExceptionCode.NOT_FOUND_USER.code,
                    UserExceptionCode.NOT_FOUND_USER.message
                )
            }

        // 현재 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(requestDto.password, user.password)) {
            throw CommonException(
                UserExceptionCode.NOT_MATCH_CURRENT_PASSWORD.code,
                UserExceptionCode.NOT_MATCH_CURRENT_PASSWORD.message
            )
        }
    }
}
