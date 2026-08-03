package com.cardgame.dao;

import com.cardgame.model.entity.GameSession;
import com.cardgame.model.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameSessionDao extends JpaRepository<GameSession, Long> {

    List<GameSession> findByPlayerUserIdAndSessionStatus(Long playerUserId, SessionStatus status);

    List<GameSession> findByOpponentUserIdAndSessionStatus(Long opponentUserId, SessionStatus status);
}
