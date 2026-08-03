package com.cardgame.service;

import com.cardgame.model.dto.response.FriendInfoResponse;
import com.cardgame.model.entity.FriendMessage;
import com.cardgame.model.entity.User;

import java.util.List;

/** 好友管理服务 */
public interface FriendService {
    void addFriend(Long userId, String targetId);
    void removeFriend(Long userId, Long friendUserId);
    List<FriendInfoResponse> getFriendList(Long userId);
    User searchUser(String uniqueTag);
    void sendMessage(Long userId, Long friendUserId, String content);
    List<FriendMessage> getMessages(Long userId, Long friendUserId);
}
