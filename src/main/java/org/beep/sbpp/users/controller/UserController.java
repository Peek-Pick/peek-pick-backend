package org.beep.sbpp.users.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.common.ActionResultDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.users.dto.*;
import org.beep.sbpp.users.service.UserFavoriteService;
import org.beep.sbpp.users.service.UserService;
import org.beep.sbpp.util.JWTUtil;
import org.beep.sbpp.util.TokenCookieUtil;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    private final UserFavoriteService favoriteService;

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

    /**
     * 새로 추가된 메서드: “찜한 상품(Favorites)” 목록 조회
     * 엔드포인트: GET /api/v1/users/products/favorites
     * (프론트에서 /api/v1/products/favorites ⇒ /api/v1/users/products/favorites 로 수정 필요)
     *
     * @param request  HttpServletRequest에서 토큰을 추출하여 userId를 얻습니다.
     * @param pageable 페이지 정보 (page, size, sort 등).
     *                 @PageableDefault로 기본값: size=10, sort=modDate DESC.
     * @return 해당 사용자가 찜한 상품을 ProductListDTO 페이징 형식으로 반환
     */
    @GetMapping("/favorites")
    public Page<ProductListDTO> getFavorites(
            HttpServletRequest request,
            @PageableDefault(size = 10, sort = "modDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long userId = userInfoUtil.getAuthUserId(request);
        return favoriteService.getFavoriteProducts(userId, pageable);
    }






}
