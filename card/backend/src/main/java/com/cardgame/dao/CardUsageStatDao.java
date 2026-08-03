package com.cardgame.dao;

import com.cardgame.model.entity.CardUsageStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardUsageStatDao extends JpaRepository<CardUsageStat, Long> {

    List<CardUsageStat> findByUserId(Long userId);

    Optional<CardUsageStat> findByUserIdAndCardId(Long userId, Long cardId);
}
