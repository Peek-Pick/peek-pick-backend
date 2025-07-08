package org.beep.sbpp.users.service;

import org.beep.sbpp.common.PageResponse;
import org.beep.sbpp.products.dto.ProductListDTO;

import java.time.LocalDateTime;

/**
 * 사용자 찜한 상품 조회 서비스
 */
public interface UserFavoriteService {

    /**
     * 사용자가 찜한 상품 목록을 커서 기반으로 조회
     *
     * @param userId        사용자 ID
     * @param size          한 페이지에 보여줄 개수
     * @param lastModDate   마지막 조회된 modDate (커서)
     * @param lastProductId 마지막 조회된 상품 ID (커서 보조)
     * @param lang          언어 코드 ("ko","en","ja")
     */
    PageResponse<ProductListDTO> getFavoriteProducts(
            Long userId,
            Integer size,
            LocalDateTime lastModDate,
            Long lastProductId,
            String lang
    );
}
