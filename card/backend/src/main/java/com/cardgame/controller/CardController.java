package com.cardgame.controller;

import com.cardgame.model.dto.response.CardDetailResponse;
import com.cardgame.model.entity.Card;
import com.cardgame.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 卡牌控制器
 * 提供卡牌数据查询 API（卡牌池、印记、种族）
 *
 * API 路径：/api/cards
 * 所有接口均需认证（Bearer Token）
 */
@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * 获取所有卡牌
     * GET /api/cards
     */
    @GetMapping
    public ResponseEntity<List<CardDetailResponse>> getAll() {
        List<CardDetailResponse> cards = cardService.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cards);
    }

    /**
     * 根据 ID 获取卡牌
     * GET /api/cards/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CardDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(cardService.findById(id)));
    }

    /**
     * 获取可选入牌组的卡牌
     * GET /api/cards/deckable
     */
    @GetMapping("/deckable")
    public ResponseEntity<List<CardDetailResponse>> getDeckable() {
        List<CardDetailResponse> cards = cardService.findDeckableCards().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cards);
    }

    /**
     * 按种族筛选卡牌
     * GET /api/cards/race/{race}
     */
    @GetMapping("/race/{race}")
    public ResponseEntity<List<CardDetailResponse>> getByRace(@PathVariable String race) {
        List<CardDetailResponse> cards = cardService.findByRace(race).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cards);
    }

    /**
     * 按印记筛选卡牌
     * GET /api/cards/sigil/{sigil}
     */
    @GetMapping("/sigil/{sigil}")
    public ResponseEntity<List<CardDetailResponse>> getBySigil(@PathVariable String sigil) {
        List<CardDetailResponse> cards = cardService.findBySigil(sigil).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cards);
    }

    private CardDetailResponse toResponse(Card c) {
        return CardDetailResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .attack(c.getAttack())
                .isSpecialAttack(c.getIsSpecialAttack())
                .health(c.getHealth())
                .bloodCost(c.getBloodCost())
                .boneCost(c.getBoneCost())
                .sigils(c.getSigils())
                .sigilList(c.getSigilList().stream().map(s -> s.getDisplayName()).collect(Collectors.toList()))
                .races(c.getRaces())
                .raceList(c.getRaceList().stream().map(r -> r.getDisplayName()).collect(Collectors.toList()))
                .maxDeckCount(c.getMaxDeckCount())
                .isLegendary(c.getIsLegendary())
                .canShuffle(c.getCanShuffle())
                .canSacrifice(c.getCanSacrifice())
                .price(c.getPrice())
                .description(c.getDescription())
                .imageUrl(c.getImageUrl())
                .sacrificeDesc(c.getSacrificeDesc())
                .briefDesc(c.getBriefDesc())
                .build();
    }
}
