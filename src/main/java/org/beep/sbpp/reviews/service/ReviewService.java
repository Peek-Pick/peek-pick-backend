package org.beep.sbpp.reviews.service;

import org.beep.sbpp.reviews.dto.ReviewAddDTO;
import org.beep.sbpp.reviews.dto.ReviewDetailDTO;
import org.beep.sbpp.reviews.dto.ReviewModifyDTO;
import org.beep.sbpp.reviews.dto.ReviewSimpleDTO;

public interface ReviewService {
    // 이후 상품별 리뷰 리스트 구현 필요

    // 이후 사용자별 리뷰 리스트 구현 필요

    ReviewSimpleDTO getOne(Long reviewId);

    ReviewDetailDTO getOneDetail(Long reviewId);

    Long register(ReviewAddDTO reviewAddDTO);

    Long modify(Long reviewId, ReviewModifyDTO reviewModifyDTO);

    Long delete(Long reviewId);
}