// src/main/java/org/beep/sbpp/search/service/ProductSearchIndexer.java
package org.beep.sbpp.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.ProductLangEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.products.repository.ProductKoRepository;
import org.beep.sbpp.products.repository.ProductEnRepository;
import org.beep.sbpp.products.repository.ProductJaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSearchIndexer implements CommandLineRunner {

    private final ProductRepository     productRepository;
    private final ProductKoRepository   koRepo;
    private final ProductEnRepository   enRepo;
    private final ProductJaRepository   jaRepo;
    private final ProductESIndexService indexService;

    @Override
    public void run(String... args) {
        log.info("🔄 애플리케이션 시작 시 전체 ES 색인 시작");
        List<ProductBaseEntity> bases = productRepository.findAll();

        for (String lang : List.of("ko", "en", "ja")) {
            int count = 0;
            for (ProductBaseEntity base : bases) {
                ProductLangEntity langE = switch (lang) {
                    case "ko" -> koRepo.findById(base.getProductId())
                            .orElseThrow(() -> new IllegalStateException("KO 누락: " + base.getProductId()));
                    case "en" -> enRepo.findById(base.getProductId())
                            .orElseThrow(() -> new IllegalStateException("EN 누락: " + base.getProductId()));
                    case "ja" -> jaRepo.findById(base.getProductId())
                            .orElseThrow(() -> new IllegalStateException("JA 누락: " + base.getProductId()));
                    default   -> throw new IllegalArgumentException("지원하지 않는 언어: " + lang);
                };
                indexService.indexOne(base, langE, lang);
                count++;
            }
            log.info("✅ [{}] 전체 ES 색인 완료: {}건", lang, count);
        }
    }
}
