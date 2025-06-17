package org.beep.sbpp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${nginx.root-dir}")
    private String nginxRootDir;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ── 제품 이미지 매핑 ─────────────────────────────────────
        //  URL: /upload/products/{filename}
        //  실제 저장 위치: C:/nginx-1.26.3/html/products/{filename}
        registry.addResourceHandler("/products/**")
                .addResourceLocations("file:" + nginxRootDir + "/products/");

        // ── 공지 이미지 매핑 ─────────────────────────────────────
        //  URL: /upload/notices/{filename}
        //  실제 저장 위치: C:/nginx-1.26.3/html/notices/{filename}
        registry.addResourceHandler("/notices/**")
                .addResourceLocations("file:" + nginxRootDir + "/notices/");

        // ── 포인트 이미지 매핑 ─────────────────────────────────────
        //  URL: /points/{filename}
        //  실제 저장 위치: C:/nginx-1.26.3/html/points/{filename}
        registry.addResourceHandler("/points/**")
                .addResourceLocations("file:" + nginxRootDir + "/points/");



        registry.addResourceHandler("/inquiries/**")
                .addResourceLocations("file:" + nginxRootDir + "/inquiries/");

    }
}
