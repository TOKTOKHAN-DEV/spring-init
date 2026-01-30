package com.spring.spring_init.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserInfoResponse {

    private String id; // 소셜 사용자 ID
    private String email; // 이메일 (필요시)
}
