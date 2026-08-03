package com.cardgame.dao;

import com.cardgame.model.entity.BattleRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BattleRecordDao extends JpaRepository<BattleRecord, Long> {

    /** 查询用户最近的 20 条对战记录 */
    List<BattleRecord> findTop20ByUserIdOrderByCreatedAtDesc(Long userId);

    List<BattleRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
}
