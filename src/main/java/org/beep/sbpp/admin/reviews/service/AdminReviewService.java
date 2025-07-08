package org.beep.sbpp.admin.reviews.service;

import org.beep.sbpp.admin.reviews.dto.AdminReviewDetailDTO;
import org.beep.sbpp.admin.reviews.dto.AdminReviewSimpleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 관리자용 리뷰 관리 서비스 인터페이스
 */
public interface AdminReviewService {

    /**
     * 리뷰 목록 조회 (필터, 페이지네이션, 다국어 상품명 지원)
     *
     * @param pageable 페이징 및 정렬 정보
     * @param category 필터 카테고리 ("reviewId", "productId", "productName", "userId", "all")
     * @param keyword  검색어
     * @param hidden   숨김 리뷰(TRUE)만 조회할지 여부
     * @param lang     상품명 검색/표시에 사용할 언어 코드 ("ko", "en", "ja")
     * @return AdminReviewSimpleDTO 페이지
     */
    Page<AdminReviewSimpleDTO> getReviewList(
            Pageable pageable,
            String category,
            String keyword,
            Boolean hidden,
            String lang
    );

    /**
     * 단일 리뷰 상세 조회
     *
     * @param reviewId 조회할 리뷰 ID
     * @param lang     상품명 표시용 언어 코드 ("ko", "en", "ja")
     * @return AdminReviewDetailDTO
     */
    AdminReviewDetailDTO getReviewDetail(Long reviewId, String lang);

    /**
     * 리뷰의 hidden 상태 토글
     *
     * @param reviewId 대상 리뷰 ID
     * @return 토글된 리뷰 ID
     */
    Long toggleHiddenStatus(Long reviewId);
}
