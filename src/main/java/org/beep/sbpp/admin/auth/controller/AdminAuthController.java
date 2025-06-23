package org.beep.sbpp.admin.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.admin.auth.entities.AdminEntity;
import org.beep.sbpp.admin.auth.repository.AdminAuthRepository;
import org.beep.sbpp.admin.auth.service.AdminAuthService;
import org.beep.sbpp.util.JWTUtil;
import org.beep.sbpp.util.TokenCookieUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/auth")
class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final AdminAuthRepository adminAuthRepository;
    private final JWTUtil jwtUtil;

    // 어드민 로그인
    @PostMapping("/login")
    public ResponseEntity<Void> adminLogin(
            @RequestParam("aid") String aid,
            @RequestParam("apw") String apw,
            HttpServletResponse response) {

        AdminEntity admin = adminAuthService.authenticate(aid, apw);

        String accessToken = jwtUtil.createToken(admin.getAdminId(), admin.getAccountId(), "ADMIN",60);
        String refreshToken = jwtUtil.createToken(admin.getAdminId(), admin.getAccountId(), "ADMIN",60 * 24 * 7);

        TokenCookieUtil.addAuthCookies(accessToken, refreshToken, response);

        return ResponseEntity.ok().build();
    }

    // accessToken 갱신
    @GetMapping("/refresh")
    public ResponseEntity<Void> adminRefresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        try {
            if (refreshToken == null) {
                throw new RuntimeException("Refresh token cookie is missing");
            }

            Map<String, Object> claims = jwtUtil.validateToken(refreshToken);

            Object aidObj = claims.get("uid");
            Object accountObj = claims.get("uem");

            if (aidObj == null || accountObj == null) {
                throw new RuntimeException("Token claims missing required fields");
            }

            Long adminId = Long.parseLong(aidObj.toString());
            String adminAccount = accountObj.toString();

            AdminEntity admin = adminAuthRepository.findById(adminId)
                    .filter(a -> a.getAccountId().equals(adminAccount))
                    .orElseThrow(() -> new RuntimeException("사용자 정보 불일치"));

            String newAccessToken = jwtUtil.createToken(admin.getAdminId(), admin.getAccountId(), "ADMIN",60);     // 60분

            TokenCookieUtil.refreshAuthCookies(newAccessToken, response);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Refresh token validation failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}