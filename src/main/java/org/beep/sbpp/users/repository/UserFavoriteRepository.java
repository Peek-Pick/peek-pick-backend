// src/main/java/org/beep/sbpp/users/repository/UserFavoriteRepository.java
package org.beep.sbpp.users.repository;

import org.beep.sbpp.products.entities.ProductLikeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFavoriteRepository extends JpaRepository<ProductLikeEntity, Long> {
    // Pageable 내부에 담긴 sort(modDate DESC 등)를 그대로 적용합니다.
    Page<ProductLikeEntity> findAllByUserEntityUserIdAndIsDeleteFalse(Long userId, Pageable pageable);
}
