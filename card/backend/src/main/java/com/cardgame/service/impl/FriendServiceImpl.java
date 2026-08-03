package com.cardgame.service.impl;

import com.cardgame.dao.*;
import com.cardgame.exception.BusinessException;
import com.cardgame.model.dto.response.BattleRecordResponse;
import com.cardgame.model.dto.response.FriendInfoResponse;
import com.cardgame.model.entity.Friend;
import com.cardgame.model.entity.FriendMessage;
import com.cardgame.model.entity.User;
import com.cardgame.service.FriendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FriendServiceImpl implements FriendService {

    private final FriendDao friendDao;
    private final FriendMessageDao messageDao;
    private final UserDao userDao;
    private final BattleRecordDao recordDao;

    public FriendServiceImpl(FriendDao friendDao, FriendMessageDao messageDao,
                             UserDao userDao, BattleRecordDao recordDao) {
        this.friendDao = friendDao;
        this.messageDao = messageDao;
        this.userDao = userDao;
        this.recordDao = recordDao;
    }

    @Override
    @Transactional
    public void addFriend(Long userId, String targetId) {
        User target = userDao.findByUniqueTag(targetId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        if (target.getId().equals(userId)) {
            throw new BusinessException("不能添加自己为好友");
        }
        if (friendDao.existsByUserIdAndFriendUserId(userId, target.getId())) {
            throw new BusinessException("已经是好友了");
        }
        friendDao.save(Friend.builder().userId(userId).friendUserId(target.getId()).build());
        friendDao.save(Friend.builder().userId(target.getId()).friendUserId(userId).build());
    }

    @Override
    @Transactional
    public void removeFriend(Long userId, Long friendUserId) {
        friendDao.deleteByUserIdAndFriendUserId(userId, friendUserId);
        friendDao.deleteByUserIdAndFriendUserId(friendUserId, userId);
    }

    @Override
    public List<FriendInfoResponse> getFriendList(Long userId) {
        return friendDao.findByUserId(userId).stream()
                .map(f -> {
                    User friend = userDao.findById(f.getFriendUserId()).orElse(null);
                    if (friend == null) return null;
                    return FriendInfoResponse.builder()
                            .userId(friend.getId())
                            .nickname(friend.getNickname())
                            .uniqueTag(friend.getUniqueTag())
                            .points(friend.getPoints())
                            .gold(friend.getGold())
                            .build();
                })
                .filter(f -> f != null)
                .collect(Collectors.toList());
    }

    @Override
    public User searchUser(String uniqueTag) {
        return userDao.findByUniqueTag(uniqueTag).orElse(null);
    }

    @Override
    @Transactional
    public void sendMessage(Long userId, Long friendUserId, String content) {
        messageDao.save(FriendMessage.builder()
                .fromUserId(userId).toUserId(friendUserId).content(content).build());
    }

    @Override
    public List<FriendMessage> getMessages(Long userId, Long friendUserId) {
        return messageDao.findMessagesBetween(userId, friendUserId);
    }
}
