package org.beep.sbpp.products.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.beep.sbpp.products.dto.ProductListDTO;

public interface ProductService {
    /**
     * likeCount 내림차순으로 페이징된 상품 랭킹 목록을 반환
     */
    Page<ProductListDTO> getRanking(Pageable pageable);
}
