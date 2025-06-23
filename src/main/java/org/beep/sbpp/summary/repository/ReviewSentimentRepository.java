package org.beep.sbpp.summary.repository;

import org.beep.sbpp.summary.entities.ReviewSentimentEntity;
import org.beep.sbpp.summary.enums.SentimentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewSentimentRepository extends JpaRepository<ReviewSentimentEntity, Long> {

    Optional<ReviewSentimentEntity> findByReviewEntity_ReviewId(Long reviewId);

    @Query("SELECT DISTINCT rs.productEntity.productId FROM ReviewSentimentEntity rs")
    List<Long> findDistinctProductIds();

    @Query("SELECT rs.comment " +
            "FROM ReviewSentimentEntity rs " +
            "WHERE rs.productEntity.productId = :productId AND rs.sentiment = :sentiment " +
            "ORDER BY rs.analyzedAt DESC")
    List<String> findCommentsByProductIdAndSentiment(@Param("productId") Long productId, @Param("sentiment") SentimentType sentiment);

}
