package org.beep.sbpp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 로컬 디스크의 uploadDir 경로를 외부 URL '/uploads/**'로 매핑.
     * application.properties 에 uploadDir 과 base-url 을 맞춰 설정하세요.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 예: uploadDir = "uploads/notices/"
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/notices/");
    }
}
