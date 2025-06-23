package org.beep.sbpp.products.service;

import org.beep.sbpp.common.PageResponse;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;

public interface ProductService {

    /**
     * 상품 랭킹
     */
    PageResponse<ProductListDTO> getRanking(
            Integer size,
            Integer lastValue,
            Long lastProductId,
            String category,
            String sortKey
    );

    /**
     * 검색 조회
     */
    PageResponse<ProductListDTO> searchProducts(
            Integer size,
            Integer lastValue,
            Long lastProductId,
            String category,
            String keyword,
            String sortKey
    );

    /**
     * 사용자 태그 기반 추천 상품 조회
     */
    PageResponse<ProductListDTO> getRecommended(
            Integer size,
            Integer lastValue,
            Long lastProductId,
            Long userId,
            String sortKey   // 추가됨
    );

    /**
     * 상품 상세 조회 (바코드 기준)
     */
    ProductDetailDTO getDetailByBarcode(String barcode);

    /**
     * 상품 ID 조회 (바코드 기준)
     */
    Long getProductIdByBarcode(String barcode);

    /**
     * 상품 위시 개수 조회 (유저아이디 기준)
     */
    Long getWishCountByUserId(Long userId);
}