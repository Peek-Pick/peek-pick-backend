package org.beep.sbpp.reviews.repository;

import org.beep.sbpp.reviews.entities.ReviewTagEntity;
import org.beep.sbpp.tags.dto.TagDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewTagRepository extends JpaRepository<ReviewTagEntity, Long> {
    // 특정 리뷰 ID에 해당하는 태그 전체 목록 조회
    @Query("""
                SELECT new org.beep.sbpp.tags.dto.TagDTO(rt.tagEntity)
                FROM ReviewTagEntity rt WHERE rt.reviewEntity.reviewId = :reviewId
    """)
    List<TagDTO> findAllTagsByReviewId(Long reviewId);
}