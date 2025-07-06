package org.beep.sbpp.products.repository;

import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.ProductTagEntity;
import org.beep.sbpp.tags.entities.TagEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductTagRepository extends JpaRepository<ProductTagEntity, Long> {
    Optional<ProductTagEntity> findByProductBaseEntityAndTagEntity(ProductBaseEntity productEntity, TagEntity tagEntity);

    // 특정 상품의 가장 많이 사용된 태그 1개 가져오기
    @Query("SELECT pt.tagEntity.tagName FROM ProductTagEntity pt WHERE pt.productEntity.productId = :productId ORDER BY pt.tagCount DESC LIMIT 1")
    List<String> findTopTagByProductId(@Param("productId") Long productId, Pageable pageable);
}