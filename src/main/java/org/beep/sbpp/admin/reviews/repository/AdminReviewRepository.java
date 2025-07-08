package org.beep.sbpp.admin.reviews.repository;

import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AdminReviewRepository extends JpaRepository<ReviewEntity, Long>, AdminReviewRepositoryCustom {
    @Modifying
    @Query("UPDATE ReviewEntity r SET r.isHidden = :hidden WHERE r.reviewId = :reviewId")
    int toggleIsHidden(@Param("reviewId") Long reviewId, @Param("hidden") boolean hidden);

    @Query("""
        SELECT FUNCTION('TO_CHAR', r.regDate, 'MM'), COUNT(r.reviewId)
        FROM ReviewEntity r
        WHERE r.regDate BETWEEN :startDateTime AND :endDateTime
        GROUP BY FUNCTION('TO_CHAR', r.regDate, 'MM')
        ORDER BY FUNCTION('TO_CHAR', r.regDate, 'MM')
    """)
    List<Object[]> countMonthlyReviews(@Param("startDateTime") LocalDateTime startDateTime,
                                       @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE EXTRACT(MONTH FROM r.regDate) = :month AND EXTRACT(YEAR FROM r.regDate) = :year")
    Long countByMonth(@Param("month") int month, @Param("year") int year);
}