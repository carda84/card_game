package com.cardgame.dao;

import com.cardgame.model.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LevelDao extends JpaRepository<Level, Long> {

    List<Level> findByDifficultyLessThanEqual(int maxDifficulty);

    List<Level> findAllByOrderByDifficultyAsc();
}
