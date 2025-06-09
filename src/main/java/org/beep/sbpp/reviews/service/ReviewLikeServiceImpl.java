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

    @Override
    public Long toggleReviewLike(Long reviewId, Long userId) {
        Optional<ReviewLikeEntity> optionalLike =
                reviewLikeRepository.findByReviewEntity_ReviewIdAndUserEntity_UserId(reviewId, userId);

        if (optionalLike.isPresent()) {
            ReviewLikeEntity like = optionalLike.get();

            if (like.getIsDelete()) {
                // 리뷰 좋아요
                reviewLikeRepository.activateLike(reviewId, userId);
                reviewLikeRepository.increaseRecommendCnt(reviewId);
            } else {
                // 리뷰 좋아요 취소
                reviewLikeRepository.deactivateLike(reviewId, userId);
                reviewLikeRepository.decreaseRecommendCnt(reviewId);
            }

            return like.getReviewLikeId();
        } else {
            // 리뷰 존재 확인
            ReviewEntity review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + reviewId));

            // 유저 존재 확인
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("No data found to get. userId: " + userId));

            // 리뷰 좋아요 - 처음
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