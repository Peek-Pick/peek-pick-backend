package org.beep.sbpp.products.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.common.PageResponse;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.ProductLangEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.products.repository.ProductTagUserRepository;
import org.beep.sbpp.search.service.ProductSearchService;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductTagUserRepository productTagUserRepository;
    private final ProductSearchService productSearchService;
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

        List<ProductBaseEntity> results = productRepository
                .findAllWithCursorAndFilter(category, null, lastValue, lastProductId, limit, sortKey, lang);

        // N+1 해결: 연관필드 접근으로 batch fetch 적용
        List<ProductListDTO> dtoList = results.stream()
                .limit(size)
                .map(base -> {
                    ProductLangEntity langEntity = switch (lang.toLowerCase()) {
                        case "ko" -> base.getKoEntity();
                        case "en" -> base.getEnEntity();
                        case "ja" -> base.getJaEntity();
                        default   -> throw new IllegalArgumentException("지원하지 않는 언어: " + lang);
                    };
                    return ProductListDTO.fromEntities(base, langEntity);
                })
                .toList();

        boolean hasNext = results.size() > size;
        return PageResponse.of(dtoList, hasNext);
    }

    /**
     * Elasticsearch 기반 상품 검색 (다국어 지원)
     * - 정확도(_score): OFFSET 페이징
     * - 좋아요(likeCount), 별점(score): search_after 커서 페이징
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
        // 정확도 순(_score)일 때는 OFFSET 기반 ES 검색
        if ("_score".equals(sortKey)) {
            return productSearchService.searchByScore(keyword, category, page, size, lang);
        }

        // 그 외(likeCount, score)는 ES 커서(search_after) 페이징으로 통일
        // size+1 로 한 건 더 불러와서 hasNext 판별
        List<ProductListDTO> all = productSearchService.search(
                keyword,
                category,
                sortKey,
                lastValue,
                lastProductId,
                size + 1,
                lang
        );

        boolean hasNext = all.size() > size;
        List<ProductListDTO> pageItems = all.stream()
                .limit(size)
                .toList();

        return PageResponse.of(pageItems, hasNext);
    }

    /**
     * 추천 상품을 커서 기반으로 조회한다. (다국어 지원)
     * - ES 로 전환 전까지는 기존 DB 로직 유지
     */
    @Override
    public PageResponse<ProductListDTO> getRecommended(
            Integer size,
            Integer lastValue,
            Long lastProductId,
            Long userId,
            String lang
    ) {
        int limit = Math.min(size + 1, 101);

        List<ProductBaseEntity> results = productTagUserRepository
                .findRecommendedByUserIdWithCursor(userId, lastValue, lastProductId, limit);

        // N+1 해결: 연관필드 접근으로 batch fetch 적용
        List<ProductListDTO> dtoList = results.stream()
                .limit(size)
                .map(base -> {
                    ProductLangEntity langEntity = switch (lang.toLowerCase()) {
                        case "ko" -> base.getKoEntity();
                        case "en" -> base.getEnEntity();
                        case "ja" -> base.getJaEntity();
                        default   -> throw new IllegalArgumentException("지원하지 않는 언어: " + lang);
                    };
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

        // N+1 해결: 연관필드 접근으로 batch fetch 적용
        ProductLangEntity langEntity = switch (lang.toLowerCase()) {
            case "ko" -> base.getKoEntity();
            case "en" -> base.getEnEntity();
            case "ja" -> base.getJaEntity();
            default   -> throw new IllegalArgumentException("지원하지 않는 언어: " + lang);
        };

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
}
