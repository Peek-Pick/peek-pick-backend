// src/main/java/org/beep/sbpp/search/service/ProductSearchServiceImpl.java
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
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {

    private final ElasticsearchOperations esOps;

    @Override
    public List<ProductListDTO> search(String keyword,
                                       String category,
                                       String sortKey,
                                       Integer lastValue,
                                       Long lastProductId,
                                       int size,
                                       String lang) {

        // 다국어 인덱스 지정
        IndexCoordinates coords = IndexCoordinates.of("products-" + lang);

        // 정렬 정의
        Sort sort = switch (sortKey) {
            case "likeCount" -> Sort.by(
                    Sort.Order.desc("likeCount"),
                    Sort.Order.asc("productId")
            );
            case "score" -> Sort.by(
                    Sort.Order.desc("score"),
                    Sort.Order.asc("productId")
            );
            default -> Sort.by(
                    Sort.Order.desc("_score"),
                    Sort.Order.asc("productId")
            );
        };

        // 쿼리 빌드
        NativeQuery query = NativeQuery.builder()
                .withQuery(qb -> {
                    BoolQuery.Builder bool = QueryBuilders.bool()
                            .must(QueryBuilders.match(m -> m.field("isDelete").query(false)));
                    if (keyword != null && !keyword.isBlank()) {
                        bool.should(QueryBuilders.multiMatch(m -> m
                                .query(keyword)
                                .fields("name.autocomplete^3", "name^5", "description^1")
                        ));
                        bool.minimumShouldMatch("1");
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
                .build();
        query.setSort(sort);

        // 커서(search_after) 조건
        if (!"_score".equals(sortKey) && lastValue != null && lastProductId != null) {
            query.setSearchAfter(List.of(lastValue, lastProductId));
        }

        SearchHits<ProductSearchDocument> hits =
                esOps.search(query, ProductSearchDocument.class, coords);

        log.info("🔍 [{}] ES 커서 검색: keyword={}, category={}, sortKey={}, hits={}",
                lang, keyword, category, sortKey, hits.getTotalHits());

        return hits.stream()
                .map(hit -> ProductListDTO.fromSearchDocument(hit.getContent()))
                .toList();
    }

    @Override
    public PageResponse<ProductListDTO> searchByScore(String keyword,
                                                      String category,
                                                      int page,
                                                      int size,
                                                      String lang) {

        IndexCoordinates coords = IndexCoordinates.of("products-" + lang);

        log.info("📊 [{}] ES 정확도 검색: page={}, size={}, kw={}, cat={}",
                lang, page, size, keyword, category);

        NativeQuery query = NativeQuery.builder()
                .withQuery(qb -> {
                    BoolQuery.Builder bool = QueryBuilders.bool()
                            .must(QueryBuilders.match(m -> m.field("isDelete").query(false)));
                    if (keyword != null && !keyword.isBlank()) {
                        bool.should(QueryBuilders.multiMatch(m -> m
                                .query(keyword)
                                .fields("name.autocomplete^3", "name^5", "description^1")
                        ));
                        bool.minimumShouldMatch("1");
                    }
                    if (category != null && !category.isBlank()) {
                        bool.filter(QueryBuilders.term(t -> t
                                .field("category.keyword")
                                .value(category)
                        ));
                    }
                    return qb.bool(bool.build());
                })
                .withPageable(PageRequest.of(page, size))
                .withSort(Sort.by(Sort.Order.desc("_score")))
                .build();

        SearchHits<ProductSearchDocument> hits =
                esOps.search(query, ProductSearchDocument.class, coords);

        List<ProductListDTO> content = hits.stream()
                .map(hit -> ProductListDTO.fromSearchDocument(hit.getContent()))
                .toList();

        return PageResponse.of(content, false);
    }
}
