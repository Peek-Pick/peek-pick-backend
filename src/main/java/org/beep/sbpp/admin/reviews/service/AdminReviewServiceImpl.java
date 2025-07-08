package org.beep.sbpp.admin.reviews.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.admin.reviews.dto.AdminReviewDetailDTO;
import org.beep.sbpp.admin.reviews.dto.AdminReviewSimpleDTO;
import org.beep.sbpp.admin.reviews.repository.AdminReviewRepository;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.ProductLangEntity;
import org.beep.sbpp.products.entities.ProductKoEntity;
import org.beep.sbpp.products.entities.ProductEnEntity;
import org.beep.sbpp.products.entities.ProductJaEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.products.repository.ProductKoRepository;
import org.beep.sbpp.products.repository.ProductEnRepository;
import org.beep.sbpp.products.repository.ProductJaRepository;
import org.beep.sbpp.reviews.dto.ReviewImgDTO;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.beep.sbpp.reviews.repository.ReviewImgRepository;
import org.beep.sbpp.reviews.repository.ReviewRepository;
import org.beep.sbpp.reviews.repository.ReviewTagRepository;
import org.beep.sbpp.tags.dto.TagDTO;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.entities.UserProfileEntity;
import org.beep.sbpp.users.repository.UserProfileRepository;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminReviewServiceImpl implements AdminReviewService {

    private final AdminReviewRepository   adminReviewRepository;
    private final ReviewRepository        reviewRepository;
    private final ReviewImgRepository     reviewImgRepository;
    private final ReviewTagRepository     reviewTagRepository;
    private final UserProfileRepository   userProfileRepository;
    private final UserRepository          userRepository;
    private final ProductRepository       productRepository;
    private final ProductKoRepository     koRepository;
    private final ProductEnRepository     enRepository;
    private final ProductJaRepository     jaRepository;

    /**
     * 리뷰 목록 조회 (필터+페이징+다국어 상품명)
     */
    @Override
    public Page<AdminReviewSimpleDTO> getReviewList(
            Pageable pageable,
            String category,
            String keyword,
            Boolean hidden,
            String lang
    ) {
        // 다국어 support 추가된 repository 호출
        Page<ReviewEntity> page = adminReviewRepository
                .findAllWithFilterAndSort(pageable, category, keyword, hidden, lang);

        return page.map(review -> {
            // BaseEntity 로드
            ProductBaseEntity base = review.getProductBaseEntity();
            // LangEntity 로드
            ProductLangEntity langE = loadLangEntity(base, lang);
            // UserProfile 로드
            UserProfileEntity profile = userProfileRepository
                    .findByUserId(review.getUserEntity().getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("No data found for userId: "
                            + review.getUserEntity().getUserId()));

            return AdminReviewSimpleDTO.builder()
                    .reviewId(review.getReviewId())
                    .userId(review.getUserEntity().getUserId())
                    .productId(base.getProductId())
                    .regDate(review.getRegDate())
                    .modDate(review.getModDate())
                    .name(langE.getName())
                    .nickname(profile.getNickname())
                    .build();
        });
    }

    /**
     * 리뷰 상세 조회 (상품명 다국어, 이미지, 태그 등)
     */
    @Override
    public AdminReviewDetailDTO getReviewDetail(Long reviewId, String lang) {
        // 리뷰 조회
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("No data found for reviewId: " + reviewId));
        // User & Profile
        UserEntity user = userRepository.findById(review.getUserEntity().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No data found for userId: " + review.getUserEntity().getUserId()));
        UserProfileEntity profile = userProfileRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No profile for userId: " + user.getUserId()));
        // Base & Lang
        ProductBaseEntity base = review.getProductBaseEntity();
        ProductLangEntity langE = loadLangEntity(base, lang);
        // 이미지 & 태그
        List<ReviewImgDTO> imgs = reviewImgRepository.selectImgAll(review.getReviewId());
        List<TagDTO> tags = reviewTagRepository.findAllTagsByReviewId(review.getReviewId());

        AdminReviewDetailDTO.AdminReviewDetailDTOBuilder b = AdminReviewDetailDTO.builder()
                .reviewId(review.getReviewId())
                .userId(user.getUserId())
                .productId(base.getProductId())
                .score(review.getScore())
                .comment(review.getComment())
                .tagList(tags)
                .regDate(review.getRegDate())
                .modDate(review.getModDate())
                .nickname(profile.getNickname())
                .profileImageUrl(profile.getProfileImgUrl())
                .recommendCnt(review.getRecommendCnt())
                .reportCnt(review.getReportCnt())
                .isHidden(review.getIsHidden())
                .name(langE.getName())
                .email(user.getEmail());

        if (!imgs.isEmpty()) {
            b.images(imgs);
        }

        return b.build();
    }

    /**
     * isHidden 상태 토글
     */
    @Override
    public Long toggleHiddenStatus(Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("No data found for reviewId: " + reviewId));

        int updated = adminReviewRepository.toggleIsHidden(reviewId, !review.getIsHidden());
        if (updated <= 0) {
            throw new IllegalArgumentException("Failed to toggle hidden for reviewId: " + reviewId);
        }
        return reviewId;
    }

    // ————————————— Helpers —————————————

    private ProductLangEntity loadLangEntity(ProductBaseEntity base, String lang) {
        return switch(lang.toLowerCase().split("[-_]")[0]) {
            case "ko" -> koRepository.findById(base.getProductId())
                    .orElseThrow(() -> new RuntimeException("No KO data: " + base.getProductId()));
            case "en" -> enRepository.findById(base.getProductId())
                    .orElseThrow(() -> new RuntimeException("No EN data: " + base.getProductId()));
            case "ja" -> jaRepository.findById(base.getProductId())
                    .orElseThrow(() -> new RuntimeException("No JA data: " + base.getProductId()));
            default   -> throw new IllegalArgumentException("Unsupported lang: " + lang);
        };
    }
}
