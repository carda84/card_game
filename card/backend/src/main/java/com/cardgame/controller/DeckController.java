package com.cardgame.controller;

import com.cardgame.model.dto.request.AddCardToDeckRequest;
import com.cardgame.model.dto.request.CreateDeckRequest;
import com.cardgame.model.dto.request.RemoveCardFromDeckRequest;
import com.cardgame.model.dto.request.RenameDeckRequest;
import com.cardgame.model.dto.response.CardDetailResponse;
import com.cardgame.model.dto.response.DeckDetailResponse;
import com.cardgame.service.DeckService;
import com.cardgame.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/decks")
public class DeckController {

    private final DeckService deckService;
    private final JwtUtil jwtUtil;

    public DeckController(DeckService deckService, JwtUtil jwtUtil) {
        this.deckService = deckService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<DeckDetailResponse> createDeck(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateDeckRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(deckService.createDeck(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<DeckDetailResponse>> getUserDecks(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(deckService.getUserDecks(userId));
    }

    @GetMapping("/{deckId}")
    public ResponseEntity<DeckDetailResponse> getDeck(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long deckId) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(deckService.getDeckDetail(userId, deckId));
    }

    @PostMapping("/add-card")
    public ResponseEntity<Void> addCard(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AddCardToDeckRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        deckService.addCardToDeck(userId, request.getDeckId(), request.getCardId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove-card")
    public ResponseEntity<Void> removeCard(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody RemoveCardFromDeckRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        deckService.removeCardFromDeck(userId, request.getDeckId(), request.getCardId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{deckId}")
    public ResponseEntity<Void> deleteDeck(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long deckId) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        deckService.deleteDeck(userId, deckId);
        return ResponseEntity.ok().build();
    }

    /** 获取用户已拥有的卡牌（用于组卡） */
    @GetMapping("/owned-cards")
    public ResponseEntity<List<CardDetailResponse>> getOwnedCards(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(deckService.getOwnedCards(userId));
    }

    /** 重命名卡组 */
    @PostMapping("/rename")
    public ResponseEntity<DeckDetailResponse> renameDeck(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody RenameDeckRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(deckService.renameDeck(userId, request.getDeckId(), request.getName()));
    }
}
