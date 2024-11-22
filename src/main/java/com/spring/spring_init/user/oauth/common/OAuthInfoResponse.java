package com.spring.spring_init.user.oauth.common;

import com.spring.spring_init.user.entity.OAuthProvider;

public interface OAuthInfoResponse {

    String getEmail();

    String getNickname();

    OAuthProvider getOAuthProvider();
}
