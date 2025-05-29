package org.beep.sbpp.products.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.entities.ProductEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Page<ProductListDTO> getRanking(Pageable pageable, String category, String keyword) {
        Page<ProductEntity> page =
                productRepository.findAllWithFilterAndSort(category, keyword, pageable);

        return page.map(e -> new ProductListDTO(
                e.getProductId(),
                e.getBarcode(),
                e.getName(),
                e.getCategory(),
                e.getImgUrl(),
                e.getLikeCount(),
                e.getReviewCount(),
                e.getScore()
        ));
    }

    @Override
    public ProductDetailDTO getDetailByBarcode(String barcode) {
        ProductEntity e = productRepository.findByBarcode(barcode)
                .orElseThrow(() ->
                        new RuntimeException("상품을 찾을 수 없습니다. 바코드=" + barcode)
                );

        // Builder 패턴으로 DTO 생성 (isLiked 는 기본값 false)
        return ProductDetailDTO.builder()
                .productId(e.getProductId())
                .barcode(e.getBarcode())
                .name(e.getName())
                .description(e.getDescription())
                .category(e.getCategory())
                .volume(e.getVolume())
                .imgUrl(e.getImgUrl())
                .ingredients(e.getIngredients())
                .allergens(e.getAllergens())
                .nutrition(e.getNutrition())
                .likeCount(e.getLikeCount())
                .reviewCount(e.getReviewCount())
                .score(e.getScore())
                .build();
    }

    @Override
    public Long getProductIdByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .map(ProductEntity::getProductId)
                .orElseThrow(() ->
                        new IllegalArgumentException("No data found to get. barcode: " + barcode)
                );
    }
}
