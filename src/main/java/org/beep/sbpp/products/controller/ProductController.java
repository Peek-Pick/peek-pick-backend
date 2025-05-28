package org.beep.sbpp.products.controller;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.service.ProductService;
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

    /**
     * GET /api/v1/products/ranking
     * 예)
     *  /ranking?page=0&size=10&sort=likeCount,DESC
     *  /ranking?page=0&size=10&sort=score,DESC&category=비스켓
     */
    @GetMapping("/ranking")
    public Page<ProductListDTO> getProductRanking(
            @PageableDefault(
                    size = 10,
                    sort = "likeCount",
                    direction = Sort.Direction.DESC
            )

            Pageable pageable,

            @RequestParam(required = false)
            String category

    ) {
        return productService.getRanking(pageable, category);
    }

    @GetMapping("/{barcode}")
    public ResponseEntity<ProductDetailDTO> getProductDetail(@PathVariable String barcode) {
        ProductDetailDTO dto = productService.getDetailByBarcode(barcode);
        return ResponseEntity.ok(dto);
    }

}
