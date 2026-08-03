package com.cardgame.controller;

import com.cardgame.model.dto.response.CharacterDetailResponse;
import com.cardgame.model.entity.Character;
import com.cardgame.service.CharacterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 人物控制器
 * 提供人物列表/人物详情 API
 *
 * API 路径：/api/characters
 * 所有接口均需认证（Bearer Token）
 */
@RestController
@RequestMapping("/api/characters")
public class CharacterController {

    private final CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    /**
     * 获取所有人物
     * GET /api/characters
     */
    @GetMapping
    public ResponseEntity<List<CharacterDetailResponse>> getAll() {
        List<CharacterDetailResponse> characters = characterService.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(characters);
    }

    /**
     * 根据 ID 获取人物
     * GET /api/characters/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CharacterDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(characterService.findById(id)));
    }

    /**
     * 获取默认人物（免费）
     * GET /api/characters/default
     */
    @GetMapping("/default")
    public ResponseEntity<List<CharacterDetailResponse>> getDefault() {
        List<CharacterDetailResponse> characters = characterService.findDefaultCharacters().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(characters);
    }

    /**
     * 获取可购买人物
     * GET /api/characters/purchasable
     */
    @GetMapping("/purchasable")
    public ResponseEntity<List<CharacterDetailResponse>> getPurchasable() {
        List<CharacterDetailResponse> characters = characterService.findPurchasableCharacters().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(characters);
    }

    private CharacterDetailResponse toResponse(Character c) {
        return CharacterDetailResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .maxHp(c.getMaxHp())
                .deckSize(c.getDeckSize())
                .specialAbilityDesc(c.getSpecialAbilityDesc())
                .initialItems(c.getInitialItems())
                .initialItemList(c.getInitialItemList().stream()
                        .map(i -> i.getDisplayName())
                        .collect(Collectors.toList()))
                .isDefault(c.getIsDefault())
                .price(c.getPrice())
                .imageUrl(c.getImageUrl())
                .build();
    }
}
