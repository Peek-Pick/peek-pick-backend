package org.beep.sbpp.admin.products.service;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.admin.products.dto.ProductRequestDto;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.entities.ProductEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductRepository productRepository;
    private final AdminProductImageStorageService imageStorage;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductListDTO> getProducts(Pageable pageable, String keyword) {
        return productRepository
                .findAllWithFilterAndSort(null, keyword, pageable)
                .map(ProductListDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailDTO getProduct(Long id) {
        ProductEntity e = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. ID=" + id));
        return ProductDetailDTO.fromEntity(e);
    }

    @Override
    public ProductDetailDTO createProduct(ProductRequestDto dto, MultipartFile image) {
        ProductEntity e = dto.toEntity();
        if (image != null && !image.isEmpty()) {
            e.setImgUrl(imageStorage.store(image));
        }
        return ProductDetailDTO.fromEntity(productRepository.save(e));
    }

    @Override
    public ProductDetailDTO updateProduct(Long id, ProductRequestDto dto, MultipartFile image) {
        ProductEntity e = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. ID=" + id));

        // 1) 기본 필드 업데이트
        e.setBarcode(dto.getBarcode());
        e.setName(dto.getName());
        e.setCategory(dto.getCategory());
        e.setDescription(dto.getDescription());
        e.setVolume(dto.getVolume());
        e.setIngredients(dto.getIngredients());

        // 2) 알레르기·영양 필드 업데이트
        e.setAllergens(dto.getAllergens());
        e.setNutrition(dto.getNutrition());

        // 3) soft-delete 상태 토글 반영
        if (dto.getIsDelete() != null) {
            e.setIsDelete(dto.getIsDelete());
        }

        // 4) 이미지 파일 or URL 우선 처리
        if (image != null && !image.isEmpty()) {
            e.setImgUrl(imageStorage.store(image));
        } else if (dto.getImgUrl() != null && !dto.getImgUrl().isEmpty()) {
            e.setImgUrl(dto.getImgUrl());
        }

        return ProductDetailDTO.fromEntity(productRepository.save(e));
    }

    /** 소프트 삭제: isDelete=true */
    @Override
    public void deleteProduct(Long id) {
        ProductEntity e = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. ID=" + id));
        e.setIsDelete(true);
        productRepository.save(e);
    }

    @Override
    public void uploadImage(Long productId, MultipartFile file) {
        ProductEntity e = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. ID=" + productId));
        if (file != null && !file.isEmpty()) {
            e.setImgUrl(imageStorage.store(file));
            productRepository.save(e);
        }
    }
}
