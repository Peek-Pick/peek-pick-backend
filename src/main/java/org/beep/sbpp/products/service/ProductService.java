package org.beep.sbpp.products.service;

import org.beep.sbpp.common.PageResponse;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;

import java.util.List;

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
     * 상품 검색 (정렬 기준에 따라 커서/오프셋 혼합 적용)
     *
     * @param size 페이지 크기
     * @param page 정확도 정렬일 경우 OFFSET용 페이지 번호 (프론트 내부에서 관리, 쿼리스트링에는 포함 ❌)
     * @param lastValue 커서 기반 정렬일 경우 기준값 (likeCount, score 등)
     * @param lastProductId 커서 보조 키
     * @param category 카테고리 필터 (nullable)
     * @param keyword 검색어
     * @param sortKey 정렬 기준: likeCount / score / _score
     * @return 상품 목록 페이지 응답
     */
    PageResponse<ProductListDTO> searchProducts(
            Integer size,
            Integer page,
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
            Long userId
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