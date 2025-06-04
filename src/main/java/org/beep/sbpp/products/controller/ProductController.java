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
     *
     * 예시)
     *   GET /api/v1/products/ranking?page=0&size=10&sort=likeCount,DESC
     *   GET /api/v1/products/ranking?page=0&size=10&sort=score,DESC&category=과자류
     */
    @GetMapping("/ranking")
    public Page<ProductListDTO> getProductRanking(
            @PageableDefault(size = 10, sort = "likeCount", direction = Sort.Direction.DESC)
            Pageable pageable,

            @RequestParam(required = false)
            String category
    ) {
        // keyword 파라미터 없이, 서비스에 null로 넘겨서 “순수 랭킹”만 조회
        return productService.getRanking(pageable, category, null);
    }

    /**
     * ■ “검색” 전용 엔드포인트 (/api/v1/products/search)
     *    - 검색어(keyword)는 필수 파라미터로 받고,
     *      (선택) category 필터를 적용할 수도 있음
     *
     * 예시)
     *   GET /api/v1/products/search?page=0&size=10&sort=likeCount,DESC&keyword=바나나맛
     *   GET /api/v1/products/search?page=0&size=10&sort=likeCount,DESC&keyword=라면&category=면류
     */
    @GetMapping("/search")
    public Page<ProductListDTO> searchProducts(
            @PageableDefault(size = 10, sort = "likeCount", direction = Sort.Direction.DESC)
            Pageable pageable,

            @RequestParam
            String keyword,

            @RequestParam(required = false)
            String category
    ) {
        // keyword가 null이 되지 않도록 @RequestParam만 받고,
        // 서비스에 그대로 넘겨서 “검색 + 정렬 + 카테고리” 모두 적용
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
        // 서비스에서 기본 상세 정보 조회
        ProductDetailDTO dto = productService.getDetailByBarcode(barcode);
        // 해당 사용자가 이 상품을 좋아요 눌렀는지 확인
        boolean liked = productLikeService.hasUserLikedProduct(dto.getProductId(), userId);

        // 빌더 패턴 사용하여 응답 DTO에 isLiked 필드 추가
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

    /**
     * ■ 좋아요(토글) 기능
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
