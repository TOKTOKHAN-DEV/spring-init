package com.spring.spring_init.user.oauth.naver;

import com.spring.spring_init.user.entity.OAuthProvider;
import com.spring.spring_init.user.oauth.common.OAuthLoginRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@NoArgsConstructor
public class NaverLoginRequest implements OAuthLoginRequest {

    private String authorizationCode;
    private String state;

    @Override
    public OAuthProvider oauthProvider() {
        return OAuthProvider.NAVER;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        body.add("state", state);
        return body;
    }
}
