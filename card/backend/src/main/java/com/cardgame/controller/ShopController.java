package com.cardgame.controller;

import com.cardgame.model.dto.request.BuyCardRequest;
import com.cardgame.model.dto.response.ShopResponse;
import com.cardgame.service.ShopService;
import com.cardgame.util.JwtUtil;
import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    private final ShopService shopService;
    private final JwtUtil jwtUtil;

    public ShopController(ShopService shopService, JwtUtil jwtUtil) {
        this.shopService = shopService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<ShopResponse> getShopItems(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(shopService.getShopItems(userId));
    }

    @PostMapping("/buy")
    public ResponseEntity<Map<String, Object>> buyCard(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody BuyCardRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        Integer remainingGold = shopService.buyCard(userId, request.getCardId());
        return ResponseEntity.ok(Map.of("remainingGold", remainingGold, "message", "购买成功"));
    }
}
