package com.cardgame.dao;

import com.cardgame.model.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendDao extends JpaRepository<Friend, Long> {

    List<Friend> findByUserId(Long userId);

    Optional<Friend> findByUserIdAndFriendUserId(Long userId, Long friendUserId);

    boolean existsByUserIdAndFriendUserId(Long userId, Long friendUserId);

    void deleteByUserIdAndFriendUserId(Long userId, Long friendUserId);
}
