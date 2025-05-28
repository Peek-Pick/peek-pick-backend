package org.beep.sbpp.products.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.beep.sbpp.products.entities.ProductEntity;

public interface ProductRepositoryCustom {
    /**
     * QueryDSL 기반으로 category 필터 + Pageable.sort(nullsLast 포함) 적용
     */
    Page<ProductEntity> findAllWithFilterAndSort(String category, Pageable pageable);
}
