package org.beep.sbpp.admin.products.repository;

import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 관리자용 JpaRepository + custom
 */
public interface AdminProductRepository extends JpaRepository<ProductBaseEntity, Long>, AdminProductRepositoryCustom {
    @Query("SELECT COUNT(p) FROM ProductBaseEntity p WHERE EXTRACT(MONTH FROM p.regDate) = :month AND EXTRACT(YEAR FROM p.regDate) = :year")
    Long countByMonth(@Param("month") int month, @Param("year") int year);
}
