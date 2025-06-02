package org.beep.sbpp.config;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
public class CustomSecurityConfig {

    // 1) 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2) 인메모리 사용자 (ROLE_ADMIN)
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        var admin = User.withUsername("admin")
                .password(encoder.encode("adminpass"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    // 3) Security 필터 체인 전체 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        log.info("Configuring SecurityFilterChain for admin notices…");

        http
                // CORS 필터 등록
                .cors(Customizer.withDefaults())

                // CSRF 비활성화 (API 서버이므로)
                .csrf(csrf -> csrf.disable())

                // 세션 비활성화(Stateless)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 인가 규칙
                .authorizeHttpRequests(auth -> auth
                                // 1) Pre-flight OPTIONS 요청은 모두 허용
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                // 2) 업로드된 이미지 파일은 인증 없이 누구나 조회 가능
                                .requestMatchers("/uploads/**").permitAll()

                                // 3) /admin/notices/** 전부는 ADMIN 권한 필요
//                        .requestMatchers("/admin/notices/**").hasRole("ADMIN")
                                .requestMatchers("/admin/notices/**").permitAll()
                                .requestMatchers("/admin/points/**").permitAll()
                                .requestMatchers("/points/**").permitAll()
                                .requestMatchers("/inquiries/**").permitAll()

                                // auth 인증
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/api/v1/reviews/**").permitAll()

                                // 4) 그 외 모든 요청은 인증 필요
//                        .anyRequest().authenticated()
                                .anyRequest().permitAll()
                )

                // HTTP Basic Auth 사용
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // 4) CORS 전역 설정 (vite dev 서버에서 오는 요청만 허용)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOriginPatterns(List.of("http://localhost:5173"));
        cors.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","HEAD","OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization","Cache-Control","Content-Type"));
        cors.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

}