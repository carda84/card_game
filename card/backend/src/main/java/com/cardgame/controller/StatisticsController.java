package com.cardgame.controller;

import com.cardgame.model.dto.response.CardStatResponse;
import com.cardgame.service.StatisticsService;
import com.cardgame.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final JwtUtil jwtUtil;

    public StatisticsController(StatisticsService statisticsService, JwtUtil jwtUtil) {
        this.statisticsService = statisticsService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/my")
    public ResponseEntity<List<CardStatResponse>> getMyStats(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(statisticsService.getCardStats(userId));
    }

    @GetMapping("/global")
    public ResponseEntity<List<CardStatResponse>> getGlobalStats() {
        return ResponseEntity.ok(statisticsService.getGlobalCardStats());
    }
}
