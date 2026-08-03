package com.cardgame.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置
 * PvP 实时对战 + 好友私信
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // TODO: 注册 WebSocket Handler
        // registry.addHandler(battleWebSocketHandler, "/ws/battle").setAllowedOrigins("*");
        // registry.addHandler(chatWebSocketHandler, "/ws/chat").setAllowedOrigins("*");
        // registry.addHandler(matchWebSocketHandler, "/ws/match").setAllowedOrigins("*");
    }
}
