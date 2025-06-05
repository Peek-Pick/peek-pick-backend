package org.beep.sbpp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * application.yml 에서 설정한 업로드 디렉토리.
     *   notice:
     *     image:
     *       upload-dir: C:/peek-pick/uploads/notices/
     */
//    @Value("${notice.image.upload-dir}")
//    private String uploadDir;

    @Value("${points.image.upload-dir}")
    private String uploadDir2;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/admin/notices/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    /**
     * 외부 파일 시스템의 uploadDir 경로를
     *  /uploads/** URL 로 매핑합니다.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // uploadDir 이 "C:/peek-pick/uploads/notices/" 라면,
        //   URL /uploads/notices/abc.jpg → 실제 C:/peek-pick/uploads/notices/abc.jpg
//        String resourceLocation = "file:" + uploadDir;
//        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations(resourceLocation);

        String resourceLocation2 = "file:" + uploadDir2 + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation2);

        registry.addResourceHandler("/upload/inquiries/**")
                .addResourceLocations("file:///C:/nginx-1.26.3/html/inquiries/");
    }
}
