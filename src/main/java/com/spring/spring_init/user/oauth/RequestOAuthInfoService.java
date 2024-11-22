package com.spring.spring_init.user.oauth;

import com.spring.spring_init.user.entity.OAuthProvider;
import com.spring.spring_init.user.oauth.common.OAuthApiClient;
import com.spring.spring_init.user.oauth.common.OAuthInfoResponse;
import com.spring.spring_init.user.oauth.common.OAuthLoginRequest;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class RequestOAuthInfoService {

    private final Map<OAuthProvider, OAuthApiClient> clients;

    public RequestOAuthInfoService(List<OAuthApiClient> clients) {
        this.clients = clients.stream().collect(
            Collectors.toUnmodifiableMap(OAuthApiClient::oAuthProvider, Function.identity())
        );
    }

    public OAuthInfoResponse request(OAuthLoginRequest params) {
        OAuthApiClient client = clients.get(params.oauthProvider());
        String accessToken = client.requestAccessToken(params);
        return client.requestOauthInfo(accessToken);
    }
}
