package org.beep.sbpp.users.repository;

import org.beep.sbpp.products.entities.ProductLikeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 사용자가 찜한(즐겨찾기) ProductLikeEntity를 페이징 조회하기 위한 레포지토리
 * → ProductLikeEntity는 products 패키지에 이미 정의되어 있으므로, 여기에서 import만 사용합니다.
 */
public interface UserFavoriteRepository extends JpaRepository<ProductLikeEntity, Long> {

    /**
     * userEntity.userId가 userId인 레코드 중에서 isDelete=false인 것만,
     * modDate(즐겨찾기 등록·수정 시각) 기준 DESC 정렬로 조회합니다.
     *
     * @param userId   조회 대상 사용자 ID
     * @param pageable 페이지 정보(페이지 번호, 사이즈, 정렬 조건)
     */
    Page<ProductLikeEntity> findAllByUserEntityUserIdAndIsDeleteFalseOrderByModDateDesc(
            Long userId,
            Pageable pageable
    );
}
