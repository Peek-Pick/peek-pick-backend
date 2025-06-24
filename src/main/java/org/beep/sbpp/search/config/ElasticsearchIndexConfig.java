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
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticsearchIndexConfig {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    private static final String INDEX_NAME = "products";

    @EventListener(ApplicationReadyEvent.class)
    public void createProductIndexIfMissing() {
        IndexCoordinates indexCoordinates = IndexCoordinates.of(INDEX_NAME);
        IndexOperations indexOps = elasticsearchOperations.indexOps(indexCoordinates);

        if (!indexOps.exists()) {
            try {
                log.info("✅ Elasticsearch '{}' 인덱스를 새로 생성합니다.", INDEX_NAME);

                Map<String, Object> settingsMap = loadJson("classpath:elasticsearch/products-settings.json");
                Map<String, Object> mappingsMap = loadJson("classpath:elasticsearch/products-mappings.json");

                Document settingsDoc = Document.from(settingsMap);
                Document mappingsDoc = Document.from(mappingsMap);

                indexOps.create(settingsDoc);
                indexOps.putMapping(mappingsDoc);

                log.info("✅ 인덱스 생성 완료.");
            } catch (Exception e) {
                log.error("❌ 인덱스 생성 실패: {}", e.getMessage(), e);
            }
        } else {
            log.info("✅ '{}' 인덱스는 이미 존재합니다.", INDEX_NAME);
        }
    }

    private Map<String, Object> loadJson(String path) throws Exception {
        Resource resource = resourceLoader.getResource(path);
        try (InputStream is = resource.getInputStream()) {
            return objectMapper.readValue(is, new TypeReference<>() {});
        }
    }
}
