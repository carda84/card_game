package com.cardgame.config;

import com.cardgame.util.GameConstants;
import org.springframework.context.annotation.Configuration;

/**
 * 游戏全局参数配置
 * 常量值统一在 GameConstants 中管理
 */
@Configuration
public class GameConfig {
    // 游戏参数统一由 GameConstants 提供，此配置类为未来扩展预留
}
