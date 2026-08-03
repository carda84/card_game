package com.cardgame.util;

import com.cardgame.dao.UserDao;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 用户唯一标识生成器
 * 生成 6 位随机数字（100000-999999），保证全局唯一
 * 展示格式：昵称#138992
 */
@Component
public class IdGenerator {

    private static final int TAG_LENGTH = 6;
    private static final int MIN_VALUE = 100000;
    private static final int MAX_VALUE = 999999;
    private static final int MAX_RETRIES = 50;

    private final UserDao userDao;

    public IdGenerator(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 生成一个全局唯一的 6 位数字标识
     *
     * @return 唯一标识字符串（如 "138992"）
     * @throws IllegalStateException 如果重试次数超限
     */
    public String generateUniqueTag() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            String tag = String.valueOf(ThreadLocalRandom.current().nextInt(MIN_VALUE, MAX_VALUE + 1));
            if (!userDao.existsByUniqueTag(tag)) {
                return tag;
            }
        }
        throw new IllegalStateException("无法生成唯一标识，请重试");
    }
}
