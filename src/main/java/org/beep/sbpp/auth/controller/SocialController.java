package org.beep.sbpp.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.auth.dto.LoginResponseDTO;
import org.beep.sbpp.auth.service.MemberService;
import org.beep.sbpp.util.TokenCookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/login")
public class SocialController {

    private final MemberService memberService;

    // 구글 SNS 인증
    @GetMapping("/google")
    public ResponseEntity<Map<String, Object>> googleLogin(
            @RequestParam("code") String code,
            HttpServletResponse response
    ) {
        LoginResponseDTO loginResult = memberService.handleGoogleLogin(code);

        Map<String, Object> responseBody = new HashMap<>();

        if (loginResult.isNew()) {
            // 신규 사용자
            responseBody.put("isNew", true);
            responseBody.put("email", loginResult.getEmail());
            return ResponseEntity.ok(responseBody);
        }

        // 기존 사용자
        TokenCookieUtil.addAuthCookies(loginResult.getAccessToken(), loginResult.getRefreshToken(), response);

        responseBody.put("isNew", false);
        responseBody.put("redirectUrl", "http://localhost:5173/main");
        return ResponseEntity.ok(responseBody);
    }
}