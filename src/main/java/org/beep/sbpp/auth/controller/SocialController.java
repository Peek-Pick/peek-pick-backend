package org.beep.sbpp.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.auth.dto.LoginResponseDTO;
import org.beep.sbpp.auth.service.MemberService;
import org.beep.sbpp.util.TokenCookieUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SocialController {

    private final MemberService memberService;

    @GetMapping("/api/v1/auth/login/google")
    public ResponseEntity<Void> googleLogin(@RequestParam("code") String code, HttpServletResponse response) {
        LoginResponseDTO tokens = memberService.handleGoogleLogin(code);
        TokenCookieUtil.addAuthCookies(tokens.getAccessToken(), tokens.getRefreshToken(), response);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://localhost:5173/home/")
                .build();
    }
}
