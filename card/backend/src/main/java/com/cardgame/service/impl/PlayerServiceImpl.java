package com.cardgame.service.impl;

import com.cardgame.dao.UserDao;
import com.cardgame.exception.BusinessException;
import com.cardgame.model.dto.response.LeaderboardResponse;
import com.cardgame.model.entity.User;
import com.cardgame.service.PlayerService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final UserDao userDao;

    public PlayerServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User getPlayerInfo(Long userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    @Override
    @Transactional
    public void addGold(Long userId, int amount) {
        User user = getPlayerInfo(userId);
        user.setGold(user.getGold() + amount);
        userDao.save(user);
    }

    @Override
    @Transactional
    public void deductGold(Long userId, int amount) {
        User user = getPlayerInfo(userId);
        if (user.getGold() < amount) {
            throw new BusinessException("金币不足");
        }
        user.setGold(user.getGold() - amount);
        userDao.save(user);
    }

    @Override
    @Transactional
    public void addPoints(Long userId, int points) {
        User user = getPlayerInfo(userId);
        user.setPoints(user.getPoints() + points);
        userDao.save(user);
    }

    @Override
    public List<LeaderboardResponse> getLeaderboard(int top) {
        AtomicInteger rank = new AtomicInteger(1);
        return userDao.findAll(PageRequest.of(0, top, Sort.by(Sort.Direction.DESC, "points")))
                .getContent().stream()
                .map(u -> LeaderboardResponse.builder()
                        .rank(rank.getAndIncrement())
                        .userId(u.getId())
                        .nickname(u.getNickname())
                        .uniqueTag(u.getUniqueTag())
                        .points(u.getPoints())
                        .build())
                .collect(Collectors.toList());
    }
}
