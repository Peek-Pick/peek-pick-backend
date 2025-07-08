// src/main/java/org/beep/sbpp/admin/products/service/AdminProductService.java
package org.beep.sbpp.admin.products.service;

import org.beep.sbpp.admin.products.dto.ProductRequestDTO;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * 관리자용 상품 관리 서비스 인터페이스
 */
public interface AdminProductService {

    Page<ProductListDTO> getProducts(Pageable pageable, String keyword, String lang);

    ProductDetailDTO getProduct(Long id, String lang);

    ProductDetailDTO createProduct(ProductRequestDTO dto, MultipartFile image, String lang);

    ProductDetailDTO updateProduct(Long id, ProductRequestDTO dto, MultipartFile image, String lang);

    void deleteProduct(Long id);
}
