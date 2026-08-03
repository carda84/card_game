package com.cardgame.model.entity;

import com.cardgame.model.enums.ItemType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 人物实体 —— 游戏开始前选择的角色
 *
 * 每个人物拥有：
 *   - 不同的血量（20-40）
 *   - 卡组数量（10-30，必须严格满足，不能要求15张只配10张）
 *   - 特殊能力（分为主动技能和被动技能）
 *   - 初始道具（可为空）
 *   - 是否默认免费（一个默认角色，其余需要购买）
 *
 * 没有给出道具，则没有初始道具
 */
@Entity
@Table(name = "characters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 人物名称 */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /** 人物血量（20-40） */
    @Column(nullable = false)
    private Integer maxHp;

    /**
     * 卡组数量（10-30）
     * 玩家必须严格拥有等于此值的卡牌数量才能进入游戏
     */
    @Column(nullable = false)
    private Integer deckSize;

    /**
     * 特殊能力描述
     * 包含主动技能和被动技能的完整说明
     * 例如：卡德 "游戏开始时，将3张松鼠牌添加至手牌"
     *      格里魔拉 "当己方卡牌因受敌方攻击死亡时，可选择将死亡卡牌在本局移除卡组"
     */
    @Column(length = 500)
    private String specialAbilityDesc;

    /**
     * 初始道具：逗号分隔的道具名称（如 "画笔"）
     * null 或空表示没有初始道具
     */
    @Column(length = 200)
    private String initialItems;

    /**
     * 是否为默认人物（免费）
     * true = 默认人物，注册即可使用
     * false = 需要购买解锁
     */
    @Column(name = "is_default_char", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    /** 解锁价格（金币），isDefault=true 时为 0 */
    @Column(nullable = false)
    @Builder.Default
    private Integer price = 0;

    /** 人物图片 URL */
    @Column(length = 500)
    private String imageUrl;

    // ==================== 辅助方法 ====================

    /**
     * 获取初始道具列表（解析为 ItemType 枚举列表）
     */
    public List<ItemType> getInitialItemList() {
        if (initialItems == null || initialItems.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(initialItems.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(ItemType::fromDisplayName)
                .collect(Collectors.toList());
    }

    /**
     * 是否有初始道具
     */
    public boolean hasInitialItems() {
        return initialItems != null && !initialItems.isBlank();
    }
}
