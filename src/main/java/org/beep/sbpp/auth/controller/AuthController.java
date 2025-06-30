package org.beep.sbpp.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.auth.repository.LoginRepository;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.util.JWTUtil;
import org.beep.sbpp.util.TokenCookieUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    // 일반 로그인
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestParam("uem") String uem,
            @RequestParam("upw") String upw,
            HttpServletResponse response) {

        UserEntity user = loginRepository.findByEmailAndIsSocialFalse(uem)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(upw, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtUtil.createToken(user.getUserId(), user.getEmail(), "USER",60);           // 60분
        String refreshToken = jwtUtil.createToken(user.getUserId(), user.getEmail(), "USER",60 * 24 * 7); // 7일

        TokenCookieUtil.addAuthCookies(accessToken, refreshToken, response); // HttpOnly 쿠키로 설정

        return ResponseEntity.ok().build();
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        TokenCookieUtil.clearAuthCookies(response);
        return ResponseEntity.ok().build();
    }

    // accessToken 갱신
    @GetMapping("/refresh")
    public ResponseEntity<Void> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        try {
            if (refreshToken == null) {
                throw new RuntimeException("Refresh token cookie is missing");
            }

            Map<String, Object> claims = jwtUtil.validateToken(refreshToken);

            Object uidObj = claims.get("uid");
            Object emailObj = claims.get("uem");

            if (uidObj == null || emailObj == null) {
                throw new RuntimeException("Token claims missing required fields");
            }

            Long userId = Long.parseLong(uidObj.toString());
            String email = emailObj.toString();

            UserEntity user = loginRepository.findById(userId)
                    .filter(u -> u.getEmail().equals(email))
                    .orElseThrow(() -> new RuntimeException("사용자 정보 불일치"));

            String newAccessToken = jwtUtil.createToken(user.getUserId(), user.getEmail(),"USER",60);     // 60분

            TokenCookieUtil.refreshAuthCookies(newAccessToken, response);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Refresh token validation failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 로그인 체크
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkLogin(
            @CookieValue(value = "accessToken", required = false) String accessToken) {

        try {
            // accessToken이 없으면 로그인 안 된 상태
            if (accessToken == null) {
                return ResponseEntity.ok(false);
            }

            // 토큰 검증
            Map<String, Object> claims = jwtUtil.validateToken(accessToken);

            Object uidObj = claims.get("uid");
            Object emailObj = claims.get("uem");

            // 클레임 누락되면 비정상 토큰
            if (uidObj == null || emailObj == null) {
                return ResponseEntity.ok(false);
            }

            // 유저 정보 확인 (선택적)
            Long userId = Long.parseLong(uidObj.toString());
            String email = emailObj.toString();

            boolean userExists = loginRepository.findById(userId)
                    .filter(u -> u.getEmail().equals(email))
                    .isPresent();

            // 유저가 존재하고, 토큰도 유효하면 로그인된 상태
            return ResponseEntity.ok(userExists);

        } catch (Exception e) {
            // 토큰 만료/비정상 등 → 로그인 안 된 상태
            return ResponseEntity.ok(false);
        }
    }

}