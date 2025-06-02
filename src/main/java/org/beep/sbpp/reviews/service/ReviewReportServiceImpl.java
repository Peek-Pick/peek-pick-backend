package org.beep.sbpp.reviews.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.reviews.dto.ReviewReportDTO;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.beep.sbpp.reviews.entities.ReviewReportEntity;
import org.beep.sbpp.reviews.repository.ReviewReportRepository;
import org.beep.sbpp.reviews.repository.ReviewRepository;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewReportServiceImpl implements ReviewReportService{
    private final ReviewReportRepository reviewReportRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Override
    public Long registerReport(ReviewReportDTO dto) {
        boolean alreadyReported = reviewReportRepository
                .existsByReviewEntity_ReviewIdAndUserEntity_UserId(dto.getReviewId(), dto.getUserId());

        // 이미 리뷰를 신고한 상태 - 중복 신고 불가
        if (alreadyReported) {
            throw new IllegalStateException("This review has already been reported.");
        }

        // 리뷰 존재 확인
        ReviewEntity reviewEntity = reviewRepository.findById(dto.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + dto.getReviewId()));

        // 유저 존재 확인
        UserEntity userEntity = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + dto.getUserId()));

        // 리뷰 신고
        ReviewReportEntity reportEntity = ReviewReportEntity.builder()
                .reviewEntity(reviewEntity)
                .userEntity(userEntity)
                .reason(dto.getReason())
                .build();

        ReviewReportEntity saved = reviewReportRepository.save(reportEntity);

        log.info("Created Report: id={}, reviewId={}, userId={}, reason={}",
                saved.getReviewReportId(), dto.getReviewId(), dto.getUserId(), dto.getReason());

        return saved.getReviewReportId();
    }
}