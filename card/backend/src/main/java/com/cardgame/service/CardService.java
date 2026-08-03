package com.cardgame.service;

import com.cardgame.model.entity.Card;
import java.util.List;

/**
 * 卡牌服务接口
 * 负责卡牌数据查询（卡牌池、印记、种族）
 */
public interface CardService {

    /** 获取所有卡牌 */
    List<Card> findAll();

    /** 根据 ID 获取卡牌 */
    Card findById(Long id);

    /** 根据名称获取卡牌 */
    Card findByName(String name);

    /** 获取所有可选入牌组的卡牌 */
    List<Card> findDeckableCards();

    /** 按种族筛选 */
    List<Card> findByRace(String race);

    /** 按印记筛选 */
    List<Card> findBySigil(String sigil);
}
