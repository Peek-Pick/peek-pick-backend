package org.beep.sbpp.reviews.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.beep.sbpp.points.enums.PointLogsDesc;
import org.beep.sbpp.points.service.PointService;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.ProductLangEntity;
import org.beep.sbpp.products.entities.ProductTagEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.products.repository.ProductKoRepository;
import org.beep.sbpp.products.repository.ProductEnRepository;
import org.beep.sbpp.products.repository.ProductJaRepository;
import org.beep.sbpp.products.repository.ProductTagRepository;
import org.beep.sbpp.reviews.dto.*;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.beep.sbpp.reviews.entities.ReviewImgEntity;
import org.beep.sbpp.reviews.entities.ReviewTagEntity;
import org.beep.sbpp.reviews.repository.ReviewImgRepository;
import org.beep.sbpp.reviews.repository.ReviewLikeRepository;
import org.beep.sbpp.reviews.repository.ReviewReportRepository;
import org.beep.sbpp.reviews.repository.ReviewRepository;
import org.beep.sbpp.reviews.repository.ReviewTagRepository;
import org.beep.sbpp.reviews.util.UnauthorizedAccessException;
import org.beep.sbpp.summary.repository.ReviewSentimentRepository;
import org.beep.sbpp.tags.dto.TagDTO;
import org.beep.sbpp.tags.entities.TagEntity;
import org.beep.sbpp.tags.repository.TagRepository;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.entities.UserProfileEntity;
import org.beep.sbpp.users.repository.UserProfileRepository;
import org.beep.sbpp.users.repository.UserRepository;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * 상품 리뷰 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository            reviewRepository;
    private final ReviewImgRepository         reviewImgRepository;
    private final ReviewLikeRepository        reviewLikeRepository;
    private final ReviewReportRepository      reviewReportRepository;
    private final ReviewTagRepository         reviewTagRepository;
    private final ReviewSentimentRepository   reviewSentimentRepository;
    private final UserRepository              userRepository;
    private final UserProfileRepository       userProfileRepository;
    private final TagRepository               tagRepository;

    // Base + Lang 구조로 변경된 상품 저장소
    private final ProductRepository           productRepository;    // 리턴 타입: ProductBaseEntity
    private final ProductKoRepository         koRepository;
    private final ProductEnRepository         enRepository;
    private final ProductJaRepository         jaRepository;
    private final ProductTagRepository        productTagRepository;

    private final PointService                pointService;
    private final UserInfoUtil                userInfoUtil;

    @Value("${nginx.root-dir}")
    private String nginxRootDir;

    File reviewsDir = new File(nginxRootDir, "reviews");

    /**
     * 사용자별 리뷰 개수 조회
     */
    @Override
    public Long countReviewsByUserId(Long userId) {
        return reviewRepository.countReviewsByUserId(userId);
    }

    /**
     * 상품별 리뷰 개수 조회
     */
    @Override
    public Long countReviewsByProductId(Long productId) {
        return reviewRepository.countReviewsByProductId(productId);
    }

    /**
     * 사용자 리뷰 목록 조회
     * @param userId   유저 아이디
     * @param pageable 페이징 정보
     * @param lang     언어 코드 ("ko","en","ja")
     */
    @Override
    public Page<ReviewSimpleDTO> getUserReviews(
            Long userId,
            Pageable pageable,
            String lang
    ) {
        // 유저 존재 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. userId: " + userId));

        // 리뷰 페이징 조회
        Page<ReviewEntity> page = reviewRepository.findByUserId(userId, pageable);

        // DTO 변환
        return page.map(review -> {
            // 이미지 조회
            List<ReviewImgDTO> imgs = reviewImgRepository.selectImgAll(review.getReviewId());

            // BaseEntity 직접 참조
            ProductBaseEntity base = review.getProductBaseEntity();

            // 언어별 Entity 로드
            ProductLangEntity langEntity = loadLangEntity(base.getProductId(), lang);

            // DTO 빌더
            ReviewSimpleDTO.ReviewSimpleDTOBuilder b = ReviewSimpleDTO.builder()
                    .reviewId(review.getReviewId())
                    .userId(review.getUserEntity().getUserId())
                    .productId(base.getProductId())
                    .score(review.getScore())
                    .recommendCnt(review.getRecommendCnt())
                    .comment(review.getComment())
                    .regDate(review.getRegDate())
                    .modDate(review.getModDate())
                    .imageThumbUrl(base.getImgThumbUrl())  // 공통
                    .name(langEntity.getName());           // 언어별

            if (!imgs.isEmpty()) {
                b.image(imgs.get(0));
            }

            return b.build();
        });
    }

    /**
     * 상품별 리뷰 목록 조회
     * @param productId 상품 아이디
     * @param userId    조회 사용자 아이디 (좋아요 여부 판단)
     * @param pageable  페이징 정보
     * @param lang      언어 코드
     */
    @Override
    public Page<ReviewDetailDTO> getProductReviews(
            Long productId,
            Long userId,
            Pageable pageable,
            String lang
    ) {
        // 상품 존재 확인
        productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. productId: " + productId));

        // 리뷰 페이징 조회
        Page<ReviewEntity> page = reviewRepository.findByProductId(productId, pageable);

        // DTO 변환
        return page.map(review -> buildReviewDetailDTO(review, userId, lang));
    }

    /**
     * 단일 리뷰 상세 조회
     * @param reviewId 리뷰 아이디
     * @param userId   조회 사용자 아이디
     * @param lang     언어 코드
     */
    @Override
    public ReviewDetailDTO getOneDetail(
            Long reviewId,
            Long userId,
            String lang
    ) {
        // 리뷰 존재 확인
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + reviewId));

        return buildReviewDetailDTO(review, userId, lang);
    }

// ——————————————————————————————————————————————
// 이하 리뷰 등록·수정·삭제 메서드 (lang 파라미터 불필요)
    /**
     * 리뷰 등록
     */
    @Override
    public Long register(ReviewAddDTO dto) {
        // comment 체크
        if (dto.getComment() == null || dto.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("No comment provided. Please write your review.");
        }

        // 유저 조회
        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. userId: " + dto.getUserId()));

        // BaseEntity 조회
        ProductBaseEntity base = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. productId: " + dto.getProductId()));

        // ReviewEntity 생성 (productBaseEntity 필드 사용)
        ReviewEntity review = ReviewEntity.builder()
                .userEntity(user)
                .productBaseEntity(base)    // 수정된 필드명
                .comment(dto.getComment())
                .score(dto.getScore())
                .recommendCnt(0)
                .reportCnt(0)
                .isHidden(false)
                .build();

        Long reviewId = reviewRepository.save(review).getReviewId();
        log.info("New review id: {}", reviewId);

        // 이미지 저장
        if (dto.getFiles() != null) {
            saveReviewImages(dto.getFiles(), review);
        }

        // 태그 저장 및 카운트 업데이트
        if (dto.getTagIdList() != null) {
            for (Long tagId : dto.getTagIdList()) {
                TagEntity tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new IllegalArgumentException("No data found to get. tagId: " + tagId));
                reviewTagRepository.save(ReviewTagEntity.builder()
                        .reviewEntity(review)
                        .tagEntity(tag)
                        .build());
                incrementProductTagCount(base, tag);
            }
        }

        // 대표 태그 갱신
        List<String> top = productTagRepository.findTopTagByProductId(
                base.getProductId(), PageRequest.of(0,1));
        base.setMainTag(top.isEmpty() ? null : top.get(0));

        // 통계 업데이트
        updateProductReviewStats(base, +1);

        // 포인트 지급
        int earned = (dto.getFiles() != null && dto.getFiles().length>0) ? 50 : 10;
        PointLogsDesc desc = (earned==50)
                ? PointLogsDesc.REVIEW_PHOTO : PointLogsDesc.REVIEW_GENERAL;
        pointService.earnPoints(user.getUserId(), earned, desc);

        return reviewId;
    }

    /**
     * 리뷰 수정
     */
    @Override
    public Long modify(Long userId, Long reviewId, ReviewModifyDTO dto) {
        // comment 체크
        if (dto.getComment() == null || dto.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("No comment provided. Please write your review.");
        }

        // 리뷰 조회 및 권한 확인
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + reviewId));
        if (!review.getUserEntity().getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to modify this review.");
        }

        // update 반영
        reviewRepository.updateOne(reviewId, dto.getComment(), dto.getScore());
        log.info("Modified Review: id={} comment='{}' score={}",
                reviewId, dto.getComment(), dto.getScore());

        // 이미지 삭제/추가
        if (dto.getDeleteImgIds() != null) {
            List<ReviewImgEntity> toDel = reviewImgRepository.findAllById(dto.getDeleteImgIds());
            deleteReviewImages(toDel);
        }
        if (dto.getFiles() != null) {
            saveReviewImages(dto.getFiles(), review);
        }

        // 태그 삭제
        if (dto.getDeleteTagIds() != null) {
            for (Long tagId : dto.getDeleteTagIds()) {
                TagEntity tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new IllegalArgumentException("No data found to get. tagId: " + tagId));
                decrementProductTagCount(review.getProductBaseEntity(), tag);
            }
            reviewTagRepository.deleteByReviewIdAndTagIds(reviewId, dto.getDeleteTagIds());
        }
        // 태그 추가
        if (dto.getNewTagIds() != null) {
            for (Long tagId : dto.getNewTagIds()) {
                TagEntity tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new IllegalArgumentException("No data found to get. tagId: " + tagId));
                reviewTagRepository.save(ReviewTagEntity.builder()
                        .reviewEntity(review)
                        .tagEntity(tag)
                        .build());
                incrementProductTagCount(review.getProductBaseEntity(), tag);
            }
        }

        // 대표 태그 & 통계 갱신
        ProductBaseEntity base = review.getProductBaseEntity();
        List<String> top = productTagRepository.findTopTagByProductId(
                base.getProductId(), PageRequest.of(0,1));
        base.setMainTag(top.isEmpty() ? null : top.get(0));
        updateProductReviewStats(base, 0);

        return reviewId;
    }

    /**
     * 리뷰 삭제
     */
    @Override
    public Long delete(Long userId, Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + reviewId));
        if (userId != -1L && !review.getUserEntity().getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this review.");
        }

        ProductBaseEntity base = review.getProductBaseEntity();

        // 이미지, 리포트, 좋아요, 태그, 센티먼트 삭제
        deleteReviewImages(reviewImgRepository.findAllByReviewEntity_ReviewId(reviewId));
        reviewReportRepository.deleteByReviewEntity_ReviewId(reviewId);
        reviewLikeRepository.deleteByReviewEntity_ReviewId(reviewId);
        List<ReviewTagEntity> tags = reviewTagRepository.findAllByReviewEntity_ReviewId(reviewId);
        for (ReviewTagEntity rt : tags) {
            decrementProductTagCount(base, rt.getTagEntity());
        }
        reviewTagRepository.deleteAll(tags);
        reviewSentimentRepository.deleteByReviewId(reviewId);
        reviewRepository.deleteById(reviewId);

        // 대표 태그 & 통계 갱신
        List<String> top = productTagRepository.findTopTagByProductId(
                base.getProductId(), PageRequest.of(0,1));
        base.setMainTag(top.isEmpty() ? null : top.get(0));
        updateProductReviewStats(base, -1);

        return reviewId;
    }

    // ========================================
    // ReviewDetailDTO 생성 헬퍼
    private ReviewDetailDTO buildReviewDetailDTO(
            ReviewEntity review, Long userId, String lang
    ) {
        // 이미지, 좋아요, 프로필, 태그 조회
        List<ReviewImgDTO> imgs = reviewImgRepository.selectImgAll(review.getReviewId());
        boolean isLiked = reviewLikeRepository.hasUserLikedReview(review.getReviewId(), userId);
        UserProfileEntity profile = userProfileRepository.findByUserId(review.getUserEntity().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. userProfileId: " +
                        review.getUserEntity().getUserId()));
        List<TagDTO> tagList = reviewTagRepository.findAllTagsByReviewId(review.getReviewId());

        // Base + LangEntity 로드
        ProductBaseEntity base = review.getProductBaseEntity();
        ProductLangEntity langEntity = loadLangEntity(base.getProductId(), lang);

        // DTO 빌드
        ReviewDetailDTO.ReviewDetailDTOBuilder b = ReviewDetailDTO.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUserEntity().getUserId())
                .productId(base.getProductId())
                .score(review.getScore())
                .comment(review.getComment())
                .recommendCnt(review.getRecommendCnt())
                .regDate(review.getRegDate())
                .modDate(review.getModDate())
                .isHidden(review.getIsHidden())
                .isLiked(isLiked)
                .nickname(profile.getNickname())
                .profileImageUrl(profile.getProfileImgUrl())
                .tagList(tagList)
                .imageUrl(base.getImgUrl())         // 공통
                .name(langEntity.getName());        // 언어별

        if (!imgs.isEmpty()) {
            b.images(imgs);
        }
        return b.build();
    }

    // ========================================
    // 이미지 저장 헬퍼
    private void saveReviewImages(MultipartFile[] files, ReviewEntity review) {
        for (MultipartFile f : files) {
            String uuid = UUID.randomUUID().toString();
            String orig = f.getOriginalFilename();

            if (orig == null) continue;

            String saved = uuid + "_" + orig;
            String thumb = "s_" + saved;

            File tgt = new File(reviewsDir + saved);
            File thf = new File(reviewsDir + thumb);

            try {
                f.transferTo(tgt);
                Thumbnails.of(tgt).size(200,200).toFile(thf);
                ReviewImgEntity img = ReviewImgEntity.builder()
                        .reviewEntity(review)
                        .imgUrl(saved)
                        .build();
                reviewImgRepository.save(img);
            } catch (Exception e) {
                log.warn("Failed to save review image: {}", e.getMessage());
            }
        }
    }

    // ========================================
    // 이미지 삭제 헬퍼
    private void deleteReviewImages(List<ReviewImgEntity> imgs) {
        for (ReviewImgEntity img : imgs) {
            String url = img.getImgUrl();

            File orig = new File(reviewsDir + url);
            File thm  = new File(reviewsDir + url);

            if (orig.exists() && !orig.delete()) log.warn("Failed to delete {}", orig);
            if (thm.exists()  && !thm.delete())  log.warn("Failed to delete {}", thm);

            reviewImgRepository.delete(img);
        }
    }

    // ========================================
    // 통계 업데이트 헬퍼
    private void updateProductReviewStats(ProductBaseEntity base, int delta) {
        int curr = Optional.ofNullable(base.getReviewCount()).orElse(0);
        base.setReviewCount(curr + delta);
        BigDecimal avg = reviewRepository.calculateAverageScoreByProduct(base.getProductId());
        base.setScore(avg);
    }

    // ========================================
    // 태그 카운트 증감 헬퍼
    private void incrementProductTagCount(ProductBaseEntity base, TagEntity tag) {
        productTagRepository.findByProductBaseEntityAndTagEntity(base, tag).ifPresentOrElse(pte -> {
            pte.setTagCount(pte.getTagCount() + 1);
            productTagRepository.save(pte);
        }, () -> {
            productTagRepository.save(ProductTagEntity.builder()
                    .productBaseEntity(base)
                    .tagEntity(tag)
                    .tagCount(1)
                    .build());
        });
    }

    private void decrementProductTagCount(ProductBaseEntity base, TagEntity tag) {
        productTagRepository.findByProductBaseEntityAndTagEntity(base, tag).ifPresent(pte -> {
            pte.setTagCount(pte.getTagCount() - 1);
            productTagRepository.save(pte);
        });
    }

    // ========================================
    // 언어별 엔티티 로드 헬퍼 (프론트에서 받은 lang 코드 사용)
    private ProductLangEntity loadLangEntity(Long productId, String lang) {
        return switch(lang.toLowerCase().split("[-_]")[0]) {
            case "ko" -> koRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("한국어 데이터 없음: " + productId));
            case "en" -> enRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("영어 데이터 없음: " + productId));
            case "ja" -> jaRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("일본어 데이터 없음: " + productId));
            default   -> throw new IllegalArgumentException("지원하지 않는 언어: " + lang);
        };
    }
}