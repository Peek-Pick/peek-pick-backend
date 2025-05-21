package org.beep.sbpp.reviews.repository;

import org.beep.sbpp.reviews.entities.ReviewReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReportRepository extends JpaRepository<ReviewReportEntity, Long> {
    boolean existsByReviewEntity_ReviewIdAndUserEntity_UserId(Long reviewId, Long userId);
}