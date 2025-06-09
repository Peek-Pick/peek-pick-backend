package org.beep.sbpp.admin.reviews.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.admin.reviews.dto.AdminReviewDetailDTO;
import org.beep.sbpp.admin.reviews.dto.AdminReviewSimpleDTO;
import org.beep.sbpp.admin.reviews.repository.AdminReviewRepository;
import org.beep.sbpp.products.entities.ProductEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.reviews.dto.ReviewImgDTO;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.beep.sbpp.reviews.repository.*;
import org.beep.sbpp.tags.dto.TagDTO;
import org.beep.sbpp.users.entities.UserProfileEntity;
import org.beep.sbpp.users.repository.UserProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminReviewServiceImpl implements AdminReviewService {
    private final AdminReviewRepository adminReviewRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final ReviewTagRepository reviewTagRepository;
    private final UserProfileRepository userProfileRepository;
    private final ProductRepository productRepository;

    @Override
    public Page<AdminReviewSimpleDTO> getReviewList(Pageable pageable, String category, String keyword, Boolean hidden) {
        // pageable - regDate 기준 최신순 정렬, category, keyword - 필터링 기준
        Page<ReviewEntity> page =
                adminReviewRepository.findAllWithFilterAndSort(pageable, category, keyword, hidden);

        return page.map(review -> {
            // 상품 조회
            ProductEntity productEntity = productRepository.findById(review.getProductEntity().getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("No data found to get. productId: " +
                            review.getProductEntity().getProductId()));

            // 닉네임 조회
            UserProfileEntity userProfileEntity = userProfileRepository.findByUserId(review.getUserEntity().getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("No data found to get. userId: " + review.getUserEntity().getUserId()));

            // 빌더로 DTO 생성
            AdminReviewSimpleDTO.AdminReviewSimpleDTOBuilder builder = AdminReviewSimpleDTO.builder()
                    .reviewId(review.getReviewId())
                    .userId(review.getUserEntity().getUserId())
                    .productId(review.getProductEntity().getProductId())
                    .regDate(review.getRegDate())
                    .modDate(review.getModDate())
                    .name(productEntity.getName())
                    .nickname(userProfileEntity.getNickname());

            return builder.build();
        });
    }

    @Override
    public AdminReviewDetailDTO getReviewDetail(Long reviewId) {
        // 리뷰 존재 확인
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + reviewId));

        // 이미지 조회
        List<ReviewImgDTO> reviewImgDTOList = reviewImgRepository.selectImgAll(reviewEntity.getReviewId());

        // 닉네임 조회
        UserProfileEntity userProfileEntity = userProfileRepository.findByUserId(reviewEntity.getUserEntity().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. userId: " + reviewEntity.getUserEntity().getUserId()));

        // 태그 조회
        List<TagDTO> tagList = reviewTagRepository.findAllTagsByReviewId(reviewEntity.getReviewId());

        // 상품 조회
        ProductEntity productEntity = productRepository.findById(reviewEntity.getProductEntity().getProductId())
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. productId: " +
                        reviewEntity.getProductEntity().getProductId()));

        AdminReviewDetailDTO.AdminReviewDetailDTOBuilder builder = AdminReviewDetailDTO.builder()
                .reviewId(reviewEntity.getReviewId())
                .userId(reviewEntity.getUserEntity().getUserId())
                .productId(reviewEntity.getProductEntity().getProductId())
                .score(reviewEntity.getScore())
                .comment(reviewEntity.getComment())
                .tagList(tagList)
                .regDate(reviewEntity.getRegDate())
                .modDate(reviewEntity.getModDate())
                .nickname(userProfileEntity.getNickname())
                .profileImageUrl(userProfileEntity.getProfileImgUrl())
                .recommendCnt(reviewEntity.getRecommendCnt())
                .reportCnt(reviewEntity.getReportCnt())
                .isHidden(reviewEntity.getIsHidden())
                .name(productEntity.getName());

        if (reviewImgDTOList != null && !reviewImgDTOList.isEmpty()) {
            builder.images(reviewImgDTOList);
        }

        return builder.build();
    }

    @Override
    public Long toggleHiddenStatus(Long reviewId) {
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Failed to toggle isHidden. reviewId: " + reviewId));

        // isHidden 토글
        int result = adminReviewRepository.toggleIsHidden(reviewId, !reviewEntity.getIsHidden());

        // isHidden 토글 실패
        if (result <= 0) {
            throw new IllegalArgumentException("Failed to toggle isHidden. reviewId: " + reviewId);
        }

        return reviewId;
    }
}