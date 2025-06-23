package org.beep.sbpp.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.admin.exception.AdminAccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

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
        String accessToken = extractAccessToken(request);
        Map<String, Object> claims = jwtUtil.validateToken(accessToken);

        Object roleObj = claims.get("role");
        if ("ADMIN".equalsIgnoreCase(String.valueOf(roleObj))) {
            throw new AdminAccessDeniedException("ADMIN 계정은 이 기능에 접근할 수 없습니다.");
        }

        Object uidObj = claims.get("uid");
        if (uidObj == null) {
            // UnauthorizedException 대신 바로 401 상태 예외 던짐
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token에 uid 정보 없음");
        }

        return Long.parseLong(uidObj.toString());
    }

    public String getAuthUserEmail(HttpServletRequest request) {
        String accessToken = extractAccessToken(request);
        Map<String, Object> claims = jwtUtil.validateToken(accessToken);

        Object roleObj = claims.get("role");
        if ("ADMIN".equalsIgnoreCase(String.valueOf(roleObj))) {
            throw new AdminAccessDeniedException("ADMIN 계정은 이 기능에 접근할 수 없습니다.");
        }

        Object uemObj = claims.get("uem");
        if (uemObj == null) {
            // UnauthorizedException 대신 바로 401 상태 예외 던짐
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token에 uid 정보 없음");
        }

        return uemObj.toString();
    }

    private String extractAccessToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "accessToken 쿠키 없음");
    }
}
