package org.beep.sbpp.admin.products.service;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.admin.products.dto.ProductRequestDTO;
import org.beep.sbpp.admin.products.repository.AdminProductRepository;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final AdminProductRepository productRepository;
    private final AdminProductImageStorageService imageStorage;

    /** 목록 조회 (soft-delete 포함, 카테고리 없이 제목·설명(keyword)만 검색) */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductListDTO> getProducts(Pageable pageable, String keyword) {
        return productRepository
                .findAllIncludeDeleted(keyword, pageable)
                .map(ProductListDTO::fromEntity);
    }

    /** 상세 조회 */
    @Override
    @Transactional(readOnly = true)
    public ProductDetailDTO getProduct(Long id) {
        ProductEntity e = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. ID=" + id));
        return ProductDetailDTO.fromEntity(e);
    }

    /** 신규 등록 */
    @Override
    public ProductDetailDTO createProduct(ProductRequestDTO dto, MultipartFile image) {
        ProductEntity e = dto.toEntity();
        if (image != null && !image.isEmpty()) {
            e.setImgUrl(imageStorage.store(image));
        }
        return ProductDetailDTO.fromEntity(productRepository.save(e));
    }

    /** 수정 */
    @Override
    public ProductDetailDTO updateProduct(Long id, ProductRequestDTO dto, MultipartFile image) {
        ProductEntity e = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. ID=" + id));

        e.setBarcode(dto.getBarcode());
        e.setName(dto.getName());
        e.setCategory(dto.getCategory());
        e.setDescription(dto.getDescription());
        e.setVolume(dto.getVolume());
        e.setIngredients(dto.getIngredients());
        e.setAllergens(dto.getAllergens());
        e.setNutrition(dto.getNutrition());
        if (dto.getIsDelete() != null) {
            e.setIsDelete(dto.getIsDelete());
        }
        if (image != null && !image.isEmpty()) {
            e.setImgUrl(imageStorage.store(image));
        } else if (dto.getImgUrl() != null && !dto.getImgUrl().isEmpty()) {
            e.setImgUrl(dto.getImgUrl());
        }

        return ProductDetailDTO.fromEntity(productRepository.save(e));
    }

    /** 소프트 삭제 처리 */
    @Override
    public void deleteProduct(Long id) {
        ProductEntity e = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. ID=" + id));
        e.setIsDelete(true);
        productRepository.save(e);
    }

    /** 단일 이미지 업로드 */
    @Override
    public void uploadImage(Long productId, MultipartFile file) {
        imageStorage.store(file);
    }
}
