// src/main/java/org/beep/sbpp/util/scheduler/ProductSearchScheduler.java
package org.beep.sbpp.util.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.ProductKoEntity;
import org.beep.sbpp.products.entities.ProductEnEntity;
import org.beep.sbpp.products.entities.ProductJaEntity;
import org.beep.sbpp.products.entities.ProductLangEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.products.repository.ProductKoRepository;
import org.beep.sbpp.products.repository.ProductEnRepository;
import org.beep.sbpp.products.repository.ProductJaRepository;
import org.beep.sbpp.search.service.ProductESIndexService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSearchScheduler {

    private final ProductRepository productRepository;
    private final ProductKoRepository koRepo;
    private final ProductEnRepository enRepo;
    private final ProductJaRepository jaRepo;
    private final ProductESIndexService indexService;

    /** ⏱ 12시간마다 실행 */
    @Scheduled(cron = "0 0 */12 * * *")
    public void reindexProducts() {
        log.info("🔄 Elasticsearch 상품 bulk 색인(기본+다국어) 12시간마다 갱신 시작");

        // 1) 모든 상품 조회 (1번 쿼리)
        List<ProductBaseEntity> bases = productRepository.findAll();
        if (bases.isEmpty()) {
            log.info("🔍 조회된 상품이 없어 색인 작업을 건너뜁니다.");
            return;
        }

        // 2) 상품 ID 리스트 추출
        List<Long> ids = bases.stream()
                .map(ProductBaseEntity::getProductId)
                .toList();

        try {
            // 3) 언어별 엔티티를 한 번에 로드 (각 1번씩)
            List<ProductKoEntity> koEntities = koRepo.findAllById(ids);
            List<ProductEnEntity> enEntities = enRepo.findAllById(ids);
            List<ProductJaEntity> jaEntities = jaRepo.findAllById(ids);

            // 4) ProductLangEntity 타입으로 업캐스팅
            List<ProductLangEntity> koLangs = new ArrayList<>(koEntities);
            List<ProductLangEntity> enLangs = new ArrayList<>(enEntities);
            List<ProductLangEntity> jaLangs = new ArrayList<>(jaEntities);

            // 5) bulk 인덱싱 호출 (1회 per 언어)
            indexService.indexBulk(bases, koLangs, "ko");
            log.info("✅ [ko] bulk 색인 완료: {}건", koLangs.size());

            indexService.indexBulk(bases, enLangs, "en");
            log.info("✅ [en] bulk 색인 완료: {}건", enLangs.size());

            indexService.indexBulk(bases, jaLangs, "ja");
            log.info("✅ [ja] bulk 색인 완료: {}건", jaLangs.size());
        } catch (Exception e) {
            log.error("❌ bulk 색인 중 예외 발생: {}", e.getMessage(), e);
        }
    }
}
