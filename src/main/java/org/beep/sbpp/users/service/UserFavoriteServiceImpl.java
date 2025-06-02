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
 * FavoriteService 인터페이스 구현체
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserFavoriteServiceImpl implements UserFavoriteService {

    private final UserFavoriteRepository favoriteRepository;

    /**
     * 사용자가 찜한 상품 목록 조회
     * - FavoriteRepository.findAllByUserEntityUserIdAndIsDeleteFalseOrderByModDateDesc(...) 호출
     * - Page<ProductLikeEntity> 를 받아서, ProductLikeEntity.getProductEntity()에서 ProductEntity를 꺼내
     *   ProductListDTO(new ProductListDTO(...))로 매핑한 뒤 Page<ProductListDTO>로 반환
     */
    @Override
    public Page<ProductListDTO> getFavoriteProducts(Long userId, Pageable pageable) {
        // 1) userId + isDelete=false + modDate DESC 기준으로 페이징 조회
        Page<ProductLikeEntity> likePage =
                favoriteRepository.findAllByUserEntityUserIdAndIsDeleteFalseOrderByModDateDesc(userId, pageable);

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
