package com.cardgame.service;

import com.cardgame.model.dto.request.LoginRequest;
import com.cardgame.model.dto.request.RegisterRequest;
import com.cardgame.model.dto.response.LoginResponse;
import com.cardgame.model.dto.response.RegisterResponse;

/**
 * 认证服务接口
 * 负责邮箱注册、验证码发送与校验、JWT 签发
 */
public interface AuthService {

    /**
     * 发送验证码到邮箱
     * 验证码有效期由配置文件决定（默认 10 分钟）
     *
     * @param email 目标邮箱
     */
    void sendCode(String email);

    /**
     * 用户注册
     * 校验验证码 → 校验邮箱未注册 → 创建用户（生成唯一标识） → 返回注册结果
     *
     * @param request 注册请求（邮箱、验证码、密码、昵称）
     * @return 注册响应
     */
    RegisterResponse register(RegisterRequest request);

    /**
     * 用户登录
     * 校验邮箱密码 → 签发 JWT Token → 返回用户信息
     *
     * @param request 登录请求（邮箱、密码）
     * @return 登录响应（含 Token）
     */
    LoginResponse login(LoginRequest request);
}
