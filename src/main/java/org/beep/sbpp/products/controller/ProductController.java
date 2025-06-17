package org.beep.sbpp.products.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.common.PageResponse;
import org.beep.sbpp.products.service.ProductLikeService;
import org.beep.sbpp.products.service.ProductService;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 상품 관련 API를 제공하는 컨트롤러
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final ProductLikeService productLikeService;
    private final UserInfoUtil userInfoUtil;

    /**
     * ■ 상품 랭킹 조회
     *   GET /api/v1/products/ranking
     *   - 정렬 기준: likeCount or score
     *   - 커서 페이징: lastValue + lastProductId
     */
    @GetMapping("/ranking")
    public PageResponse<ProductListDTO> getProductRanking(
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer lastValue,
            @RequestParam(required = false) Long lastProductId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "likeCount") String sort
    ) {
        return productService.getRanking(size, lastValue, lastProductId, category, sort);
    }

    /**
     * ■ 상품 검색 조회
     *   GET /api/v1/products/search
     *   - 정렬 기준: likeCount or score
     *   - 커서 페이징: lastValue + lastProductId
     */
    @GetMapping("/search")
    public PageResponse<ProductListDTO> searchProducts(
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer lastValue,
            @RequestParam(required = false) Long lastProductId,
            @RequestParam String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "likeCount") String sort
    ) {
        return productService.searchProducts(size, lastValue, lastProductId, category, keyword, sort);
    }

    /**
     * ■ 추천 상품 조회
     *   GET /api/v1/products/recommended
     *   - 로그인 사용자의 관심 태그 기반
     *   - 정렬 기준: likeCount or score (현재는 likeCount만 사용)
     *   - 커서 페이징: lastValue + lastProductId
     */
    @GetMapping("/recommended")
    public PageResponse<ProductListDTO> getRecommended(
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer lastValue,
            @RequestParam(required = false) Long lastProductId,
            @RequestParam(required = false, defaultValue = "likeCount") String sort,
            HttpServletRequest request
    ) {
        Long userId = userInfoUtil.getAuthUserId(request);
        return productService.getRecommended(size, lastValue, lastProductId, userId, sort);
    }

    /**
     * ■ 상품 상세 조회
     *   GET /api/v1/products/{barcode}
     */
    @GetMapping("/{barcode}")
    public ResponseEntity<ProductDetailDTO> getProductDetail(
            @PathVariable String barcode,
            HttpServletRequest request
    ) {
        Long userId = userInfoUtil.getAuthUserId(request);
        ProductDetailDTO dto = productService.getDetailByBarcode(barcode);
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
}
