package com.spring.spring_init.verify.service

import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant
import java.util.*

@Component
class EmailTokenGenerator {

    //유저에게 제공할 6자리 인증번호
    fun generateVerificationCode(): String {
        return (Random().nextInt(900000) + 100000).toString()
    }

    //이메일 식별에 필요한 토큰 생성기
    fun generateVerificationToken(
        email: String,
        code: String
    ): String {
        val hashString = email + code + Instant.now().toEpochMilli()
        return try {
            val digest = MessageDigest.getInstance("SHA-1")
            val hash = digest.digest(hashString.toByteArray())
            val hexString = StringBuilder()
            for (b in hash) {
                val hex = Integer.toHexString(0xff and b.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            hexString.toString()
        } catch (e: Exception) {
            throw RuntimeException("SHA-1 algorithm not found", e)
        }
    }

    fun generateUidByUserId(userId: Long): String {
        // 문자열을 바이트 배열로 변환
        val bytes = userId.toString().toByteArray(StandardCharsets.UTF_8)

        // Base64 URL-safe 형식으로 인코딩
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    fun decodeUidByUserId(encoded: String): Long {
        val bytes = Base64.getUrlDecoder().decode(encoded)
        return String(bytes, StandardCharsets.UTF_8).toLong()
    }
}
