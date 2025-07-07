// src/main/java/org/beep/sbpp/util/scheduler/ProductSearchScheduler.java
package org.beep.sbpp.util.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.ProductLangEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.products.repository.ProductKoRepository;
import org.beep.sbpp.products.repository.ProductEnRepository;
import org.beep.sbpp.products.repository.ProductJaRepository;
import org.beep.sbpp.search.service.ProductESIndexService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSearchScheduler {

    private final ProductRepository       productRepository;
    private final ProductKoRepository     koRepo;
    private final ProductEnRepository     enRepo;
    private final ProductJaRepository     jaRepo;
    private final ProductESIndexService   indexService;

    /** ⏱ 3시간마다 정각 실행 */
    @Scheduled(cron = "0 0 */3 * * *")
    public void reindexProducts() {
        log.info("🔄 Elasticsearch 상품 색인(기본+다국어) 갱신 시작");
        List<ProductBaseEntity> bases = productRepository.findAll();

        for (String lang : List.of("ko","en","ja")) {
            try {
                for (ProductBaseEntity base : bases) {
                    ProductLangEntity langE = loadLang(base, lang);
                    indexService.indexOne(base, langE, lang);
                }
                log.info("✅ [{}] 상품 색인 갱신 완료: 총 {}건", lang, bases.size());
            } catch (Exception e) {
                log.error("❌ [{}] 상품 색인 갱신 실패: {}", lang, e.getMessage(), e);
            }
        }
    }

    private ProductLangEntity loadLang(ProductBaseEntity base, String lang) {
        return switch (lang) {
            case "ko" -> koRepo.findById(base.getProductId())
                    .orElseThrow(() ->
                            new IllegalStateException("KO 데이터 없음: " + base.getProductId()));
            case "en" -> enRepo.findById(base.getProductId())
                    .orElseThrow(() ->
                            new IllegalStateException("EN 데이터 없음: " + base.getProductId()));
            case "ja" -> jaRepo.findById(base.getProductId())
                    .orElseThrow(() ->
                            new IllegalStateException("JA 데이터 없음: " + base.getProductId()));
            default   -> throw new IllegalArgumentException("지원하지 않는 언어: " + lang);
        };
    }
}
