package com.spring.spring_init.oauth.userInfo;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    private Map<String, Object> kakaoAccount;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    }

    @Override
    public String getId() {
        return attributes.get("id").toString();
    }

    //Todo: 카카오 response 구조 확인하고 수집 항목에 따라 변경 필요
    @Override
    public String getEmail() {
        return kakaoAccount != null
            ? kakaoAccount.get("email").toString()
            :  attributes.get("id").toString();
    }
}
