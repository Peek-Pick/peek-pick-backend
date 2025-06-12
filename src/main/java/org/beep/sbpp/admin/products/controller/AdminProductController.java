package org.beep.sbpp.admin.products.controller;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.admin.products.dto.ProductRequestDTO;
import org.beep.sbpp.admin.products.service.AdminProductService;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService productService;

    /** 목록 조회 */
    @GetMapping
    public ResponseEntity<Page<ProductListDTO>> list(
            Pageable pageable,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(productService.getProducts(pageable, keyword));
    }

    /** 상세 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDTO> detail(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    /** 생성 (multipart/form-data) */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDetailDTO> create(
            @ModelAttribute ProductRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(productService.createProduct(dto, image));
    }

    /** 수정 (multipart/form-data) */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDetailDTO> update(
            @PathVariable Long id,
            @ModelAttribute ProductRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, dto, image));
    }

    /** 소프트 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
