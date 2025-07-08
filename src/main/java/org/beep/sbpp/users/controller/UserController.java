package org.beep.sbpp.users.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.common.ActionResultDTO;
import org.beep.sbpp.common.PageResponse;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.users.dto.*;
import org.beep.sbpp.users.service.UserFavoriteService;
import org.beep.sbpp.users.service.UserService;
import org.beep.sbpp.util.JWTUtil;
import org.beep.sbpp.util.TokenCookieUtil;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserInfoUtil userInfoUtil;
    private final JWTUtil jwtUtil;
    private final UserFavoriteService favoriteService;

    // 회원가입 풀세트
    @PostMapping("/signup")
    public ResponseEntity<ActionResultDTO<Long>> fullsignup(@RequestBody UserSignupRequestDTO dto, HttpServletResponse response) {
        log.info("Controller tagIdList: {}", dto);

        Long userId = userService.fullSignup(dto);

        //회원가입 이후 자동 로그인 → 토큰 발급 → 쿠키 저장
        String accessToken = jwtUtil.createToken(userId, dto.getEmail(),"USER", 60);           // 60분
        String refreshToken = jwtUtil.createToken(userId, dto.getEmail(), "USER", 60 * 24 * 7); // 7일

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

        log.info("받은 파일: {}", file != null ? file.getOriginalFilename() : "파일 없음");
        Long userId = userInfoUtil.getAuthUserId(request);
        userService.updateUserMyPage(userId, dto, file);
        return ResponseEntity.ok(ActionResultDTO.success(userId));
    }

    /**
     * 사용자가 찜한 상품 목록을 커서 기반으로 조회
     * - 프론트엔드 요청 예시: GET /api/v1/users/favorites?size=12&lastModDate=...&lastProductId=...
     * - size는 필수, 나머지 커서 값은 optional
     */
    @GetMapping("/favorites")
    public PageResponse<ProductListDTO> getFavorites(
            HttpServletRequest request,
            @RequestParam(name = "size") Integer size,
            @RequestParam(name = "lastModDate", required = false) LocalDateTime lastModDate,
            @RequestParam(name = "lastProductId", required = false) Long lastProductId,
            @RequestParam(required = false, defaultValue = "en") String lang
    ) {
        Long userId = userInfoUtil.getAuthUserId(request);
        return favoriteService.getFavoriteProducts(userId, size, lastModDate, lastProductId, lang);
    }

    // 비밀번호 확인
    @PostMapping("/check-password")
    public ResponseEntity<Void> checkPassword(
            HttpServletRequest request,
            @RequestBody PasswordCheckRequestDTO dto
    ){
        Long userId = userInfoUtil.getAuthUserId(request);
        userService.checkPassword(userId, dto);
        return ResponseEntity.ok().build();
    }

    // 닉네임 확인(마이페이지에서)
//    @PostMapping("/check-nickname")
//    public ResponseEntity<Void> checkNickname(
//            HttpServletRequest request,
//            @RequestBody NicknameCheckRequestDTO dto
//    ){
//        Long userId = userInfoUtil.getAuthUserId(request);
//
//        userService.chekNickname(userId, dto);
//        return ResponseEntity.ok().build();
//
//    }

    // 이메일 확인 { "exists": false }
    @GetMapping("/signup/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // 닉네임 확인(회원가입시)
    @GetMapping("/signup/check-nickname-duplicate")
    public ResponseEntity<Map<String, Boolean>> checkNicknameDuplicate(@RequestParam String nickname) {
        boolean exists = userService.isNicknameExists(nickname);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // 계정 삭제
    @PatchMapping("/delete")
    public ResponseEntity<ActionResultDTO> updateUserStatus(
            HttpServletRequest request,
            @RequestBody UserStatusUpdateRequestDTO dto
    ) {
        Long userId = userInfoUtil.getAuthUserId(request);
        userService.updateUserStatus(userId, dto.getStatus());
        return ResponseEntity.ok(ActionResultDTO.success(userId));
    }
}
