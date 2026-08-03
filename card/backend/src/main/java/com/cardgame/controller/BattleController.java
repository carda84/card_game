package com.cardgame.controller;

import com.cardgame.model.dto.request.*;
import com.cardgame.model.dto.response.*;
import com.cardgame.service.BattleService;
import com.cardgame.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battle")
public class BattleController {

    private final BattleService battleService;
    private final JwtUtil jwtUtil;

    public BattleController(BattleService battleService, JwtUtil jwtUtil) {
        this.battleService = battleService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/start")
    public ResponseEntity<BattleStartResponse> startBattle(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody StartBattleRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(battleService.startBattle(userId, request));
    }

    @PostMapping("/draw")
    public ResponseEntity<DrawResultResponse> drawCard(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody DrawCardRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(battleService.drawCard(userId, request));
    }

    @PostMapping("/play-card")
    public ResponseEntity<PlayCardResultResponse> playCard(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody PlayCardRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(battleService.playCard(userId, request));
    }

    @PostMapping("/sacrifice")
    public ResponseEntity<Void> sacrifice(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody SacrificeRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        battleService.sacrificeCards(userId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/end-turn")
    public ResponseEntity<TurnEndResponse> endTurn(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody EndTurnRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(battleService.endTurn(userId, request));
    }

    @PostMapping("/surrender")
    public ResponseEntity<BattleEndResponse> surrender(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody SurrenderRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(battleService.surrender(userId, request));
    }

    @GetMapping("/board/{sessionId}")
    public ResponseEntity<BoardStateResponse> getBoardState(
            @PathVariable Long sessionId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        }
        return ResponseEntity.ok(battleService.getBoardStateForCaller(sessionId, userId));
    }

    @PostMapping("/skill")
    public ResponseEntity<Void> useSkill(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UseActiveSkillRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        battleService.useActiveSkill(userId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/item")
    public ResponseEntity<Void> useItem(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UseItemRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        battleService.useItem(userId, request);
        return ResponseEntity.ok().build();
    }
}
