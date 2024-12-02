package com.spring.spring_init.verify.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class EmailTokenGenerator {

    //유저에게 제공할 6자리 인증번호
    public String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    //이메일 식별에 필요한 토큰 생성기
    public String generateVerificationToken(
        final String email,
        final String code
    ) {
        String hashString = email + code + Instant.now().toEpochMilli();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(hashString.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not found", e);
        }
    }

    public String generateUidByUserId(Long userId) {
        // 문자열을 바이트 배열로 변환
        byte[] bytes = Long.toString(userId).getBytes(StandardCharsets.UTF_8);

        // Base64 URL-safe 형식으로 인코딩
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return encoded;
    }

    public Long decodeUidByUserId(String encoded) {
        byte[] bytes = Base64.getUrlDecoder().decode(encoded);
        return Long.parseLong(new String(bytes, StandardCharsets.UTF_8));
    }
}
