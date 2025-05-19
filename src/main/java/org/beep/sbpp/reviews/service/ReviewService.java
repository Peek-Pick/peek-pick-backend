package org.beep.sbpp.reviews.service;

import org.beep.sbpp.reviews.dto.ReviewDTO;
import org.beep.sbpp.reviews.dto.ReviewImgDTO;

import java.util.List;

public interface ReviewService {
    // 이후 사용자별 리뷰 리스트 구현 필요
    // 이후 상품별 리뷰 리스트 구현 필요

    ReviewDTO getOne(Long reviewId);

    Long register(ReviewDTO reviewDTO);

    Long modify(Long reviewId, String comment, Integer score);

    Long delete(Long reviewId);
}