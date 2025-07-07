// src/main/java/org/beep/sbpp/search/service/ProductSearchService.java
package org.beep.sbpp.search.service;

import org.beep.sbpp.common.PageResponse;
import org.beep.sbpp.products.dto.ProductListDTO;

import java.util.List;

/**
 * Elasticsearch 기반 상품 검색 서비스
 */
public interface ProductSearchService {
    /**
     * 커서 기반 검색 (likeCount, score)
     * @param keyword        검색어
     * @param category       카테고리 필터
     * @param sortKey        정렬 기준 ("likeCount","score")
     * @param lastValue      커서 기준값
     * @param lastProductId  커서 보조키
     * @param size           조회 크기
     * @param lang           언어 코드 ("ko","en","ja")
     */
    List<ProductListDTO> search(
            String keyword,
            String category,
            String sortKey,
            Integer lastValue,
            Long lastProductId,
            int size,
            String lang
    );

    /**
     * 정확도 순 OFFSET 기반 검색
     * @param keyword  검색어
     * @param category 카테고리 필터
     * @param page     페이지 번호 (0부터)
     * @param size     페이지 크기
     * @param lang     언어 코드 ("ko","en","ja")
     */
    PageResponse<ProductListDTO> searchByScore(
            String keyword,
            String category,
            int page,
            int size,
            String lang
    );
}
