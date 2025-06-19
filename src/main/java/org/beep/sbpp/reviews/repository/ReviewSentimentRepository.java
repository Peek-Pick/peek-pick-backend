package org.beep.sbpp.reviews.repository;

import org.beep.sbpp.reviews.entities.ReviewSentimentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewSentimentRepository extends JpaRepository<ReviewSentimentEntity, Long> {

    Optional<ReviewSentimentEntity> findByReviewEntity_ReviewId(Long reviewId);

}
