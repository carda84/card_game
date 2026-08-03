package com.cardgame.service;

/**
 * 邮件服务接口
 * 负责发送验证码邮件
 */
public interface EmailService {

    /**
     * 发送验证码到指定邮箱
     *
     * @param email 目标邮箱地址
     * @param code  6 位数字验证码
     */
    void sendVerificationCode(String email, String code);
}
