package org.beep.sbpp.products.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.common.PageResponse;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.service.ProductLikeService;
import org.beep.sbpp.products.service.ProductService;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 상품 관련 API 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final ProductLikeService productLikeService;
    private final UserInfoUtil userInfoUtil;

    /**
     * 🏆 랭킹 기반 상품 조회
     * - 커서 기반 페이징
     * - 정렬 기준: likeCount or score
     */
    @GetMapping("/ranking")
    public PageResponse<ProductListDTO> getProductRanking(
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer lastValue,
            @RequestParam(required = false) Long lastProductId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "score") String sort,
            @RequestParam(required = false, defaultValue = "en") String lang
    ) {
        return productService.getRanking(size, lastValue, lastProductId, category, sort, lang);
    }

    /**
     * 🔍 검색 기반 상품 조회 (Elasticsearch)
     * - keyword, category, sort, cursor 기반
     * - 무한 스크롤 대응
     * - 정확도 정렬(_score) 시 커서 조건 무시 & 200개까지 반환
     *
     *  별점/좋아요는 DB에 저장된 값이며, 이를 통해 무한스크롤 방식에서
     *  Cursor 기반으로 정렬하기에 좋음.
     *
     *  그러나, Elasticsearch 정확도 순 정렬은 Cursor기반 정렬이 어려움
     *  오히려 OFFSET방식이 더욱 적합.
     *
     */
    @GetMapping("/search")
    public PageResponse<ProductListDTO> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "_score") String sort,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer lastValue, // 커서 기반 - 좋아요 수/별점 기준 정렬
            @RequestParam(required = false) Long lastProductId,
            @RequestParam(required = false, defaultValue = "en") String lang
    ) {
        Integer pageParam = "_score".equals(sort) ? (page != null ? page : 0) : null;
        return productService.searchProducts(size, pageParam, lastValue, lastProductId, category, keyword, sort, lang);
    }


    /**
     * ■ 추천 상품 조회
     *   GET /api/v1/products/recommended
     *   - 로그인 사용자의 관심 태그 기반
     *   - 정렬 기준: _score(ES 정확도) or likeCount or score
     *   - 커서 페이징: lastValue + lastProductId
     */
    @GetMapping("/recommended")
    public PageResponse<ProductListDTO> getRecommended(
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer lastValue,
            @RequestParam(required = false) Long lastProductId,
            @RequestParam(required = false, defaultValue = "en") String lang,
            HttpServletRequest request
    ) {
        Long userId = userInfoUtil.getAuthUserId(request);
        return productService.getRecommended(size, lastValue, lastProductId, userId, lang);
    }

    /**
     * 📦 상품 바코드 기반 상세 조회
     */
    @GetMapping("/{barcode}")
    public ResponseEntity<ProductDetailDTO> getProductDetail(
            @PathVariable String barcode,
            @RequestParam(required = false, defaultValue = "en") String lang,
            HttpServletRequest request
    ) {
        Long userId = userInfoUtil.getAuthUserId(request);
        ProductDetailDTO dto = productService.getDetailByBarcode(barcode, lang);
        boolean liked = productLikeService.hasUserLikedProduct(dto.getProductId(), userId);
        dto.setIsLiked(liked);
        return ResponseEntity.ok(dto);
    }

    /**
     * ■ 좋아요 토글
     *   POST /api/v1/products/{barcode}/like
     */
    @PostMapping("/{barcode}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable String barcode,
            HttpServletRequest request
    ) {
        Long userId = userInfoUtil.getAuthUserId(request);
        Long productId = productService.getProductIdByBarcode(barcode);
        productLikeService.toggleProductLike(productId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * ■ 마이페이지 위시 개수
     *   GET /api/v1/products/wishCount
     */
    @GetMapping("/wishCount")
    public ResponseEntity<Long> countWishCountByUserId(HttpServletRequest request) {
        Long userId = userInfoUtil.getAuthUserId(request);
        Long count = productService.getWishCountByUserId(userId);

        return ResponseEntity.ok(count);
    }
}
