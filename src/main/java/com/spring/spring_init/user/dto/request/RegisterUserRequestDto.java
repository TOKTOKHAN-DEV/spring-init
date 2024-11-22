package com.spring.spring_init.user.dto.request;

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

    @NotNull(message = "username은 필수 항목 입니다")
    private String username;

    @NotNull(message = "password은 필수 항목 입니다")
    private String password;
}
