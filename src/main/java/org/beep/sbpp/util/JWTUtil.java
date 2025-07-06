package org.beep.sbpp.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JWTUtil {

    private final SecretKey secretKey;

    public JWTUtil(@Value("${jwt.secret}") String secret) {
        try {
            this.secretKey = Keys.hmacShaKeyFor(secret.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid secret key", e);
        }
    }

    public String createToken(Long userId, String email, String role, int minutes) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(String.valueOf(userId))
                .claim("uid", userId)
                .claim("uem", email)
                .claim("role", role)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(minutes).toInstant()))
                .signWith(secretKey)
                .compact();
    }

    public Map<String, Object> validateToken(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return jws.getPayload();
        } catch (JwtException e) {
            // Exception은 상위에서 구체적으로 캐치
            throw e;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            Date expiration = jws.getPayload().getExpiration();
            return expiration.before(new Date());

        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            log.warn("토큰 파싱 중 예외 발생: {}", e.getMessage());
            return false;
        }
    }


}

