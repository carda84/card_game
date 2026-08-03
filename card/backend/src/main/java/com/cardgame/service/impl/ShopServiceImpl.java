package com.cardgame.service.impl;

import com.cardgame.dao.*;
import com.cardgame.exception.BusinessException;
import com.cardgame.model.dto.response.CardDetailResponse;
import com.cardgame.model.dto.response.ShopResponse;
import com.cardgame.model.entity.Card;
import com.cardgame.model.entity.PlayerCard;
import com.cardgame.model.entity.User;
import com.cardgame.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商店服务实现
 *
 * 规则（基于 value.txtx）：
 *   - 用户注册时获得 18 张默认卡牌（不在商店出售）
 *   - 商店展示所有非默认卡牌，统一售价 20 金币
 *   - 购买 = 解锁，已解锁的卡牌可无限次编入卡组
 *   - 对战获胜 +100 金币，失败 +30 金币
 */
@Slf4j
@Service
public class ShopServiceImpl implements ShopService {

    /** 统一售价 */
    private static final int UNIFORM_PRICE = 20;

    /** 默认卡牌名称集合（注册时发放，不在商店出售） */
    private static final Set<String> DEFAULT_CARD_NAMES = Set.of(
            "白鼬", "牛蛙", "狼", "狼崽", "麋鹿", "小麋鹿",
            "麻雀", "渡鸦", "负鼠", "蝰蛇", "臭鼬", "石龙子",
            "蜂巢", "蟑螂", "响尾蛇", "螳螂", "鼹鼠", "环形虫"
    );

    private final CardDao cardDao;
    private final PlayerCardDao playerCardDao;
    private final UserDao userDao;

    public ShopServiceImpl(CardDao cardDao,
                           PlayerCardDao playerCardDao,
                           UserDao userDao) {
        this.cardDao = cardDao;
        this.playerCardDao = playerCardDao;
        this.userDao = userDao;
    }

    /**
     * 获取商店商品列表
     * 展示所有非默认卡牌，统一 20 金币，标注是否已解锁
     */
    @Override
    public ShopResponse getShopItems(Long userId) {
        // 查出用户已解锁的卡牌 ID 集合
        var ownedCardIds = playerCardDao.findByUserId(userId).stream()
                .map(PlayerCard::getCardId)
                .collect(Collectors.toSet());

        List<ShopResponse.ShopItemDto> items = cardDao.findAll().stream()
                // 排除默认卡牌
                .filter(c -> !DEFAULT_CARD_NAMES.contains(c.getName()))
                // 排除衍生/特殊卡牌
                .filter(c -> c.getPrice() > 0 || c.getMaxDeckCount() > 0)
                .map(c -> ShopResponse.ShopItemDto.builder()
                        .id(c.getId())
                        .cardId(c.getId())
                        .cardName(c.getName())
                        .price(UNIFORM_PRICE)
                        .stock(-1)
                        .owned(ownedCardIds.contains(c.getId()))
                        .card(toCardDetail(c))
                        .build())
                .collect(Collectors.toList());

        return ShopResponse.builder().items(items).build();
    }

    /**
     * 解锁卡牌（一次性购买）
     * 购买后该卡牌解锁，可无限次编入卡组，不可重复购买
     */
    @Override
    @Transactional
    public Integer buyCard(Long userId, Long cardId) {
        // 1. 校验卡牌存在且非默认卡
        Card card = cardDao.findById(cardId)
                .orElseThrow(() -> new BusinessException("卡牌不存在"));
        if (DEFAULT_CARD_NAMES.contains(card.getName())) {
            throw new BusinessException("默认卡牌无法在商店购买");
        }

        // 2. 校验是否已解锁
        if (playerCardDao.findByUserIdAndCardId(userId, cardId).isPresent()) {
            throw new BusinessException("该卡牌已解锁，无需重复购买");
        }

        // 3. 校验金币
        User user = userDao.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        if (user.getGold() < UNIFORM_PRICE) {
            throw new BusinessException("金币不足，需要 " + UNIFORM_PRICE + " 金币，当前拥有 " + user.getGold());
        }

        // 4. 扣金币
        user.setGold(user.getGold() - UNIFORM_PRICE);
        userDao.save(user);

        // 5. 解锁卡牌（quantity=1 表示已拥有）
        PlayerCard pc = PlayerCard.builder()
                .userId(userId)
                .cardId(cardId)
                .quantity(1)
                .build();
        playerCardDao.save(pc);

        log.info("用户 {} 解锁卡牌 [{}]，花费 {} 金币，剩余 {} 金币",
                userId, card.getName(), UNIFORM_PRICE, user.getGold());
        return user.getGold();
    }

    /**
     * 奖励金币（对战结算调用）
     */
    @Override
    @Transactional
    public void awardGold(Long userId, int gold) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        user.setGold(user.getGold() + gold);
        userDao.save(user);
        log.info("用户 {} 获得 {} 金币奖励，当前金币: {}", userId, gold, user.getGold());
    }

    private CardDetailResponse toCardDetail(Card c) {
        return CardDetailResponse.builder()
                .id(c.getId()).name(c.getName()).attack(c.getAttack())
                .health(c.getHealth()).bloodCost(c.getBloodCost()).boneCost(c.getBoneCost())
                .sigils(c.getSigils()).races(c.getRaces())
                .isLegendary(c.getIsLegendary()).description(c.getDescription())
                .build();
    }
}
