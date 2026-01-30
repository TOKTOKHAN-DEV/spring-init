package com.spring.spring_init.oauth.userInfo;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    private Map<String, Object> response;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        this.response = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getId() {
        return response.get("id").toString();
    }

    //Todo: 카카오 response 구조 확인하고 수집 항목에 따라 변경 필요
    @Override
    public String getEmail() {
        return response.get("email").toString();
    }
}
