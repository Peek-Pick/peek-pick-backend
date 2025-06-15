package org.beep.sbpp.points.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.points.dto.PointLogsDTO;
import org.beep.sbpp.admin.points.dto.PointStoreListDTO;
import org.beep.sbpp.points.service.PointService;
import org.beep.sbpp.points.dto.UserCouponDTO;
import org.beep.sbpp.points.service.UserCouponService;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;
    private final UserCouponService userCouponService;
    private final UserInfoUtil userInfoUtil;

    // 상품 목록 (사용자)
    @GetMapping("/points/store")
    public ResponseEntity<Page<PointStoreListDTO>> listCoupon(@RequestParam(required = false) String type, Pageable pageable) {
        Page<PointStoreListDTO> result = pointService.list(type, pageable);
        return ResponseEntity.ok(result);
    }

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

    @GetMapping("/users/mypage/coupons")
    public ResponseEntity<Page<UserCouponDTO>> getUserCoupons(
            HttpServletRequest request,
            Pageable pageable,
            @RequestParam(required = false) String status) {
        try {
            Long uid = userInfoUtil.getAuthUserId(request);
            Page<UserCouponDTO> couponList = userCouponService.list(uid, status, pageable);
            return ResponseEntity.ok(couponList);
        } catch (Exception e) {
            log.error("쿠폰 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    // 유저의 포인트 내역 반환
    @GetMapping("/users/mypage/points/history")
    public ResponseEntity<Page<PointLogsDTO>> getUserPointLogs(HttpServletRequest request, Pageable pageable) {
        try {
            Long uid = userInfoUtil.getAuthUserId(request);

            Page<PointLogsDTO> pointLogsList = pointService.pointLogsList(uid, pageable);

            return ResponseEntity.ok(pointLogsList);
        } catch (Exception e) {
            log.error("포인트 내역 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


}
