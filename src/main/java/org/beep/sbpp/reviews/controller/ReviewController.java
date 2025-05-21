package org.beep.sbpp.reviews.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.reviews.dto.*;
import org.beep.sbpp.reviews.service.ReviewLikeService;
import org.beep.sbpp.reviews.service.ReviewReportService;
import org.beep.sbpp.reviews.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;
    private final ReviewReportService reviewReportService;

    // 리뷰 작성
    @PostMapping
    public ResponseEntity<Long> registerReview(@RequestBody ReviewAddDTO reviewAddDTO) {
        Long reviewId = reviewService.register(reviewAddDTO);
        return ResponseEntity.ok(reviewId);
    }


    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<Long> modifyReview(@PathVariable Long reviewId, @RequestBody ReviewModifyDTO request) {
        Long updatedReviewId = reviewService.modify(reviewId, request);
        return ResponseEntity.ok(updatedReviewId);
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.delete(reviewId);
        return ResponseEntity.ok().build();
    }

    // 리뷰 좋아요/싫어요
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<Void> likeReview(@PathVariable Long reviewId, @RequestParam Long userId) {
        reviewLikeService.toggleReviewLike(reviewId, userId);
        return ResponseEntity.ok().build();
    }

    // 리뷰 신고
    @PostMapping("/{reviewId}/report")
    public ResponseEntity<Void> reportReview(@PathVariable Long reviewId, @RequestBody ReviewReportDTO dto) {
        dto.setReviewId(reviewId);
        reviewReportService.registerReport(dto);
        return ResponseEntity.ok().build();
    }
}