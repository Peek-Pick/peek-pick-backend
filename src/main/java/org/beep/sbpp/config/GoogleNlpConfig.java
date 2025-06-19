//package org.beep.sbpp.config;
//
//import com.google.api.gax.core.FixedCredentialsProvider;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.cloud.language.v1.LanguageServiceClient;
//import com.google.cloud.language.v1.LanguageServiceSettings;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//
//@Configuration
//public class GoogleNlpConfig {
//
//    @Bean
//    public LanguageServiceClient languageServiceClient() throws IOException {
////        String json = System.getenv("GCP_KEY_JSON");
////        if (json == null || json.isEmpty()) {
////            throw new IllegalStateException("환경변수 GCP_KEY_JSON이 설정되지 않았습니다.");
////        }
////
////        InputStream keyStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
////        GoogleCredentials credentials = GoogleCredentials.fromStream(keyStream);
////
////        LanguageServiceSettings settings = LanguageServiceSettings.newBuilder()
////                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
////                .build();
//
//        return LanguageServiceClient.create();
//    }
//}
