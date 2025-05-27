package org.beep.sbpp.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.beep.sbpp.products.entities.ProductEntity;

import java.util.Optional;

/**
 * ProductEntity에 대한 CRUD 및 페이징, 정렬 기능을 제공하는 리포지토리
 */
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    // 기본 제공되는 findAll(Pageable pageable) 메서드로
    // 페이징 및 정렬 기능을 그대로 사용합니다.

    Optional<ProductEntity> findByBarcode(String barcode);

}
