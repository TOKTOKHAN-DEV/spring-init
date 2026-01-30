package com.spring.spring_init.oauth.userInfo;

import com.spring.spring_init.oauth.OAuthProvider;
import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2MemberInfo(
        OAuthProvider oAuthProvider,
        Map<String, Object> attributes
    ) {
        switch (oAuthProvider) {
            case GOOGLE -> {
                return new GoogleOAuth2UserInfo(attributes);
            }
            case KAKAO -> {
                return new KakaoOAuth2UserInfo(attributes);
            }
            case NAVER -> {
                return new NaverOAuth2UserInfo(attributes);
            }
//            default -> throw new UnsupportedProviderException();
            default -> throw new RuntimeException("UnsupportedProviderException");
        }
    }
}
