// 📄 src/main/java/org/beep/sbpp/products/controller/ProductSearchController.java
package org.beep.sbpp.products.controller;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.document.ProductSearchDocument;
import org.beep.sbpp.products.service.ProductSearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/search/test")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    @PostMapping("/save")
    public String saveTestDoc() {
        ProductSearchDocument doc = ProductSearchDocument.builder()
                .id("1")
                .name("새우깡")
                .description("바삭한 새우 과자")
                .brand("농심")
                .barcode("8801043011234")
                .category("과자")
                .build();

        productSearchService.save(doc);
        return "Saved!";
    }

    @GetMapping("/all")
    public List<ProductSearchDocument> getAll() {
        return productSearchService.findAll();
    }
}
