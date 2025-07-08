package org.beep.sbpp.products.service;

import org.beep.sbpp.common.PageResponse;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;

public interface ProductService {

    /**
     * 상품 랭킹 (다국어 지원)
     *
     * @param size 페이지 크기
     * @param lastValue 커서 기반 정렬 커서 값
     * @param lastProductId 보조 커서 (productId)
     * @param category 언어별 category 필터
     * @param sortKey "likeCount" 또는 "score"
     * @param lang "ko", "en", "ja"
     */
    PageResponse<ProductListDTO> getRanking(
            Integer size,
            Integer lastValue,
            Long lastProductId,
            String category,
            String sortKey,
            String lang
    );

    /**
     * 상품 검색 (다국어 지원)
     *
     * @param size 페이지 크기
     * @param page _score 정렬일 때만 사용하는 OFFSET 페이지 번호
     * @param lastValue 커서 기반 정렬 커서 값
     * @param lastProductId 보조 커서 (productId)
     * @param category 언어별 category 필터
     * @param keyword 검색어
     * @param sortKey "likeCount", "score", "_score"
     * @param lang "ko", "en", "ja"
     */
    PageResponse<ProductListDTO> searchProducts(
            Integer size,
            Integer page,
            Integer lastValue,
            Long lastProductId,
            String category,
            String keyword,
            String sortKey,
            String lang
    );

    /**
     * 사용자 태그 기반 추천 상품 조회 (다국어 지원)
     */
    PageResponse<ProductListDTO> getRecommended(
            Integer size,
            Integer lastValue,
            Long lastProductId,
            Long userId,
            String lang
    );

    /**
     * 상품 상세 조회 (바코드 기준, 다국어 지원)
     */
    ProductDetailDTO getDetailByBarcode(String barcode, String lang);

    /**
     * 상품 ID 조회 (바코드 기준)
     */
    Long getProductIdByBarcode(String barcode);

    /**
     * 상품 위시 개수 조회 (유저아이디 기준)
     */
    Long getWishCountByUserId(Long userId);
}
