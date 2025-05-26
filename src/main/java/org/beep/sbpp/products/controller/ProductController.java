package org.beep.sbpp.products.controller;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/v1/products/ranking?page=0&size=10&sort=likeCount,DESC
     */
    @GetMapping("/ranking")
    public Page<ProductListDTO> getProductRanking(
            @PageableDefault(
                    size = 10,
                    sort = "likeCount",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return productService.getRanking(pageable);
    }
}
