package org.beep.sbpp.products.repository;

import org.beep.sbpp.products.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {

    /**
     * 커서 기반 QueryDSL
     *  - name OR description 키워드 검색
     *  - keyword 없을 때는 category 필터
     *  - soft delete 필터 포함
     *  - 커서 조건: likeCount 또는 score 기준
     *      (ex. likeCount < ?, or (likeCount = ? and productId > ?))
     *  - 정렬: likeCount or score DESC, productId ASC
     */
    List<ProductEntity> findAllWithCursorAndFilter(
            String category,
            String keyword,
            Integer lastValue,        // likeCount or score
            Long lastProductId,
            int size,
            String sortKey            // "likeCount" or "score"
    );
}
