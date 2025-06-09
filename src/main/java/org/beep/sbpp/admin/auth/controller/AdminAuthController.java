package org.beep.sbpp.admin.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.admin.auth.entities.AdminEntity;
import org.beep.sbpp.admin.auth.service.AdminAuthService;
import org.beep.sbpp.util.JWTUtil;
import org.beep.sbpp.util.TokenCookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/auth")
class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final JWTUtil jwtUtil;

    // 어드민 로그인
    @PostMapping("/login")
    public ResponseEntity<Void> adminLogin(
            @RequestParam("aid") String aid,
            @RequestParam("apw") String apw,
            HttpServletResponse response) {

        AdminEntity admin = adminAuthService.authenticate(aid, apw);

        String accessToken = jwtUtil.createToken(admin.getAdminId(), admin.getAccountId(), 60);
        String refreshToken = jwtUtil.createToken(admin.getAdminId(), admin.getAccountId(), 60 * 24 * 7);

        TokenCookieUtil.addAuthCookies(accessToken, refreshToken, response);

        return ResponseEntity.ok().build();
    }
}
