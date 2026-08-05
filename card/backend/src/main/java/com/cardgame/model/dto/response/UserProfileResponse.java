package com.cardgame.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 个人简介响应
 * 展示用户的基本信息、资源、对战统计
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    // ===== 基本信息 =====
    private Long userId;
    private String nickname;
    private String uniqueTag;
    private String fullId;       // nickname#uniqueTag
    private String email;
    private String createdAt;    // 注册时间
    private String avatar;       // 头像 URL

    // ===== 资源 =====
    private Integer gold;
    private Integer points;

    // ===== 对战统计 =====
    private Integer totalBattles;
    private Integer wins;
    private Integer losses;
    private Integer surrenders;
    private Double winRate;      // 胜率（百分比，如 65.5）

    // ===== PvE / PvP 分开统计 =====
    private Integer pveBattles;
    private Integer pveWins;
    private Integer pvpBattles;
    private Integer pvpWins;

    // ===== 其他 =====
    private Integer deckCount;      // 卡组数量
    private Integer characterCount; // 已解锁人物数量
    private Integer rank;           // 排名（按积分）
}
