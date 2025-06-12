// src/main/java/org/beep/sbpp/products/repository/ProductTagUserRepository.java
package org.beep.sbpp.products.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.beep.sbpp.products.dto.ProductListDTO;

/**
 * 사용자 관심 태그 기반 추천 상품 조회용 리포지토리
 */
public interface ProductTagUserRepository {
    /**
     * 로그인한 사용자의 관심 태그와 매칭되는 상품을 DTO 로 페이징 조회
     */
    Page<ProductListDTO> findRecommendedByUserId(Long userId, Pageable pageable);
}
