// src/main/java/org/beep/sbpp/search/config/ElasticsearchIndexConfig.java
package org.beep.sbpp.search.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticsearchIndexConfig {

    private final ElasticsearchOperations esOps;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    // 언어별 인덱스 리스트
    private static final List<String> INDEX_NAMES = List.of(
            "products-ko", "products-en", "products-ja"
    );

    @EventListener(ApplicationReadyEvent.class)
    public void createIndices() {
        for (String idx : INDEX_NAMES) {
            IndexCoordinates coords = IndexCoordinates.of(idx);
            IndexOperations ops = esOps.indexOps(coords);
            if (!ops.exists()) {
                try {
                    log.info("✅ '{}' 인덱스를 생성합니다.", idx);

                    // 접미사(ko/en/ja) 추출
                    String lang = idx.substring(idx.lastIndexOf('-') + 1);

                    // 언어별 설정·매핑 파일 경로
                    String settingsPath = String.format(
                            "classpath:elasticsearch/products-settings-%s.json", lang);
                    String mappingsPath = String.format(
                            "classpath:elasticsearch/products-mappings-%s.json", lang);

                    Map<String,Object> settings = loadJson(settingsPath);
                    Map<String,Object> mappings = loadJson(mappingsPath);

                    ops.create(Document.from(settings));
                    ops.putMapping(Document.from(mappings));

                    log.info("✅ '{}' 생성 완료.", idx);
                } catch (Exception e) {
                    log.error("❌ '{}' 생성 실패: {}", idx, e.getMessage(), e);
                }
            } else {
                log.info("✅ '{}' 인덱스 이미 존재", idx);
            }
        }
    }

    private Map<String,Object> loadJson(String resourcePath) throws Exception {
        Resource res = resourceLoader.getResource(resourcePath);
        try (InputStream is = res.getInputStream()) {
            return objectMapper.readValue(is, new TypeReference<>() {});
        }
    }
}
