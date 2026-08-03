package com.cardgame.dao;

import com.cardgame.model.entity.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeckDao extends JpaRepository<Deck, Long> {

    List<Deck> findByUserId(Long userId);

    List<Deck> findByUserIdAndCharacterId(Long userId, Long characterId);

    long countByUserId(Long userId);
}
