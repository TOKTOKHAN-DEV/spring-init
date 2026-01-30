package com.spring.spring_init.common.security.jwt;

import com.spring.spring_init.common.security.user.UserDetailsImpl;
import com.spring.spring_init.user.entity.User;
import com.spring.spring_init.user.entity.UserRole;
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
                    userDetails.getUserRole()
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
            throw e;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            throw e;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
            throw e;
        }
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
        UserRole userRole = (UserRole) claims.get(AUTHORITIES_KEY);


        return new UserDetailsImpl(userId, username, userRole);
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
