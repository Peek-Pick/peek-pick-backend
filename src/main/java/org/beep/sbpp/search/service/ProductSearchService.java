package org.beep.sbpp.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.entities.ProductEntity;
import org.beep.sbpp.search.document.ProductSearchDocument;
import org.beep.sbpp.search.repository.ProductSearchRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 최신 Spring Data Elasticsearch (5.4.5 기준) 기반
 * Elasticsearch 상품 검색 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductSearchRepository productSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * PostgreSQL 상품 데이터를 Elasticsearch 인덱스로 동기화
     */
    public void syncFromEntity(ProductEntity entity) {
        if (entity == null) return;
        productSearchRepository.save(ProductSearchDocument.fromEntity(entity));
        log.info("상품 인덱싱 완료: productId={}, name={}", entity.getProductId(), entity.getName());
    }

    /**
     * 키워드 및 카테고리 기반 검색
     *
     * @param keyword 검색어
     * @param category 카테고리 (nullable)
     * @param sortKey 정렬 기준: likeCount, score, _score
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 검색된 상품 목록
     */
    public List<ProductListDTO> search(String keyword, String category, String sortKey, int page, int size) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(qb -> {
                    BoolQuery.Builder bool = QueryBuilders.bool().must(QueryBuilders.match(m -> m
                            .field("isDelete")
                            .query(false)
                    ));

                    if (keyword != null && !keyword.isBlank()) {
                        bool.must(QueryBuilders.multiMatch(m -> m
                                .fields("name", "description", "category")
                                .query(keyword)
                        ));
                    }

                    if (category != null && !category.isBlank()) {
                        bool.filter(QueryBuilders.match(m -> m
                                .field("category")
                                .query(category)
                        ));
                    }

                    return qb.bool(bool.build());
                })
                .withPageable(PageRequest.of(page, size, getSort(sortKey)))
                .build();

        SearchHits<ProductSearchDocument> hits = elasticsearchOperations.search(query, ProductSearchDocument.class);

        return hits.stream()
                .map(hit -> ProductListDTO.fromSearchDocument(hit.getContent()))
                .collect(Collectors.toList());
    }

    /**
     * 정렬 기준 변환
     */
    private Sort getSort(String sortKey) {
        return switch (sortKey) {
            case "likeCount" -> Sort.by(Sort.Order.desc("likeCount"));
            case "score" -> Sort.by(Sort.Order.desc("score"));
            default -> Sort.by(Sort.Order.desc("_score")); // 기본: 정확도
        };
    }
}
