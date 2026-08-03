package com.cardgame.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 卡牌详情响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDetailResponse {

    private Long id;
    private String name;
    private Integer attack;
    private Boolean isSpecialAttack;
    private Integer health;
    private Integer bloodCost;
    private Integer boneCost;
    private String sigils;
    private List<String> sigilList;
    private String races;
    private List<String> raceList;
    private Integer maxDeckCount;
    private Boolean isLegendary;
    private Boolean canShuffle;
    private Boolean canSacrifice;
    private Integer price;
    private String description;
    private String imageUrl;

    /** 献祭类型描述（如 "2血" / "4骨"） */
    private String sacrificeDesc;

    /** 卡牌简要格式（如 2-3-2-空袭-狼） */
    private String briefDesc;
}
