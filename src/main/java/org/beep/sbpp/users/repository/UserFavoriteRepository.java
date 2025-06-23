package org.beep.sbpp.users.repository;

import org.beep.sbpp.products.entities.ProductLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 사용자 찜한 상품 기본 JPA 리포지토리
 * - 커스텀 구현체(UserFavoriteRepositoryImpl)와 함께 동작
 */
public interface UserFavoriteRepository extends JpaRepository<ProductLikeEntity, Long>, UserFavoriteRepositoryCustom {
    // JpaRepository에서 기본 CRUD 제공
}
