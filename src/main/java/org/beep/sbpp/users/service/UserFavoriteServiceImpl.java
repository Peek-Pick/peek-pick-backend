package org.beep.sbpp.users.service;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.common.PageResponse;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.ProductLangEntity;
import org.beep.sbpp.products.entities.ProductLikeEntity;
import org.beep.sbpp.products.repository.ProductEnRepository;
import org.beep.sbpp.products.repository.ProductJaRepository;
import org.beep.sbpp.products.repository.ProductKoRepository;
import org.beep.sbpp.users.repository.UserFavoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserFavoriteService 인터페이스 구현체
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserFavoriteServiceImpl implements UserFavoriteService {

    private final UserFavoriteRepository favoriteRepository;
    private final ProductKoRepository koRepository;
    private final ProductEnRepository enRepository;
    private final ProductJaRepository jaRepository;


    /**
     * 사용자가 찜한 상품 목록을 커서 기반으로 조회한다.
     * - ProductLikeEntity 에서 연관된 ProductBaseEntity 꺼내
     *   ProductListDTO.fromEntityWithModDate() 로 변환하여 반환.
     */
    @Override
    public PageResponse<ProductListDTO> getFavoriteProducts(Long userId, Integer size, LocalDateTime lastModDate, Long lastProductId, String lang) {
        // 1) userId + isDelete=false + 커서 조건 기준으로 조회 (size + 1개 조회)
        List<ProductLikeEntity> likeList =
                favoriteRepository.findAllByCursor(userId, lastModDate, lastProductId, size + 1);

        // 2) ProductLikeEntity → ProductBaseEntity + modDate → ProductListDTO 매핑
        List<ProductListDTO> dtoList = likeList.stream()
                .limit(size) // 초과분 제거
                .map(like -> {
                    ProductBaseEntity base = like.getProductBaseEntity();
                    ProductLangEntity langE = loadLangEntity(base.getProductId(), lang);
                    // fromEntitiesWithModDate 사용
                    return ProductListDTO.fromEntitiesWithModDate(
                            base,
                            langE,
                            like.getModDate()
                    );
                })
                .collect(Collectors.toList());

        // 3) hasNext 판단 후 PageResponse 반환
        boolean hasNext = likeList.size() > size;
        return PageResponse.of(dtoList, hasNext);
    }

    private ProductLangEntity loadLangEntity(Long productId, String lang) {
        return switch(lang.toLowerCase()) {
            case "ko" -> koRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("한국어 데이터 없음: " + productId));
            case "en" -> enRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("영어 데이터 없음: " + productId));
            case "ja" -> jaRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("일본어 데이터 없음: " + productId));
            default   -> throw new IllegalArgumentException("지원하지 않는 언어: " + lang);
        };
    }
}
