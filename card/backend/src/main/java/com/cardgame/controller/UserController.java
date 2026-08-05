package com.cardgame.controller;

import com.cardgame.dao.BattleRecordDao;
import com.cardgame.dao.DeckDao;
import com.cardgame.dao.UserDao;
import com.cardgame.model.dto.response.UserProfileResponse;
import com.cardgame.model.entity.BattleRecord;
import com.cardgame.model.entity.User;
import com.cardgame.model.enums.BattleMode;
import com.cardgame.model.enums.BattleResult;
import com.cardgame.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 用户控制器
 * 提供个人简介、用户信息相关接口
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserDao userDao;
    private final BattleRecordDao battleRecordDao;
    private final DeckDao deckDao;
    private final JwtUtil jwtUtil;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public UserController(UserDao userDao, BattleRecordDao battleRecordDao,
                          DeckDao deckDao, JwtUtil jwtUtil) {
        this.userDao = userDao;
        this.battleRecordDao = battleRecordDao;
        this.deckDao = deckDao;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 获取当前用户个人简介
     * GET /api/user/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        User user = userDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 查询所有对战记录
        List<BattleRecord> records = battleRecordDao.findByUserIdOrderByCreatedAtDesc(userId);

        int totalBattles = records.size();
        int wins = 0, losses = 0, surrenders = 0;
        int pveBattles = 0, pveWins = 0, pvpBattles = 0, pvpWins = 0;

        for (BattleRecord r : records) {
            if (r.getResult() == BattleResult.WIN) wins++;
            else if (r.getResult() == BattleResult.LOSE) losses++;
            else if (r.getResult() == BattleResult.SURRENDER) surrenders++;

            if (r.getMode() == BattleMode.PVE) {
                pveBattles++;
                if (r.getResult() == BattleResult.WIN) pveWins++;
            } else if (r.getMode() == BattleMode.PVP) {
                pvpBattles++;
                if (r.getResult() == BattleResult.WIN) pvpWins++;
            }
        }

        double winRate = totalBattles > 0 ? Math.round(wins * 1000.0 / totalBattles) / 10.0 : 0.0;

        // 卡组数量
        long deckCount = deckDao.countByUserId(userId);

        // 已解锁人物数量
        int characterCount = 0;
        if (user.getUnlockedCharacters() != null && !user.getUnlockedCharacters().isBlank()) {
            characterCount = user.getUnlockedCharacters().split(",").length;
        }

        // 排名（按积分降序）
        List<User> allUsers = userDao.findAll();
        int rank = (int) allUsers.stream()
                .filter(u -> u.getPoints() > user.getPoints())
                .count() + 1;

        UserProfileResponse response = UserProfileResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .uniqueTag(user.getUniqueTag())
                .fullId(user.getFullId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().format(DATE_FMT) : "")
                .gold(user.getGold())
                .points(user.getPoints())
                .totalBattles(totalBattles)
                .wins(wins)
                .losses(losses)
                .surrenders(surrenders)
                .winRate(winRate)
                .pveBattles(pveBattles)
                .pveWins(pveWins)
                .pvpBattles(pvpBattles)
                .pvpWins(pvpWins)
                .deckCount((int) deckCount)
                .characterCount(characterCount)
                .rank(rank)
                .build();

        return ResponseEntity.ok(response);
    }
}
