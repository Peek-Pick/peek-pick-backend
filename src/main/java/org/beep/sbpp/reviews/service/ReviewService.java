package org.beep.sbpp.reviews.service;

import org.beep.sbpp.reviews.dto.ReviewAddDTO;
import org.beep.sbpp.reviews.dto.ReviewDetailDTO;
import org.beep.sbpp.reviews.dto.ReviewModifyDTO;
import org.beep.sbpp.reviews.dto.ReviewSimpleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 리뷰 관련 주요 기능 정의
 */
public interface ReviewService {

    /**
     * 사용자별 리뷰 개수 조회
     */
    Long countReviewsByUserId(Long userId);

    /**
     * 상품별 리뷰 개수 조회
     */
    Long countReviewsByProductId(Long productId);

    /**
     * 사용자 리뷰 목록 조회
     * @param userId   유저 아이디
     * @param pageable 페이징 정보
     * @param lang     언어 코드 ("ko","en","ja")
     */
    Page<ReviewSimpleDTO> getUserReviews(Long userId, Pageable pageable, String lang);

    /**
     * 상품별 리뷰 목록 조회
     * @param productId 상품 아이디
     * @param userId    조회 사용자 아이디 (좋아요 여부 조회용)
     * @param pageable  페이징 정보
     * @param lang      언어 코드
     */
    Page<ReviewDetailDTO> getProductReviews(Long productId, Long userId, Pageable pageable, String lang);

    /**
     * 단일 리뷰 상세 조회
     * @param reviewId 리뷰 아이디
     * @param userId   조회 사용자 아이디
     * @param lang     언어 코드
     */
    ReviewDetailDTO getOneDetail(Long reviewId, Long userId, String lang);

    /**
     * 리뷰 등록
     */
    Long register(ReviewAddDTO reviewAddDTO);

    /**
     * 리뷰 수정
     */
    Long modify(Long userId, Long reviewId, ReviewModifyDTO reviewModifyDTO);

    /**
     * 리뷰 삭제
     */
    Long delete(Long userId, Long reviewId);
}
