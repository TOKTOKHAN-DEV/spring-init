package com.spring.spring_init.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetConfirmRequest {

    @NotNull(message = "password 는 필수 값 입니다.")
    @Schema(description = "비밀번호")
    private String password;

    @NotNull(message = "passwordConfirm 는 필수 값 입니다.")
    @Schema(description = "비밀전호 확인")
    private String passwordConfirm;

    @NotNull(message = "uid 는 필수 값 입니다.")
    private String uid;

    @NotNull(message = "token 는 필수 값 입니다.")
    private String token;

}
