package com.cardgame.service.impl;

import com.cardgame.dao.*;
import com.cardgame.exception.BusinessException;
import com.cardgame.model.dto.request.CreateDeckRequest;
import com.cardgame.model.dto.response.DeckDetailResponse;
import com.cardgame.model.entity.Card;
import com.cardgame.model.entity.Deck;
import com.cardgame.model.entity.DeckCard;
import com.cardgame.model.entity.PlayerCard;
import com.cardgame.model.entity.User;
import com.cardgame.util.GameConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DeckServiceImpl 单元测试（Mockito）
 * 覆盖卡组创建、卡牌添加/移除、卡组校验、重命名、删除的核心逻辑
 */
@ExtendWith(MockitoExtension.class)
class DeckServiceImplTest {

    @Mock private DeckDao deckDao;
    @Mock private DeckCardDao deckCardDao;
    @Mock private CardDao cardDao;
    @Mock private CharacterDao characterDao;
    @Mock private PlayerCardDao playerCardDao;

    @InjectMocks
    private DeckServiceImpl deckService;

    private Deck testDeck;
    private com.cardgame.model.entity.Character testCharacter;
    private Card normalCard;
    private Card legendaryCard;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L).nickname("测试用户").uniqueTag("123456")
                .email("test@example.com").gold(100).build();

        testCharacter = com.cardgame.model.entity.Character.builder()
                .id(1L).name("猎人").maxHp(25).deckSize(15)
                .isDefault(true).build();

        testDeck = Deck.builder()
                .id(1L).userId(1L).characterId(1L).name("测试卡组").build();

        normalCard = Card.builder()
                .id(10L).name("狼").attack(2).health(2)
                .bloodCost(1).boneCost(0).maxDeckCount(2)
                .isLegendary(false).build();

        legendaryCard = Card.builder()
                .id(20L).name("传奇龙").attack(5).health(5)
                .bloodCost(3).boneCost(0).maxDeckCount(1)
                .isLegendary(true).build();
    }

    @Nested
    @DisplayName("createDeck 创建卡组")
    class CreateDeck {

        @Test
        @DisplayName("创建成功，返回卡组详情")
        void createDeck_success() {
            when(deckDao.countByUserId(1L)).thenReturn(0L);
            when(characterDao.findById(1L)).thenReturn(Optional.of(testCharacter));
            when(deckDao.save(any(Deck.class))).thenAnswer(inv -> {
                Deck d = inv.getArgument(0);
                d.setId(1L);
                return d;
            });
            when(deckDao.findById(1L)).thenReturn(Optional.of(testDeck));
            when(deckCardDao.findByDeckId(1L)).thenReturn(List.of());

            CreateDeckRequest req = new CreateDeckRequest();
            req.setName("新卡组");
            req.setCharacterId(1L);

            DeckDetailResponse response = deckService.createDeck(1L, req);

            verify(deckDao).save(any(Deck.class));
            assertNotNull(response);
        }

        @Test
        @DisplayName("卡组数量超限时抛出异常")
        void createDeck_exceedsLimit() {
            when(deckDao.countByUserId(1L)).thenReturn(20L);

            CreateDeckRequest req = new CreateDeckRequest();
            req.setName("新卡组");
            req.setCharacterId(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> deckService.createDeck(1L, req));
            assertTrue(ex.getMessage().contains("卡组数量已达上限"));
        }

        @Test
        @DisplayName("人物不存在时抛出异常")
        void createDeck_characterNotFound() {
            when(deckDao.countByUserId(1L)).thenReturn(0L);
            when(characterDao.findById(99L)).thenReturn(Optional.empty());

            CreateDeckRequest req = new CreateDeckRequest();
            req.setName("新卡组");
            req.setCharacterId(99L);

            assertThrows(BusinessException.class, () -> deckService.createDeck(1L, req));
        }
    }

    @Nested
    @DisplayName("addCardToDeck 添加卡牌到卡组")
    class AddCardToDeck {

        @Test
        @DisplayName("添加成功")
        void addCard_success() {
            when(deckDao.findById(1L)).thenReturn(Optional.of(testDeck));
            when(playerCardDao.findByUserIdAndCardId(1L, 10L))
                    .thenReturn(Optional.of(PlayerCard.builder().build()));
            when(cardDao.findById(10L)).thenReturn(Optional.of(normalCard));
            when(characterDao.findById(1L)).thenReturn(Optional.of(testCharacter));
            when(deckCardDao.countByDeckId(1L)).thenReturn(0L);
            when(deckCardDao.findByDeckId(1L)).thenReturn(List.of());
            when(deckCardDao.save(any(DeckCard.class))).thenAnswer(i -> i.getArgument(0));

            assertDoesNotThrow(() -> deckService.addCardToDeck(1L, 1L, 10L));
            verify(deckCardDao).save(any(DeckCard.class));
        }

        @Test
        @DisplayName("无权操作他人卡组时抛出异常")
        void addCard_unauthorized() {
            Deck otherDeck = Deck.builder()
                    .id(2L).userId(2L).characterId(1L).name("他人卡组").build();
            when(deckDao.findById(2L)).thenReturn(Optional.of(otherDeck));

            assertThrows(BusinessException.class,
                    () -> deckService.addCardToDeck(1L, 2L, 10L));
        }

        @Test
        @DisplayName("未拥有卡牌时抛出异常")
        void addCard_notOwned() {
            when(deckDao.findById(1L)).thenReturn(Optional.of(testDeck));
            when(playerCardDao.findByUserIdAndCardId(1L, 10L)).thenReturn(Optional.empty());

            assertThrows(BusinessException.class,
                    () -> deckService.addCardToDeck(1L, 1L, 10L));
        }

        @Test
        @DisplayName("卡组已满时抛出异常")
        void addCard_deckFull() {
            when(deckDao.findById(1L)).thenReturn(Optional.of(testDeck));
            when(playerCardDao.findByUserIdAndCardId(1L, 10L))
                    .thenReturn(Optional.of(PlayerCard.builder().build()));
            when(cardDao.findById(10L)).thenReturn(Optional.of(normalCard));
            when(characterDao.findById(1L)).thenReturn(Optional.of(testCharacter));
            when(deckCardDao.countByDeckId(1L)).thenReturn(15L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> deckService.addCardToDeck(1L, 1L, 10L));
            assertTrue(ex.getMessage().contains("卡组已满"));
        }

        @Test
        @DisplayName("同名卡牌超过 maxDeckCount 时抛出异常")
        void addCard_exceedsMaxDeckCount() {
            // 已有 2 张同名卡
            DeckCard dc1 = DeckCard.builder().deckId(1L).cardId(10L).build();
            DeckCard dc2 = DeckCard.builder().deckId(1L).cardId(10L).build();
            when(deckDao.findById(1L)).thenReturn(Optional.of(testDeck));
            when(playerCardDao.findByUserIdAndCardId(1L, 10L))
                    .thenReturn(Optional.of(PlayerCard.builder().build()));
            when(cardDao.findById(10L)).thenReturn(Optional.of(normalCard));
            when(characterDao.findById(1L)).thenReturn(Optional.of(testCharacter));
            when(deckCardDao.countByDeckId(1L)).thenReturn(5L);
            when(deckCardDao.findByDeckId(1L)).thenReturn(List.of(dc1, dc2));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> deckService.addCardToDeck(1L, 1L, 10L));
            assertTrue(ex.getMessage().contains("最多只能放"));
        }

        @Test
        @DisplayName("传奇卡超过上限（3张）时抛出异常")
        void addCard_legendaryLimit() {
            // 已有 3 张传奇卡
            Card leg1 = Card.builder().id(21L).name("传奇A").isLegendary(true).maxDeckCount(1).build();
            Card leg2 = Card.builder().id(22L).name("传奇B").isLegendary(true).maxDeckCount(1).build();
            Card leg3 = Card.builder().id(23L).name("传奇C").isLegendary(true).maxDeckCount(1).build();
            DeckCard dc1 = DeckCard.builder().deckId(1L).cardId(21L).build();
            DeckCard dc2 = DeckCard.builder().deckId(1L).cardId(22L).build();
            DeckCard dc3 = DeckCard.builder().deckId(1L).cardId(23L).build();

            when(deckDao.findById(1L)).thenReturn(Optional.of(testDeck));
            when(playerCardDao.findByUserIdAndCardId(1L, 20L))
                    .thenReturn(Optional.of(PlayerCard.builder().build()));
            when(cardDao.findById(20L)).thenReturn(Optional.of(legendaryCard));
            when(characterDao.findById(1L)).thenReturn(Optional.of(testCharacter));
            when(deckCardDao.countByDeckId(1L)).thenReturn(3L);
            when(deckCardDao.findByDeckId(1L)).thenReturn(List.of(dc1, dc2, dc3));
            when(cardDao.findById(21L)).thenReturn(Optional.of(leg1));
            when(cardDao.findById(22L)).thenReturn(Optional.of(leg2));
            when(cardDao.findById(23L)).thenReturn(Optional.of(leg3));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> deckService.addCardToDeck(1L, 1L, 20L));
            assertTrue(ex.getMessage().contains("传奇卡牌不能超过"));
        }
    }

    @Nested
    @DisplayName("removeCardFromDeck 从卡组移除卡牌")
    class RemoveCardFromDeck {

        @Test
        @DisplayName("移除成功")
        void removeCard_success() {
            DeckCard dc = DeckCard.builder().deckId(1L).cardId(10L).build();
            when(deckDao.findById(1L)).thenReturn(Optional.of(testDeck));
            when(deckCardDao.findByDeckId(1L)).thenReturn(List.of(dc));

            deckService.removeCardFromDeck(1L, 1L, 10L);

            verify(deckCardDao).delete(dc);
        }

        @Test
        @DisplayName("无权操作他人卡组时抛出异常")
        void removeCard_unauthorized() {
            Deck otherDeck = Deck.builder()
                    .id(2L).userId(2L).characterId(1L).name("他人卡组").build();
            when(deckDao.findById(2L)).thenReturn(Optional.of(otherDeck));

            assertThrows(BusinessException.class,
                    () -> deckService.removeCardFromDeck(1L, 2L, 10L));
        }
    }

    @Nested
    @DisplayName("deleteDeck 删除卡组")
    class DeleteDeck {

        @Test
        @DisplayName("删除成功，同时清除卡组内卡牌")
        void deleteDeck_success() {
            when(deckDao.findById(1L)).thenReturn(Optional.of(testDeck));

            deckService.deleteDeck(1L, 1L);

            verify(deckCardDao).deleteByDeckId(1L);
            verify(deckDao).deleteById(1L);
        }

        @Test
        @DisplayName("无权删除他人卡组时抛出异常")
        void deleteDeck_unauthorized() {
            Deck otherDeck = Deck.builder()
                    .id(2L).userId(2L).characterId(1L).name("他人卡组").build();
            when(deckDao.findById(2L)).thenReturn(Optional.of(otherDeck));

            assertThrows(BusinessException.class, () -> deckService.deleteDeck(1L, 2L));
        }
    }

    @Nested
    @DisplayName("renameDeck 重命名卡组")
    class RenameDeck {

        @Test
        @DisplayName("重命名成功")
        void renameDeck_success() {
            when(deckDao.findById(1L)).thenReturn(Optional.of(testDeck));
            when(deckDao.save(any(Deck.class))).thenReturn(testDeck);
            when(deckCardDao.findByDeckId(1L)).thenReturn(List.of());
            when(characterDao.findById(1L)).thenReturn(Optional.of(testCharacter));

            DeckDetailResponse response = deckService.renameDeck(1L, 1L, "新名称");

            assertEquals("新名称", testDeck.getName());
        }

        @Test
        @DisplayName("名称为空时抛出异常")
        void renameDeck_emptyName() {
            when(deckDao.findById(1L)).thenReturn(Optional.of(testDeck));

            assertThrows(BusinessException.class,
                    () -> deckService.renameDeck(1L, 1L, ""));
        }

        @Test
        @DisplayName("名称超过 100 字符时抛出异常")
        void renameDeck_nameTooLong() {
            when(deckDao.findById(1L)).thenReturn(Optional.of(testDeck));

            String longName = "A".repeat(101);
            assertThrows(BusinessException.class,
                    () -> deckService.renameDeck(1L, 1L, longName));
        }

        @Test
        @DisplayName("无权重命名他人卡组时抛出异常")
        void renameDeck_unauthorized() {
            Deck otherDeck = Deck.builder()
                    .id(2L).userId(2L).characterId(1L).name("他人卡组").build();
            when(deckDao.findById(2L)).thenReturn(Optional.of(otherDeck));

            assertThrows(BusinessException.class,
                    () -> deckService.renameDeck(1L, 2L, "新名称"));
        }
    }

    @Nested
    @DisplayName("getDeckDetail 卡组详情与校验")
    class GetDeckDetail {

        @Test
        @DisplayName("卡牌数等于 deckSize 且传奇卡不超限时 isValid=true")
        void validDeck() {
            // 构建 15 张普通卡
            List<DeckCard> deckCards = java.util.stream.IntStream.range(0, 15)
                    .mapToObj(i -> DeckCard.builder().deckId(1L).cardId((long) (100 + i)).build())
                    .toList();
            List<Card> cards = java.util.stream.IntStream.range(0, 15)
                    .mapToObj(i -> Card.builder().id((long) (100 + i)).name("卡" + i)
                            .isLegendary(false).attack(1).health(1).build())
                    .toList();

            when(deckDao.findById(1L)).thenReturn(Optional.of(testDeck));
            when(deckCardDao.findByDeckId(1L)).thenReturn(deckCards);
            for (int i = 0; i < 15; i++) {
                when(cardDao.findById((long) (100 + i))).thenReturn(Optional.of(cards.get(i)));
            }
            when(characterDao.findById(1L)).thenReturn(Optional.of(testCharacter));

            DeckDetailResponse response = deckService.getDeckDetail(1L, 1L);

            assertTrue(response.getIsValid());
            assertNull(response.getValidationMessage());
        }

        @Test
        @DisplayName("卡牌数不足时 isValid=false，提示缺少张数")
        void insufficientCards() {
            when(deckDao.findById(1L)).thenReturn(Optional.of(testDeck));
            when(deckCardDao.findByDeckId(1L)).thenReturn(List.of());
            when(characterDao.findById(1L)).thenReturn(Optional.of(testCharacter));

            DeckDetailResponse response = deckService.getDeckDetail(1L, 1L);

            assertFalse(response.getIsValid());
            assertTrue(response.getValidationMessage().contains("需要"));
        }
    }
}
