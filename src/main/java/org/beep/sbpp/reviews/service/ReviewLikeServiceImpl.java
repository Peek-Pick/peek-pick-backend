package org.beep.sbpp.reviews.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.beep.sbpp.reviews.entities.ReviewLikeEntity;
import org.beep.sbpp.reviews.repository.ReviewLikeRepository;
import org.beep.sbpp.reviews.repository.ReviewRepository;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewLikeServiceImpl implements ReviewLikeService {
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    // 토글 실패시 오류 메세지 수정 필요
    @Override
    public Long toggleReviewLike(Long reviewId, Long userId) {
        Optional<ReviewLikeEntity> optionalLike =
                reviewLikeRepository.findByReviewEntity_ReviewIdAndUserEntity_UserId(reviewId, userId);

        if (optionalLike.isPresent()) {
            ReviewLikeEntity like = optionalLike.get();

            if (like.getIsDelete()) {
                reviewLikeRepository.activateLike(reviewId, userId);
                reviewLikeRepository.increaseRecommendCnt(reviewId);
            } else {
                reviewLikeRepository.deactivateLike(reviewId, userId);
                reviewLikeRepository.decreaseRecommendCnt(reviewId);
            }

            return like.getReviewLikeId();

        } else {
            ReviewEntity review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + reviewId));

            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("No data found to get. userId: " + userId));

            ReviewLikeEntity newLike = ReviewLikeEntity.builder()
                    .reviewEntity(review)
                    .userEntity(user)
                    .isDelete(false)
                    .build();

            reviewLikeRepository.save(newLike);
            reviewLikeRepository.increaseRecommendCnt(reviewId);
            return newLike.getReviewLikeId();
        }
    }
}