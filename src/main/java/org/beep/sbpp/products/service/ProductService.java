// src/main/java/org/beep/sbpp/products/service/ProductService.java
package org.beep.sbpp.products.service;

import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    /**
     * sort 기준(likeCount 또는 score), category 필터, name/description 키워드 검색을 함께 적용
     */
    Page<ProductListDTO> getRanking(Pageable pageable, String category, String keyword);

    ProductDetailDTO getDetailByBarcode(String barcode);

    Long getProductIdByBarcode(String barcode);
}
