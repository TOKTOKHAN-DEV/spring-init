package com.spring.spring_init.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequestDto {

    @Schema(name = "currentPassword", description = "기존 비밀번호")
    @NotNull(message = "currentPassword 필수 값 입니다.")
    private String currentPassword;

    @Schema(name = "password", description = "새로운 비밀번호")
    @NotNull(message = "password 필수 값 입니다.")
    private String password;

    @Schema(name = "passwordConfirm", description = "새로운 비밀번호 확인")
    @NotNull(message = "passwordConfirm 필수 값 입니다.")
    private String passwordConfirm;
}
