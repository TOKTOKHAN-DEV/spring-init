package com.spring.spring_init.user.oauth.kakao;

import com.spring.spring_init.user.entity.OAuthProvider;
import com.spring.spring_init.user.oauth.common.OAuthLoginRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@NoArgsConstructor
public class KakaoLoginRequest implements OAuthLoginRequest {

    private String authorizationCode;

    @Override
    public OAuthProvider oauthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        return body;
    }
}
