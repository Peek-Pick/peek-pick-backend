package org.beep.sbpp.admin.products.service;

import org.beep.sbpp.admin.products.dto.ProductRequestDto;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * 관리자용 상품 비즈니스 인터페이스
 */
public interface AdminProductService {
    Page<ProductListDTO> getProducts(Pageable pageable, String keyword);
    ProductDetailDTO getProduct(Long id);
    ProductDetailDTO createProduct(ProductRequestDto dto, MultipartFile image);
    ProductDetailDTO updateProduct(Long id, ProductRequestDto dto, MultipartFile image);
    void deleteProduct(Long id);

    /** 단일 이미지 업로드 전용 */
    void uploadImage(Long productId, MultipartFile file);
}
