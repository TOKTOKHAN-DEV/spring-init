package com.spring.spring_init.verify.controller

import com.spring.spring_init.common.dto.ResponseDTO
import com.spring.spring_init.common.security.user.UserDetailsImpl
import com.spring.spring_init.verify.dto.request.VerifyPasswordRequestDto
import com.spring.spring_init.verify.service.VerifyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/verifier")
class VerifyController(
    private val verifyService: VerifyService
) : VerifyApi {

    @PostMapping("/password")
    override fun verifyPassword(
        @Validated @RequestBody request: VerifyPasswordRequestDto,
        @AuthenticationPrincipal userDetails: UserDetailsImpl
    ): ResponseEntity<ResponseDTO<Void>> {
        verifyService.verifyPassword(request, userDetails)

        return ResponseEntity.ok(
            ResponseDTO(
                statusCode = HttpStatus.OK.value(),
                message = "SUCCESS",
                data = null
            )
        )
    }
}
