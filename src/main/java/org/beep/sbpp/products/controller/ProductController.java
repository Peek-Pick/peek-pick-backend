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

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductLikeService productLikeService;
    private final UserInfoUtil userInfoUtil;
    /**
     * GET /api/v1/products/ranking
     * 예)
     *  /ranking?page=0&size=10&sort=likeCount,DESC
     *  /ranking?page=0&size=10&sort=score,DESC&category=비스켓
     */
    @GetMapping("/ranking")
    public Page<ProductListDTO> getProductRanking(
            @PageableDefault(size = 10, sort = "likeCount", direction = Sort.Direction.DESC)
            Pageable pageable,

            @RequestParam(required = false)
            String category,

            @RequestParam(required = false)
            String keyword

    ) {
        return productService.getRanking(pageable, category, keyword);
    }

    @GetMapping("/{barcode}")
    public ResponseEntity<ProductDetailDTO> getProductDetail(
            @PathVariable String barcode,
            HttpServletRequest request
    ) {
        Long userId = userInfoUtil.getAuthUserId(request);
        ProductDetailDTO dto = productService.getDetailByBarcode(barcode);
        boolean liked = productLikeService.hasUserLikedProduct(dto.getProductId(), userId);
        // builder 사용하여 새로운 DTO 생성
        ProductDetailDTO response = ProductDetailDTO.builder()
                .productId(dto.getProductId())
                .barcode(dto.getBarcode())
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .volume(dto.getVolume())
                .imgUrl(dto.getImgUrl())
                .ingredients(dto.getIngredients())
                .allergens(dto.getAllergens())
                .nutrition(dto.getNutrition())
                .likeCount(dto.getLikeCount())
                .reviewCount(dto.getReviewCount())
                .score(dto.getScore())
                .isLiked(liked)
                .build();
        return ResponseEntity.ok(response);
    }

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
