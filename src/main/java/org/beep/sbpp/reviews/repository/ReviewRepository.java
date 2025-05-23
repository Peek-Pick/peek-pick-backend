package org.beep.sbpp.reviews.repository;

import jakarta.transaction.Transactional;
import org.beep.sbpp.reviews.dto.ReviewDTO;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository  extends JpaRepository<ReviewEntity, Long> {
    // 상품별 리스트 수정 필요
    @Query("SELECT r FROM ReviewEntity r WHERE r.isDelete = false")
    Page<ReviewEntity> findByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.userEntity.userId = :userId AND r.isDelete = false")
    Long countReviewsByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM ReviewEntity r WHERE r.userEntity.userId = :userId AND r.isDelete = false")
    Page<ReviewEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
                select new org.beep.sbpp.reviews.dto.ReviewDTO(r.reviewId, r.userEntity.userId, r.score, r.recommendCnt,
                             r.comment, r.isHidden, r.isDelete, r.regDate, r.modDate)
                from ReviewEntity r where r.reviewId = :reviewId and r.isDelete = false
            """)
    ReviewDTO selectOne(@Param("reviewId") Long reviewId);

    @Modifying
    @Transactional
    @Query("""
                update ReviewEntity r set r.comment = :comment, r.score = :score, r.modDate = CURRENT_TIMESTAMP
                                where r.reviewId = :reviewId and r.isDelete = false
            """)
    int updateOne(@Param("reviewId") Long reviewId, @Param("comment") String comment, @Param("score") Integer score);

    @Modifying
    @Transactional
    @Query("update ReviewEntity r set r.isDelete = true where r.reviewId = :reviewId")
    int deleteOne(@Param("reviewId") Long reviewId);
}