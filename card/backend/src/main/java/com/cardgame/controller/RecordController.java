package com.cardgame.controller;

import com.cardgame.model.dto.response.BattleRecordResponse;
import com.cardgame.service.RecordService;
import com.cardgame.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;
    private final JwtUtil jwtUtil;

    public RecordController(RecordService recordService, JwtUtil jwtUtil) {
        this.recordService = recordService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<BattleRecordResponse>> getRecords(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(recordService.getRecentRecords(userId));
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<BattleRecordResponse> getRecord(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long recordId) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(recordService.getRecordDetail(userId, recordId));
    }
}
