package org.beep.sbpp.reviews.repository;

import org.beep.sbpp.reviews.entities.ReviewLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLikeEntity, Long> {
    Optional<ReviewLikeEntity> findByReviewEntity_ReviewIdAndUserEntity_UserId(Long reviewId, Long userId);

    @Query("""
                SELECT CASE WHEN COUNT(rl) > 0 THEN true ELSE false END
                FROM ReviewLikeEntity rl
                WHERE rl.reviewEntity.reviewId = :reviewId
                AND rl.userEntity.userId = :userId AND rl.isDelete = false
    """)
    boolean hasUserLikedReview(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    @Modifying
    @Query("""
                update ReviewLikeEntity rl
                set rl.isDelete = false
                where rl.reviewEntity.reviewId = :reviewId
                and rl.userEntity.userId = :userId
    """)
    int activateLike(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    @Modifying
    @Query("""
                update ReviewLikeEntity rl
                set rl.isDelete = true
                where rl.reviewEntity.reviewId = :reviewId
                and rl.userEntity.userId = :userId
    """)
    int deactivateLike(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    @Modifying
    @Query("""
                update ReviewEntity r
                set r.recommendCnt = r.recommendCnt + 1
                where r.reviewId = :reviewId
    """)
    int increaseRecommendCnt(@Param("reviewId") Long reviewId);

    @Modifying
    @Query("""
                update ReviewEntity r
                set r.recommendCnt = r.recommendCnt - 1
                where r.reviewId = :reviewId
    """)
    int decreaseRecommendCnt(@Param("reviewId") Long reviewId);
}