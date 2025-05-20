package org.beep.sbpp.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.auth.dto.LoginResponseDTO;
import org.beep.sbpp.auth.service.MemberService;
import org.beep.sbpp.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SocalController {

    private final MemberService memberService;

    private final JWTUtil jwtUtil;

    @GetMapping("/api/v1/auth/login/google")
    public ResponseEntity<Void> googleLogin(@RequestParam("code") String code, HttpServletResponse response) {
        LoginResponseDTO tokens = memberService.handleGoogleLogin(code);

        // Refresh Token을 HttpOnly Cookie로 설정
        Cookie refreshTokenCookie = new Cookie("refresh_token", tokens.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS 환경에서만 사용
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 3); // 7일 60 * 60 * 24 * 7

        response.addCookie(refreshTokenCookie);

        // Access Token은 프론트에 전달하거나, 같이 쿠키로 전달할 수 있음
        Cookie accessTokenCookie = new Cookie("access_token", tokens.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 1); // 1분

        response.addCookie(accessTokenCookie);

        // 리다이렉트 or 응답 (리다이렉트로 홈화면 이동)
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://localhost:5173/auth") // 홈화면으로 이동
                .build();
    }

    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<String[]> login (@RequestParam("uem") String uem,@RequestParam("upw") String upw) {

        log.info("");
        log.info("login----------------------------");
        log.info(uem + " " + upw);
        log.info("");

        // 사용자 정보를 조회 생략

        String accessToken = jwtUtil.createToken(Map.of("uem",uem), 5);
        String refreshToken = jwtUtil.createToken(Map.of("uem",uem), 60 * 24 * 7);

        String[] result = new String[]{accessToken, refreshToken};

        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/v1/auth/refresh")
    public ResponseEntity<String[]> refresh(@RequestParam("refreshToken") String refreshToken) {
        try {

            // refreshToken 유효성 검사 및 사용자 정보 추출
            Map<String, Object> claims = jwtUtil.validateToken(refreshToken);
            String uem = (String) claims.get("uem");

            // 새 토큰 발급
            String newAccessToken = jwtUtil.createToken(Map.of("uem", uem), 5);
            String newRefreshToken = jwtUtil.createToken(Map.of("uem", uem), 60 * 24 * 7);

            return ResponseEntity.ok(new String[]{newAccessToken, newRefreshToken});

        } catch (Exception e) {
            log.error("Refresh token validation failed", e);
            throw new RuntimeException("Invalid refresh token");
        }
    }
}
