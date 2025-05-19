package org.beep.sbpp.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.auth.service.MemberService;
import org.beep.sbpp.util.JWTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SocalController {

    private final MemberService memberService;

    private final JWTUtil jwtUtil;

    @GetMapping("/api/v1/auth/login/google")
    public ResponseEntity<String[]> getGoogle( @RequestParam("accessToken") String accessToken) {

        log.info("getGoogle: " + accessToken);

        String googleEmail = memberService.getGoogleEmail(accessToken);

        log.info("googleEmail: " + googleEmail);

        String[] result = new String[]{"access.........", "refresh............."};

        return ResponseEntity.ok(result);
    }
}
