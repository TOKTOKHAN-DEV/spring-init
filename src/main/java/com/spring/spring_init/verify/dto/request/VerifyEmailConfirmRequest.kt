package com.spring.spring_init.verify.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyEmailConfirmRequest {

    @NotNull(message = "email 필수 값 입니다")
    @Schema(description = "이메일")
    private String email;

    @NotNull(message = "code 필수 값 입니다")
    @Schema(description = "이메일 인증 코드")
    private String code;
}
