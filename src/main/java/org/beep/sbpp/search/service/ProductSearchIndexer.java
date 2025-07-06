package org.beep.sbpp.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.search.document.ProductSearchDocument;
import org.beep.sbpp.search.repository.ProductSearchRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 애플리케이션 시작 시 PostgreSQL의 상품 데이터를
 * Elasticsearch에 색인하는 컴포넌트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSearchIndexer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;

    @Override
    public void run(String... args) {
        // PostgreSQL에서 전체 상품 로드
        List<ProductBaseEntity> entities = productRepository.findAll();

        // 각 엔티티를 ES 도큐먼트로 변환
        List<ProductSearchDocument> documents = entities.stream()
                .map(ProductSearchDocument::fromEntity)
                .toList();

        // Elasticsearch에 색인
        productSearchRepository.saveAll(documents);

        log.info("✅ Elasticsearch에 상품 색인 완료: 총 {}건", documents.size());
    }
}
