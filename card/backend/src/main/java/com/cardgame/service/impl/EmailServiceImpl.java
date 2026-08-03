package com.cardgame.service.impl;

import com.cardgame.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务实现
 * 通过 Spring Mail 发送验证码邮件
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:cowbackstraps@163.com}")
    private String fromAddress;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationCode(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(email);
            message.setSubject("【卡牌游戏】注册验证码");
            message.setText("您的注册验证码为：" + code + "\n\n验证码有效期内有效，请勿泄露给他人。");
            mailSender.send(message);
            log.info("验证码已发送至 {}", email);
        } catch (Exception e) {
            log.error("发送验证码邮件失败，email={}", email, e);
        }
    }
}