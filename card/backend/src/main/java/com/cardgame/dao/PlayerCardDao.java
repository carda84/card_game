package com.cardgame.dao;

import com.cardgame.model.entity.PlayerCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerCardDao extends JpaRepository<PlayerCard, Long> {

    List<PlayerCard> findByUserId(Long userId);

    Optional<PlayerCard> findByUserIdAndCardId(Long userId, Long cardId);

    void deleteByUserId(Long userId);
}
