package org.beep.sbpp.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.auth.security.CustomUserPrincipal;
import org.beep.sbpp.util.JWTUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public enum JWTErrorCode {
        NO_ACCESS_TOKEN(401, "No access token"),
        EXPIRED_TOKEN(401, "Expired token"),
        BAD_SIGNATURE(401, "Bad signature"),
        MALFORMED_TOKEN(401, "Malformed token");

        private final int code;
        private final String message;

        JWTErrorCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        log.info("---------shouldNotFilter---------");
        return request.getServletPath().startsWith("/api/v1/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("---------doFilterInternal---------");
        log.info("Request URI: {}", request.getRequestURI());

        String accessToken = null;

        // 1. Authorization 헤더 검사
        String headerStr = request.getHeader("Authorization");
        if (headerStr != null && headerStr.startsWith("Bearer ")) {
            accessToken = headerStr.substring(7);
        }

        // 2. accessToken 쿠키 검사
        if (accessToken == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        // AccessToken이 없는 경우
        if (accessToken == null || accessToken.contains("undefined")) {
            handleException(response, JWTErrorCode.NO_ACCESS_TOKEN);
            return;
        }

        try {
            // JWT 검증 및 파싱
            Map<String, Object> tokenMap = jwtUtil.validateToken(accessToken);

            // 표준 sub 클레임에서 userId 추출
            String userId = (String) tokenMap.get("sub");

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            new CustomUserPrincipal(userId),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("JWT validation error", e);
            String message = e.getMessage();

            if (message.startsWith("JWT signature")) {
                handleException(response, JWTErrorCode.BAD_SIGNATURE);
            } else if (message.startsWith("Malformed")) {
                handleException(response, JWTErrorCode.MALFORMED_TOKEN);
            } else if (message.startsWith("JWT expired")) {
                handleException(response, JWTErrorCode.EXPIRED_TOKEN);
            } else {
                handleException(response, JWTErrorCode.NO_ACCESS_TOKEN);
            }
        }
    }

    private void handleException(HttpServletResponse response, JWTErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getCode());
        response.setContentType("application/json");
        response.getWriter().println("{\"error\": \"" + errorCode.getMessage() + "\"}");
    }
}
