package org.beep.sbpp.reviews.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.beep.sbpp.products.entities.ProductEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.reviews.dto.*;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.beep.sbpp.reviews.entities.ReviewImgEntity;
import org.beep.sbpp.reviews.entities.ReviewTagEntity;
import org.beep.sbpp.reviews.repository.*;
import org.beep.sbpp.reviews.util.UnauthorizedAccessException;
import org.beep.sbpp.tags.dto.TagDTO;
import org.beep.sbpp.tags.entities.TagEntity;
import org.beep.sbpp.tags.repository.TagRepository;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final ReviewTagRepository reviewTagRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final TagRepository tagRepository;
    private final ProductRepository productRepository;

    @Override
    public Page<ReviewDetailDTO> getProductReviews(Long productId, Long userId, Pageable pageable) {
        // 상품 존재 확인
        productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. productId: " + productId));

        Page<ReviewEntity> reviewPage = reviewRepository.findByProductId(productId, pageable);

        return reviewPage.map(review -> {
            // 이미지 조회
            List<ReviewImgDTO> reviewImgDTOList = reviewImgRepository.selectImgAll(review.getReviewId());

            // 좋아요 여부 조회
            boolean isLiked = reviewLikeRepository.hasUserLikedReview(review.getReviewId(), userId);

            // 닉네임 조회
            Optional<UserProfileEntity> userProfileEntity = userProfileRepository.findByUserId(userId);
            String nickname = userProfileEntity.map(UserProfileEntity::getNickname).orElse(null);

            // 태그 조회
            List<TagDTO> tagList = reviewTagRepository.findAllTagsByReviewId(review.getReviewId());

            // 상품 조회
            ProductEntity productEntity = productRepository.findById(review.getProductEntity().getProductId()).get();

            // 빌더로 DTO 생성
            ReviewDetailDTO.ReviewDetailDTOBuilder builder = ReviewDetailDTO.builder()
                    .reviewId(review.getReviewId())
                    .userId(review.getUserEntity().getUserId())
                    .productId(review.getProductEntity().getProductId())
                    .score(review.getScore())
                    .comment(review.getComment())
                    .recommendCnt(review.getRecommendCnt())
                    .regDate(review.getRegDate())
                    .modDate(review.getModDate())
                    .isHidden(review.getIsHidden())
                    .isLiked(isLiked)
                    .nickname(nickname)
                    .tagList(tagList)
                    .imageUrl(productEntity.getImgUrl())
                    .name(productEntity.getName());

            if (reviewImgDTOList != null && !reviewImgDTOList.isEmpty()) {
                builder.images(reviewImgDTOList);
            }

            return builder.build();
        });
    }

    // 조회 실패시 오류 메세지 수정 필요
    @Override
    public Page<ReviewSimpleDTO> getUserReviews(Long userId, Pageable pageable) {
        // 유저 존재 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. userId: " + userId));

        Page<ReviewEntity> reviewPage = reviewRepository.findByUserId(userId, pageable);

        // 유저별 리뷰 리스트
        return reviewPage.map(review -> {
            List<ReviewImgDTO> reviewImgDTOList = reviewImgRepository.selectImgAll(review.getReviewId());
            ProductEntity productEntity = productRepository.findById(review.getProductEntity().getProductId()).get();

            ReviewSimpleDTO.ReviewSimpleDTOBuilder builder = ReviewSimpleDTO.builder()
                    .reviewId(review.getReviewId())
                    .userId(review.getUserEntity().getUserId())
                    .productId(review.getProductEntity().getProductId())
                    .score(review.getScore())
                    .recommendCnt(review.getRecommendCnt())
                    .comment(review.getComment())
                    .regDate(review.getRegDate())
                    .modDate(review.getModDate())
                    .imageUrl(productEntity.getImgUrl())
                    .name(productEntity.getName());

            if (reviewImgDTOList != null && !reviewImgDTOList.isEmpty()) {
                builder.image(reviewImgDTOList.get(0));
            }

            return builder.build();
        });
    }

    public Long countReviewsByProductId(Long productId) {
        return reviewRepository.countReviewsByProductId(productId);
    }

    public Long countReviewsByUserId(Long userId) {
        return reviewRepository.countReviewsByUserId(userId);
    }

    // 조회 실패시 오류 메세지 수정 필요
    @Override
    public ReviewDetailDTO getOneDetail(Long reviewId, Long userId) {
        // 리뷰 존재 확인
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + reviewId));

        // 좋아요 여부 조회
        boolean isLiked = reviewLikeRepository.hasUserLikedReview(reviewId, userId);

        // 닉네임 조회
        Optional<UserProfileEntity> userProfileEntity = userProfileRepository.findByUserId(userId);
        String nickname = userProfileEntity.map(UserProfileEntity::getNickname).orElse(null);

        // 태그 조회
        List<TagDTO> tagList = reviewTagRepository.findAllTagsByReviewId(reviewId);

        // 이미지 조회
        List<ReviewImgDTO> reviewImgDTOList = reviewImgRepository.selectImgAll(reviewId);

        // 상품 조회
        ProductEntity productEntity = productRepository.findById(reviewEntity.getProductEntity().getProductId()).get();

        // 빌더로 DTO 생성
        ReviewDetailDTO.ReviewDetailDTOBuilder builder = ReviewDetailDTO.builder()
                .reviewId(reviewEntity.getReviewId())
                .userId(reviewEntity.getUserEntity().getUserId())
                .productId(reviewEntity.getProductEntity().getProductId())
                .score(reviewEntity.getScore())
                .comment(reviewEntity.getComment())
                .recommendCnt(reviewEntity.getRecommendCnt())
                .regDate(reviewEntity.getRegDate())
                .modDate(reviewEntity.getModDate())
                .isHidden(reviewEntity.getIsHidden())
                .isLiked(isLiked)
                .nickname(nickname)
                .tagList(tagList)
                .imageUrl(productEntity.getImgUrl())
                .name(productEntity.getName());

        if (reviewImgDTOList != null && !reviewImgDTOList.isEmpty()) {
            builder.images(reviewImgDTOList);
        }

        return builder.build();
    }

    // 등록 실패시 오류 메세지 수정 필요
    @Override
    public Long register(ReviewAddDTO reviewAddDTO) {
        // 유저 존재 확인
        UserEntity userEntity = userRepository.findById(reviewAddDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. userId: " + reviewAddDTO.getUserId()));

        // 상품 존재 확인
        ProductEntity productEntity = productRepository.findById(reviewAddDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. productId: " + reviewAddDTO.getProductId()));

        ReviewEntity reviewEntity = ReviewEntity.builder()
                .userEntity(userEntity)
                .productEntity(productEntity)
                .comment(reviewAddDTO.getComment())
                .score(reviewAddDTO.getScore())
                .recommendCnt(0)
                .isHidden(false)
                .build();

        // 리뷰 저장
        Long reviewId =  reviewRepository.save(reviewEntity).getReviewId();
        log.info("New review id: {}", reviewId);

        // 이미지 저장
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

        // 태그 저장
        if (reviewAddDTO.getTagIdList() != null && !reviewAddDTO.getTagIdList().isEmpty()) {
            List<ReviewTagEntity> reviewTagEntities = reviewAddDTO.getTagIdList().stream()
                    .map(tagId -> {
                        TagEntity tagEntity = tagRepository.findById(tagId)
                                .orElseThrow(() -> new IllegalArgumentException("No data found to get. tagID: " + tagId));

                        return ReviewTagEntity.builder()
                                .reviewEntity(reviewEntity)
                                .tagEntity(tagEntity)
                                .build();
                    })
                    .collect(Collectors.toList());

            reviewTagRepository.saveAll(reviewTagEntities);
            log.info("Saved reviewTags: {}", reviewTagEntities.size());
        }

        return reviewId;
    }

    // 수정 실패시 오류 메세지 수정 필요
    @Override
    public Long modify(Long userId, Long reviewId, ReviewModifyDTO reviewModifyDTO) {
        // 리뷰 존재 및 권한 확인
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + reviewId));

        if (!reviewEntity.getUserEntity().getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to modify this review.");
        }

        // 리뷰 수정
        String comment = reviewModifyDTO.getComment();
        Integer score = reviewModifyDTO.getScore();

        int result = reviewRepository.updateOne(reviewId, comment, score);

        if (result <= 0) {
            throw new IllegalArgumentException("Failed to modify. reviewId: " + reviewId);
        }

        log.info("Modified Review: id={} comment='{}' score={}", reviewId, comment, score);

        // 리뷰 이미지 수정 - 삭제
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

        // 리뷰 이미지 수정 - 추가
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

        // 태그 수정 - 삭제
        List<Long> deleteTagIds = reviewModifyDTO.getDeleteTagIds();

        if (deleteTagIds != null && !deleteTagIds.isEmpty()) {
            reviewTagRepository.deleteByReviewIdAndTagIds(reviewId, deleteTagIds);
            log.info("Deleted tags for review {}: {}", reviewId, deleteTagIds);
        }

        // 태그 수정 - 추가
        List<Long> newTagIds = reviewModifyDTO.getNewTagIds();

        if (newTagIds != null && !newTagIds.isEmpty()) {
            for (Long tagId : newTagIds) {
                ReviewTagEntity newRt = ReviewTagEntity.builder()
                        .reviewEntity(reviewEntity)
                        .tagEntity(TagEntity.builder().tagId(tagId).build())
                        .build();
                reviewTagRepository.save(newRt);
            }
            log.info("Added new tags for review {}: {}", reviewId, newTagIds);
        }

        return reviewId;
    }

    // 삭제 실패시 오류 메시지 수정 필요
    @Override
    public Long delete(Long userId, Long reviewId) {
        // 리뷰 존재 및 권한 확인
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + reviewId));

        if (!reviewEntity.getUserEntity().getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this review.");
        }

        // 리뷰 이미지 삭제
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

        // 리뷰 리포트 삭제
        int deletedReports = reviewReportRepository.deleteByReviewEntity_ReviewId(reviewId);
        log.info("삭제된 리포트 개수: {}", deletedReports);

        // 리뷰 좋아요 삭제
        int deletedLikes = reviewLikeRepository.deleteByReviewEntity_ReviewId(reviewId);
        log.info("삭제된 좋아요 개수: {}", deletedLikes);

        // 리뷰 태그 삭제
        int deletedTags = reviewTagRepository.deleteByReviewEntity_ReviewId(reviewId);
        log.info("삭제된 태그 개수: {}", deletedTags);

        reviewRepository.deleteById(reviewId);

        return reviewId;
    }
}