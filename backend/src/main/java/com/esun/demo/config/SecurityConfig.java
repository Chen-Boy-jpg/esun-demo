package com.esun.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // 修正：建議直接 import
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.esun.demo.exception.JwtAuthenticationException;

import java.util.Arrays;

@Configuration
@EnableWebSecurity // ✅ 建議加上這個註解
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Autowired
    private JwtAuthenticationException authException;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 關閉 CSRF
                .csrf(csrf -> csrf.disable())

                // 加入 CORS 配置 (選配，但建議加上)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 異常處理
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authException))

                // 路徑權限設定
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/api/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll() // 圖片路徑
                        .requestMatchers(HttpMethod.GET, "/api/posts/**", "/api/comments/**").permitAll()
                        .anyRequest().authenticated())

                // JWT 過濾器順序
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 設定
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("*")); // 開發階段可設為 *
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}