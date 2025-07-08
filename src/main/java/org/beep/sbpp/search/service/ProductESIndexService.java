package org.beep.sbpp.search.service;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.ProductLangEntity;
import org.beep.sbpp.search.document.ProductSearchDocument;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductESIndexService {

    private final ElasticsearchOperations esOps;

    /**
     * 단일 문서 인덱싱 (upsert)
     */
    public void indexOne(
            ProductBaseEntity base,
            ProductLangEntity lang,
            String langCode
    ) {
        ProductSearchDocument doc = ProductSearchDocument.fromEntities(base, lang);
        IndexCoordinates coords = IndexCoordinates.of("products-" + langCode);
        esOps.save(doc, coords);
    }

    /**
     * 다수 문서 bulk 인덱싱
     */
    public void indexBulk(
            List<ProductBaseEntity> bases,
            List<ProductLangEntity> langs,
            String langCode
    ) {
        IndexCoordinates coords = IndexCoordinates.of("products-" + langCode);
        // bases와 langs 크기가 같다고 가정
        List<ProductSearchDocument> docs =
                bases.stream()
                        .map(base -> {
                            // find corresponding lang
                            ProductLangEntity langE = langs.stream()
                                    .filter(l -> l.getProductBase().getProductId().equals(base.getProductId()))
                                    .findFirst()
                                    .orElseThrow();
                            return ProductSearchDocument.fromEntities(base, langE);
                        })
                        .collect(Collectors.toList());
        // bulk save
        esOps.save(docs, coords);
    }
}
