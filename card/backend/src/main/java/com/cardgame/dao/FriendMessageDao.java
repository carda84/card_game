package com.cardgame.dao;

import com.cardgame.model.entity.FriendMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendMessageDao extends JpaRepository<FriendMessage, Long> {

    /** 查询两个用户之间的消息（按时间升序） */
    @Query("SELECT m FROM FriendMessage m WHERE " +
           "(m.fromUserId = :uid1 AND m.toUserId = :uid2) OR " +
           "(m.fromUserId = :uid2 AND m.toUserId = :uid1) " +
           "ORDER BY m.createdAt ASC")
    List<FriendMessage> findMessagesBetween(@Param("uid1") Long uid1, @Param("uid2") Long uid2);

    List<FriendMessage> findByToUserIdOrderByCreatedAtDesc(Long toUserId);
}
