package org.beep.sbpp.reviews.repository;

import jakarta.transaction.Transactional;
import org.beep.sbpp.reviews.dto.ReviewDTO;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository  extends JpaRepository<ReviewEntity, Long> {
    @Query("""
                select new org.beep.sbpp.reviews.dto.ReviewDTO(r.reviewId, r.score, r.recommendCnt,
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