// src/main/java/org/beep/sbpp/admin/products/service/AdminProductServiceImpl.java
package org.beep.sbpp.admin.products.service;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.admin.products.dto.ProductRequestDTO;
import org.beep.sbpp.admin.products.repository.AdminProductRepository;
import org.beep.sbpp.chatbot.service.ChatbotEmbeddingService;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.entities.*;
import org.beep.sbpp.products.repository.ProductEnRepository;
import org.beep.sbpp.products.repository.ProductJaRepository;
import org.beep.sbpp.products.repository.ProductKoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final AdminProductRepository productRepository;
    private final ProductKoRepository      koRepository;
    private final ProductEnRepository      enRepository;
    private final ProductJaRepository      jaRepository;
    private final AdminProductImageStorageService imageStorage;
    private final ChatbotEmbeddingService  chatbotEmbeddingService;

    private ProductLangEntity loadLang(ProductBaseEntity base, String lang) {
        return switch(lang.toLowerCase().split("[-_]")[0]) {
            case "ko" -> koRepository.findById(base.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("한국어 데이터 없음 ID=" + base.getProductId()));
            case "en" -> enRepository.findById(base.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("영어 데이터 없음 ID=" + base.getProductId()));
            case "ja" -> jaRepository.findById(base.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("일본어 데이터 없음 ID=" + base.getProductId()));
            default   -> throw new IllegalArgumentException("지원하지 않는 언어: " + lang);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductListDTO> getProducts(Pageable pageable, String keyword, String lang) {
        return productRepository
                .findAllIncludeDeleted(keyword, lang, pageable)
                .map(base -> {
                    ProductLangEntity langE = loadLang(base, lang);
                    return ProductListDTO.fromEntities(base, langE);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailDTO getProduct(Long id, String lang) {
        ProductBaseEntity base = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. ID=" + id));
        ProductLangEntity langE = loadLang(base, lang);
        return ProductDetailDTO.fromEntities(base, langE);
    }

    @Override
    public ProductDetailDTO createProduct(ProductRequestDTO dto, MultipartFile image, String lang) {
        // 1) Base 생성 및 저장
        ProductBaseEntity base = dto.toBaseEntity();
        String[] paths = imageStorage.store(image, dto.getBarcode());
        base.setImgUrl(paths[0]);
        base.setImgThumbUrl(paths[1]);
        base = productRepository.save(base);

        // 2) Lang 생성 및 저장
        ProductLangEntity langE = dto.toLangEntity(base, lang);
        switch(lang.toLowerCase().split("[-_]")[0]) {
            case "ko": koRepository.save((ProductKoEntity)langE); break;
            case "en": enRepository.save((ProductEnEntity)langE); break;
            case "ja": jaRepository.save((ProductJaEntity)langE); break;
        }

        // 3) 챗봇 벡터 업데이트
        chatbotEmbeddingService.addProduct(base, langE);

        return ProductDetailDTO.fromEntities(base, langE);
    }

    @Override
    public ProductDetailDTO updateProduct(Long id, ProductRequestDTO dto, MultipartFile image, String lang) {
        // 1) Base 조회·수정
        ProductBaseEntity base = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. ID=" + id));
        dto.updateBaseEntity(base);
        if (image != null && !image.isEmpty()) {
            String[] paths = imageStorage.store(image, dto.getBarcode());
            base.setImgUrl(paths[0]);
            base.setImgThumbUrl(paths[1]);
        }
        base = productRepository.save(base);

        // 2) Lang 조회·수정
        ProductLangEntity langE = loadLang(base, lang);
        dto.updateLangEntity(langE);
        switch(lang.toLowerCase().split("[-_]")[0]) {
            case "ko": koRepository.save((ProductKoEntity)langE); break;
            case "en": enRepository.save((ProductEnEntity)langE); break;
            case "ja": jaRepository.save((ProductJaEntity)langE); break;
        }

        // 3) 챗봇 벡터 업데이트
        chatbotEmbeddingService.addProduct(base, langE);

        return ProductDetailDTO.fromEntities(base, langE);
    }

    @Override
    public void deleteProduct(Long id) {
        ProductBaseEntity base = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. ID=" + id));
        base.setIsDelete(true);
        productRepository.save(base);
    }
}
