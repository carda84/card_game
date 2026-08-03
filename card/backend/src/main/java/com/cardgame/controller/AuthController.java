package com.cardgame.controller;

import com.cardgame.model.dto.request.LoginRequest;
import com.cardgame.model.dto.request.RegisterRequest;
import com.cardgame.model.dto.request.SendCodeRequest;
import com.cardgame.model.dto.response.LoginResponse;
import com.cardgame.model.dto.response.RegisterResponse;
import com.cardgame.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * 处理邮箱注册、验证码发送、登录、登出
 *
 * API 路径：/api/auth/*
 * 所有接口均为公开接口，无需 Token
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 发送验证码到邮箱
     * POST /api/auth/send-code
     * Body: { "email": "user@example.com" }
     */
    @PostMapping("/send-code")
    public ResponseEntity<Map<String, String>> sendCode(@Valid @RequestBody SendCodeRequest request) {
        authService.sendCode(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "验证码已发送"));
    }

    /**
     * 用户注册
     * POST /api/auth/register
     * Body: { "email": "...", "verificationCode": "...", "password": "...", "nickname": "..." }
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 用户登录
     * POST /api/auth/login
     * Body: { "email": "...", "password": "..." }
     * Response: { "token": "...", "nickname": "...", "uniqueTag": "...", "gold": 100, "points": 0 }
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 用户登出
     * POST /api/auth/logout
     * 由于 JWT 是无状态的，登出由前端清除 Token 实现
     * 此接口仅作为语义占位，后续可加黑名单机制
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "登出成功"));
    }
}
