package org.beep.sbpp.auth.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
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
        return request.getServletPath().startsWith("/api/v1/auth") ||
                request.getServletPath().equals("/api/v1/tags") ||
                request.getServletPath().startsWith("/api/v1/users/signup") ||
                request.getServletPath().startsWith("/api/v1/admin/auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("---------doFilterInternal---------");
        log.info("Request URI: {}", request.getRequestURI());

        String accessToken = null;

        String headerStr = request.getHeader("Authorization");
        if (headerStr != null && headerStr.startsWith("Bearer ")) {
            accessToken = headerStr.substring(7);
        }

        if (accessToken == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        if (accessToken == null || accessToken.contains("undefined")) {
            handleException(response, JWTErrorCode.NO_ACCESS_TOKEN);
            return;
        }

        try {
            Map<String, Object> tokenMap = jwtUtil.validateToken(accessToken);

            String userId = (String) tokenMap.get("sub");
            String email = (String) tokenMap.get("uem");
            String role = ((String) tokenMap.get("role")).toUpperCase();

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            new CustomUserPrincipal(userId, email, role),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            handleException(response, JWTErrorCode.EXPIRED_TOKEN);
        } catch (MalformedJwtException e) {
            handleException(response, JWTErrorCode.MALFORMED_TOKEN);
        } catch (SignatureException e) {
            handleException(response, JWTErrorCode.BAD_SIGNATURE);
        } catch (JwtException e) {
            handleException(response, JWTErrorCode.NO_ACCESS_TOKEN);
        } catch (Exception e) {
            log.error("Unknown token error", e);
            handleException(response, JWTErrorCode.NO_ACCESS_TOKEN);
        }
    }

    private void handleException(HttpServletResponse response, JWTErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getCode());
        response.setContentType("application/json");
        response.getWriter().println("{\"error\": \"" + errorCode.getMessage() + "\"}");
    }
}
