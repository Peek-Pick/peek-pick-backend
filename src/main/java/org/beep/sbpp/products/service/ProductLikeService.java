package org.beep.sbpp.products.service;

/**
 * 상품 좋아요 토글 기능 서비스 인터페이스
 */
public interface ProductLikeService {
    /**
     * 주어진 상품에 대해 사용자의 좋아요 상태를 토글하고,
     * 결과로 생성되거나 기존 좋아요 엔티티의 ID를 반환합니다.
     *
     * @param productId 상품 ID
     * @param userId    사용자 ID
     * @return 좋아요 엔티티 ID
     */
    Long toggleProductLike(Long productId, Long userId);

    /**
     * 특정 사용자가 주어진 상품에 좋아요를 누른 상태인지 확인합니다.
     *
     * @param productId 상품 ID
     * @param userId    사용자 ID
     * @return 좋아요 상태 (true: 좋아요, false: 미좋아요)
     */
    boolean hasUserLikedProduct(Long productId, Long userId);
}