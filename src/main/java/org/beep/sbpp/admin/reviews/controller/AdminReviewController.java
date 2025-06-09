package org.beep.sbpp.admin.reviews.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.admin.reviews.dto.AdminReviewDetailDTO;
import org.beep.sbpp.admin.reviews.dto.AdminReviewReportDTO;
import org.beep.sbpp.admin.reviews.dto.AdminReviewSimpleDTO;
import org.beep.sbpp.admin.reviews.service.AdminReviewReportService;
import org.beep.sbpp.admin.reviews.service.AdminReviewService;
import org.beep.sbpp.reviews.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {
    private final AdminReviewService adminReviewService;
    private final AdminReviewReportService adminReviewReportService;
    private final ReviewService reviewService;

    // 필터링된 리뷰 리스트 - 페이지
    @GetMapping
    public ResponseEntity<Page<AdminReviewSimpleDTO>> getAdminReviews(
            @PageableDefault(sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {

        return ResponseEntity.ok(adminReviewService.getReviewList(pageable, category, keyword));
    }

    // 특정 리뷰 디테일
    @GetMapping("/{reviewId}")
    public ResponseEntity<AdminReviewDetailDTO> getAdminReviewDetail(@PathVariable Long reviewId) {
        return ResponseEntity.ok(adminReviewService.getReviewDetail(reviewId));
    }

    // 필터링된 리뷰 신고 리스트 - 페이지
    @GetMapping("/report")
    public ResponseEntity<Page<AdminReviewReportDTO>> getAdminReviewReports(
            @PageableDefault(sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {

        return ResponseEntity.ok(adminReviewReportService.getReviewReportList(pageable, category, keyword));
    }

    // 특정 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Long> deleteReview(@PathVariable Long reviewId) {
        // 실제 어드민 테이블 생기면 수정 필요
        return ResponseEntity.ok(reviewService.delete(-1L, reviewId));
    }

    // 특정 리뷰 숨김 상태 토글
    @PutMapping("/hide/{reviewId}")
    public ResponseEntity<Long> toggleReviewHidden(@PathVariable Long reviewId) {
        return ResponseEntity.ok(adminReviewService.toggleHiddenStatus(reviewId));
    }
}