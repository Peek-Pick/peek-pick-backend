package org.beep.sbpp.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.auth.dto.LoginResponseDTO;
import org.beep.sbpp.auth.service.MemberService;
import org.beep.sbpp.users.enums.Status;
import org.beep.sbpp.util.TokenCookieUtil;
import org.springframework.http.HttpStatus;
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

        if (loginResult.getStatus() == Status.BANNED) {
            responseBody.put("banned", true);
            responseBody.put("banUntil", loginResult.getBanUntil());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
        }

        if (loginResult.isNew()) {
            responseBody.put("isNew", true);
            responseBody.put("email", loginResult.getEmail());
            return ResponseEntity.ok(responseBody);
        }

        TokenCookieUtil.addAuthCookies(loginResult.getAccessToken(), loginResult.getRefreshToken(), response);

        responseBody.put("isNew", false);
        responseBody.put("redirectUrl", "https://www.peek-pick.click/main");
        return ResponseEntity.ok(responseBody);
    }

}