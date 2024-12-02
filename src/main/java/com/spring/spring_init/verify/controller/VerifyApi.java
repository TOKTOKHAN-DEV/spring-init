package com.spring.spring_init.verify.controller;

import com.spring.spring_init.common.apidocs.ApiExceptionExplanation;
import com.spring.spring_init.common.apidocs.ApiResponseExplanations;
import com.spring.spring_init.common.dto.ResponseDTO;
import com.spring.spring_init.common.security.user.UserDetailsImpl;
import com.spring.spring_init.user.exception.UserExceptionCode;
import com.spring.spring_init.verify.dto.request.VerifyPasswordRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[Verifier]")
public interface VerifyApi {

    @Operation(summary = "비밀번호 검증")
    @ApiResponseExplanations(
        errors = {
            @ApiExceptionExplanation(
                name = "비밀번호가 일치하지 않습니다.",
                value = UserExceptionCode.class,
                constant = "NOT_MATCH_CURRENT_PASSWORD"
            )
        }
    )
    ResponseEntity<ResponseDTO<Void>> verifyPassword(
        @Validated @RequestBody VerifyPasswordRequestDto request,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    );
}
