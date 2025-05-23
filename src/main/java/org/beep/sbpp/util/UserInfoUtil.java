package org.beep.sbpp.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserInfoUtil {

    private final JWTUtil jwtUtil;

    public Long getAuthUserId(HttpServletRequest request) {
        // 1. 쿠키에서 accessToken 추출
        String accessToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        if (accessToken == null) {
            throw new RuntimeException("accessToken 쿠키 없음");
        }

        // 2. JWT 파싱해서 uid 추출
        Map<String, Object> claims = jwtUtil.validateToken(accessToken);
        Object uidObj = claims.get("uid");
        if (uidObj == null) throw new RuntimeException("Token에 uid 정보 없음");

        return Long.parseLong(uidObj.toString());
    }
}
