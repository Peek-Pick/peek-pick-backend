package org.beep.sbpp.products.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.beep.sbpp.products.entities.ProductEntity;

public interface ProductRepositoryCustom {
    /**
     * QueryDSL 기반으로
     *  - name OR description 키워드 검색
     *  - keyword 없을 때는 category 필터
     *  - Pageable.sort(NULLS LAST 포함) 적용
     */
    Page<ProductEntity> findAllWithFilterAndSort(String category, String keyword, Pageable pageable);
}
