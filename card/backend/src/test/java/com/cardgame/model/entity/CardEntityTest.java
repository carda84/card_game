package com.cardgame.model.entity;

import com.cardgame.model.enums.Race;
import com.cardgame.model.enums.Sigil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Card 实体辅助方法单元测试
 * 覆盖印记/种族解析、卡牌分类、简要描述等逻辑
 */
class CardEntityTest {

    @Test
    @DisplayName("getSigilList 解析逗号分隔的印记")
    void getSigilList_parsesCommaSeparated() {
        Card card = Card.builder().sigils("空袭,水袭").build();

        List<Sigil> sigils = card.getSigilList();
        assertEquals(2, sigils.size());
        assertTrue(sigils.contains(Sigil.AIR_RAID));
        assertTrue(sigils.contains(Sigil.WATER_RAID));
    }

    @Test
    @DisplayName("getSigilList 无印记返回空列表")
    void getSigilList_nullSigils_returnsEmpty() {
        Card card = Card.builder().sigils(null).build();

        assertTrue(card.getSigilList().isEmpty());
    }

    @Test
    @DisplayName("getSigilList 空字符串返回空列表")
    void getSigilList_emptySigils_returnsEmpty() {
        Card card = Card.builder().sigils("").build();

        assertTrue(card.getSigilList().isEmpty());
    }

    @Test
    @DisplayName("hasSigil 存在指定印记返回 true")
    void hasSigil_present_returnsTrue() {
        Card card = Card.builder().sigils("空袭,水袭").build();

        assertTrue(card.hasSigil(Sigil.AIR_RAID));
    }

    @Test
    @DisplayName("hasSigil 不存在指定印记返回 false")
    void hasSigil_absent_returnsFalse() {
        Card card = Card.builder().sigils("空袭").build();

        assertFalse(card.hasSigil(Sigil.WATER_RAID));
    }

    @Test
    @DisplayName("getRaceList 解析逗号分隔的种族")
    void getRaceList_parsesCommaSeparated() {
        Card card = Card.builder().races("狼,虫").build();

        List<Race> races = card.getRaceList();
        assertEquals(2, races.size());
        assertTrue(races.contains(Race.WOLF));
        assertTrue(races.contains(Race.INSECT));
    }

    @Test
    @DisplayName("getRaceList 无种族返回空列表")
    void getRaceList_nullRaces_returnsEmpty() {
        Card card = Card.builder().races(null).build();

        assertTrue(card.getRaceList().isEmpty());
    }

    @Test
    @DisplayName("canBeInDeck maxDeckCount > 0 返回 true")
    void canBeInDeck_positiveCount_returnsTrue() {
        Card card = Card.builder().maxDeckCount(2).build();

        assertTrue(card.canBeInDeck());
    }

    @Test
    @DisplayName("canBeInDeck maxDeckCount = 0 返回 false")
    void canBeInDeck_zeroCount_returnsFalse() {
        Card card = Card.builder().maxDeckCount(0).build();

        assertFalse(card.canBeInDeck());
    }

    @Test
    @DisplayName("isBloodSacrifice 血献祭卡返回 true")
    void isBloodSacrifice_bloodCost_returnsTrue() {
        Card card = Card.builder().bloodCost(2).boneCost(0).build();

        assertTrue(card.isBloodSacrifice());
    }

    @Test
    @DisplayName("isBloodSacrifice 骨头献祭卡返回 false")
    void isBloodSacrifice_boneCost_returnsFalse() {
        Card card = Card.builder().bloodCost(0).boneCost(3).build();

        assertFalse(card.isBloodSacrifice());
    }

    @Test
    @DisplayName("isBoneSacrifice 骨头献祭卡返回 true")
    void isBoneSacrifice_boneCost_returnsTrue() {
        Card card = Card.builder().boneCost(4).bloodCost(0).build();

        assertTrue(card.isBoneSacrifice());
    }

    @Test
    @DisplayName("getSacrificeDesc 血献祭返回 '2血'")
    void getSacrificeDesc_bloodCost() {
        Card card = Card.builder().bloodCost(2).boneCost(0).build();

        assertEquals("2血", card.getSacrificeDesc());
    }

    @Test
    @DisplayName("getSacrificeDesc 骨头献祭返回 '4骨'")
    void getSacrificeDesc_boneCost() {
        Card card = Card.builder().bloodCost(0).boneCost(4).build();

        assertEquals("4骨", card.getSacrificeDesc());
    }

    @Test
    @DisplayName("getSacrificeDesc 无献祭返回 '0'")
    void getSacrificeDesc_noCost() {
        Card card = Card.builder().bloodCost(0).boneCost(0).build();

        assertEquals("0", card.getSacrificeDesc());
    }

    @Test
    @DisplayName("getBriefDesc 格式化输出血量-攻击力-献祭数-印记-种族")
    void getBriefDesc_formatsCorrectly() {
        Card card = Card.builder()
                .health(2)
                .attack(3)
                .isSpecialAttack(false)
                .bloodCost(1)
                .boneCost(0)
                .sigils("空袭")
                .races("狼")
                .build();

        String desc = card.getBriefDesc();
        assertEquals("2-3-1-空袭-狼", desc);
    }

    @Test
    @DisplayName("getBriefDesc 特殊攻击力显示 '特殊'")
    void getBriefDesc_specialAttack() {
        Card card = Card.builder()
                .health(1)
                .attack(null)
                .isSpecialAttack(true)
                .bloodCost(0)
                .boneCost(2)
                .sigils(null)
                .races(null)
                .build();

        String desc = card.getBriefDesc();
        assertEquals("1-特殊-2*--", desc);
    }
}
