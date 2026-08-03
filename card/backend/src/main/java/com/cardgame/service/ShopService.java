package com.cardgame.service;

import com.cardgame.model.dto.response.ShopResponse;

/** 商店服务 */
public interface ShopService {
    /** 获取商店商品列表（所有非默认卡牌，统一 20 金币） */
    ShopResponse getShopItems(Long userId);

    /** 解锁卡牌（购买一次即可无限编入卡组），返回剩余金币 */
    Integer buyCard(Long userId, Long cardId);

    /** 奖励金币（用于对战结算） */
    void awardGold(Long userId, int gold);
}
