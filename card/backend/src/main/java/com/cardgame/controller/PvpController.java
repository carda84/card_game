package com.cardgame.controller;

import com.cardgame.model.dto.request.StartMatchRequest;
import com.cardgame.model.dto.response.MatchResultResponse;
import com.cardgame.service.MatchService;
import com.cardgame.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pvp")
public class PvpController {

    private final MatchService matchService;
    private final JwtUtil jwtUtil;

    public PvpController(MatchService matchService, JwtUtil jwtUtil) {
        this.matchService = matchService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/queue")
    public ResponseEntity<Void> joinQueue(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody StartMatchRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        matchService.joinQueue(userId, request.getDeckId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/queue")
    public ResponseEntity<Void> leaveQueue(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        matchService.leaveQueue(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/match")
    public ResponseEntity<MatchResultResponse> findMatch(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(matchService.findMatch(userId));
    }
}
