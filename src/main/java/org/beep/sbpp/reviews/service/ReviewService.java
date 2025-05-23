package org.beep.sbpp.reviews.service;

import org.beep.sbpp.reviews.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    // 상품별 리스트 수정 필요
    Page<ReviewSimpleDTO> getProductReviews(Long productId, Long userId, Pageable pageable);

   Long countReviewsByUserId(Long userId);

    Page<ReviewSimpleDTO> getUserReviews(Long userId, Pageable pageable);

    ReviewSimpleDTO getOne(Long reviewId, Long userId);

    ReviewDetailDTO getOneDetail(Long reviewId, Long userId);

    Long register(ReviewAddDTO reviewAddDTO);

    Long modify(Long reviewId, ReviewModifyDTO reviewModifyDTO);

    Long delete(Long reviewId);
}