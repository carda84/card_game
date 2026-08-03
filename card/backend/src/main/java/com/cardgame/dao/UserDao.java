package com.cardgame.dao;

import com.cardgame.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User 数据访问层
 */
@Repository
public interface UserDao extends JpaRepository<User, Long> {

    /** 根据邮箱查找用户 */
    Optional<User> findByEmail(String email);

    /** 根据唯一标识查找用户（如 #138992） */
    Optional<User> findByUniqueTag(String uniqueTag);

    /** 判断邮箱是否已被注册 */
    boolean existsByEmail(String email);

    /** 判断唯一标识是否已被占用 */
    boolean existsByUniqueTag(String uniqueTag);
}
