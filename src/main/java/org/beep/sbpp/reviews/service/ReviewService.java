package org.beep.sbpp.reviews.service;

import org.beep.sbpp.reviews.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    Page<ReviewDetailDTO> getProductReviews(Long productId, Long userId, Pageable pageable);

    Page<ReviewSimpleDTO> getUserReviews(Long userId, Pageable pageable);

    Long countReviewsByProductId(Long productId);

    Long countReviewsByUserId(Long userId);

    ReviewDetailDTO getOneDetail(Long reviewId, Long userId);

    Long register(ReviewAddDTO reviewAddDTO);

    Long modify(Long userId, Long reviewId, ReviewModifyDTO reviewModifyDTO);

    Long delete(Long userId, Long reviewId);
}