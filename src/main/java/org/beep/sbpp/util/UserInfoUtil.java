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

    /*  사용법:    #Controller

        private final UserInfoUtil userInfoUtil;                // 의존성 주입

        @GetMapping(...)
        public ResponseEntity<Integer> {함수명}(HttpServletRequest request) {
            Long uid = userInfoUtil.getAuthUserId(request);     // uid에 사용자 정보 파싱
            ...
            ..
        }

        *** 이제 uid에 로그인한 사용자의 id가 들어있음                                  */

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

    public String getAuthUserEmail(HttpServletRequest request) {
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
        Object uemObj = claims.get("uem");
        if (uemObj == null) throw new RuntimeException("Token에 uem 정보 없음");

        return uemObj.toString();
    }
}
