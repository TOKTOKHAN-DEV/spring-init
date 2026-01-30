package com.spring.spring_init.oauth.userInfo

class NaverOAuth2UserInfo(
    attributes: Map<String, Any>
) : OAuth2UserInfo(attributes) {

    private val response: Map<String, Any> = attributes["response"] as Map<String, Any>

    override fun getId(): String {
        return response["id"].toString()
    }

    //Todo: 카카오 response 구조 확인하고 수집 항목에 따라 변경 필요
    override fun getEmail(): String {
        return response["email"].toString()
    }
}
