package org.beep.sbpp.search.repository;

import org.beep.sbpp.search.document.ProductSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Elasticsearch 상품 도큐먼트 저장소
 * - Spring Data Elasticsearch 기반
 * - 기본 CRUD 및 쿼리 메서드 제공
 */
@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductSearchDocument, String> {

    /**
     * 바코드로 단건 조회 (정확 일치 검색)
     */
    ProductSearchDocument findByBarcode(String barcode);

    /**
     * 상품명 일부 포함 여부로 검색 (match query)
     */
    Iterable<ProductSearchDocument> findByNameContaining(String keyword);
}
