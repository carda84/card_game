package com.cardgame.service.impl;

import com.cardgame.dao.CardDao;
import com.cardgame.dao.PlayerCardDao;
import com.cardgame.dao.UserDao;
import com.cardgame.exception.BusinessException;
import com.cardgame.model.dto.response.ShopResponse;
import com.cardgame.model.entity.Card;
import com.cardgame.model.entity.PlayerCard;
import com.cardgame.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ShopServiceImpl 单元测试（Mockito）
 * 覆盖商店列表、购买卡牌、奖励金币的核心逻辑
 */
@ExtendWith(MockitoExtension.class)
class ShopServiceImplTest {

    @Mock
    private CardDao cardDao;

    @Mock
    private PlayerCardDao playerCardDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private ShopServiceImpl shopService;

    private Card defaultCard;
    private Card shopCard;
    private User testUser;

    @BeforeEach
    void setUp() {
        // 默认卡牌（不在商店出售）
        defaultCard = Card.builder()
                .id(1L).name("白鼬").price(0).maxDeckCount(2).build();

        // 商店卡牌
        shopCard = Card.builder()
                .id(2L).name("蚂蚁").price(20).maxDeckCount(2).build();

        // 测试用户
        testUser = User.builder()
                .id(1L).nickname("测试用户").uniqueTag("123456")
                .email("test@example.com").gold(100).build();
    }

    @Nested
    @DisplayName("getShopItems 商店列表")
    class GetShopItems {

        @Test
        @DisplayName("排除默认卡牌，仅展示商店卡牌")
        void excludesDefaultCards() {
            when(cardDao.findAll()).thenReturn(List.of(defaultCard, shopCard));
            when(playerCardDao.findByUserId(1L)).thenReturn(List.of());

            ShopResponse response = shopService.getShopItems(1L);

            assertEquals(1, response.getItems().size());
            assertEquals("蚂蚁", response.getItems().get(0).getCardName());
        }

        @Test
        @DisplayName("已拥有的卡牌标记为 owned=true")
        void marksOwnedCards() {
            when(cardDao.findAll()).thenReturn(List.of(shopCard));
            PlayerCard owned = PlayerCard.builder().userId(1L).cardId(2L).quantity(1).build();
            when(playerCardDao.findByUserId(1L)).thenReturn(List.of(owned));

            ShopResponse response = shopService.getShopItems(1L);

            assertTrue(response.getItems().get(0).getOwned());
        }

        @Test
        @DisplayName("未拥有的卡牌标记为 owned=false")
        void marksUnownedCards() {
            when(cardDao.findAll()).thenReturn(List.of(shopCard));
            when(playerCardDao.findByUserId(1L)).thenReturn(List.of());

            ShopResponse response = shopService.getShopItems(1L);

            assertFalse(response.getItems().get(0).getOwned());
        }

        @Test
        @DisplayName("所有商品统一价格 20 金币")
        void uniformPrice() {
            when(cardDao.findAll()).thenReturn(List.of(shopCard));
            when(playerCardDao.findByUserId(1L)).thenReturn(List.of());

            ShopResponse response = shopService.getShopItems(1L);

            assertEquals(20, response.getItems().get(0).getPrice());
        }
    }

    @Nested
    @DisplayName("buyCard 购买卡牌")
    class BuyCard {

        @Test
        @DisplayName("购买成功：扣除金币并解锁卡牌")
        void buyCard_success() {
            when(cardDao.findById(2L)).thenReturn(Optional.of(shopCard));
            when(playerCardDao.findByUserIdAndCardId(1L, 2L)).thenReturn(Optional.empty());
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
            when(userDao.save(any(User.class))).thenReturn(testUser);
            when(playerCardDao.save(any(PlayerCard.class))).thenAnswer(i -> i.getArgument(0));

            Integer remainingGold = shopService.buyCard(1L, 2L);

            assertEquals(80, remainingGold);
            verify(userDao).save(testUser);
            verify(playerCardDao).save(any(PlayerCard.class));
        }

        @Test
        @DisplayName("购买不存在的卡牌抛出异常")
        void buyCard_cardNotFound() {
            when(cardDao.findById(99L)).thenReturn(Optional.empty());

            assertThrows(BusinessException.class, () -> shopService.buyCard(1L, 99L));
        }

        @Test
        @DisplayName("购买默认卡牌抛出异常")
        void buyCard_defaultCard() {
            when(cardDao.findById(1L)).thenReturn(Optional.of(defaultCard));

            assertThrows(BusinessException.class, () -> shopService.buyCard(1L, 1L));
        }

        @Test
        @DisplayName("重复购买已解锁卡牌抛出异常")
        void buyCard_alreadyOwned() {
            when(cardDao.findById(2L)).thenReturn(Optional.of(shopCard));
            when(playerCardDao.findByUserIdAndCardId(1L, 2L))
                    .thenReturn(Optional.of(PlayerCard.builder().build()));

            assertThrows(BusinessException.class, () -> shopService.buyCard(1L, 2L));
        }

        @Test
        @DisplayName("金币不足时抛出异常")
        void buyCard_insufficientGold() {
            testUser.setGold(10);
            when(cardDao.findById(2L)).thenReturn(Optional.of(shopCard));
            when(playerCardDao.findByUserIdAndCardId(1L, 2L)).thenReturn(Optional.empty());
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> shopService.buyCard(1L, 2L));
            assertTrue(ex.getMessage().contains("金币不足"));
        }
    }

    @Nested
    @DisplayName("awardGold 奖励金币")
    class AwardGold {

        @Test
        @DisplayName("获胜奖励 100 金币")
        void awardGold_win() {
            testUser.setGold(50);
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
            when(userDao.save(any(User.class))).thenReturn(testUser);

            shopService.awardGold(1L, 100);

            assertEquals(150, testUser.getGold());
            verify(userDao).save(testUser);
        }

        @Test
        @DisplayName("失败奖励 30 金币")
        void awardGold_lose() {
            testUser.setGold(50);
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
            when(userDao.save(any(User.class))).thenReturn(testUser);

            shopService.awardGold(1L, 30);

            assertEquals(80, testUser.getGold());
        }

        @Test
        @DisplayName("用户不存在时抛出异常")
        void awardGold_userNotFound() {
            when(userDao.findById(99L)).thenReturn(Optional.empty());

            assertThrows(BusinessException.class, () -> shopService.awardGold(99L, 100));
        }
    }
}
