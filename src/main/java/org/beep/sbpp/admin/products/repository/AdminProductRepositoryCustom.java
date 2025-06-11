package org.beep.sbpp.admin.products.repository;

import org.beep.sbpp.products.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 관리자용: soft-delete 포함, 제목·설명(keyword) 검색 전용 커스텀 인터페이스
 */
public interface AdminProductRepositoryCustom {
    Page<ProductEntity> findAllIncludeDeleted(String keyword, Pageable pageable);
}
