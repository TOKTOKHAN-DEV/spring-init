package com.spring.spring_init.oauth.userInfo

class KakaoOAuth2UserInfo(
    attributes: Map<String, Any>
) : OAuth2UserInfo(attributes) {

    private val kakaoAccount: Map<String, Any>? = attributes["kakao_account"] as? Map<String, Any>

    override fun getId(): String {
        return attributes["id"].toString()
    }

    //Todo: 카카오 response 구조 확인하고 수집 항목에 따라 변경 필요
    override fun getEmail(): String {
        return if (kakaoAccount != null) {
            kakaoAccount["email"].toString()
        } else {
            attributes["id"].toString()
        }
    }
}
