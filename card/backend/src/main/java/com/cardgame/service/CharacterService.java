package com.cardgame.service;

import com.cardgame.model.entity.Character;
import java.util.List;

/**
 * 人物服务接口
 * 负责人物查询（血量、卡组数量、主动/被动技能）
 */
public interface CharacterService {

    /** 获取所有人物 */
    List<Character> findAll();

    /** 根据 ID 获取人物 */
    Character findById(Long id);

    /** 根据名称获取人物 */
    Character findByName(String name);

    /** 获取默认人物列表（免费） */
    List<Character> findDefaultCharacters();

    /** 获取可购买人物列表 */
    List<Character> findPurchasableCharacters();
}
