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

    // 중복 신고 오류 메세지 수정 필요
    @Override
    public Long registerReport(ReviewReportDTO dto) {
        boolean alreadyReported = reviewReportRepository
                .existsByReviewEntity_ReviewIdAndUserEntity_UserId(dto.getReviewId(), dto.getUserId());

        if (alreadyReported) {
            throw new IllegalStateException("This review has already been reported.");
        }

        ReviewEntity reviewEntity = reviewRepository.findById(dto.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + dto.getReviewId()));

        UserEntity userEntity = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + dto.getUserId()));

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