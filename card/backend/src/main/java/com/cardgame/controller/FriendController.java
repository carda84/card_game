package com.cardgame.controller;

import com.cardgame.model.dto.request.AddFriendRequest;
import com.cardgame.model.dto.request.InviteBattleRequest;
import com.cardgame.model.dto.request.SendMessageRequest;
import com.cardgame.model.dto.response.FriendInfoResponse;
import com.cardgame.model.entity.FriendMessage;
import com.cardgame.model.entity.User;
import com.cardgame.service.FriendService;
import com.cardgame.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;
    private final JwtUtil jwtUtil;

    public FriendController(FriendService friendService, JwtUtil jwtUtil) {
        this.friendService = friendService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<Void> addFriend(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AddFriendRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        friendService.addFriend(userId, request.getTargetId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{friendUserId}")
    public ResponseEntity<Void> removeFriend(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long friendUserId) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        friendService.removeFriend(userId, friendUserId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<FriendInfoResponse>> getFriends(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(friendService.getFriendList(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<User> searchUser(@RequestParam String tag) {
        return ResponseEntity.ok(friendService.searchUser(tag));
    }

    @PostMapping("/message")
    public ResponseEntity<Void> sendMessage(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody SendMessageRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        friendService.sendMessage(userId, request.getFriendUserId(), request.getContent());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/messages/{friendUserId}")
    public ResponseEntity<List<FriendMessage>> getMessages(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long friendUserId) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(friendService.getMessages(userId, friendUserId));
    }
}
