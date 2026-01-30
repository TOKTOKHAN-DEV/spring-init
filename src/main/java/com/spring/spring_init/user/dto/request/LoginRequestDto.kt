package com.spring.spring_init.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @Schema(name = "email", description = "아이디")
    @NotNull(message = "email 필수 값 입니다.")
    private String email;


    @Schema(name = "password", description = "비밀번호")
    @NotNull(message = "password 필수 값 입니다.")
    private String password;
}
