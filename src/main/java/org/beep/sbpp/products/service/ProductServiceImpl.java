package org.beep.sbpp.products.service;

import lombok.RequiredArgsConstructor;
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
                        e.getName(),
                        e.getCategory(),
                        e.getImgUrl(),  // entity의 imageUrl 매핑
                        e.getLikeCount()
                ));
    }
}
