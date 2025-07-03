package org.beep.sbpp.products.repository;

import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 사용자 관심 태그 기반 추천 상품 조회용 리포지토리
 */
public interface ProductTagUserRepository {

    /**
     * 로그인한 사용자의 관심 태그와 매칭되는 상품을 DTO 로 페이징 조회 (기존 방식)
     */
    //Page<ProductListDTO> findRecommendedByUserId(Long userId, Pageable pageable);

    /**
     * 로그인한 사용자의 관심 태그와 매칭되는 상품을 커서 방식으로 페이징 조회
     * - 정렬: likeCount DESC 또는 score DESC, productId ASC
     * - 커서 조건: 정렬 기준 값 (likeCount or score), productId
     * - soft delete 제외
     *
     * @param userId 사용자 ID
     * @param lastValue 정렬 기준 필드의 커서 값
     * @param lastProductId 보조 커서 (productId)
     * @param size 페이지 크기
     */
    List<ProductEntity> findRecommendedByUserIdWithCursor(Long userId, Integer lastValue, Long lastProductId, int size);

}
