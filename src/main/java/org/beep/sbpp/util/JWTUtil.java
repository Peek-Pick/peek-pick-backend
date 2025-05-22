package org.beep.sbpp.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JWTUtil {
    private static String key = "1234567890123456789012345678901234567890";

    public String createToken(Long userId, String email, int min) {
        SecretKey key;
        try {
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(String.valueOf(userId))
                .claim("uid", userId)  // sub: userId
                .claim("uem", email)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                .signWith(key)
                .compact();
    }

    public Map<String, Object> validateToken(String token) {
        SecretKey key;
        try {
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException("키 변환 실패");
        }

        Jws<Claims> jws;
        try {
            jws = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage()); // 서명 실패 등
        }

        Claims claims = jws.getPayload();

        // 🔴 명시적으로 토큰 만료 확인
        if (claims.getExpiration() != null && claims.getExpiration().before(new Date())) {
            throw new RuntimeException("JWT expired");  // 필터에서 잡힘
        }

        return claims;
    }
}

