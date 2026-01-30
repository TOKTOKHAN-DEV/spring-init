package com.spring.spring_init.common.security.user

import com.spring.spring_init.common.exception.CommonException
import com.spring.spring_init.user.exception.UserExceptionCode
import com.spring.spring_init.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email).orElseThrow {
            CommonException(
                UserExceptionCode.LOGIN_FAIL.code,
                UserExceptionCode.LOGIN_FAIL.message
            )
        }
        return UserDetailsImpl(
            userId = user.userId!!,
            email = user.email,
            password = user.password,
            userRole = user.userRole
        )
    }
}
