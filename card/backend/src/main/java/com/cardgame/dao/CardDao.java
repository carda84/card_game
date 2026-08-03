package com.cardgame.dao;

import com.cardgame.model.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardDao extends JpaRepository<Card, Long> {

    Optional<Card> findByName(String name);

    /** 查找所有可选入牌组的卡牌（maxDeckCount > 0） */
    List<Card> findByMaxDeckCountGreaterThan(int minCount);

    /** 按种族筛选 */
    @Query("SELECT c FROM Card c WHERE c.races LIKE %:race%")
    List<Card> findByRace(@Param("race") String race);

    /** 按印记筛选 */
    @Query("SELECT c FROM Card c WHERE c.sigils LIKE %:sigil%")
    List<Card> findBySigil(@Param("sigil") String sigil);

    /** 查找所有传奇卡 */
    List<Card> findByIsLegendaryTrue();
}
