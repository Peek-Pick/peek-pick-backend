package org.beep.sbpp.summary.repository;

import jakarta.transaction.Transactional;
import org.beep.sbpp.summary.entities.ProductReviewSummaryEntity;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.summary.enums.SentimentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ProductReviewSummaryRepository extends JpaRepository<ProductReviewSummaryEntity, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductReviewSummaryEntity s " +
            "WHERE s.productBaseEntity.productId = :productId AND s.sentiment = :sentiment")
    void deleteByProductIdAndSentiment(@Param("productId") Long productId, @Param("sentiment") SentimentType sentiment);

    default void saveOrUpdate(Long productId, SentimentType sentiment, String summaryText, int reviewCount) {
        deleteByProductIdAndSentiment(productId, sentiment);

        ProductBaseEntity product = ProductBaseEntity.builder()
                .productId(productId)
                .build();

        ProductReviewSummaryEntity summary = ProductReviewSummaryEntity.builder()
                .productBaseEntity(product)
                .sentiment(sentiment)
                .summaryText(summaryText)
                .reviewCount(reviewCount)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .build();

        save(summary);
    }

    Optional<ProductReviewSummaryEntity> findByProductBaseEntity_productIdAndSentiment(Long productId, SentimentType sentiment);

}
