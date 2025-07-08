package org.beep.sbpp.summary.repository;

import jakarta.transaction.Transactional;
import org.beep.sbpp.summary.entities.ReviewSentimentEntity;
import org.beep.sbpp.summary.enums.SentimentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewSentimentRepository extends JpaRepository<ReviewSentimentEntity, Long> {

    Optional<ReviewSentimentEntity> findByReviewEntity_ReviewId(Long reviewId);

    @Query("SELECT DISTINCT rs.productBaseEntity.productId FROM ReviewSentimentEntity rs")
    List<Long> findDistinctProductIds();

    @Query("SELECT rs.comment " +
            "FROM ReviewSentimentEntity rs " +
            "WHERE rs.productBaseEntity.productId = :productId AND rs.sentiment = :sentiment " +
            "ORDER BY rs.analyzedAt DESC")
    List<String> findCommentsByProductIdAndSentiment(@Param("productId") Long productId, @Param("sentiment") SentimentType sentiment);

    @Modifying
    @Transactional
    @Query("DELETE FROM ReviewSentimentEntity rs WHERE rs.reviewEntity.reviewId = :reviewId")
    void deleteByReviewId(Long reviewId);
}