package org.beep.sbpp.search.service;

import org.beep.sbpp.common.PageResponse;
import org.beep.sbpp.products.dto.ProductListDTO;

import java.util.List;

public interface ProductSearchService {

    /**
     * 커서 기반 검색 (likeCount, score)
     */
    List<ProductListDTO> search(String keyword, String category, String sortKey,
                                Integer lastValue, Long lastProductId, int size);

    /**
     * 정확도 순 OFFSET 기반 검색
     */
    PageResponse<ProductListDTO> searchByScore(String keyword, String category, int page, int size);
}
