package org.beep.sbpp.reviews.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.beep.sbpp.reviews.dto.*;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.beep.sbpp.reviews.entities.ReviewImgEntity;
import org.beep.sbpp.reviews.repository.ReviewImgRepository;
import org.beep.sbpp.reviews.repository.ReviewLikeRepository;
import org.beep.sbpp.reviews.repository.ReviewRepository;
import org.beep.sbpp.reviews.util.UnauthorizedAccessException;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.entities.UserProfileEntity;
import org.beep.sbpp.users.repository.UserProfileRepository;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    public Page<ReviewSimpleDTO> getProductReviews(Long productId, Long userId, Pageable pageable) {
        Page<ReviewEntity> reviewPage = reviewRepository.findByProductId(productId, pageable);

        return reviewPage.map(review -> {
            List<ReviewImgDTO> reviewImgDTOList = reviewImgRepository.selectImgAll(review.getReviewId());

            // 좋아요 여부 조회
            boolean isLiked = reviewLikeRepository.hasUserLikedReview(review.getReviewId(), userId);

            ReviewSimpleDTO.ReviewSimpleDTOBuilder builder = ReviewSimpleDTO.builder()
                    .reviewId(review.getReviewId())
                    .userId(review.getUserEntity().getUserId())
                    .score(review.getScore())
                    .recommendCnt(review.getRecommendCnt())
                    .comment(review.getComment())
                    .regDate(review.getRegDate())
                    .modDate(review.getModDate())
                    .isLiked(isLiked);

            if (reviewImgDTOList != null && !reviewImgDTOList.isEmpty()) {
                builder.image(reviewImgDTOList.get(0));
            }

            return builder.build();
        });
    }

    public Long countReviewsByUserId(Long userId) {
        return reviewRepository.countReviewsByUserId(userId);
    }

    // 조회 실패시 오류 메세지 수정 필요
    @Override
    public Page<ReviewSimpleDTO> getUserReviews(Long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. userId: " + userId));

        Page<ReviewEntity> reviewPage = reviewRepository.findByUserId(userId, pageable);

        return reviewPage.map(review -> {
            List<ReviewImgDTO> reviewImgDTOList = reviewImgRepository.selectImgAll(review.getReviewId());

            ReviewSimpleDTO.ReviewSimpleDTOBuilder builder = ReviewSimpleDTO.builder()
                    .reviewId(review.getReviewId())
                    .userId(review.getUserEntity().getUserId())
                    .score(review.getScore())
                    .recommendCnt(review.getRecommendCnt())
                    .comment(review.getComment())
                    .regDate(review.getRegDate())
                    .modDate(review.getModDate());

            if (reviewImgDTOList != null && !reviewImgDTOList.isEmpty()) {
                builder.image(reviewImgDTOList.get(0));
            }

            return builder.build();
        });
    }

    // 조회 실패시 오류 메세지 수정 필요
    @Override
    public ReviewDetailDTO getOneDetail(Long reviewId, Long userId) {
        ReviewDTO reviewDTO = reviewRepository.selectOne(reviewId);

        if (reviewDTO == null) {
            throw new IllegalArgumentException("No data found to get. reviewId: " + reviewId);
        }

        List<ReviewImgDTO> reviewImgDTOList = reviewImgRepository.selectImgAll(reviewId);

        // 좋아요 여부 조회
        boolean isLiked = reviewLikeRepository.hasUserLikedReview(reviewDTO.getReviewId(), userId);

        ReviewDetailDTO.ReviewDetailDTOBuilder builder = ReviewDetailDTO.builder()
                .reviewId(reviewDTO.getReviewId())
                .userId(reviewDTO.getUserId())
                .score(reviewDTO.getScore())
                .comment(reviewDTO.getComment())
                .recommendCnt(reviewDTO.getRecommendCnt())
                .regDate(reviewDTO.getRegDate())
                .modDate(reviewDTO.getModDate())
                .isHidden(reviewDTO.getIsHidden())
                .isLiked(isLiked);

        if (reviewImgDTOList != null && !reviewImgDTOList.isEmpty()) {
            builder.images(reviewImgDTOList);
        }

        Optional<UserProfileEntity> userProfileEntity = userProfileRepository.findByUserId(reviewDTO.getUserId());
        String nickname = userProfileEntity.map(UserProfileEntity::getNickname).orElse(null);
        builder.nickname(nickname);

        return builder.build();
    }

    // 등록 실패시 오류 메세지 수정 필요
    @Override
    public Long register(ReviewAddDTO reviewAddDTO) {
        UserEntity userEntity = userRepository.findById(reviewAddDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. userId: " + reviewAddDTO.getUserId()));

        ReviewEntity reviewEntity = ReviewEntity.builder()
                .userEntity(userEntity)
                .comment(reviewAddDTO.getComment())
                .score(reviewAddDTO.getScore())
                .recommendCnt(0)
                .isHidden(false)
                .isDelete(false)
                .build();

        Long reviewId =  reviewRepository.save(reviewEntity).getReviewId();
        log.info("New review id: {}", reviewId);

        if (reviewAddDTO.getFiles() != null) {
            MultipartFile[] files =  reviewAddDTO.getFiles();

            for(MultipartFile file: files) {
                String uuid = UUID.randomUUID().toString();

                String saveFileName = uuid +"_" + file.getOriginalFilename();
                String thumbFileName = "s_" + saveFileName;

                File target = new File("C:\\nginx-1.26.3\\html\\" + saveFileName );
                File thumbFile = new File("C:\\nginx-1.26.3\\html\\" + thumbFileName);

                try {
                    file.transferTo(target);

                    Thumbnails.of(target)
                            .size(200,200)
                            .toFile(thumbFile);

                    ReviewImgEntity reviewImgEntity = ReviewImgEntity.builder()
                            .reviewEntity(reviewEntity)
                            .imgUrl(saveFileName)
                            .build();

                    Long reviewImgId = reviewImgRepository.save(reviewImgEntity).getReviewImgId();
                    log.info("Saved reviewImageFiles: {}", reviewImgId);
                } catch (Exception e) {
                    log.info(e.getMessage());
                }
            }
        }

        return reviewId;
    }

    // 수정 실패시 오류 메세지 수정 필요
    @Override
    public Long modify(Long userId, Long reviewId, ReviewModifyDTO reviewModifyDTO) {
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + reviewId));

        if (!reviewEntity.getUserEntity().getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to modify this review.");
        }

        String comment = reviewModifyDTO.getComment();
        Integer score = reviewModifyDTO.getScore();

        int result = reviewRepository.updateOne(reviewId, comment, score);

        if (result <= 0) {
            throw new IllegalArgumentException("Failed to modify. reviewId: " + reviewId);
        }

        log.info("Modified Review: id={} comment='{}' score={}", reviewId, comment, score);

        List<Long> deleteImgIds = reviewModifyDTO.getDeleteImgIds();
        MultipartFile[] newFiles= reviewModifyDTO.getFiles();

        if (deleteImgIds != null && !deleteImgIds.isEmpty()) {
            String basePath = "C:\\nginx-1.26.3\\html\\";

            for (Long imgId : deleteImgIds) {
                reviewImgRepository.findById(imgId).ifPresent(reviewImgEntity -> {
                    String imgUrl = reviewImgEntity.getImgUrl();

                    File original = new File(basePath + imgUrl);
                    File thumbnail = new File(basePath + "s_" + imgUrl);

                    if (original.exists() && !original.delete()) {
                        log.warn("Failed to delete original image file: {}", original.getAbsolutePath());
                    }
                    if (thumbnail.exists() && !thumbnail.delete()) {
                        log.warn("Failed to delete thumbnail image file: {}", thumbnail.getAbsolutePath());
                    }

                    reviewImgRepository.deleteById(imgId);
                    log.info("Deleted ReviewImg record and files: id={} url={}", imgId, imgUrl);
                });
            }
        }

        if (newFiles != null) {
            for(MultipartFile file: newFiles) {
                String uuid = UUID.randomUUID().toString();

                String saveFileName = uuid + "_" + file.getOriginalFilename();
                String thumbFileName = "s_" + saveFileName;

                File target = new File("C:\\nginx-1.26.3\\html\\" + saveFileName);
                File thumbFile = new File("C:\\nginx-1.26.3\\html\\" + thumbFileName);

                try {
                    file.transferTo(target);

                    Thumbnails.of(target)
                            .size(200, 200)
                            .toFile(thumbFile);

                    ReviewImgEntity reviewImgEntity = ReviewImgEntity.builder()
                            .reviewEntity(reviewEntity)
                            .imgUrl(saveFileName)
                            .build();

                    Long reviewImgId = reviewImgRepository.save(reviewImgEntity).getReviewImgId();
                    log.info("Saved reviewImageFiles: {}", reviewImgId);
                } catch (Exception e) {
                    log.info(e.getMessage());
                }
            }
        }

        return reviewId;
    }

    // 삭제 실패시 오류 메시지 수정 필요
    @Override
    public Long delete(Long userId, Long reviewId) {
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + reviewId));

        if (!reviewEntity.getUserEntity().getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this review.");
        }

        List<ReviewImgEntity> reviewImgEntities = reviewImgRepository.findAllByReviewEntity_ReviewId(reviewId);

        if (!reviewImgEntities.isEmpty()) {
            String basePath = "C:\\nginx-1.26.3\\html\\";

            for (ReviewImgEntity imgEntity : reviewImgEntities) {
                String imgUrl = imgEntity.getImgUrl();

                // 파일 객체 준비
                File original = new File(basePath + imgUrl);
                File thumbnail = new File(basePath + "s_" + imgUrl);

                // 실제 파일 삭제 시도
                if (original.exists() && !original.delete()) {
                    log.warn("Failed to delete original image file: {}", original.getAbsolutePath());
                }
                if (thumbnail.exists() && !thumbnail.delete()) {
                    log.warn("Failed to delete thumbnail image file: {}", thumbnail.getAbsolutePath());
                }

                // DB 레코드 삭제
                reviewImgRepository.delete(imgEntity);
                log.info("Deleted ReviewImg record and files: id={} url={}", imgEntity.getReviewImgId(), imgUrl);
            }
        }

        int result = reviewRepository.deleteOne(reviewId);

        if (result > 0) {
            return reviewId;
        } else {
            throw new IllegalArgumentException("Failed to delete review. reviewId: " + reviewId);
        }
    }
}