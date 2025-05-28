package org.beep.sbpp.reviews.repository;

import jakarta.transaction.Transactional;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository  extends JpaRepository<ReviewEntity, Long> {
    @Query("SELECT r FROM ReviewEntity r WHERE r.productEntity.productId = :productId")
    Page<ReviewEntity> findByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT r FROM ReviewEntity r WHERE r.userEntity.userId = :userId")
    Page<ReviewEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.productEntity.productId = :productId")
    Long countReviewsByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.userEntity.userId = :userId")
    Long countReviewsByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("""
                update ReviewEntity r set r.comment = :comment, r.score = :score, r.modDate = CURRENT_TIMESTAMP
                                where r.reviewId = :reviewId
            """)
    int updateOne(@Param("reviewId") Long reviewId, @Param("comment") String comment, @Param("score") Integer score);
}