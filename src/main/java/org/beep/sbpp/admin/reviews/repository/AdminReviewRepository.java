package org.beep.sbpp.admin.reviews.repository;

import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminReviewRepository extends JpaRepository<ReviewEntity, Long>,
        AdminReviewRepositoryCustom {

    @Modifying
    @Query("UPDATE ReviewEntity r SET r.isHidden = :hidden WHERE r.reviewId = :reviewId")
    int toggleIsHidden(@Param("reviewId") Long reviewId, @Param("hidden") boolean hidden);
}