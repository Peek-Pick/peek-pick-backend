package org.beep.sbpp.reviews.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.beep.sbpp.points.enums.PointLogsDesc;
import org.beep.sbpp.points.service.PointService;
import org.beep.sbpp.products.entities.ProductEntity;
import org.beep.sbpp.products.entities.ProductTagEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.products.repository.ProductTagRepository;
import org.beep.sbpp.reviews.dto.*;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.beep.sbpp.reviews.entities.ReviewImgEntity;
import org.beep.sbpp.reviews.entities.ReviewTagEntity;
import org.beep.sbpp.reviews.repository.*;
import org.beep.sbpp.reviews.util.UnauthorizedAccessException;
import org.beep.sbpp.summary.repository.ReviewSentimentRepository;
import org.beep.sbpp.tags.dto.TagDTO;
import org.beep.sbpp.tags.entities.TagEntity;
import org.beep.sbpp.tags.repository.TagRepository;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.entities.UserProfileEntity;
import org.beep.sbpp.users.repository.UserProfileRepository;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
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
    private final ReviewReportRepository reviewReportRepository;
    private final ReviewTagRepository reviewTagRepository;
    private final ReviewSentimentRepository reviewSentimentRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final TagRepository tagRepository;
    private final ProductRepository productRepository;
    private final ProductTagRepository productTagRepository;
    private final PointService pointService;

    public Long countReviewsByUserId(Long userId) {
        return reviewRepository.countReviewsByUserId(userId);
    }

    public Long countReviewsByProductId(Long productId) {
        return reviewRepository.countReviewsByProductId(productId);
    }

    @Override
    public Page<ReviewSimpleDTO> getUserReviews(Long userId, Pageable pageable) {
        // 유저 존재 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. userId: " + userId));

        // 유저별 리뷰 페이지 조회
        Page<ReviewEntity> reviewPage = reviewRepository.findByUserId(userId, pageable);

        return reviewPage.map(review -> {
            // 이미지 조회
            List<ReviewImgDTO> reviewImgDTOList = reviewImgRepository.selectImgAll(review.getReviewId());

            // 상품 조회
            ProductEntity productEntity = productRepository.findById(review.getProductEntity().getProductId()).get();

            // 빌더로 DTO 생성
            ReviewSimpleDTO.ReviewSimpleDTOBuilder builder = ReviewSimpleDTO.builder()
                    .reviewId(review.getReviewId())
                    .userId(review.getUserEntity().getUserId())
                    .productId(review.getProductEntity().getProductId())
                    .score(review.getScore())
                    .recommendCnt(review.getRecommendCnt())
                    .comment(review.getComment())
                    .regDate(review.getRegDate())
                    .modDate(review.getModDate())
                    .imageThumbUrl(productEntity.getImgThumbUrl())
                    .name(productEntity.getName());

            if (reviewImgDTOList != null && !reviewImgDTOList.isEmpty()) {
                builder.image(reviewImgDTOList.get(0));
            }

            return builder.build();
        });
    }

    @Override
    public Page<ReviewDetailDTO> getProductReviews(Long productId, Long userId, Pageable pageable) {
        // 상품 존재 확인
        productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. productId: " + productId));

        // 상품별 리뷰 페이지 조회
        Page<ReviewEntity> reviewPage = reviewRepository.findByProductId(productId, pageable);

        return reviewPage.map(review -> buildReviewDetailDTO(review, userId));
    }

    @Override
    public ReviewDetailDTO getOneDetail(Long reviewId, Long userId) {
        // 리뷰 존재 확인
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + reviewId));

        return buildReviewDetailDTO(reviewEntity, userId);
    }

    @Override
    public Long register(ReviewAddDTO reviewAddDTO) {
        // comment 존재 확인
        if (reviewAddDTO.getComment() == null || reviewAddDTO.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("No comment provided. Please write your review.");
        }

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
                .reportCnt(0)
                .isHidden(false)
                .build();

        // 리뷰 저장
        Long reviewId =  reviewRepository.save(reviewEntity).getReviewId();
        log.info("New review id: {}", reviewId);

        // 이미지 저장 부분
        if (reviewAddDTO.getFiles() != null) {
            saveReviewImages(reviewAddDTO.getFiles(), reviewEntity);
        }

        // 태그 저장
        if (reviewAddDTO.getTagIdList() != null && !reviewAddDTO.getTagIdList().isEmpty()) {
            List<ReviewTagEntity> reviewTagEntities = new ArrayList<>();

            for (Long tagId : reviewAddDTO.getTagIdList()) {
                TagEntity tagEntity = tagRepository.findById(tagId)
                        .orElseThrow(() -> new IllegalArgumentException("No data found to get. tagID: " + tagId));

                // ReviewTagEntity 생성
                ReviewTagEntity reviewTagEntity = ReviewTagEntity.builder()
                        .reviewEntity(reviewEntity)
                        .tagEntity(tagEntity)
                        .build();

                reviewTagEntities.add(reviewTagEntity);

                // ProductTagEntity 처리
                incrementProductTagCount(productEntity, tagEntity);
            }

            reviewTagRepository.saveAll(reviewTagEntities);
        }

        // 상품 대표 태그 갱신
        List<String> topTags = productTagRepository.findTopTagByProductId(productEntity.getProductId(),
                PageRequest.of(0, 1));

        String topTag = topTags.isEmpty() ? null : topTags.get(0);
        productEntity.setMainTag(topTag);

        // 저장 이후에 평점/리뷰수 갱신 (delta +1 적용)
        updateProductReviewStats(productEntity, +1);

        // 포인트 지급 (일반리뷰 작성: 10p, 포토리뷰 작성: 50p)
        int earned = (reviewAddDTO.getFiles() != null) ? 50 : 10;
        PointLogsDesc desc = (earned == 50) ? PointLogsDesc.REVIEW_PHOTO : PointLogsDesc.REVIEW_GENERAL;

        pointService.earnPoints(userEntity.getUserId(), earned, desc);

        return reviewId;
    }

    @Override
    public Long modify(Long userId, Long reviewId, ReviewModifyDTO reviewModifyDTO) {
        // comment 존재 확인
        if (reviewModifyDTO.getComment() == null || reviewModifyDTO.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("No comment provided. Please write your review.");
        }

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

        // 상품 존재 확인
        ProductEntity productEntity = productRepository.findById(reviewEntity.getProductEntity().getProductId())
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. productId: " + reviewEntity.getProductEntity().getProductId()));

        // 리뷰 이미지 수정 - 삭제
        List<Long> deleteImgIds = reviewModifyDTO.getDeleteImgIds();
        if (deleteImgIds != null && !deleteImgIds.isEmpty()) {
            List<ReviewImgEntity> imgEntitiesToDelete = reviewImgRepository.findAllById(deleteImgIds);
            deleteReviewImages(imgEntitiesToDelete);
        }

        // 리뷰 이미지 수정 - 추가
        MultipartFile[] newFiles= reviewModifyDTO.getFiles();
        if (newFiles != null && newFiles.length > 0) {
            saveReviewImages(newFiles, reviewEntity);
        }

        // 태그 수정 - 삭제
        List<Long> deleteTagIds = reviewModifyDTO.getDeleteTagIds();

        if (deleteTagIds != null && !deleteTagIds.isEmpty()) {
            // 상품 태그 개수 변경
            for (Long tagId : deleteTagIds) {
                TagEntity tagEntity = tagRepository.findById(tagId)
                        .orElseThrow(() -> new IllegalArgumentException("No data found to get. tagId: " + tagId));

                decrementProductTagCount(productEntity, tagEntity);
            }

            reviewTagRepository.deleteByReviewIdAndTagIds(reviewId, deleteTagIds);
            log.info("Deleted tags for review {}: {}", reviewId, deleteTagIds);
        }

        // 태그 수정 - 추가
        List<Long> newTagIds = reviewModifyDTO.getNewTagIds();

        if (newTagIds != null && !newTagIds.isEmpty()) {
            for (Long tagId : newTagIds) {
                // 리뷰에 태그 추가
                ReviewTagEntity newRt = ReviewTagEntity.builder()
                        .reviewEntity(reviewEntity)
                        .tagEntity(TagEntity.builder().tagId(tagId).build())
                        .build();
                reviewTagRepository.save(newRt);

                // 상품 태그 개수 변경
                TagEntity tagEntity = tagRepository.findById(tagId)
                        .orElseThrow(() -> new IllegalArgumentException("No data found to get. tagId: " + tagId));

                incrementProductTagCount(productEntity, tagEntity);
            }
            log.info("Added new tags for review {}: {}", reviewId, newTagIds);
        }

        // 상품 대표 태그 갱신
        List<String> topTags = productTagRepository.findTopTagByProductId(productEntity.getProductId(),
                PageRequest.of(0, 1));

        String topTag = topTags.isEmpty() ? null : topTags.get(0);
        productEntity.setMainTag(topTag);

        // 수정 이후에 평점 갱신 (delta 0 적용)
        updateProductReviewStats(productEntity, 0);

        return reviewId;
    }

    @Override
    public Long delete(Long userId, Long reviewId) {
        // 리뷰 존재 및 권한 확인
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + reviewId));

        // 실제 어드민 테이블 생기면 수정 필요
        if (userId != -1L) {
            if (!reviewEntity.getUserEntity().getUserId().equals(userId)) {
                throw new UnauthorizedAccessException("You are not authorized to delete this review.");
            }
        }

        // 상품 존재 확인
        ProductEntity productEntity = productRepository.findById(reviewEntity.getProductEntity().getProductId())
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. productId: " + reviewEntity.getProductEntity().getProductId()));

        // 리뷰 이미지 삭제
        List<ReviewImgEntity> reviewImgEntities = reviewImgRepository.findAllByReviewEntity_ReviewId(reviewId);
        if (!reviewImgEntities.isEmpty()) {
            deleteReviewImages(reviewImgEntities);
        }

        // 리뷰 리포트 삭제
        int deletedReports = reviewReportRepository.deleteByReviewEntity_ReviewId(reviewId);
        log.info("삭제된 리포트 개수: {}", deletedReports);

        // 리뷰 좋아요 삭제
        int deletedLikes = reviewLikeRepository.deleteByReviewEntity_ReviewId(reviewId);
        log.info("삭제된 좋아요 개수: {}", deletedLikes);

        // 리뷰 태그 조회
        List<ReviewTagEntity> reviewTagEntities = reviewTagRepository.findAllByReviewEntity_ReviewId(reviewId);

        // 태그별 productTag count 감소
        for (ReviewTagEntity reviewTag : reviewTagEntities) {
            TagEntity tagEntity = reviewTag.getTagEntity();
            decrementProductTagCount(productEntity, tagEntity);
        }

        // 리뷰 태그 삭제
        reviewTagRepository.deleteAll(reviewTagEntities);
        log.info("삭제된 태그 개수: {}", reviewTagEntities.size());

        // 리뷰 센티먼트 삭제
        reviewSentimentRepository.deleteByReviewId(reviewId);

        // 리뷰 삭제
        reviewRepository.deleteById(reviewId);

        // 삭제 이후에 평점 갱신 (delta -1 적용)
        updateProductReviewStats(productEntity, -1);

        // 상품 대표 태그 갱신
        List<String> topTags = productTagRepository.findTopTagByProductId(productEntity.getProductId(),
                PageRequest.of(0, 1));

        String topTag = topTags.isEmpty() ? null : topTags.get(0);
        productEntity.setMainTag(topTag);

        return reviewId;
    }

    // ReviewEntity와 userId를 기반으로 ReviewDetailDTO를 생성
    private ReviewDetailDTO buildReviewDetailDTO(ReviewEntity review, Long userId) {
        // 이미지 조회
        List<ReviewImgDTO> reviewImgDTOList = reviewImgRepository.selectImgAll(review.getReviewId());

        // 좋아요 여부 조회
        boolean isLiked = reviewLikeRepository.hasUserLikedReview(review.getReviewId(), userId);

        // 유저 프로필 조회
        UserProfileEntity userProfileEntity = userProfileRepository.findByUserId(review.getUserEntity().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. userProfileId: " + review.getUserEntity().getUserId()));

        // 태그 조회
        List<TagDTO> tagList = reviewTagRepository.findAllTagsByReviewId(review.getReviewId());

        // 상품 조회
        ProductEntity productEntity = productRepository.findById(review.getProductEntity().getProductId())
                .orElseThrow(() ->
                        new IllegalArgumentException("No data found to get. productId: " +
                                review.getProductEntity().getProductId()));

        // DTO 빌더
        ReviewDetailDTO.ReviewDetailDTOBuilder builder = ReviewDetailDTO.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUserEntity().getUserId())
                .productId(productEntity.getProductId())
                .score(review.getScore())
                .comment(review.getComment())
                .recommendCnt(review.getRecommendCnt())
                .regDate(review.getRegDate())
                .modDate(review.getModDate())
                .isHidden(review.getIsHidden())
                .isLiked(isLiked)
                .nickname(userProfileEntity.getNickname())
                .profileImageUrl(userProfileEntity.getProfileImgUrl())
                .tagList(tagList)
                .imageUrl(productEntity.getImgUrl())
                .name(productEntity.getName());

        if (reviewImgDTOList != null && !reviewImgDTOList.isEmpty()) {
            builder.images(reviewImgDTOList);
        }

        return builder.build();
    }

    private void saveReviewImages(MultipartFile[] files, ReviewEntity review) {
        for (MultipartFile file : files) {
            String uuid = UUID.randomUUID().toString();
            String originalFileName = file.getOriginalFilename();

            if (originalFileName == null) continue;

            String saveFileName = uuid + "_" + originalFileName;
            String thumbFileName = "s_" + saveFileName;

            File target = new File("C:\\nginx-1.26.3\\html\\reviews\\" + saveFileName);
            File thumbFile = new File("C:\\nginx-1.26.3\\html\\reviews\\" + thumbFileName);

            try {
                // 원본 파일 저장
                file.transferTo(target);

                // 썸네일 생성 (200x200)
                Thumbnails.of(target)
                        .size(200, 200)
                        .toFile(thumbFile);

                // DB 저장
                ReviewImgEntity reviewImgEntity = ReviewImgEntity.builder()
                        .reviewEntity(review)
                        .imgUrl(saveFileName)
                        .build();

                reviewImgRepository.save(reviewImgEntity);
                log.info("Saved reviewImageFiles: {}", reviewImgEntity.getReviewImgId());
            } catch (Exception e) {
                log.warn("Failed to save review image: {}", e.getMessage());
            }
        }
    }

    private void deleteReviewImages(List<ReviewImgEntity> imgEntities) {
        for (ReviewImgEntity imgEntity : imgEntities) {
            String imgUrl = imgEntity.getImgUrl();
            File original = new File("C:\\nginx-1.26.3\\html\\reviews\\" + imgUrl);
            File thumbnail = new File("C:\\nginx-1.26.3\\html\\reviews\\" + "s_" + imgUrl);

            if (original.exists() && !original.delete()) {
                log.warn("Failed to delete original image file: {}", original.getAbsolutePath());
            }
            if (thumbnail.exists() && !thumbnail.delete()) {
                log.warn("Failed to delete thumbnail image file: {}", thumbnail.getAbsolutePath());
            }

            reviewImgRepository.delete(imgEntity);
            log.info("Deleted ReviewImg record and files: id={} url={}", imgEntity.getReviewImgId(), imgUrl);
        }
    }

    private void updateProductReviewStats(ProductEntity productEntity, int delta) {
        // 리뷰 개수 업데이트
        int currentCount = productEntity.getReviewCount() != null
                ? productEntity.getReviewCount() : 0;
        productEntity.setReviewCount(currentCount + delta);

        // 평균 평점 재계산
        BigDecimal avgScore = reviewRepository
                .calculateAverageScoreByProduct(productEntity.getProductId());
        productEntity.setScore(avgScore);
    }

    private void incrementProductTagCount(ProductEntity productEntity, TagEntity tagEntity) {
        Optional<ProductTagEntity> optionalProductTagEntity = productTagRepository
                .findByProductEntityAndTagEntity(productEntity, tagEntity);

        if (optionalProductTagEntity.isPresent()) {
            ProductTagEntity productTagEntity = optionalProductTagEntity.get();
            productTagEntity.setTagCount(productTagEntity.getTagCount() + 1);
            productTagRepository.save(productTagEntity);
        } else {
            ProductTagEntity newProductTagEntity = ProductTagEntity.builder()
                    .productEntity(productEntity)
                    .tagEntity(tagEntity)
                    .tagCount(1)
                    .build();
            productTagRepository.save(newProductTagEntity);
        }
    }

    private void decrementProductTagCount(ProductEntity productEntity, TagEntity tagEntity) {
        Optional<ProductTagEntity> optionalProductTagEntity = productTagRepository
                .findByProductEntityAndTagEntity(productEntity, tagEntity);

        if (optionalProductTagEntity.isPresent()) {
            ProductTagEntity productTagEntity = optionalProductTagEntity.get();
            int currentCount = productTagEntity.getTagCount();
            productTagEntity.setTagCount(currentCount - 1);
            productTagRepository.save(productTagEntity);
        }
    }
}