package org.beep.sbpp.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 루트 디렉토리의 .env 파일을 가장 먼저 읽어
 * Spring Environment와 System Properties에 반영
 */
public class EnvVarPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            File envFile = new File(".env");
            if (!envFile.exists()) return;

            Map<String, Object> props = new HashMap<>();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(envFile), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#") || !line.contains("=")) continue;

                    String[] pair = line.split("=", 2);
                    if (pair.length != 2) continue;

                    String key = pair[0].trim();
                    String value = pair[1].trim();
                    if (!StringUtils.hasText(key)) continue;

                    props.put(key, value);

                    if (key.equals("GOOGLE_APPLICATION_CREDENTIALS")) {
                        System.setProperty(key, value);
                    }
                }
            }

            environment.getPropertySources().addFirst(new MapPropertySource("root-env", props));

        } catch (Exception e) {
            System.err.println("[EnvVarPostProcessor] 루트 .env 로딩 실패: " + e.getMessage());
        }
    }
}
