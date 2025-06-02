// src/main/java/org/beep/sbpp/users/service/UserFavoriteService.java
package org.beep.sbpp.users.service;

import org.beep.sbpp.products.dto.ProductListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * “찜한 상품” 관련 비즈니스 로직 인터페이스
 */
public interface UserFavoriteService {

    /**
     * 특정 사용자가 찜한 상품 목록을 페이지 단위로 조회
     *
     * @param userId   조회 대상 사용자 ID
     * @param pageable 페이지 정보 (페이지 번호, 사이즈, 정렬 등)
     * @return ProductListDTO 형태의 Page
     */
    Page<ProductListDTO> getFavoriteProducts(Long userId, Pageable pageable);
}
