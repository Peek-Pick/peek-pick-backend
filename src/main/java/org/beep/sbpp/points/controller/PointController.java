package org.beep.sbpp.points.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.points.dto.PointRedeemDTO;
import org.beep.sbpp.points.dto.PointStoreListDTO;
import org.beep.sbpp.points.service.PointService;
import org.beep.sbpp.points.service.PointStoreService;
import org.beep.sbpp.users.dto.UserCouponDTO;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.service.UserCouponService;
import org.beep.sbpp.util.JWTUtil;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;
    private final UserCouponService userCouponService;
    private final UserInfoUtil userInfoUtil;

    // 포인트 사용 (쿠폰 구매)
    @PatchMapping("/points/redeem/{pointStoreId}")
    public ResponseEntity<Integer> redeemCoupon(@PathVariable Long pointStoreId,
                                                HttpServletRequest request) {
        try {
            Long uid = userInfoUtil.getAuthUserId(request);

            int remainingPoints = pointService.redeemPoints(uid, pointStoreId);

            return ResponseEntity.ok(remainingPoints);
        } catch (Exception e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    //유저의 쿠폰 리스트 반환
    @GetMapping("/users/mypage/coupons")
    public ResponseEntity<Page<UserCouponDTO>> getUserCoupons(HttpServletRequest request, Pageable pageable) {
        try {
            Long uid = userInfoUtil.getAuthUserId(request);

            Page<UserCouponDTO> couponList = userCouponService.list(uid, pageable);

            return ResponseEntity.ok(couponList);
        } catch (Exception e) {
            log.error("쿠폰 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }






}
