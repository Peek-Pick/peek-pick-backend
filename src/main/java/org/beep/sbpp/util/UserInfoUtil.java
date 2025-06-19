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

        // 2. JWT 파싱
        Map<String, Object> claims = jwtUtil.validateToken(accessToken);

        // 3. ROLE 체크: ADMIN이면 차단
        Object roleObj = claims.get("role");
        if (roleObj != null && "ADMIN".equalsIgnoreCase(roleObj.toString())) {
            throw new RuntimeException("ADMIN 계정은 이 기능에 접근할 수 없습니다.");
        }

        // 4. uid 추출
        Object uidObj = claims.get("uid");
        if (uidObj == null) {
            throw new RuntimeException("Token에 uid 정보 없음");
        }

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

        // 2. JWT 파싱
        Map<String, Object> claims = jwtUtil.validateToken(accessToken);

        // 3. ROLE 체크: ADMIN이면 차단
        Object roleObj = claims.get("role");
        if (roleObj != null && "ADMIN".equalsIgnoreCase(roleObj.toString())) {
            throw new RuntimeException("ADMIN 계정은 이 기능에 접근할 수 없습니다.");
        }

        // 4. uem 추출
        Object uemObj = claims.get("uem");
        if (uemObj == null) throw new RuntimeException("Token에 uem 정보 없음");

        return uemObj.toString();
    }
}
