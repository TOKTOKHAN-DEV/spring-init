package com.spring.spring_init.user.oauth.common;

import com.spring.spring_init.user.entity.OAuthProvider;

public interface OAuthApiClient {

    OAuthProvider oAuthProvider();

    String requestAccessToken(OAuthLoginRequest params);

    OAuthInfoResponse requestOauthInfo(String accessToken);
}
