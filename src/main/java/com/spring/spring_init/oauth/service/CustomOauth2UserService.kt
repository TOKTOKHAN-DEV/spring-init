package com.spring.spring_init.oauth.service;

import com.spring.spring_init.oauth.OAuthProvider;
import com.spring.spring_init.oauth.PrincipalDetailsImpl;
import com.spring.spring_init.oauth.userInfo.OAuth2UserInfo;
import com.spring.spring_init.oauth.userInfo.OAuth2UserInfoFactory;
import com.spring.spring_init.user.entity.User;
import com.spring.spring_init.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            return process(userRequest, user);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        OAuthProvider authProvider = OAuthProvider.valueOf(
            userRequest
                .getClientRegistration()
                .getRegistrationId()
                .toUpperCase()
        );
        OAuth2UserInfo memberInfo = OAuth2UserInfoFactory.getOAuth2MemberInfo(authProvider, oauth2User.getAttributes());

        Optional<User> userOptional = userRepository.findByOauthId(memberInfo.getId());

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (authProvider != user.getProvider()) {
//                throw new OAuthProviderMissMatchException();
                throw new RuntimeException();
            }
        } else {
            user = createMember(memberInfo, authProvider);
        }
        return new PrincipalDetailsImpl(user, oauth2User.getAttributes());
    }

    private User createMember(OAuth2UserInfo userInfo, OAuthProvider authProvider) {
        return userRepository.save(new User(userInfo, authProvider));
    }
}
