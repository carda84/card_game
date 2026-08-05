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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Value("${app.upload.avatar-dir:uploads/avatars}")
    private String avatarDir;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/gif", "image/webp");

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

        // 头像 URL
        String avatarUrl = null;
        if (user.getAvatar() != null && !user.getAvatar().isBlank()) {
            avatarUrl = "/uploads/avatars/" + user.getAvatar();
        }

        UserProfileResponse response = UserProfileResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .uniqueTag(user.getUniqueTag())
                .fullId(user.getFullId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().format(DATE_FMT) : "")
                .avatar(avatarUrl)
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

    /**
     * 上传头像
     * POST /api/user/avatar
     * Content-Type: multipart/form-data
     */
    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        User user = userDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 校验文件
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "文件不能为空"));
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body(Map.of("message", "文件大小不能超过 5MB"));
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            return ResponseEntity.badRequest().body(Map.of("message", "仅支持 JPG、PNG、GIF、WebP 格式"));
        }

        try {
            // 确保目录存在
            Path uploadPath = Paths.get(avatarDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = "avatar_" + userId + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;

            // 保存文件
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            // 删除旧头像
            if (user.getAvatar() != null && !user.getAvatar().isBlank()) {
                Path oldPath = uploadPath.resolve(user.getAvatar());
                Files.deleteIfExists(oldPath);
            }

            // 更新用户记录
            user.setAvatar(fileName);
            userDao.save(user);

            String avatarUrl = "/uploads/avatars/" + fileName;
            return ResponseEntity.ok(Map.of("avatar", avatarUrl, "message", "头像上传成功"));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "上传失败：" + e.getMessage()));
        }
    }
}
