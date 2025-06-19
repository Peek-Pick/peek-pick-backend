package org.beep.sbpp.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * GCP_KEY_JSON 환경변수에서 JSON 내용을 읽어 임시 파일로 저장하고,
 * GOOGLE_APPLICATION_CREDENTIALS 환경변수로 등록하는 초기 설정 컴포넌트
 */
@Component
public class GoogleCredentialsConfig {

    @PostConstruct
    public void initGoogleCredentials() throws IOException {
        String json = System.getenv("GCP_KEY_JSON");
        if (json == null || json.isEmpty()) {
            throw new IllegalStateException("환경변수 GCP_KEY_JSON이 설정되지 않았습니다.");
        }

        // 임시 파일 생성
        Path tempFile = Paths.get(System.getProperty("java.io.tmpdir"), "gcp-key.json");
        Files.write(tempFile, json.getBytes());

        // GCP SDK가 자동으로 인식할 수 있도록 시스템 속성 설정
        System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", tempFile.toString());
    }
}
