package com.cardgame.controller;

import com.cardgame.exception.BusinessException;
import com.cardgame.model.dto.response.LeaderboardResponse;
import com.cardgame.model.entity.User;
import com.cardgame.service.PlayerService;
import com.cardgame.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;
    private final JwtUtil jwtUtil;

    public PlayerController(PlayerService playerService, JwtUtil jwtUtil) {
        this.playerService = playerService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentPlayer(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        User user = playerService.getPlayerInfo(userId);
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "nickname", user.getNickname(),
                "uniqueTag", user.getUniqueTag(),
                "fullId", user.getFullId(),
                "email", user.getEmail(),
                "gold", user.getGold(),
                "points", user.getPoints(),
                "createdAt", user.getCreatedAt().toString()
        ));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardResponse>> getLeaderboard(
            @RequestParam(defaultValue = "20") int top) {
        return ResponseEntity.ok(playerService.getLeaderboard(top));
    }
}
