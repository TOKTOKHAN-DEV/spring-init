package com.spring.spring_init.oauth.userInfo

abstract class OAuth2UserInfo(
    protected val attributes: Map<String, Any>
) {
    abstract fun getId(): String

    abstract fun getEmail(): String
}
