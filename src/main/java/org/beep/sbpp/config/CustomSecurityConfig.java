// File ▶ Project View: src/main/java/org/beep/sbpp/config/CustomSecurityConfig.java

package org.beep.sbpp.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;       // ← 추가
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;               // ← 추가
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
public class CustomSecurityConfig {

    // 1) 이 빈을 Primary로 지정
    @Bean
    @Primary
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(List.of("http://localhost:5173"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public CorsFilter corsFilter(@Qualifier("corsConfigurationSource") CorsConfigurationSource source) {
        return new CorsFilter(source);
    }


    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        log.info("---------- securityFilterChain 시작 ----------");

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authz -> authz
                                .requestMatchers("/admin/notices/**").permitAll()
                        // 1) GET 목록 조회는 모두 공개
//                        .requestMatchers(HttpMethod.GET, "/admin/notices/**").permitAll()
                        // 2) POST/PUT/DELETE 등 쓰기 작업만 ADMIN 권한 필요
//                        .requestMatchers("/admin/notices/**").hasRole("ADMIN")
                        // 3) 나머지 요청은 모두 허용 (필요시 변경)
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());

        return http.build();
    }
}
