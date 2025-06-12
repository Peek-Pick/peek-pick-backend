// src/main/java/org/beep/sbpp/products/service/ProductService.java
package org.beep.sbpp.products.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;

public interface ProductService {
    Page<ProductListDTO> getRanking(Pageable pageable, String category, String keyword);

    ProductDetailDTO getDetailByBarcode(String barcode);

    Long getProductIdByBarcode(String barcode);

    Page<ProductListDTO> getRecommended(Pageable pageable, Long userId);
}
