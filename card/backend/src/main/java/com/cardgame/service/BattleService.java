package com.cardgame.service;

import com.cardgame.model.dto.request.*;
import com.cardgame.model.dto.response.*;

/** 战斗核心引擎服务 */
public interface BattleService {
    BattleStartResponse startBattle(Long userId, StartBattleRequest request);
    DrawResultResponse drawCard(Long userId, DrawCardRequest request);
    PlayCardResultResponse playCard(Long userId, PlayCardRequest request);
    void sacrificeCards(Long userId, SacrificeRequest request);
    TurnEndResponse endTurn(Long userId, EndTurnRequest request);
    BattleEndResponse surrender(Long userId, SurrenderRequest request);
    BoardStateResponse getBoardState(Long sessionId);
    /** 为指定用户视角构建棋盘响应（PvP 时翻转数据） */
    BoardStateResponse getBoardStateForCaller(Long sessionId, Long userId);
    void useActiveSkill(Long userId, UseActiveSkillRequest request);
    void useItem(Long userId, UseItemRequest request);
}
