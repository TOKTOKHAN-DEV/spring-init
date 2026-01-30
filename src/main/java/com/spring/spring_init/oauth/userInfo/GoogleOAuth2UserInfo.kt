package com.spring.spring_init.oauth.userInfo

class GoogleOAuth2UserInfo(
    attributes: Map<String, Any>
) : OAuth2UserInfo(attributes) {

    override fun getId(): String {
        return attributes["sub"].toString()
    }

    override fun getEmail(): String {
        return attributes["email"].toString()
    }
}
