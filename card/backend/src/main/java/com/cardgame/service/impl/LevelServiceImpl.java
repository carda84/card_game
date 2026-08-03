package com.cardgame.service.impl;

import com.cardgame.dao.LevelDao;
import com.cardgame.exception.BusinessException;
import com.cardgame.model.entity.Level;
import com.cardgame.service.LevelService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LevelServiceImpl implements LevelService {

    private final LevelDao levelDao;

    public LevelServiceImpl(LevelDao levelDao) {
        this.levelDao = levelDao;
    }

    @Override
    public List<Level> getAllLevels() {
        return levelDao.findAllByOrderByDifficultyAsc();
    }

    @Override
    public Level getLevelById(Long id) {
        return levelDao.findById(id)
                .orElseThrow(() -> new BusinessException("关卡不存在"));
    }
}
