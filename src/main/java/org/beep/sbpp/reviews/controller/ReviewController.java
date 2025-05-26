package org.beep.sbpp.reviews.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.reviews.dto.*;
import org.beep.sbpp.reviews.service.ReviewLikeService;
import org.beep.sbpp.reviews.service.ReviewReportService;
import org.beep.sbpp.reviews.service.ReviewService;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;
    private final ReviewReportService reviewReportService;
    private final UserInfoUtil userInfoUtil;

    // 상품별 리뷰 조회
    @GetMapping(params = "productId")
    public ResponseEntity<Page<ReviewSimpleDTO>> getProductReviews(@RequestParam(value = "productId") Long productId, HttpServletRequest request,
                                                                   @PageableDefault(size = 10, sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = userInfoUtil.getAuthUserId(request);

        Page<ReviewSimpleDTO> reviews = reviewService.getProductReviews(productId, userId, pageable);

        log.info("Reviews = {}", reviews.toString());

        return ResponseEntity.ok(reviews);
    }

    // 사용자 리뷰 개수 조회
    @GetMapping("/count")
    public ResponseEntity<Long> countReviewsByUserId(HttpServletRequest request) {
        Long userId = userInfoUtil.getAuthUserId(request);

        Long count = reviewService.countReviewsByUserId(userId);

        return ResponseEntity.ok(count);
    }

    // 사용자별 리뷰 조회
    @GetMapping
    public ResponseEntity<Page<ReviewSimpleDTO>> getUserReviews(@PageableDefault(size = 10, sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable,
                                                                HttpServletRequest request
    ) {
        Long userId = userInfoUtil.getAuthUserId(request);

        Page<ReviewSimpleDTO> reviews = reviewService.getUserReviews(userId, pageable);

        log.info("User = {} Reviews = {}", userId, reviews.toString());

        return ResponseEntity.ok(reviews);
    }

    // 특정 리뷰 상세 조회
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDetailDTO> getReview(@PathVariable Long reviewId, HttpServletRequest request) {
        Long userId = userInfoUtil.getAuthUserId(request);

        ReviewDetailDTO review = reviewService.getOneDetail(reviewId, userId);

        log.info("Review = {}", review);

        return ResponseEntity.ok(review);
    }

    // 리뷰 등록
    @PostMapping
    public ResponseEntity<Long> registerReview(@RequestPart("review") String reviewJson,
                                               @RequestPart(value="files", required=false) MultipartFile[] files,
                                               HttpServletRequest request
    ) throws JsonProcessingException {
        log.info("Raw review JSON = {}", reviewJson);

        ObjectMapper objectMapper = new ObjectMapper();
        ReviewAddDTO reviewAddDTO = objectMapper.readValue(reviewJson, ReviewAddDTO.class);

        Long userId = userInfoUtil.getAuthUserId(request);
        reviewAddDTO.setUserId(userId);

        reviewAddDTO.setFiles(files);

        log.info("Parsed DTO = {}", reviewAddDTO);

        Long reviewId = reviewService.register(reviewAddDTO);

        log.info("Review ID = {}", reviewId);
        return ResponseEntity.ok(reviewId);
    }

    // 리뷰 수정 - userId 비교 검증 필요
    @PutMapping("/{reviewId}")
    public ResponseEntity<Long> modifyReview(@PathVariable Long reviewId, @RequestBody ReviewModifyDTO request) {
        Long updatedReviewId = reviewService.modify(reviewId, request);
        return ResponseEntity.ok(updatedReviewId);
    }

    // 리뷰 삭제 - userId 비교 검증 필요
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.delete(reviewId);
        return ResponseEntity.ok().build();
    }

    // 리뷰 좋아요/싫어요
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<Void> likeReview(@PathVariable Long reviewId,
                                           HttpServletRequest request) {
        Long userId = userInfoUtil.getAuthUserId(request);

        reviewLikeService.toggleReviewLike(reviewId, userId);

        return ResponseEntity.ok().build();
    }

    // 리뷰 신고
    @PostMapping("/{reviewId}/report")
    public ResponseEntity<Void> reportReview(@PathVariable Long reviewId,
                                             @RequestBody ReviewReportDTO dto,
                                             HttpServletRequest request) {
        Long userId = userInfoUtil.getAuthUserId(request);
        dto.setUserId(userId);

        dto.setReviewId(reviewId);

        reviewReportService.registerReport(dto);
        return ResponseEntity.ok().build();
    }
}