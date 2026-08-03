package com.cardgame.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * 负责 Token 的生成、解析与校验
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;
    private final long rememberMeExpirationMs;

    public JwtUtil(@Value("${app.jwt.secret}") String secret,
                   @Value("${app.jwt.expiration-hours:24}") int expirationHours,
                   @Value("${app.jwt.remember-me-expiration-hours:336}") int rememberMeExpirationHours) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationHours * 3600L * 1000L;
        this.rememberMeExpirationMs = rememberMeExpirationHours * 3600L * 1000L;
    }

    /**
     * 生成 JWT Token（普通有效期）
     */
    public String generateToken(Long userId, String email) {
        return generateToken(userId, email, expirationMs);
    }

    /**
     * 生成 JWT Token（记住我 - 长有效期）
     */
    public String generateRememberMeToken(Long userId, String email) {
        return generateToken(userId, email, rememberMeExpirationMs);
    }

    private String generateToken(Long userId, String email, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * 从 Token 中提取用户 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    /**
     * 从 Token 中提取邮箱
     */
    public String getEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("email", String.class);
    }

    /**
     * 校验 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}