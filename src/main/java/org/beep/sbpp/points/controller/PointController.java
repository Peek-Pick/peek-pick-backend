package org.beep.sbpp.points.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.points.dto.PointStoreListDTO;
import org.beep.sbpp.points.service.PointStoreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/points")
@Slf4j
@RequiredArgsConstructor
public class PointController {

    private final PointStoreService service;

    // 포인트 상점 상품 리스트
    @GetMapping("/store")
    public Page<PointStoreListDTO> getStoreList(Pageable pageable) {
        return service.list(pageable);
    }

    /*// 포인트 사용 (쿠폰 구매)
    @PostMapping("/redeem")
    public ResponseEntity<String> redeemCoupon(@RequestBody RedeemRequestDTO dto) {
        service.redeem(dto);  // 예: 포인트 차감 + 쿠폰 지급
        return ResponseEntity.ok("포인트 사용 완료");
    }*/


}
