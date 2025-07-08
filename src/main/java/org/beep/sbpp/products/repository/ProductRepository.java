package org.beep.sbpp.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductBaseEntity, Long>, ProductRepositoryCustom {

    Optional<ProductBaseEntity> findByBarcode(String barcode);

    @Query("SELECT COUNT(p) FROM ProductLikeEntity p WHERE p.userEntity.userId = :userId AND p.isDelete = false ")
    Long countWishByUserId(Long userId);
}
