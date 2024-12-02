package com.spring.spring_init.common.security.jwt;

import com.spring.spring_init.common.security.user.UserDetailsImpl;
import com.spring.spring_init.user.entity.Authority;
import com.spring.spring_init.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
@Slf4j
public class TokenProvider implements InitializingBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID = "userId";
    private static final String AUTHORITIES_KEY = "authorities";
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.token-validity-in-milliseconds}")
    private long accessTokenValidityInMs;
    @Value("${jwt.refresh-token-validity-in-milliseconds}")
    private long refreshTokenValidityInMs;

    private Key key;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public TokenResponseDto getTokenByUser(final UserDetailsImpl userDetails) {
        return new TokenResponseDto(
            createToken(userDetails, TokenType.ACCESS),
            createToken(userDetails, TokenType.REFRESH)
        );
    }

    public TokenResponseDto getTokenByOauth(
        final User user
    ) {
        return new TokenResponseDto(
            createToken(user, TokenType.ACCESS),
            createToken(user, TokenType.REFRESH)
        );
    }

    public String getAccessTokenByUser(
        final UserDetailsImpl userDetails
    ) {
        return createToken(userDetails, TokenType.ACCESS);
    }

    private String createToken(
        final User user,
        final TokenType tokenType
    ) {
        Date date = new Date();

        return BEARER_PREFIX +
            Jwts.builder()
                .setSubject(user.getEmail())
                .claim(USER_ID, user.getUserId())
//                .claim(AUTHORITIES_KEY,
//                    userDetails.getSetAuthorities().stream()
//                        .map(Authority::getAuthorityName)
//                        .toList()
//                )
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() +
                        (tokenType.equals(TokenType.ACCESS)
                            ? accessTokenValidityInMs
                            : refreshTokenValidityInMs)
                    )
                )
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    private String createToken(
        final UserDetailsImpl userDetails,
        final TokenType tokenType
    ) {
        Date date = new Date();

        return BEARER_PREFIX +
            Jwts.builder()
                .setSubject(userDetails.getEmail())
                .claim(USER_ID, userDetails.getUserId())
                .claim(AUTHORITIES_KEY,
                    userDetails.getSetAuthorities().stream()
                        .map(Authority::getAuthorityName)
                        .toList()
                )
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() +
                        (tokenType.equals(TokenType.ACCESS)
                            ? accessTokenValidityInMs
                            : refreshTokenValidityInMs)
                    )
                )
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public boolean validateToken(final String token) {
        String parseToken = parseToken(token);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(parseToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }
        return false;
    }

    public Authentication createAuthentication(final String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        UserDetails userDetails = getUserDetailsFromToken(claims);
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        return new UsernamePasswordAuthenticationToken(
            userDetails, null, authorities);
    }

    private UserDetails getUserDetailsFromToken(final Claims claims) {
        Long userId = ((Number) claims.get(USER_ID)).longValue();
        String username = claims.getSubject();
        //맘대로 캐스팅 했다가 타입 안맞는 경우가 생길 수 있을까?
        List<String> roles = (List<String>) claims.get(AUTHORITIES_KEY);

        Set<Authority> authorities = roles.stream()
            .map(Authority::new)
            .collect(Collectors.toSet());

        return new UserDetailsImpl(userId, username, authorities);
    }

    private Claims parseClaims(final String accessToken) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(parseToken(accessToken))
            .getBody();
    }

    private String parseToken(final String token) {
        return token.replace(BEARER_PREFIX, "");
    }
}
