package org.beep.sbpp.products.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.common.PageResponse;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.ProductKoEntity;
import org.beep.sbpp.products.entities.ProductEnEntity;
import org.beep.sbpp.products.entities.ProductJaEntity;
import org.beep.sbpp.products.entities.ProductLangEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.products.repository.ProductTagUserRepository;
import org.beep.sbpp.products.repository.ProductKoRepository;
import org.beep.sbpp.products.repository.ProductEnRepository;
import org.beep.sbpp.products.repository.ProductJaRepository;
import org.beep.sbpp.search.service.ProductSearchServiceImpl;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

/**
 * 상품 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;               // Base + Custom
    private final ProductTagUserRepository productTagUserRepository;
    private final ProductKoRepository koRepository;
    private final ProductEnRepository enRepository;
    private final ProductJaRepository jaRepository;
    private final ProductSearchServiceImpl productSearchService;
    private final UserInfoUtil userInfoUtil;

    /**
     * 상품 랭킹을 커서 기반으로 조회한다. (다국어 지원)
     */
    @Override
    public PageResponse<ProductListDTO> getRanking(
            Integer size,
            Integer lastValue,
            Long lastProductId,
            String category,
            String sortKey,
            String lang
    ) {
        int limit = Math.min(size + 1, 101); // TOP 100 제한

        // 1) BaseEntity 목록 조회 (커서 + 언어별 필터 포함)
        List<ProductBaseEntity> results = productRepository
                .findAllWithCursorAndFilter(category, null, lastValue, lastProductId, limit, sortKey, lang);

        // 2) Base + LangEntity 조합하여 DTO 변환
        List<ProductListDTO> dtoList = results.stream()
                .limit(size)
                .map(base -> {
                    ProductLangEntity langEntity = loadLangEntity(base.getProductId(), lang);
                    return ProductListDTO.fromEntities(base, langEntity);
                })
                .toList();

        boolean hasNext = results.size() > size;
        return PageResponse.of(dtoList, hasNext);
    }

    /**
     * Elasticsearch 기반 상품 검색 (다국어 지원)
     */
    @Override
    public PageResponse<ProductListDTO> searchProducts(
            Integer size,
            Integer page,
            Integer lastValue,
            Long lastProductId,
            String category,
            String keyword,
            String sortKey,
            String lang
    ) {
        // 1) _score 정렬인 경우: 페이지 기반 ES 검색
        if ("_score".equals(sortKey)) {
            int pageParam = page != null ? page : 0;
            // productSearchService.searchByScore()도 lang 파라미터 받아 처리하도록 변경 필요
            return productSearchService.searchByScore(keyword, category, pageParam, size, lang);
        }

        // 2) likeCount/score 정렬: 커서 방식 DB 조회
        int limit = size + 1;
        List<ProductBaseEntity> results = productRepository
                .findAllWithCursorAndFilter(category, keyword, lastValue, lastProductId, limit, sortKey, lang);

        List<ProductListDTO> dtoList = results.stream()
                .limit(size)
                .map(base -> {
                    ProductLangEntity langEntity = loadLangEntity(base.getProductId(), lang);
                    return ProductListDTO.fromEntities(base, langEntity);
                })
                .toList();

        boolean hasNext = results.size() > size;
        return PageResponse.of(dtoList, hasNext);
    }

    /**
     * 추천 상품을 커서 기반으로 조회한다. (다국어 지원)
     */
    @Override
    public PageResponse<ProductListDTO> getRecommended(
            Integer size,
            Integer lastValue,
            Long lastProductId,
            Long userId,
            String lang
    ) {
        int limit = Math.min(size + 1, 101); // TOP 100 제한

        List<ProductBaseEntity> results = productTagUserRepository
                .findRecommendedByUserIdWithCursor(userId, lastValue, lastProductId, limit);

        List<ProductListDTO> dtoList = results.stream()
                .limit(size)
                .map(base -> {
                    ProductLangEntity langEntity = loadLangEntity(base.getProductId(), lang);
                    return ProductListDTO.fromEntities(base, langEntity);
                })
                .toList();

        boolean hasNext = results.size() > size;
        return PageResponse.of(dtoList, hasNext);
    }

    /**
     * 바코드로 상품 상세 정보 단건 조회 (다국어 지원)
     */
    @Override
    public ProductDetailDTO getDetailByBarcode(String barcode, String lang) {
        ProductBaseEntity base = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다. 바코드=" + barcode));

        ProductLangEntity langEntity = loadLangEntity(base.getProductId(), lang);
        return ProductDetailDTO.fromEntities(base, langEntity);
    }

    /**
     * 바코드로 상품 ID 조회
     */
    @Override
    public Long getProductIdByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .map(ProductBaseEntity::getProductId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. barcode: " + barcode));
    }

    /**
     * 상품 위시 개수 조회 (유저아이디 기준)
     */
    @Override
    public Long getWishCountByUserId(Long userId) {
        return productRepository.countWishByUserId(userId);
    }

    // --------------------------------------------------
    // 공통) 언어에 따라 Lang Entity 를 로드하는 헬퍼 메서드
    private ProductLangEntity loadLangEntity(Long productId, String lang) {
        return switch (lang.toLowerCase()) {
            case "ko" -> koRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("한국어 데이터 없음: " + productId));
            case "en" -> enRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("영어 데이터 없음: " + productId));
            case "ja" -> jaRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("일본어 데이터 없음: " + productId));
            default -> throw new IllegalArgumentException("지원하지 않는 언어: " + lang);
        };
    }
}
