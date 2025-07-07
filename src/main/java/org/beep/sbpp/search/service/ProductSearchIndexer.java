// src/main/java/org/beep/sbpp/search/service/ProductSearchIndexer.java
package org.beep.sbpp.search.service;

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
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSearchIndexer implements CommandLineRunner {

    private final ProductRepository   productRepository;
    private final ProductKoRepository koRepo;
    private final ProductEnRepository enRepo;
    private final ProductJaRepository jaRepo;
    private final ProductESIndexService indexService;

    /**
     * 애플리케이션 시작 시 전체 ES 색인 시작
     * before: 루프 안에서 findById() 반복 호출 → N+1 문제 발생
     * after: findAllById() + indexBulk() 호출로 N+1 해결
     */
    @Override
    public void run(String... args) {
        log.info("🔄 애플리케이션 시작 시 전체 ES 색인 시작");

        // 1) 모든 BaseEntity를 한 번에 조회 (1회 쿼리)
        List<ProductBaseEntity> bases = productRepository.findAll();

        // 2) ID 리스트 추출
        List<Long> ids = bases.stream()
                .map(ProductBaseEntity::getProductId)
                .toList();

        // 3) 언어별 엔티티를 한 번에 로드 (findAllById → 한 방 쿼리)
        List<ProductKoEntity>   koEntities = koRepo.findAllById(ids);
        List<ProductEnEntity>   enEntities = enRepo.findAllById(ids);
        List<ProductJaEntity>   jaEntities = jaRepo.findAllById(ids);

        // 4) bulk 인덱싱을 위해 ProductLangEntity 리스트로 변환
        List<ProductLangEntity> koLangs = new ArrayList<>(koEntities);  // N+1 해결: 한 방에 모두 로드된 후 변환
        List<ProductLangEntity> enLangs = new ArrayList<>(enEntities);
        List<ProductLangEntity> jaLangs = new ArrayList<>(jaEntities);

        // 5) bulk 인덱싱 호출
        indexService.indexBulk(bases, koLangs, "ko");
        log.info("✅ [ko] 전체 ES bulk 색인 완료: {}건", koLangs.size());

        indexService.indexBulk(bases, enLangs, "en");
        log.info("✅ [en] 전체 ES bulk 색인 완료: {}건", enLangs.size());

        indexService.indexBulk(bases, jaLangs, "ja");
        log.info("✅ [ja] 전체 ES bulk 색인 완료: {}건", jaLangs.size());
    }
}
