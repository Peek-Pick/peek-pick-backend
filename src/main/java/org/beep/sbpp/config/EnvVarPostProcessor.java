package org.beep.sbpp.config;

import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EnvVarPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, org.springframework.boot.SpringApplication application) {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(".env"));

            Map<String, Object> map = new HashMap<>();
            for (String name : props.stringPropertyNames()) {
                String value = props.getProperty(name);
                map.put(name, value);
                // 시스템 속성에도 등록 (구글 클라우드 SDK 같은 것들이 필요할 수 있음)
                System.setProperty(name, value);
            }

            environment.getPropertySources().addFirst(new MapPropertySource("early-env", map));
            System.out.println("✅ .env 파일 조기 로딩 성공");
        } catch (IOException e) {
            System.err.println("❌ .env 파일 로딩 실패: " + e.getMessage());
        }
    }
}
