package com.spring.spring_init.user.controller

import com.spring.spring_init.common.dto.ResponseDTO
import com.spring.spring_init.common.security.user.UserDetailsImpl
import com.spring.spring_init.user.dto.request.*
import com.spring.spring_init.user.dto.response.*
import com.spring.spring_init.user.service.UserService
import io.swagger.v3.oas.annotations.Hidden
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RequestMapping("/v1/user")
@RestController
class UserController(
    private val userService: UserService
) : UserApi {

    // 회원가입
    @PostMapping("/register")
    override fun registerUser(
        @Validated @RequestBody requestDto: RegisterUserRequestDto
    ): ResponseEntity<ResponseDTO<RegisterUserResponseDto>> {
        return ResponseEntity.ok(
            ResponseDTO(
                statusCode = HttpStatus.OK.value(),
                message = "SUCCESS",
                data = userService.registerUser(requestDto)
            )
        )
    }

    // 로그인
    @PostMapping("/login")
    override fun login(
        @Validated @RequestBody requestDto: LoginRequestDto,
        response: HttpServletResponse
    ): ResponseEntity<ResponseDTO<LoginResponseDto>> {
        val loginResponse = userService.login(requestDto)
        response.setHeader("Authorization", loginResponse.accessToken)

        return ResponseEntity.ok(
            ResponseDTO(
                statusCode = HttpStatus.OK.value(),
                message = "SUCCESS",
                data = loginResponse
            )
        )
    }

    // 유저 리프레시
    @PostMapping("/refresh")
    override fun userRefresh(
        @Validated @RequestBody requestDto: UserRefreshRequestDto,
        @AuthenticationPrincipal userDetails: UserDetailsImpl
    ): ResponseEntity<ResponseDTO<UserRefreshResponseDto>> {
        return ResponseEntity.ok(
            ResponseDTO(
                statusCode = HttpStatus.OK.value(),
                message = "SUCCESS",
                data = userService.userRefresh(requestDto, userDetails)
            )
        )
    }

    // 유저 정보 조회
    @GetMapping("/{id}")
    override fun getUserInfo(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetailsImpl
    ): ResponseEntity<ResponseDTO<UserInfoResponseDto>> {
        return ResponseEntity.ok(
            ResponseDTO(
                statusCode = HttpStatus.OK.value(),
                message = "SUCCESS",
                data = userService.getUserInfo(id)
            )
        )
    }

    // 유저 삭제(탈퇴)
    @DeleteMapping("/{id}")
    override fun deleteUser(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetailsImpl
    ): ResponseEntity<ResponseDTO<String>> {
        return ResponseEntity.ok(
            ResponseDTO(
                statusCode = HttpStatus.OK.value(),
                message = "SUCCESS",
                data = userService.deleteUser(id, userDetails)
            )
        )
    }

    // 비밀번호 변경
    @PostMapping("/password-change")
    override fun changePassword(
        @Validated @RequestBody requestDto: PasswordChangeRequestDto,
        @AuthenticationPrincipal userDetail: UserDetailsImpl
    ): ResponseEntity<ResponseDTO<String>> {
        return ResponseEntity.ok(
            ResponseDTO(
                statusCode = HttpStatus.OK.value(),
                message = "SUCCESS",
                data = userService.changePassword(requestDto, userDetail)
            )
        )
    }

    // 비밀번호 초기화 메일 발송
    @PostMapping("/password-reset")
    override fun passwordReset(
        @Validated @RequestBody request: PasswordResetRequest
    ): ResponseEntity<ResponseDTO<PasswordResetResponse>> {
        return ResponseEntity.ok(
            ResponseDTO(
                statusCode = HttpStatus.OK.value(),
                message = "SUCCESS",
                data = userService.passwordReset(request)
            )
        )
    }

    /**
     * 이메일을 통한 비밀번호 초기화
     */
    @PostMapping("/password-reset-confirm")
    fun passwordResetConfirm(
        @Validated @RequestBody request: PasswordResetConfirmRequest
    ): ResponseEntity<ResponseDTO<Void>> {
        userService.passwordResetConfirm(request)

        return ResponseEntity.ok(
            ResponseDTO(
                statusCode = HttpStatus.OK.value(),
                message = "SUCCESS",
                data = null
            )
        )
    }

    @Hidden
    @PostMapping("/swagger-login")
    fun swaggerLogin(
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) password: String?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): SwaggerLoginResponseDto {
        val convertRequestDto = LoginRequestDto(username!!, password!!)
        val responseDto = userService.login(convertRequestDto)

        response.setHeader("Authorization", responseDto.accessToken)
        return SwaggerLoginResponseDto(responseDto.accessToken)
    }
}
