package org.beep.sbpp.admin.products.repository;

import org.beep.sbpp.products.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 관리자용 JpaRepository + custom
 */
public interface AdminProductRepository
        extends JpaRepository<ProductEntity, Long>,
        AdminProductRepositoryCustom {
}
