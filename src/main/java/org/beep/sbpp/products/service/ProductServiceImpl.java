package org.beep.sbpp.products.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.common.PageResponse;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.entities.ProductEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.products.repository.ProductTagUserRepository;
import org.beep.sbpp.search.service.ProductSearchServiceImpl;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 상품 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductTagUserRepository productTagUserRepository;
    private final ProductLikeService productLikeService;
    private final UserInfoUtil userInfoUtil;
    private final ProductSearchServiceImpl productSearchService;

    /**
     * 상품 랭킹을 커서 기반으로 조회한다.
     * - 카테고리/정렬 옵션만 존재하는 요청 (keyword 없음)
     * - 최대 100개까지만 제공됨 (TOP 100 제한)
     */
    @Override
    public PageResponse<ProductListDTO> getRanking(Integer size, Integer lastValue, Long lastProductId, String category, String sortKey) {
        int limit = Math.min(size + 1, 101); // ✅ TOP 100 제한

        List<ProductEntity> results = productRepository
                .findAllWithCursorAndFilter(category, null, lastValue, lastProductId, limit, sortKey);

        List<ProductListDTO> dtoList = results.stream()
                .limit(size)
                .map(ProductListDTO::fromEntity)
                .toList();

        boolean hasNext = results.size() > size;
        return PageResponse.of(dtoList, hasNext);
    }

    /**
     * Elasticsearch 기반 상품 검색
     * - 정렬: likeCount / score / _score
     * - 키워드 + 카테고리 필터 포함
     * - 무한 스크롤을 위한 size + hasNext 구조 유지
     * - _score 정렬일 경우 커서 페이징 제외
     */
    @Override
    public PageResponse<ProductListDTO> searchProducts(Integer size, Integer page, Integer lastValue, Long lastProductId, String category, String keyword, String sortKey) {

        if ("_score".equals(sortKey)) {
            int pageParam = page != null ? page : 0; // ✅ 프론트에서 넘긴 pageParam 사용
            return productSearchService.searchByScore(keyword, category, pageParam, size);
        }

        // ✅ 좋아요/별점 정렬은 기존 커서 방식 유지
        int limit = size + 1;
        List<ProductListDTO> results = productSearchService.search(
                keyword,
                category,
                sortKey,
                lastValue,
                lastProductId,
                limit
        );

        List<ProductListDTO> dtoList = results.stream().limit(size).toList();
        boolean hasNext = results.size() > size;

        return PageResponse.of(dtoList, hasNext);
    }


    /**
     * 추천 상품을 커서 기반으로 조회한다.
     * - 사용자 관심 태그 기반 필터
     * - 정렬 기준에 따라 커서 조건 달라짐
     */
    @Override
    public PageResponse<ProductListDTO> getRecommended(Integer size, Integer lastValue, Long lastProductId, Long userId) {
        int limit = Math.min(size + 1, 101); // TOP 100 제한

        List<ProductEntity> results = productTagUserRepository
                .findRecommendedByUserIdWithCursor(userId, lastValue, lastProductId, limit); // ✅ sortKey 제거

        List<ProductListDTO> dtoList = results.stream()
                .limit(size)
                .map(ProductListDTO::fromEntity)
                .toList();

        boolean hasNext = results.size() > size;
        return PageResponse.of(dtoList, hasNext);
    }

    /**
     * 바코드로 상품 상세 정보 단건 조회
     */
    @Override
    public ProductDetailDTO getDetailByBarcode(String barcode) {
        ProductEntity e = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다. 바코드=" + barcode));
        return ProductDetailDTO.fromEntity(e);
    }

    /**
     * 바코드로 상품 ID 조회
     */
    @Override
    public Long getProductIdByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .map(ProductEntity::getProductId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. barcode: " + barcode));
    }

    /**
     * 상품 위시 개수 조회 (유저아이디 기준)
     */
    @Override
    public Long getWishCountByUserId(Long userId) {
        return productRepository.countWishByUserId(userId);
    }
}
