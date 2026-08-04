package com.cardgame.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * 跨域配置
 * 允许前端开发服务器（Vite 默认 5173）及局域网设备访问后端 API
 * 通过 CorsConfigurationSource Bean 被 Spring Security 的 .cors() 自动集成
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:http://localhost:5173,http://127.0.0.1:5173}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 精确匹配的来源
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        // 通配符匹配局域网 IP（如 192.168.x.x:5173）
        config.setAllowedOriginPatterns(List.of(
                "http://192.168.*.*:5173",
                "http://10.*.*.*:5173",
                "http://172.16.*.*:5173",
                "http://172.17.*.*:5173",
                "http://172.18.*.*:5173",
                "http://172.19.*.*:5173",
                "http://172.2*.*.*:5173",
                "http://172.3*.*.*:5173"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
