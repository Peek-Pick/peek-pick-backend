// src/main/java/org/beep/sbpp/products/service/ProductServiceImpl.java
package org.beep.sbpp.products.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.entities.ProductEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /**
     * 상품 랭킹(또는 검색 + 정렬)을 페이징 조회한다.
     */
    @Override
    public Page<ProductListDTO> getRanking(Pageable pageable, String category, String keyword) {
        return productRepository
                .findAllWithFilterAndSort(category, keyword, pageable)
                .map(ProductListDTO::fromEntity);
    }

    /**
     * 바코드로 상품 단건 상세 조회
     */
    @Override
    public ProductDetailDTO getDetailByBarcode(String barcode) {
        ProductEntity e = productRepository.findByBarcode(barcode)
                .orElseThrow(() ->
                        new RuntimeException("상품을 찾을 수 없습니다. 바코드=" + barcode)
                );
        return ProductDetailDTO.fromEntity(e);
    }

    /**
     * 바코드로 상품 ID 조회
     */
    @Override
    public Long getProductIdByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .map(ProductEntity::getProductId)
                .orElseThrow(() ->
                        new IllegalArgumentException("No data found to get. barcode: " + barcode)
                );
    }
}
