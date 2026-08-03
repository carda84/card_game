package com.cardgame.service;

import com.cardgame.model.entity.Level;

import java.util.List;

/** PvE 关卡管理服务 */
public interface LevelService {
    List<Level> getAllLevels();
    Level getLevelById(Long id);
}
