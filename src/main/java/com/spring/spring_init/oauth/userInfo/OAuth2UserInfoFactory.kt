package com.spring.spring_init.oauth.userInfo

import com.spring.spring_init.oauth.OAuthProvider

object OAuth2UserInfoFactory {

    @JvmStatic
    fun getOAuth2MemberInfo(
        oAuthProvider: OAuthProvider,
        attributes: Map<String, Any>
    ): OAuth2UserInfo {
        return when (oAuthProvider) {
            OAuthProvider.GOOGLE -> GoogleOAuth2UserInfo(attributes)
            OAuthProvider.KAKAO -> KakaoOAuth2UserInfo(attributes)
            OAuthProvider.NAVER -> NaverOAuth2UserInfo(attributes)
//            else -> throw UnsupportedProviderException()
        }
    }
}
