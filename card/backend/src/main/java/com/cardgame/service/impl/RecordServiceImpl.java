package com.cardgame.service.impl;

import com.cardgame.dao.BattleRecordDao;
import com.cardgame.dao.UserDao;
import com.cardgame.exception.BusinessException;
import com.cardgame.model.dto.response.BattleRecordResponse;
import com.cardgame.model.entity.BattleRecord;
import com.cardgame.model.entity.User;
import com.cardgame.service.RecordService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordServiceImpl implements RecordService {

    private final BattleRecordDao recordDao;
    private final UserDao userDao;

    public RecordServiceImpl(BattleRecordDao recordDao, UserDao userDao) {
        this.recordDao = recordDao;
        this.userDao = userDao;
    }

    @Override
    public List<BattleRecordResponse> getRecentRecords(Long userId) {
        return recordDao.findTop20ByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BattleRecordResponse getRecordDetail(Long userId, Long recordId) {
        BattleRecord record = recordDao.findById(recordId)
                .orElseThrow(() -> new BusinessException("记录不存在"));
        return toResponse(record);
    }

    private BattleRecordResponse toResponse(BattleRecord r) {
        String opponentName = null;
        if (r.getOpponentId() != null) {
            User opponent = userDao.findById(r.getOpponentId()).orElse(null);
            if (opponent != null) opponentName = opponent.getFullId();
        }
        return BattleRecordResponse.builder()
                .id(r.getId())
                .mode(r.getMode().name())
                .result(r.getResult().name())
                .opponentName(opponentName)
                .selfCharacterId(r.getSelfCharacterId())
                .opponentCharacterId(r.getOpponentCharacterId())
                .turns(r.getTurns())
                .reward(r.getReward())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
