package org.beep.sbpp.users.service;

import org.beep.sbpp.common.PageResponse;
import org.beep.sbpp.products.dto.ProductListDTO;

import java.time.LocalDateTime;

/**
 * “찜한 상품” 관련 비즈니스 로직 인터페이스
 */
public interface UserFavoriteService {

    /**
     * 특정 사용자가 찜한 상품 목록을 커서 기반으로 조회
     *
     * @param userId        조회 대상 사용자 ID
     * @param size          한 번에 조회할 개수
     * @param lastModDate   마지막 항목의 수정일
     * @param lastProductId 마지막 항목의 상품 ID
     * @return ProductListDTO 목록과 hasNext 포함 PageResponse
     */
    PageResponse<ProductListDTO> getFavoriteProducts(Long userId, Integer size, LocalDateTime lastModDate, Long lastProductId);
}
