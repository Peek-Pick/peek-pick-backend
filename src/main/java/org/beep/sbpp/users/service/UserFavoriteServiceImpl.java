// src/main/java/org/beep/sbpp/users/service/UserFavoriteServiceImpl.java
package org.beep.sbpp.users.service;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.entities.ProductLikeEntity;
import org.beep.sbpp.users.repository.UserFavoriteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 사용자가 찜한 상품 목록을 페이징 조회한다.
     * - ProductLikeEntity 에서 연관된 ProductEntity 꺼내
     *   ProductListDTO.fromEntity() 로 변환하여 반환.
     */
    @Override
    public Page<ProductListDTO> getFavoriteProducts(Long userId, Pageable pageable) {
        // 1) userId + isDelete=false + pageable 기준으로 조회
        Page<ProductLikeEntity> likePage =
                favoriteRepository.findAllByUserEntityUserIdAndIsDeleteFalse(userId, pageable);

        // 2) ProductLikeEntity → ProductEntity → ProductListDTO 매핑
        List<ProductListDTO> dtoList = likePage.stream()
                .map(ProductLikeEntity::getProductEntity)
                .map(ProductListDTO::fromEntity)
                .collect(Collectors.toList());

        // 3) PageImpl 생성
        return new PageImpl<>(dtoList, pageable, likePage.getTotalElements());
    }
}
