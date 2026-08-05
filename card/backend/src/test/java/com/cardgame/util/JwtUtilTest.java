package com.cardgame.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 单元测试
 * 覆盖 Token 生成、解析、校验的核心逻辑
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // 至少 32 字符的密钥
        jwtUtil = new JwtUtil(
                "test-secret-key-at-least-32-chars-long-for-unit-test",
                24,   // 普通有效期 24 小时
                336   // 记住我有效期 336 小时
        );
    }

    @Test
    @DisplayName("生成 Token 后应能正确解析出 userId")
    void generateToken_shouldContainCorrectUserId() {
        String token = jwtUtil.generateToken(42L, "alice@example.com");

        Long userId = jwtUtil.getUserIdFromToken(token);
        assertEquals(42L, userId);
    }

    @Test
    @DisplayName("生成 Token 后应能正确解析出 email")
    void generateToken_shouldContainCorrectEmail() {
        String token = jwtUtil.generateToken(1L, "bob@example.com");

        String email = jwtUtil.getEmailFromToken(token);
        assertEquals("bob@example.com", email);
    }

    @Test
    @DisplayName("合法 Token 校验应返回 true")
    void validateToken_validToken_returnsTrue() {
        String token = jwtUtil.generateToken(1L, "test@example.com");

        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("篡改的 Token 校验应返回 false")
    void validateToken_tamperedToken_returnsFalse() {
        String token = jwtUtil.generateToken(1L, "test@example.com");
        String tampered = token + "tampered";

        assertFalse(jwtUtil.validateToken(tampered));
    }

    @Test
    @DisplayName("空字符串 Token 校验应返回 false")
    void validateToken_emptyToken_returnsFalse() {
        assertFalse(jwtUtil.validateToken(""));
    }

    @Test
    @DisplayName("null Token 校验应返回 false")
    void validateToken_nullToken_returnsFalse() {
        assertFalse(jwtUtil.validateToken(null));
    }

    @Test
    @DisplayName("记住我 Token 也应可正常校验和解析")
    void generateRememberMeToken_shouldBeValid() {
        String token = jwtUtil.generateRememberMeToken(99L, "remember@example.com");

        assertTrue(jwtUtil.validateToken(token));
        assertEquals(99L, jwtUtil.getUserIdFromToken(token));
        assertEquals("remember@example.com", jwtUtil.getEmailFromToken(token));
    }

    @Test
    @DisplayName("不同密钥生成的 Token 互相不兼容")
    void differentSecrets_tokensIncompatible() {
        JwtUtil otherUtil = new JwtUtil(
                "another-secret-key-at-least-32-chars-long-here",
                24, 336
        );

        String token = jwtUtil.generateToken(1L, "a@b.com");
        // 另一个 JwtUtil 用不同密钥，校验应失败
        assertFalse(otherUtil.validateToken(token));
    }
}
