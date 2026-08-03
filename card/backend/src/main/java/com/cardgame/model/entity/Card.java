package com.cardgame.model.entity;

import com.cardgame.model.enums.Race;
import com.cardgame.model.enums.Sigil;
import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 卡牌实体 —— 卡牌模板（非运行时实例）
 *
 * 以邪恶冥刻为原型，每张卡牌拥有的数据：
 *   名称、血量（整形）、攻击力（整形，部分为特殊）、印记、种族、
 *   要求献祭数量（血/骨头）、牌组最大拥有数量
 *
 * 固有属性：名称、血量、攻击力、献祭数
 * 可变属性：印记（可通过特殊事件在对局中赋予）
 *
 * 卡牌展示格式：血量-攻击力-献祭数-印记-种族
 *   献祭数有 * 代表骨头献祭，如蝙蝠 1-1-2* = 血量1 攻击力1 骨头献祭2
 */
@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 卡牌名称 */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * 攻击力：单次攻击对位卡牌所造成的伤害值
     * null 表示"特殊"攻击力（如蚂蚁、蚁后），实际值由 CardLogicService 在游戏进程中计算
     */
    @Column
    private Integer attack;

    /** 标记攻击力是否为"特殊"（null 时此字段为 true） */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isSpecialAttack = false;

    /** 血量：可以承受的伤害最大值，降至 0 或以下死亡 */
    @Column(nullable = false)
    private Integer health;

    /**
     * 血献祭数：要求献祭等量的己方卡牌（可以为 0）
     * 与 boneCost 互斥，同一张卡只有一种献祭类型
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer bloodCost = 0;

    /**
     * 骨头献祭数：要求消耗等量的骨头
     * 骨头是双方卡牌因献祭和死亡留下的物品，每张卡牌默认生成 1 个骨头
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer boneCost = 0;

    /**
     * 印记列表：逗号分隔的中文印记名称（如 "空袭,水袭"）
     * 参考 {@link Sigil} 枚举
     */
    @Column(length = 500)
    private String sigils;

    /**
     * 种族列表：逗号分隔的中文种族名称（如 "狼,虫"）
     * 种族可能不唯一（如融合兽拥有多个种族）
     * 参考 {@link Race} 枚举
     */
    @Column(length = 200)
    private String races;

    /**
     * 牌组最大拥有数量：在备战牌组中该卡牌最多同时存在的数量
     * 0 表示不能选入初始牌组（如松鼠、兔子、尾巴等衍生/特殊卡牌）
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer maxDeckCount = 0;

    /** 是否为传奇卡（牌组中传奇卡总数不能大于 3） */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isLegendary = false;

    /**
     * 是否可以进入洗牌堆
     * false 表示死亡是永久的（松鼠、蜜蜂、兔子、尾巴、破碎的蛋、铃铛、堤坝）
     * 参考 carda.txt 洗牌逻辑
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean canShuffle = true;

    /**
     * 是否可以被献祭
     * false 表示不能被选为献祭对象（铃铛、堤坝、破碎的蛋）
     * 参考 carda.txt 献祭操作声明
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean canSacrifice = true;

    /** 卡牌描述（扩展字段） */
    @Column(length = 500)
    private String description;

    /** 卡牌图片 URL（扩展字段） */
    @Column(length = 500)
    private String imageUrl;

    /** 卡牌价格（用于商店购买） */
    @Column(nullable = false)
    @Builder.Default
    private Integer price = 0;

    // ==================== 辅助方法 ====================

    /**
     * 获取印记列表（解析为 Sigil 枚举列表）
     */
    public List<Sigil> getSigilList() {
        if (sigils == null || sigils.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(sigils.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Sigil::fromDisplayName)
                .collect(Collectors.toList());
    }

    /**
     * 获取种族列表（解析为 Race 枚举列表）
     */
    public List<Race> getRaceList() {
        if (races == null || races.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(races.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Race::fromDisplayName)
                .collect(Collectors.toList());
    }

    /**
     * 是否拥有指定印记
     */
    public boolean hasSigil(Sigil sigil) {
        return getSigilList().contains(sigil);
    }

    /**
     * 是否拥有指定种族
     */
    public boolean hasRace(Race race) {
        return getRaceList().contains(race);
    }

    /**
     * 是否可以选入牌组
     */
    public boolean canBeInDeck() {
        return maxDeckCount > 0;
    }

    /**
     * 是否为血献祭卡牌
     */
    public boolean isBloodSacrifice() {
        return bloodCost > 0 && boneCost == 0;
    }

    /**
     * 是否为骨头献祭卡牌
     */
    public boolean isBoneSacrifice() {
        return boneCost > 0;
    }

    /**
     * 获取献祭描述（用于展示）
     * 如 "2血" 或 "4骨"
     */
    public String getSacrificeDesc() {
        if (bloodCost > 0) return bloodCost + "血";
        if (boneCost > 0) return boneCost + "骨";
        return "0";
    }

    /**
     * 获取卡牌简要描述（格式：血量-攻击力-献祭数-印记-种族）
     * 如蝙蝠：1-1-2*-空袭-
     */
    public String getBriefDesc() {
        String atk = isSpecialAttack ? "特殊" : String.valueOf(attack);
        String cost = boneCost > 0 ? boneCost + "*" : String.valueOf(bloodCost);
        String s = sigils != null ? sigils : "";
        String r = races != null ? races : "";
        return health + "-" + atk + "-" + cost + "-" + s + "-" + r;
    }
}
