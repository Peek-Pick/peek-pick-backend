package org.beep.sbpp.users.repository;

import org.beep.sbpp.products.entities.ProductLikeEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 찜한 상품 목록을 커서 방식으로 조회하는 커스텀 인터페이스
 */
public interface UserFavoriteRepositoryCustom {

    /**
     * 커서 기반 페이징
     * - userId와 isDelete 조건 적용
     * - modDate < ? OR (modDate = ? AND productId < ?) 기준 커서 페이징
     * - 정렬: modDate DESC, productId DESC
     *
     * @param userId         사용자 ID
     * @param lastModDate    마지막 수정일시
     * @param lastProductId  마지막 상품 ID
     * @param size           조회할 개수
     * @return 찜한 상품 목록
     */
    List<ProductLikeEntity> findAllByCursor(Long userId, LocalDateTime lastModDate, Long lastProductId, int size);
}
