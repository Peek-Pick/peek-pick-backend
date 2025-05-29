package org.beep.sbpp.users.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.common.ActionResultDTO;
import org.beep.sbpp.users.dto.*;
import org.beep.sbpp.users.service.UserService;
import org.beep.sbpp.util.JWTUtil;
import org.beep.sbpp.util.TokenCookieUtil;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserInfoUtil userInfoUtil;
    private final JWTUtil jwtUtil;

    // 회원가입 풀세트
    @PostMapping("/signup")
    public ResponseEntity<ActionResultDTO<Long>> fullsignup(@RequestBody UserSignupRequestDTO dto, HttpServletResponse response) {
        log.info("Controller tagIdList: {}", dto);

        Long userId = userService.fullSignup(dto);

        //회원가입 이후 자동 로그인 → 토큰 발급 → 쿠키 저장
        String accessToken = jwtUtil.createToken(userId, dto.getEmail(),60);           // 60분
        String refreshToken = jwtUtil.createToken(userId, dto.getEmail(),60 * 24 * 7); // 7일

        TokenCookieUtil.addAuthCookies(accessToken, refreshToken, response); // HttpOnly 쿠키로 설정

        return ResponseEntity.ok(ActionResultDTO.success(userId));
    }

    // myPage 조회
    @GetMapping("/mypage")
    public ResponseEntity<UserMyPageResponseDTO> getMyPage(HttpServletRequest request) {
        Long userId = userInfoUtil.getAuthUserId(request);
        UserMyPageResponseDTO dto = userService.getUserMyPage(userId);
        return ResponseEntity.ok(dto);
    }

    // myPage Edit 조회
    @GetMapping("/mypage/edit")
    public ResponseEntity<UserMyPageEditResDTO> getMyPageEdit(HttpServletRequest request) {
        Long userId = userInfoUtil.getAuthUserId(request);
        UserMyPageEditResDTO dto = userService.getUserMyPageEdit(userId);
        return ResponseEntity.ok(dto);
    }

    // myPage Edit 수정
    @PutMapping("/mypage/edit")
    public ResponseEntity<ActionResultDTO> updateMyPage(
            @RequestPart("data") UserMyPageEditRequestDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request) {

        Long userId = userInfoUtil.getAuthUserId(request);
        userService.updateUserMyPage(userId, dto, file);
        return ResponseEntity.ok(ActionResultDTO.success(userId));
    }

}
