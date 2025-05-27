package org.beep.sbpp.products.service;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.entities.ProductEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.products.service.ProductService;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Page<ProductListDTO> getRanking(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(e -> new ProductListDTO(

                        e.getProductId(),
                        e.getBarcode(),
                        e.getName(),
                        e.getCategory(),
                        e.getImgUrl(),  // entity의 imageUrl 매핑
                        e.getLikeCount()
                ));
    }

    @Override
    public ProductDetailDTO getDetailByBarcode(String barcode) {
        ProductEntity e = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다. 바코드=" + barcode));

        return new ProductDetailDTO(
                e.getBarcode(),
                e.getName(),
                e.getCategory(),
                e.getVolume(),
                e.getImgUrl(),
                e.getIngredients(),
                e.getAllergens(),
                e.getNutrition(),
                e.getLikeCount()
        );
    }

}
