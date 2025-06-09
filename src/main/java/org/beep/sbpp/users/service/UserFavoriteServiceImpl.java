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
     * 사용자가 찜한 상품 목록 조회
     * - UserFavoriteRepository.findAllByUserEntityUserIdAndIsDeleteFalse(...) 호출
     *   Pageable에 담긴 sort(modDate DESC 등)를 그대로 적용
     */
    @Override
    public Page<ProductListDTO> getFavoriteProducts(Long userId, Pageable pageable) {
        // 1) userId + isDelete=false + pageable(sort=modDate DESC 등) 기준으로 페이징 조회
        Page<ProductLikeEntity> likePage =
                favoriteRepository.findAllByUserEntityUserIdAndIsDeleteFalse(userId, pageable);

        // 2) Page<ProductLikeEntity> → List<ProductListDTO> 매핑
        List<ProductListDTO> dtoList = likePage.stream()
                .map(likeEntity -> {
                    var p = likeEntity.getProductEntity(); // 연관된 ProductEntity
                    return new ProductListDTO(
                            p.getProductId(),
                            p.getBarcode(),
                            p.getName(),
                            p.getCategory(),
                            p.getImgUrl(),
                            p.getLikeCount(),
                            p.getReviewCount(),
                            p.getScore()
                    );
                })
                .collect(Collectors.toList());

        // 3) PageImpl 생성: content, pageable, totalElements
        return new PageImpl<>(dtoList, pageable, likePage.getTotalElements());
    }
}
