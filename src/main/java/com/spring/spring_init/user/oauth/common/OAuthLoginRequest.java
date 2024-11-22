package com.spring.spring_init.user.oauth.common;

import com.spring.spring_init.user.entity.OAuthProvider;
import org.springframework.util.MultiValueMap;

public interface OAuthLoginRequest {

    OAuthProvider oauthProvider();

    MultiValueMap<String, String> makeBody();
}
