package org.beep.sbpp.reviews.repository;

import org.beep.sbpp.reviews.dto.ReviewImgDTO;
import org.beep.sbpp.reviews.entities.ReviewImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewImgRepository extends JpaRepository<ReviewImgEntity, Long> {
    @Query("""
                select new org.beep.sbpp.reviews.dto.ReviewImgDTO(ri.reviewImgId, ri.reviewEntity.reviewId, ri.imgUrl)
                from ReviewImgEntity ri
                where ri.reviewEntity.reviewId = :reviewId
                order by ri.reviewImgId asc
    """)
    List<ReviewImgDTO> selectImgAll(@Param("reviewId") Long reviewId);
}