package org.beep.sbpp.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.common.PageResponse;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.search.document.ProductSearchDocument;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Elasticsearch 기반 상품 검색을 처리하는 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    
    
    /**
     * 정확도 순 정렬 : OFFSET 기반 페이징
     * 좋아요 / 별점 순 정렬 :커서 기반 페이징
     */
    @Override
    public List<ProductListDTO> search(String keyword, String category, String sortKey,
                                       Integer lastValue, Long lastProductId, int size) {

        Sort sort = switch (sortKey) {
            case "likeCount" -> Sort.by(
                    Sort.Order.desc("likeCount"),
                    Sort.Order.asc("productId")
            );
            case "score" -> Sort.by(
                    Sort.Order.desc("score"),
                    Sort.Order.asc("productId")
            );
            default -> Sort.by(  // _score 정렬은 여기선 사용하지 않음
                    Sort.Order.desc("_score"),
                    Sort.Order.asc("productId")
            );
        };

        NativeQuery query = NativeQuery.builder()
                .withQuery(qb -> {
                    BoolQuery.Builder bool = QueryBuilders.bool()
                            .must(QueryBuilders.match(m -> m
                                    .field("isDelete")
                                    .query(false)
                            ));

                    if (keyword != null && !keyword.isBlank()) {
                        bool.should(QueryBuilders.multiMatch(m -> m
                                .query(keyword)
                                .fields("name.autocomplete^3", "name^5", "description^1")
                        ));
                        bool.minimumShouldMatch("1"); // ✅ 키워드 반드시 포함
                    }

                    if (category != null && !category.isBlank()) {
                        bool.filter(QueryBuilders.term(t -> t
                                .field("category.keyword")
                                .value(category)
                        ));
                    }

                    return qb.bool(bool.build());
                })
                .withMaxResults(size + 1)
                .withMinScore(2.0f) // ✅ 점수 필터: 2.0 이하 결과 제거
                .build();

        query.setSort(sort);

        // 커서 조건 적용
        if (!"_score".equals(sortKey) && lastValue != null && lastProductId != null) {
            query.setSearchAfter(List.of(lastValue, lastProductId));
        }

        SearchHits<ProductSearchDocument> hits =
                elasticsearchOperations.search(query, ProductSearchDocument.class);
        log.info("🔍 [ES 커서 검색] keyword={}, category={}, sortKey={}, resultCount={}",
                keyword, category, sortKey, hits.getTotalHits());
        return hits.stream()
                .map(hit -> ProductListDTO.fromSearchDocument(hit.getContent()))
                .toList();
    }

    /**
     * 정확도 순 정렬 (OFFSET 기반 페이징)
     */
    @Override
    public PageResponse<ProductListDTO> searchByScore(String keyword, String category, int page, int size) {
        log.info("📊 [ES 정확도 검색] page={}, size={}, keyword={}, category={}", page, size, keyword, category);

        NativeQuery query = NativeQuery.builder()
                .withQuery(qb -> {
                    BoolQuery.Builder bool = QueryBuilders.bool()
                            .must(QueryBuilders.match(m -> m
                                    .field("isDelete")
                                    .query(false)
                            ));

                    if (keyword != null && !keyword.isBlank()) {
                        bool.should(QueryBuilders.multiMatch(m -> m
                                .query(keyword)
                                .fields("name.autocomplete^3", "name^5", "description^1")
                        ));
                        bool.minimumShouldMatch("1"); // ✅ 키워드 반드시 포함
                    }

                    if (category != null && !category.isBlank()) {
                        bool.filter(QueryBuilders.term(t -> t
                                .field("category.keyword")
                                .value(category)
                        ));
                    }

                    return qb.bool(bool.build());
                })
                .withPageable(PageRequest.of(page, size)) // ✅ OFFSET
                .withSort(Sort.by(Sort.Order.desc("_score")))
                .withMinScore(2.0f) // ✅ 정확도 필터 추가
                .build();

        SearchHits<ProductSearchDocument> hits =
                elasticsearchOperations.search(query, ProductSearchDocument.class);

        List<ProductListDTO> content = hits.stream()
                .map(hit -> ProductListDTO.fromSearchDocument(hit.getContent()))
                .toList();
        log.info("🔎 [ES 쿼리 정렬 기준] {}", query.getSort());
        return PageResponse.of(content, false); // totalElements 등 필요 시 확장 가능
    }
}
