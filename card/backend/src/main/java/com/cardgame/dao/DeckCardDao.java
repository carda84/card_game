package com.cardgame.dao;

import com.cardgame.model.entity.DeckCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeckCardDao extends JpaRepository<DeckCard, Long> {

    List<DeckCard> findByDeckId(Long deckId);

    void deleteByDeckId(Long deckId);

    long countByDeckId(Long deckId);
}
