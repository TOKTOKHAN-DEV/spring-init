package com.spring.spring_init.oauth.service

import com.spring.spring_init.oauth.OAuthProvider
import com.spring.spring_init.oauth.PrincipalDetailsImpl
import com.spring.spring_init.oauth.userInfo.OAuth2UserInfo
import com.spring.spring_init.oauth.userInfo.OAuth2UserInfoFactory
import com.spring.spring_init.user.entity.User
import com.spring.spring_init.user.repository.UserRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOauth2UserService(
    private val userRepository: UserRepository
) : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val user = super.loadUser(userRequest)

        return try {
            process(userRequest, user)
        } catch (e: Exception) {
            throw RuntimeException()
        }
    }

    private fun process(userRequest: OAuth2UserRequest, oauth2User: OAuth2User): OAuth2User {
        val authProvider = OAuthProvider.valueOf(
            userRequest
                .clientRegistration
                .registrationId
                .uppercase()
        )
        val memberInfo = OAuth2UserInfoFactory.getOAuth2MemberInfo(authProvider, oauth2User.attributes)

        val userOptional = userRepository.findByOauthId(memberInfo.getId())

        val user: User = if (userOptional.isPresent) {
            val existingUser = userOptional.get()
            if (authProvider != existingUser.provider) {
                throw RuntimeException()
            }
            existingUser
        } else {
            createMember(memberInfo, authProvider)
        }
        return PrincipalDetailsImpl(user, oauth2User.attributes)
    }

    private fun createMember(userInfo: OAuth2UserInfo, authProvider: OAuthProvider): User {
        return userRepository.save(User(userInfo, authProvider))
    }
}
