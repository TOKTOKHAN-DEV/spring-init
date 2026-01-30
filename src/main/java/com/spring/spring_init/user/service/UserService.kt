package com.spring.spring_init.user.service

import com.spring.spring_init.common.exception.CommonException
import com.spring.spring_init.common.security.jwt.TokenProvider
import com.spring.spring_init.common.security.user.UserDetailsImpl
import com.spring.spring_init.user.dto.request.*
import com.spring.spring_init.user.dto.response.*
import com.spring.spring_init.user.entity.User
import com.spring.spring_init.user.entity.UserRole
import com.spring.spring_init.user.exception.UserExceptionCode
import com.spring.spring_init.user.repository.UserRepository
import com.spring.spring_init.verify.entity.EmailVerifier
import com.spring.spring_init.verify.entity.EmailVerifyPurpose
import com.spring.spring_init.verify.exception.EmailVerifyExceptionCode
import com.spring.spring_init.verify.repository.EmailVerifyRepository
import com.spring.spring_init.verify.service.EmailTokenGenerator
import com.spring.spring_init.verify.service.MailSender
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val tokenProvider: TokenProvider,
    private val emailVerifyRepository: EmailVerifyRepository,
    private val mailSender: MailSender,
    private val emailTokenGenerator: EmailTokenGenerator
) {

    // 회원가입
    @Transactional
    fun registerUser(requestDto: RegisterUserRequestDto): RegisterUserResponseDto {
        //이메일 토큰 검증
        validateEmailToken(
            requestDto.email,
            requestDto.emailToken,
            EmailVerifyPurpose.EMAIL_VALIDATION
        )

        //이메일 중복 여부 검증
        checkEmailExists(requestDto)

        //비밀번호 확인란 일치 여부 검증
        checkPasswordConfirm(requestDto)

        val savedUser = userRepository.save(
            User(
                email = requestDto.email,
                password = passwordEncoder.encode(requestDto.password),
                userRole = UserRole.ROLE_USER
            )
        )

        //인증 관련 이메일 기록 전부 삭제 처리
        emailVerifyRepository.deleteByEmailAndPurpose(
            savedUser.email,
            EmailVerifyPurpose.EMAIL_VALIDATION
        )

        return RegisterUserResponseDto(savedUser)
    }

    // 로그인
    fun login(requestDto: LoginRequestDto): LoginResponseDto {
        val authenticationToken = UsernamePasswordAuthenticationToken(
            requestDto.email,
            requestDto.password
        )
        val authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken)

        val userDetails = authentication.principal as UserDetailsImpl
        val tokenByUser = tokenProvider.getTokenByUser(userDetails)

        return LoginResponseDto(tokenByUser)
    }

    // 유저 정보 조회
    fun getUserInfo(id: Long): UserInfoResponseDto {
        val user = getUser(id)
        return UserInfoResponseDto(user)
    }

    // 유저 탈퇴
    @Transactional
    fun deleteUser(id: Long, userDetails: UserDetailsImpl): String {
        validateUserId(id, userDetails)
        val user = getUser(id)

        user.delete()
        userRepository.save(user)

        return user.email
    }

    // 유저 리프레시
    fun userRefresh(
        requestDto: UserRefreshRequestDto,
        userDetails: UserDetailsImpl
    ): UserRefreshResponseDto {
        tokenProvider.validateToken(requestDto.refreshToken)
        val accessToken = tokenProvider.getAccessTokenByUser(userDetails)

        return UserRefreshResponseDto(accessToken, requestDto.refreshToken)
    }

    // 비밀번호 변경
    @Transactional
    fun changePassword(requestDto: PasswordChangeRequestDto, userDetails: UserDetailsImpl): String {
        val user = getUser(userDetails.userId)

        validatePassword(
            requestDto.currentPassword,
            requestDto.password,
            requestDto.passwordConfirm,
            user,
            false
        )

        user.changePassword(passwordEncoder.encode(requestDto.password))
        userRepository.save(user)

        return user.email
    }

    @Transactional
    //비밀번호 초기화 이메일 발송
    fun passwordReset(request: PasswordResetRequest): PasswordResetResponse {

        //이메일로 회원 존재 여부 검증
        val user = userRepository.findByEmail(request.email).orElseThrow {
            CommonException(
                UserExceptionCode.NOT_FOUND_USER.code,
                UserExceptionCode.NOT_FOUND_USER.message
            )
        }

        val token = emailTokenGenerator.generateVerificationToken(request.email, "")

        //비밀번호 초기화 메일 발송
        mailSender.sendEmail(
            request.email,
            "",
            token,
            user,
            EmailVerifyPurpose.RESET_PASSWORD
        )

        //발송한 이메일 정보 저장
        emailVerifyRepository.save(
            EmailVerifier(
                email = request.email,
                code = null,
                token = token,
                purpose = EmailVerifyPurpose.RESET_PASSWORD
            )
        )
        return PasswordResetResponse(request.email)
    }

    /**
     * 이메일을 통한 비밀번호 초기화
     */
    @Transactional
    fun passwordResetConfirm(request: PasswordResetConfirmRequest) {
        val userId = emailTokenGenerator.decodeUidByUserId(request.uid)
        val user = getUser(userId)

        validateEmailToken(
            user.email,
            request.token,
            EmailVerifyPurpose.RESET_PASSWORD
        )

        validatePassword(
            null,
            request.password,
            request.passwordConfirm,
            user,
            true
        )

        user.changePassword(request.password)
    }

    // -------------------------------- //

    // 유저 정보 조회
    private fun getUser(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow {
                CommonException(
                    UserExceptionCode.NOT_FOUND_USER.code,
                    UserExceptionCode.NOT_FOUND_USER.message
                )
            }
    }

    // 로그인한 유저 Id와 요청한 유저 Id가 일치하는지 확인
    private fun validateUserId(id: Long, userDetails: UserDetailsImpl) {
        if (userDetails.userId != id) {
            throw CommonException(
                UserExceptionCode.NOT_MATCH_USER.code,
                UserExceptionCode.NOT_MATCH_USER.message
            )
        }
    }

    // 비밀번호 확인란 일치 여부 검증
    private fun checkPasswordConfirm(requestDto: RegisterUserRequestDto) {
        if (requestDto.password != requestDto.passwordConfirm) {
            throw CommonException(
                UserExceptionCode.PASSWORD_MISMATCH.code,
                UserExceptionCode.PASSWORD_MISMATCH.message
            )
        }
    }

    // 이메일 중복 여부 검증
    private fun checkEmailExists(requestDto: RegisterUserRequestDto) {
        userRepository.findByEmail(requestDto.email)
            .ifPresent {
                throw CommonException(
                    UserExceptionCode.EXIST_EMAIL.code,
                    UserExceptionCode.EXIST_EMAIL.message
                )
            }
    }

    // 이메일 토큰 검증
    private fun validateEmailToken(
        email: String,
        token: String,
        purpose: EmailVerifyPurpose
    ) {
        val emailVerifier = emailVerifyRepository.findByEmailAndToken(
            email,
            token
        ).orElseThrow {
            CommonException(
                UserExceptionCode.UNVERIFIED_EMAIL.code,
                UserExceptionCode.UNVERIFIED_EMAIL.message
            )
        }

        val lastEmailVerifier = emailVerifyRepository.findFirstByEmailAndPurposeOrderByCreatedAtDesc(
            email,
            purpose
        ).orElseThrow {
            CommonException(
                UserExceptionCode.UNVERIFIED_EMAIL.code,
                UserExceptionCode.UNVERIFIED_EMAIL.message
            )
        }

        if (emailVerifier != lastEmailVerifier) {
            throw CommonException(
                EmailVerifyExceptionCode.INVALID_TOKEN.code,
                EmailVerifyExceptionCode.INVALID_TOKEN.message
            )
        }
    }

    //비밀번호 변경에서 비밀번호 검증 절차
    private fun validatePassword(
        currentPassword: String?,
        newPassword: String,
        newPasswordConfirm: String,
        user: User,
        isResetPassword: Boolean
    ) {
        // 현재 비밀번호 확인
        if (!isResetPassword && currentPassword != null && !passwordEncoder.matches(currentPassword, user.password)) {
            throw CommonException(
                UserExceptionCode.NOT_MATCH_CURRENT_PASSWORD.code,
                UserExceptionCode.NOT_MATCH_CURRENT_PASSWORD.message
            )
        }

        // 새로운 비밀번호와 기존 비밀번호가 같은지 확인
        if (passwordEncoder.matches(newPassword, user.password)) {
            throw CommonException(
                UserExceptionCode.SAME_PASSWORD.code,
                UserExceptionCode.SAME_PASSWORD.message
            )
        }

        // 새로운 비밀번호 확인
        if (newPassword != newPasswordConfirm) {
            throw CommonException(
                UserExceptionCode.NOT_MATCH_CHANGE_PASSWORD.code,
                UserExceptionCode.NOT_MATCH_CHANGE_PASSWORD.message
            )
        }
    }

//    fun swaggerLogin(
//        convertRequestDto: LoginRequestDto,
//        request: HttpServletRequest,
//        response: HttpServletResponse
//    ): SwaggerLoginResponseDto {
//    }
}
