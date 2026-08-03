package com.cardgame.service;

import com.cardgame.model.dto.response.BattleRecordResponse;

import java.util.List;

/** 对战记录服务 */
public interface RecordService {
    List<BattleRecordResponse> getRecentRecords(Long userId);
    BattleRecordResponse getRecordDetail(Long userId, Long recordId);
}
