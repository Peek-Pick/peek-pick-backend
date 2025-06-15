package org.beep.sbpp.admin.points.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.admin.points.dto.PointStoreAddDTO;
import org.beep.sbpp.admin.points.dto.PointStoreDTO;
import org.beep.sbpp.admin.points.dto.PointStoreListDTO;
import org.beep.sbpp.admin.points.service.AdminPointService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/points")
@Slf4j
@RequiredArgsConstructor
public class AdminPointController {

    private final AdminPointService service;

    // 상품 추가 (쿠폰 등록)
    @PostMapping
    public ResponseEntity<Long> addCoupon(@ModelAttribute PointStoreAddDTO dto) {
        Long id = service.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    //상품 조회
    @GetMapping("/{pointstoreId}")
    public ResponseEntity<PointStoreDTO> readCoupon(@PathVariable Long pointstoreId) {
        PointStoreDTO dto = service.read(pointstoreId);
        return ResponseEntity.ok(dto);
    }

    // 상품 목록 (관리자용 전체 목록)
    @GetMapping
    public ResponseEntity<Page<PointStoreListDTO>> listCoupon(
            @PageableDefault(sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean hidden)
    {
        Page<PointStoreListDTO> result = service.list(pageable, category, keyword, hidden);
        return ResponseEntity.ok(result);
    }

    // 상품 수정
    @PutMapping("/{pointstoreId}")
    public ResponseEntity<Void> updateCoupon(@PathVariable Long pointstoreId,
                                               @ModelAttribute PointStoreAddDTO dto) {
        log.debug("넘어온 imgUrl = {}", dto.getImgUrl()); // 추가
        dto.setPointstoreId(pointstoreId);
        service.modify(dto);
        return ResponseEntity.ok().build();
    }

    // 상품 삭제
    @PatchMapping("/{pointstoreId}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long pointstoreId) {
        service.delete(pointstoreId);
        return ResponseEntity.ok().build();
    }
}
