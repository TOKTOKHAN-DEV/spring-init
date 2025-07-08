package com.spring.spring_init.user.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

//    private final UserRepository userRepository;
//    private final TokenProvider tokenProvider;
//    private final RequestOAuthInfoService requestOAuthInfoService;
//
//    public TokenResponseDto login(OAuthLoginRequest params) {
//        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
//        User user = findOrCreateMember(oAuthInfoResponse);
//        return tokenProvider.getTokenByOauth(user);
//    }
//
//    private User findOrCreateMember(OAuthInfoResponse oAuthInfoResponse) {
//        return userRepository.findByEmail(oAuthInfoResponse.getEmail())
//            .orElseGet(() -> newMember(oAuthInfoResponse));
//    }
//
//    private User newMember(OAuthInfoResponse oAuthInfoResponse) {
//        User user = new User(
//            oAuthInfoResponse.getNickname(),
//            oAuthInfoResponse.getEmail(),
//            oAuthInfoResponse.getOAuthProvider()
//        );
//        return userRepository.save(user);
//    }
}
