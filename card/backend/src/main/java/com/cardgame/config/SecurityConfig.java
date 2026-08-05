package com.cardgame.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 *
 * - 无状态会话（JWT）
 * - 放行注册/登录/验证码等公开接口
 * - 其余接口均需认证
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 启用 CORS（必须在最前面，否则 OPTIONS 预检请求会被 Security 拦截）
            .cors(Customizer.withDefaults())

            // 禁用 CSRF（REST API 无状态，不需要 CSRF 保护）
            .csrf(AbstractHttpConfigurer::disable)

            // 无状态会话：不创建 HttpSession
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 请求授权规则
            .authorizeHttpRequests(auth -> auth
                // 公开接口
                .requestMatchers(
                    "/api/auth/**",          // 注册/登录/发送验证码
                    "/uploads/**",            // 上传的静态资源（头像等）
                    "/h2-console/**",        // H2 数据库控制台（仅 dev）
                    "/error"                 // Spring 默认错误页
                ).permitAll()
                // 其余接口需要认证
                .anyRequest().authenticated()
            )

            // 在 UsernamePasswordAuthenticationFilter 之前加入 JWT 过滤器
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // 允许 H2 使用 iframe（仅 dev 环境）
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
