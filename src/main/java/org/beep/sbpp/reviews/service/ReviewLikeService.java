package org.beep.sbpp.reviews.service;

public interface ReviewLikeService {
    public Long toggleReviewLike(Long reviewId, Long userId);
}
