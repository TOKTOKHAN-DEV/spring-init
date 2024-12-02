package com.spring.spring_init.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequestDto {

    @NotNull(message = "email 필수 항목 입니다")
    private String email;

    @NotNull(message = "emailToken 필수 항목 입니다")
    @Schema(description = "email verifier를 통해 얻은 token값 입니다.")
    private String emailToken;

    @NotNull(message = "password 필수 항목 입니다")
    @Schema(description = "비밀번호")
    private String password;

    @NotNull(message = "passwordConfirm 필수 항목 입니다")
    @Schema(description = "비밀번호 확인")
    private String passwordConfirm;

    @NotNull(message = "penName 필수 항목 입니다")
    @Schema(description = "필명")
    private String penName;
}
