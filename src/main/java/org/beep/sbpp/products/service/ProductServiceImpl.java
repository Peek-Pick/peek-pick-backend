package org.beep.sbpp.products.service;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.entities.ProductEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Page<ProductListDTO> getRanking(Pageable pageable, String category) {
        // QueryDSL 기반 필터+정렬
        Page<ProductEntity> page =
                productRepository.findAllWithFilterAndSort(category, pageable);

        return page.map(e -> new ProductListDTO(
                e.getProductId(),
                e.getBarcode(),
                e.getName(),
                e.getCategory(),
                e.getImgUrl(),
                e.getLikeCount(),
                e.getScore()
        ));
    }

    @Override
    public ProductDetailDTO getDetailByBarcode(String barcode) {
        ProductEntity e = productRepository.findByBarcode(barcode)
                .orElseThrow(() ->
                        new RuntimeException("상품을 찾을 수 없습니다. 바코드=" + barcode)
                );

        return new ProductDetailDTO(
                e.getBarcode(),
                e.getName(),
                e.getDescription(),
                e.getCategory(),
                e.getVolume(),
                e.getImgUrl(),
                e.getIngredients(),
                e.getAllergens(),
                e.getNutrition(),
                e.getLikeCount(),
                e.getScore()
        );
    }
}
