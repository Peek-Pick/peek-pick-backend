// src/main/java/org/beep/sbpp/products/controller/ProductController.java
package org.beep.sbpp.products.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.service.ProductLikeService;
import org.beep.sbpp.products.service.ProductService;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     */
    @GetMapping("/ranking")
    public Page<ProductListDTO> getProductRanking(
            @PageableDefault(size = 10, sort = "likeCount", direction = Sort.Direction.DESC)
            Pageable pageable,
            @RequestParam(required = false) String category
    ) {
        return productService.getRanking(pageable, category, null);
    }

    /**
     * ■ 상품 검색 조회
     *   GET /api/v1/products/search
     */
    @GetMapping("/search")
    public Page<ProductListDTO> searchProducts(
            @PageableDefault(size = 10, sort = "likeCount", direction = Sort.Direction.DESC)
            Pageable pageable,
            @RequestParam String keyword,
            @RequestParam(required = false) String category
    ) {
        return productService.getRanking(pageable, category, keyword);
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
        // DTO 내부에 fromEntity로 기본 필드를 세팅했으므로 isLiked만 추가
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
