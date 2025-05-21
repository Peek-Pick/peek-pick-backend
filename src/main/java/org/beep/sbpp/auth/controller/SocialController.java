package org.beep.sbpp.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.auth.dto.LoginResponseDTO;
import org.beep.sbpp.auth.repository.LoginRepository;
import org.beep.sbpp.auth.service.MemberService;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.util.JWTUtil;
import org.beep.sbpp.util.TokenCookieUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SocialController {

    private final MemberService memberService;
    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @GetMapping("/api/v1/auth/login/google")
    public ResponseEntity<Void> googleLogin(@RequestParam("code") String code, HttpServletResponse response) {
        LoginResponseDTO tokens = memberService.handleGoogleLogin(code);
        TokenCookieUtil.addAuthCookies(tokens.getAccessToken(), tokens.getRefreshToken(), response);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://localhost:5173/admin/notices/list/")
                .build();
    }

    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<Void> login(
            @RequestParam("uem") String uem,
            @RequestParam("upw") String upw,
            HttpServletResponse response) {

        UserEntity user = loginRepository.findByEmailAndIsSocialFalse(uem)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(upw, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtUtil.createToken(user.getUserId(), user.getEmail(), 60);           // 60분
        String refreshToken = jwtUtil.createToken(user.getUserId(), user.getEmail(), 60 * 24 * 7); // 7일

        TokenCookieUtil.addAuthCookies(accessToken, refreshToken, response);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/api/v1/auth/refresh")
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

            String newAccessToken = jwtUtil.createToken(user.getUserId(), user.getEmail(), 60);           // 60분
            String newRefreshToken = jwtUtil.createToken(user.getUserId(), user.getEmail(), 60 * 24 * 7); // 7일

            TokenCookieUtil.addAuthCookies(newAccessToken, newRefreshToken, response);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Refresh token validation failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
