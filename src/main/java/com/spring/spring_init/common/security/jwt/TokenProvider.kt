package com.spring.spring_init.common.security.jwt

import com.spring.spring_init.common.security.user.UserDetailsImpl
import com.spring.spring_init.user.entity.User
import com.spring.spring_init.user.entity.UserRole
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.security.Key
import java.util.*

@Configuration
class TokenProvider : InitializingBean {

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        private const val USER_ID = "userId"
        private const val AUTHORITIES_KEY = "authorities"
        private val log = LoggerFactory.getLogger(TokenProvider::class.java)
    }

    private val signatureAlgorithm = SignatureAlgorithm.HS256

    @Value("\${jwt.secret}")
    private lateinit var secret: String

    @Value("\${jwt.token-validity-in-milliseconds}")
    private var accessTokenValidityInMs: Long = 0

    @Value("\${jwt.refresh-token-validity-in-milliseconds}")
    private var refreshTokenValidityInMs: Long = 0

    private lateinit var key: Key

    override fun afterPropertiesSet() {
        key = Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun getTokenByUser(userDetails: UserDetailsImpl): TokenResponseDto {
        return TokenResponseDto(
            createToken(userDetails, TokenType.ACCESS),
            createToken(userDetails, TokenType.REFRESH)
        )
    }

    fun getTokenByOauth(user: User): TokenResponseDto {
        return TokenResponseDto(
            createToken(user, TokenType.ACCESS),
            createToken(user, TokenType.REFRESH)
        )
    }

    fun getAccessTokenByUser(userDetails: UserDetailsImpl): String {
        return createToken(userDetails, TokenType.ACCESS)
    }

    private fun createToken(user: User, tokenType: TokenType): String {
        val date = Date()
        return BEARER_PREFIX +
            Jwts.builder()
                .setSubject(user.email)
                .claim(USER_ID, user.userId)
                .setIssuedAt(date)
                .setExpiration(Date(date.time +
                    if (tokenType == TokenType.ACCESS) accessTokenValidityInMs
                    else refreshTokenValidityInMs
                ))
                .signWith(key, signatureAlgorithm)
                .compact()
    }

    private fun createToken(userDetails: UserDetailsImpl, tokenType: TokenType): String {
        val date = Date()
        return BEARER_PREFIX +
            Jwts.builder()
                .setSubject(userDetails.email)
                .claim(USER_ID, userDetails.userId)
                .claim(AUTHORITIES_KEY, userDetails.userRole)
                .setIssuedAt(date)
                .setExpiration(Date(date.time +
                    if (tokenType == TokenType.ACCESS) accessTokenValidityInMs
                    else refreshTokenValidityInMs
                ))
                .signWith(key, signatureAlgorithm)
                .compact()
    }

    fun validateToken(token: String): Boolean {
        val parseToken = parseToken(token)
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(parseToken)
            true
        } catch (e: io.jsonwebtoken.security.SecurityException) {
            log.info("Invalid JWT Token", e)
            false
        } catch (e: MalformedJwtException) {
            log.info("Invalid JWT Token", e)
            false
        } catch (e: ExpiredJwtException) {
            log.info("Expired JWT Token", e)
            false
        } catch (e: UnsupportedJwtException) {
            log.info("Unsupported JWT Token", e)
            false
        } catch (e: IllegalArgumentException) {
            log.info("JWT claims string is empty", e)
            false
        }
    }

    fun createAuthentication(accessToken: String): Authentication {
        val claims = parseClaims(accessToken)

        if (claims[AUTHORITIES_KEY] == null) {
            throw RuntimeException("권한 정보가 없는 토큰입니다.")
        }

        val userDetails = getUserDetailsFromToken(claims)
        val authorities: Collection<GrantedAuthority> = userDetails.authorities

        return UsernamePasswordAuthenticationToken(userDetails, null, authorities)
    }

    private fun getUserDetailsFromToken(claims: Claims): UserDetails {
        val userId = (claims[USER_ID] as Number).toLong()
        val username = claims.subject
        val userRole = claims[AUTHORITIES_KEY] as UserRole

        return UserDetailsImpl(userId, username, userRole)
    }

    private fun parseClaims(accessToken: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(parseToken(accessToken))
            .body
    }

    private fun parseToken(token: String): String {
        return token.replace(BEARER_PREFIX, "")
    }
}
