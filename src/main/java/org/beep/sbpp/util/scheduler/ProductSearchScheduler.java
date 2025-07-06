package org.beep.sbpp.util.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.search.document.ProductSearchDocument;
import org.beep.sbpp.search.repository.ProductSearchRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSearchScheduler {

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;
    
    @Scheduled(cron = "0 0 */3 * * *") // ⏱ 3시간마다 실행 (정각)
    public void reindexProducts() {
        log.info("🔄 Elasticsearch 상품 색인 갱신 시작");

        try {
            List<ProductBaseEntity> products = productRepository.findAll();
            List<ProductSearchDocument> documents = products.stream()
                    .map(ProductSearchDocument::fromEntity)
                    .toList();

            productSearchRepository.saveAll(documents);

            log.info("✅ 상품 색인 갱신 완료: 총 {}건", documents.size());
        } catch (Exception e) {
            log.error("❌ 상품 색인 갱신 실패: {}", e.getMessage(), e);
        }
    }
}
