package com.cardgame.service.impl;

import com.cardgame.dao.*;
import com.cardgame.exception.BusinessException;
import com.cardgame.model.dto.request.*;
import com.cardgame.model.dto.response.*;
import com.cardgame.model.entity.Card;
import com.cardgame.model.entity.Deck;
import com.cardgame.model.entity.DeckCard;
import com.cardgame.model.entity.GameSession;
import com.cardgame.model.entity.BattleRecord;
import com.cardgame.model.entity.User;
import com.cardgame.model.enums.*;
import com.cardgame.service.*;
import com.cardgame.util.GameConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BattleServiceImpl implements BattleService {

    private static final int WIN_GOLD_REWARD = 100;
    private static final int LOSE_GOLD_REWARD = 30;
    private static final int WIN_POINTS = 10;
    private static final int LOSE_POINTS = -5;
    private static final String SQUIRREL_NAME = "松鼠";

    private final CardDao cardDao;
    private final DeckDao deckDao;
    private final DeckCardDao deckCardDao;
    private final UserDao userDao;
    private final CharacterDao characterDao;
    private final GameSessionDao gameSessionDao;
    private final BattleRecordDao battleRecordDao;
    private final ShopService shopService;
    private final ShuffleService shuffleService;
    private final ObjectMapper objectMapper;

    public BattleServiceImpl(CardDao cardDao, DeckDao deckDao, DeckCardDao deckCardDao,
                             UserDao userDao, CharacterDao characterDao, GameSessionDao gameSessionDao,
                             BattleRecordDao battleRecordDao, ShopService shopService,
                             ShuffleService shuffleService, ObjectMapper objectMapper) {
        this.cardDao = cardDao;
        this.deckDao = deckDao;
        this.deckCardDao = deckCardDao;
        this.userDao = userDao;
        this.characterDao = characterDao;
        this.gameSessionDao = gameSessionDao;
        this.battleRecordDao = battleRecordDao;
        this.shopService = shopService;
        this.shuffleService = shuffleService;
        this.objectMapper = objectMapper;
    }

    // ==================== 核心 API ====================

    @Override
    @Transactional
    public BattleStartResponse startBattle(Long userId, StartBattleRequest request) {
        Deck deck = deckDao.findById(request.getDeckId())
                .orElseThrow(() -> new BusinessException("卡组不存在"));
        if (!deck.getUserId().equals(userId))
            throw new BusinessException("无权使用此卡组");

        com.cardgame.model.entity.Character character = characterDao.findById(deck.getCharacterId())
                .orElseThrow(() -> new BusinessException("人物不存在"));

        // 获取卡组卡牌 ID 列表
        List<Long> deckCardIds = deckCardDao.findByDeckId(deck.getId()).stream()
                .map(DeckCard::getCardId).collect(Collectors.toList());
        if (deckCardIds.isEmpty())
            throw new BusinessException("卡组为空，请先编辑卡组");

        // 洗牌
        List<Long> shuffledIds = shuffleService.shuffleDeck(null, deckCardIds);

        // 构建初始手牌（抽 INITIAL_HAND_SIZE 张）
        List<Long> handIds = new ArrayList<>();
        int drawCount = Math.min(GameConstants.INITIAL_HAND_SIZE, shuffledIds.size());
        for (int i = 0; i < drawCount; i++) {
            handIds.add(shuffledIds.get(i));
        }
        List<Long> remainingDrawPile = new ArrayList<>(shuffledIds.subList(drawCount, shuffledIds.size()));

        // 创建 GameSession
        boolean isPlayerFirst = new Random().nextBoolean();
        GameSession session = GameSession.builder()
                .mode(request.getMode())
                .levelId(request.getLevelId())
                .playerUserId(userId)
                .playerCharacterId(character.getId())
                .currentPlayer(isPlayerFirst ? userId : null)
                .turnNumber(0)
                .cardsPlayedThisTurn(0)
                .playerHp(character.getMaxHp())
                .opponentHp(30)
                .playerMaxHp(character.getMaxHp())
                .opponentMaxHp(30)
                .playerBones(0)
                .opponentBones(0)
                .playerBloodThisTurn(0)
                .turnPhase(TurnPhase.DRAW)
                .sessionStatus(SessionStatus.IN_PROGRESS)
                .playerItems(toJson(character.getInitialItemList().stream()
                        .map(ItemType::getDisplayName).collect(Collectors.toList())))
                .opponentItems(toJson(Collections.emptyList()))
                .build();

        // PvE: 设置 AI 对手
        if (request.getMode() == BattleMode.PVE) {
            setupPveOpponent(session);
        }

        // 保存牌组状态
        session.setPlayerHand(toJson(handIds));
        session.setPlayerDrawPile(toJson(remainingDrawPile));
        session.setBoardState(toJson(new BoardData(
                Arrays.asList(null, null, null, null),
                Arrays.asList(null, null, null, null)
        )));

        gameSessionDao.save(session);
        log.info("用户 {} 开始对战，sessionId={}, mode={}", userId, session.getId(), request.getMode());

        // 构建响应
        List<CardDetailResponse> initialHand = handIds.stream()
                .map(this::cardToDetail).collect(Collectors.toList());

        return BattleStartResponse.builder()
                .sessionId(session.getId())
                .initialHand(initialHand)
                .opponentName(request.getMode() == BattleMode.PVE ? "AI 对手" : "对手")
                .isPlayerFirst(isPlayerFirst)
                .build();
    }

    @Override
    @Transactional
    public DrawResultResponse drawCard(Long userId, DrawCardRequest request) {
        GameSession session = getSessionAndValidate(request.getSessionId(), userId);
        if (session.getTurnPhase() != TurnPhase.DRAW)
            throw new BusinessException("当前不是抽牌阶段");

        boolean asPlayer = isViewingAsPlayer(session, userId);
        Long drawnCardId;
        String drawType = request.getDrawType().name();
        int remainingSize = 0;

        if (request.getDrawType() == DrawType.SQUIRREL) {
            // 松鼠牌：无限供应
            Card squirrel = cardDao.findAll().stream()
                    .filter(c -> SQUIRREL_NAME.equals(c.getName())).findFirst()
                    .orElseThrow(() -> new BusinessException("松鼠卡牌不存在"));
            drawnCardId = squirrel.getId();
        } else {
            // 牌组抽牌（根据视角选择抽牌堆）
            List<Long> drawPile = parseJsonList(asPlayer ? session.getPlayerDrawPile() : session.getOpponentDrawPile());
            if (drawPile.isEmpty())
                throw new BusinessException("牌组已空，请选择抽松鼠牌");
            drawnCardId = drawPile.remove(0);
            if (asPlayer) session.setPlayerDrawPile(toJson(drawPile));
            else session.setOpponentDrawPile(toJson(drawPile));
            remainingSize = drawPile.size();
        }

        // 加入手牌（按视角）
        List<Long> hand = parseJsonList(asPlayer ? session.getPlayerHand() : session.getOpponentHand());
        hand.add(drawnCardId);
        if (asPlayer) session.setPlayerHand(toJson(hand));
        else session.setOpponentHand(toJson(hand));
        session.setTurnPhase(TurnPhase.PLAY_CARD);
        gameSessionDao.save(session);

        return DrawResultResponse.builder()
                .card(cardToDetail(drawnCardId))
                .drawType(drawType)
                .remainingDeckSize(remainingSize)
                .build();
    }

    @Override
    @Transactional
    public PlayCardResultResponse playCard(Long userId, PlayCardRequest request) {
        GameSession session = getSessionAndValidate(request.getSessionId(), userId);
        if (session.getTurnPhase() != TurnPhase.PLAY_CARD)
            throw new BusinessException("当前不是出牌阶段");

        int slotIndex = request.getSlotIndex();
        if (slotIndex < 0 || slotIndex > 3)
            throw new BusinessException("无效格位");

        boolean asPlayer = isViewingAsPlayer(session, userId);
        BoardData board = parseBoard(session.getBoardState());
        // 视角上的"己方"格位
        List<SlotData> mySlots = asPlayer ? board.playerSlots : board.opponentSlots;

        if (mySlots.get(slotIndex) != null)
            throw new BusinessException("该格位已有卡牌");

        // 从手牌取卡牌
        List<Long> hand = parseJsonList(asPlayer ? session.getPlayerHand() : session.getOpponentHand());
        int handIdx = request.getHandCardIndex();
        if (handIdx < 0 || handIdx >= hand.size())
            throw new BusinessException("无效手牌索引");
        Long cardId = hand.get(handIdx);
        Card card = cardDao.findById(cardId).orElseThrow(() -> new BusinessException("卡牌不存在"));

        // 处理献祭
        List<Long> sacrificedCardIds = new ArrayList<>();
        if (card.getBloodCost() > 0) {
            List<Integer> sacrificeSlots = request.getSacrificeSlotIndices();
            if (sacrificeSlots == null || sacrificeSlots.size() != card.getBloodCost())
                throw new BusinessException("需要献祭 " + card.getBloodCost() + " 张场上卡牌");
            for (int sacSlot : sacrificeSlots) {
                if (sacSlot < 0 || sacSlot > 3)
                    throw new BusinessException("无效献祭格位");
                SlotData sacCard = mySlots.get(sacSlot);
                if (sacCard == null)
                    throw new BusinessException("献祭格位无卡牌");
                Card sacCardTemplate = cardDao.findById(sacCard.cardId).orElse(null);
                if (sacCardTemplate != null && !sacCardTemplate.getCanSacrifice())
                    throw new BusinessException(sacCardTemplate.getName() + " 不可被献祭");
                sacrificedCardIds.add(sacCard.cardId);
                if (asPlayer) session.setPlayerBones(session.getPlayerBones() + 1);
                else session.setOpponentBones(session.getOpponentBones() + 1);
                mySlots.set(sacSlot, null);
            }
        } else if (card.getBoneCost() > 0) {
            int bones = asPlayer ? session.getPlayerBones() : session.getOpponentBones();
            if (bones < card.getBoneCost())
                throw new BusinessException("骨头不足，需要 " + card.getBoneCost() + " 个");
            if (asPlayer) session.setPlayerBones(bones - card.getBoneCost());
            else session.setOpponentBones(bones - card.getBoneCost());
        }

        // 出牌
        hand.remove(handIdx);
        if (asPlayer) session.setPlayerHand(toJson(hand));
        else session.setOpponentHand(toJson(hand));
        mySlots.set(slotIndex, new SlotData(cardId, card.getHealth(), card.getAttack(),
                card.getSigils(), card.getIsSpecialAttack()));
        session.setBoardState(toJson(board));
        session.setCardsPlayedThisTurn(session.getCardsPlayedThisTurn() + 1);
        gameSessionDao.save(session);

        return PlayCardResultResponse.builder()
                .success(true)
                .slotIndex(slotIndex)
                .playedCard(cardToDetail(cardId))
                .sacrificedCardIds(sacrificedCardIds)
                .boardState(buildBoardStateFor(session, board, asPlayer))
                .build();
    }

    @Override
    @Transactional
    public void sacrificeCards(Long userId, SacrificeRequest request) {
        // 独立献祭接口（用于提前献祭场景）
        GameSession session = getSessionAndValidate(request.getSessionId(), userId);
        BoardData board = parseBoard(session.getBoardState());
        for (Long cardId : request.getSacrificeCardIds()) {
            for (int i = 0; i < 4; i++) {
                SlotData slot = board.playerSlots.get(i);
                if (slot != null && slot.cardId.equals(cardId)) {
                    Card c = cardDao.findById(cardId).orElse(null);
                    if (c != null && !c.getCanSacrifice())
                        throw new BusinessException(c.getName() + " 不可被献祭");
                    board.playerSlots.set(i, null);
                    session.setPlayerBones(session.getPlayerBones() + 1);
                    break;
                }
            }
        }
        session.setBoardState(toJson(board));
        gameSessionDao.save(session);
    }

    @Override
    @Transactional
    public TurnEndResponse endTurn(Long userId, EndTurnRequest request) {
        GameSession session = getSessionAndValidate(request.getSessionId(), userId);
        BoardData board = parseBoard(session.getBoardState());
        boolean asPlayer = isViewingAsPlayer(session, userId);
        boolean isPvp = session.getMode() == BattleMode.PVP;

        // PvP：从当前行动方视角发起攻击
        // 攻击方 = asPlayer ? playerSlots : opponentSlots
        // 被攻击方 = asPlayer ? opponentSlots : playerSlots
        boolean playerIsAttacking = asPlayer;
        List<TurnEndResponse.AttackResult> attacks = resolveCombat(board, session, playerIsAttacking);

        // PvE: AI 回合
        if (!isPvp && session.getOpponentHp() > 0 && session.getPlayerHp() > 0) {
            executeAiTurn(session, board, attacks);
        }

        // 检查游戏结束
        boolean isGameOver = session.getPlayerHp() <= 0 || session.getOpponentHp() <= 0;
        String winner = null;
        if (isGameOver) {
            session.setSessionStatus(SessionStatus.FINISHED);
            // 胜利方：血量>0 的一方
            boolean playerCreatorWon = session.getPlayerHp() > 0;
            winner = playerCreatorWon ? "PLAYER" : "OPPONENT";
            // 结算：以 playerUserId 为准（creator 获胜=WIN，opponent 获胜=LOSE）
            settleBattle(session, playerCreatorWon);
        } else {
            // 新回合
            session.setTurnNumber(session.getTurnNumber() + 1);
            session.setCardsPlayedThisTurn(0);
            session.setTurnPhase(TurnPhase.DRAW);
            // PvP 切换当前行动方
            if (isPvp) {
                Long nextPlayer = asPlayer ? session.getOpponentUserId() : session.getPlayerUserId();
                session.setCurrentPlayer(nextPlayer);
            }
        }

        session.setBoardState(toJson(board));
        gameSessionDao.save(session);

        return TurnEndResponse.builder()
                .attacks(attacks)
                .playerHp(asPlayer ? session.getPlayerHp() : session.getOpponentHp())
                .opponentHp(asPlayer ? session.getOpponentHp() : session.getPlayerHp())
                .isGameOver(isGameOver)
                .winner(winner)
                .build();
    }

    @Override
    @Transactional
    public BattleEndResponse surrender(Long userId, SurrenderRequest request) {
        GameSession session = gameSessionDao.findById(request.getSessionId())
                .orElseThrow(() -> new BusinessException("对战不存在"));
        boolean isCreator = session.getPlayerUserId().equals(userId);
        boolean isOpponent = session.getMode() == BattleMode.PVP
                && session.getOpponentUserId() != null
                && session.getOpponentUserId().equals(userId);
        if (!isCreator && !isOpponent)
            throw new BusinessException("无权操作");
        if (session.getSessionStatus() == SessionStatus.FINISHED)
            throw new BusinessException("对战已结束");

        session.setSessionStatus(SessionStatus.FINISHED);
        // 投降方血量设为 0，便于轮询方检测游戏结束
        if (isCreator) {
            session.setPlayerHp(0);
        } else {
            session.setOpponentHp(0);
        }
        // 投降方 = 当前操作者，胜利方 = 另一方
        // settleBattle(session, creatorWins) 以 creator 视角计算
        boolean creatorWins = !isCreator; // creator 投降则 creator 输
        settleBattle(session, creatorWins);
        gameSessionDao.save(session);

        return BattleEndResponse.builder()
                .result(BattleResult.SURRENDER.name())
                .goldReward(LOSE_GOLD_REWARD)
                .pointsChange(LOSE_POINTS)
                .turns(session.getTurnNumber())
                .build();
    }

    @Override
    public BoardStateResponse getBoardState(Long sessionId) {
        return getBoardStateForCaller(sessionId, null);
    }

    @Override
    public BoardStateResponse getBoardStateForCaller(Long sessionId, Long userId) {
        GameSession session = gameSessionDao.findById(sessionId)
                .orElseThrow(() -> new BusinessException("对战不存在"));
        BoardData board = parseBoard(session.getBoardState());
        // 如果指定了 userId 且是 PvP 中的 opponent，翻转视角
        boolean asPlayer = userId == null || session.getPlayerUserId().equals(userId);
        return buildBoardStateFor(session, board, asPlayer);
    }

    private BoardStateResponse buildBoardState(GameSession session, BoardData board) {
        return buildBoardStateFor(session, board, true);
    }

    @Override
    @Transactional
    public void useActiveSkill(Long userId, UseActiveSkillRequest request) {
        // TODO: 人物主动技能实现
        log.info("用户 {} 使用主动技能, sessionId={}", userId, request.getSessionId());
    }

    @Override
    @Transactional
    public void useItem(Long userId, UseItemRequest request) {
        GameSession session = getSessionAndValidate(request.getSessionId(), userId);
        boolean asPlayer = isViewingAsPlayer(session, userId);
        List<String> items = parseJsonStringList(asPlayer ? session.getPlayerItems() : session.getOpponentItems());
        int idx = request.getItemIndex();
        if (idx < 0 || idx >= items.size())
            throw new BusinessException("无效道具索引");

        String itemName = items.get(idx);
        ItemType itemType = ItemType.fromDisplayName(itemName);
        BoardData board = parseBoard(session.getBoardState());
        List<SlotData> mySlots = asPlayer ? board.playerSlots : board.opponentSlots;
        List<SlotData> oppSlots = asPlayer ? board.opponentSlots : board.playerSlots;

        switch (itemType) {
            case SCISSORS: // 剪刀：摧毁对手随机一张卡牌
                for (int i = 0; i < 4; i++) {
                    if (oppSlots.get(i) != null) {
                        oppSlots.set(i, null);
                        if (asPlayer) session.setPlayerBones(session.getPlayerBones() + 1);
                        else session.setOpponentBones(session.getOpponentBones() + 1);
                        log.info("剪刀摧毁对手卡牌: slot={}", i);
                        break;
                    }
                }
                break;
            case PAINTBRUSH: // 画笔：清除对手所有卡牌印记
                for (int i = 0; i < 4; i++) {
                    SlotData s = oppSlots.get(i);
                    if (s != null) s.sigils = null;
                }
                break;
            case FAN: // 扇子：本回合己方卡牌获得空袭
                log.info("扇子: 己方卡牌本回合获得空袭");
                break;
            case FISHHOOK: // 鱼钩：拉对手有空位的对位卡
                for (int i = 0; i < 4; i++) {
                    SlotData oppCard = oppSlots.get(i);
                    if (oppCard != null && mySlots.get(i) == null) {
                        mySlots.set(i, oppCard);
                        oppSlots.set(i, null);
                        log.info("鱼钩拉取对手卡牌: slot={}", i);
                        break;
                    }
                }
                break;
        }

        items.remove(idx);
        if (asPlayer) session.setPlayerItems(toJson(items));
        else session.setOpponentItems(toJson(items));
        session.setBoardState(toJson(board));
        gameSessionDao.save(session);
    }

    // ==================== 战斗结算 ====================

    private List<TurnEndResponse.AttackResult> resolveCombat(BoardData board, GameSession session, boolean isPlayerAttacking) {
        List<TurnEndResponse.AttackResult> results = new ArrayList<>();
        List<SlotData> attackerSlots = isPlayerAttacking ? board.playerSlots : board.opponentSlots;
        List<SlotData> defenderSlots = isPlayerAttacking ? board.opponentSlots : board.playerSlots;

        for (int i = 0; i < 4; i++) {
            SlotData attacker = attackerSlots.get(i);
            if (attacker == null) continue;

            int attackPower = getAttackPower(attacker);
            if (attackPower <= 0) continue;

            // 检查空袭印记
            boolean hasAirRaid = hasSigil(attacker, "空袭");
            boolean hasDoubleStrike = hasSigil(attacker, "双重打击");

            SlotData defender = defenderSlots.get(i);

            if (defender != null && !hasAirRaid) {
                // 有对位卡牌：攻击卡牌
                // 检查高跳（阻止空袭）
                int damage = attackPower;
                // 盾：减伤1
                if (hasSigil(defender, "盾")) damage = Math.max(0, damage - 1);

                defender.currentHealth -= damage;
                boolean defenderDied = defender.currentHealth <= 0;

                // 反击：攻击者受到1伤害
                boolean attackerDied = false;
                if (hasSigil(defender, "反击")) {
                    attacker.currentHealth -= 1;
                    attackerDied = attacker.currentHealth <= 0;
                }

                // 剧毒：直接杀死
                if (hasSigil(attacker, "剧毒")) {
                    defender.currentHealth = 0;
                    defenderDied = true;
                }

                if (defenderDied) {
                    defenderSlots.set(i, null);
                    // 生骨头
                    if (isPlayerAttacking) session.setPlayerBones(session.getPlayerBones() + 1);
                    else session.setOpponentBones(session.getOpponentBones() + 1);
                }
                if (attackerDied) {
                    attackerSlots.set(i, null);
                    if (isPlayerAttacking) session.setOpponentBones(session.getOpponentBones() + 1);
                    else session.setPlayerBones(session.getPlayerBones() + 1);
                }

                results.add(TurnEndResponse.AttackResult.builder()
                        .attackerSlot(i).defenderSlot(i).damage(damage)
                        .defenderDied(defenderDied).attackerDied(attackerDied).build());

                // 双重打击：再攻击一次
                if (hasDoubleStrike && !attackerDied && defenderDied) {
                    // 第二次攻击打玩家
                    if (isPlayerAttacking) session.setOpponentHp(session.getOpponentHp() - attackPower);
                    else session.setPlayerHp(session.getPlayerHp() - attackPower);
                }
            } else {
                // 空位或空袭：直接攻击玩家
                if (isPlayerAttacking) {
                    session.setOpponentHp(session.getOpponentHp() - attackPower);
                } else {
                    session.setPlayerHp(session.getPlayerHp() - attackPower);
                }
                results.add(TurnEndResponse.AttackResult.builder()
                        .attackerSlot(i).defenderSlot(i).damage(attackPower)
                        .defenderDied(false).attackerDied(false).build());
            }
        }
        return results;
    }

    // ==================== AI 逻辑 (PvE) ====================

    private void executeAiTurn(GameSession session, BoardData board, List<TurnEndResponse.AttackResult> attacks) {
        // AI 抽牌
        List<Long> aiDrawPile = parseJsonList(session.getOpponentDrawPile());
        List<Long> aiHand = parseJsonList(session.getOpponentHand());

        if (!aiDrawPile.isEmpty()) {
            aiHand.add(aiDrawPile.remove(0));
            session.setOpponentDrawPile(toJson(aiDrawPile));
        } else {
            // 牌组空了，抽松鼠
            Card squirrel = cardDao.findAll().stream()
                    .filter(c -> SQUIRREL_NAME.equals(c.getName())).findFirst().orElse(null);
            if (squirrel != null) aiHand.add(squirrel.getId());
        }

        // AI 出牌（简单策略：尝试打出能出的最贵卡牌）
        aiPlayCards(session, board, aiHand);
        session.setOpponentHand(toJson(aiHand));

        // AI 攻击
        attacks.addAll(resolveCombat(board, session, false));
    }

    private void aiPlayCards(GameSession session, BoardData board, List<Long> aiHand) {
        // 按费用从高到低排序，尝试出牌
        List<Card> handCards = aiHand.stream()
                .map(id -> cardDao.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(c -> -(c.getBloodCost() + c.getBoneCost())))
                .collect(Collectors.toList());

        int played = 0;
        for (Card card : handCards) {
            if (played >= 2) break; // AI 每回合最多出 2 张

            // 找空位
            int emptySlot = -1;
            for (int i = 0; i < 4; i++) {
                if (board.opponentSlots.get(i) == null) { emptySlot = i; break; }
            }
            if (emptySlot == -1) break;

            boolean canPlay = false;

            if (card.getBloodCost() == 0 && card.getBoneCost() == 0) {
                canPlay = true; // 免费卡
            } else if (card.getBloodCost() > 0) {
                // 尝试献祭自己的低价值卡牌
                canPlay = aiTrySacrifice(board, card.getBloodCost(), session);
            } else if (card.getBoneCost() > 0 && session.getOpponentBones() >= card.getBoneCost()) {
                session.setOpponentBones(session.getOpponentBones() - card.getBoneCost());
                canPlay = true;
            }

            if (canPlay) {
                board.opponentSlots.set(emptySlot, new SlotData(card.getId(), card.getHealth(),
                        card.getAttack(), card.getSigils(), card.getIsSpecialAttack()));
                aiHand.remove(card.getId());
                played++;
            }
        }
    }

    private boolean aiTrySacrifice(BoardData board, int required, GameSession session) {
        // 找 AI 牌桌上可以献祭的卡牌
        List<Integer> sacrificeableSlots = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            SlotData s = board.opponentSlots.get(i);
            if (s != null) {
                Card c = cardDao.findById(s.cardId).orElse(null);
                if (c != null && c.getCanSacrifice()) sacrificeableSlots.add(i);
            }
        }
        if (sacrificeableSlots.size() < required) return false;

        // 献祭最便宜的卡牌
        sacrificeableSlots.sort(Comparator.comparingInt(i -> {
            SlotData s = board.opponentSlots.get(i);
            Card c = cardDao.findById(s.cardId).orElse(null);
            return c != null ? c.getBloodCost() + c.getBoneCost() : 0;
        }));

        for (int j = 0; j < required; j++) {
            int sacSlot = sacrificeableSlots.get(j);
            board.opponentSlots.set(sacSlot, null);
            session.setOpponentBones(session.getOpponentBones() + 1);
        }
        return true;
    }

    // ==================== PvE 对手初始化 ====================

    private void setupPveOpponent(GameSession session) {
        session.setOpponentUserId(null);
        session.setOpponentHp(30);
        session.setOpponentMaxHp(30);

        // 生成 AI 卡组：从所有非传奇、可入组卡中随机选 15 张
        List<Card> allCards = cardDao.findAll().stream()
                .filter(c -> c.getMaxDeckCount() > 0 && !c.getIsLegendary())
                .collect(Collectors.toList());
        Collections.shuffle(allCards);
        int aiDeckSize = Math.min(15, allCards.size());
        List<Long> aiDeckIds = allCards.subList(0, aiDeckSize).stream()
                .map(Card::getId).collect(Collectors.toList());

        // 洗牌
        List<Long> aiShuffled = new ArrayList<>(aiDeckIds);
        Collections.shuffle(aiShuffled);

        // AI 初始手牌
        List<Long> aiHand = new ArrayList<>();
        int aiDrawCount = Math.min(5, aiShuffled.size());
        for (int i = 0; i < aiDrawCount; i++) aiHand.add(aiShuffled.get(i));
        List<Long> aiDrawPile = new ArrayList<>(aiShuffled.subList(aiDrawCount, aiShuffled.size()));

        session.setOpponentHand(toJson(aiHand));
        session.setOpponentDrawPile(toJson(aiDrawPile));
    }

    // ==================== 结算 ====================

    private void settleBattle(GameSession session, boolean creatorWins) {
        boolean isPvp = session.getMode() == BattleMode.PVP && session.getOpponentUserId() != null;

        // Creator 结算
        int creatorGold = creatorWins ? WIN_GOLD_REWARD : LOSE_GOLD_REWARD;
        int creatorPoints = creatorWins ? WIN_POINTS : LOSE_POINTS;
        shopService.awardGold(session.getPlayerUserId(), creatorGold);
        User creator = userDao.findById(session.getPlayerUserId()).orElse(null);
        if (creator != null) {
            creator.setPoints(Math.max(0, creator.getPoints() + creatorPoints));
            userDao.save(creator);
        }

        // 保存 Creator 战斗记录
        BattleRecord creatorRecord = BattleRecord.builder()
                .userId(session.getPlayerUserId())
                .opponentId(session.getOpponentUserId())
                .mode(session.getMode())
                .result(creatorWins ? BattleResult.WIN : BattleResult.LOSE)
                .selfCharacterId(session.getPlayerCharacterId())
                .opponentCharacterId(session.getOpponentCharacterId())
                .turns(session.getTurnNumber())
                .reward(creatorGold)
                .build();
        battleRecordDao.save(creatorRecord);

        // PvP: 对手结算
        if (isPvp) {
            boolean opponentWins = !creatorWins;
            int opponentGold = opponentWins ? WIN_GOLD_REWARD : LOSE_GOLD_REWARD;
            int opponentPoints = opponentWins ? WIN_POINTS : LOSE_POINTS;
            shopService.awardGold(session.getOpponentUserId(), opponentGold);
            User opponent = userDao.findById(session.getOpponentUserId()).orElse(null);
            if (opponent != null) {
                opponent.setPoints(Math.max(0, opponent.getPoints() + opponentPoints));
                userDao.save(opponent);
            }
            BattleRecord opponentRecord = BattleRecord.builder()
                    .userId(session.getOpponentUserId())
                    .opponentId(session.getPlayerUserId())
                    .mode(session.getMode())
                    .result(opponentWins ? BattleResult.WIN : BattleResult.LOSE)
                    .selfCharacterId(session.getOpponentCharacterId())
                    .opponentCharacterId(session.getPlayerCharacterId())
                    .turns(session.getTurnNumber())
                    .reward(opponentGold)
                    .build();
            battleRecordDao.save(opponentRecord);
        }

        log.info("战斗结算: sessionId={}, creatorWins={}, gold={}/{}", session.getId(), creatorWins, creatorGold, isPvp ? (creatorWins ? LOSE_GOLD_REWARD : WIN_GOLD_REWARD) : "-");
    }

    // ==================== 辅助方法 ====================

    private GameSession getSessionAndValidate(Long sessionId, Long userId) {
        GameSession session = gameSessionDao.findById(sessionId)
                .orElseThrow(() -> new BusinessException("对战不存在"));
        // PvP 允许双方操作，PvE 只允许 creator
        boolean isCreator = session.getPlayerUserId().equals(userId);
        boolean isOpponent = session.getMode() == BattleMode.PVP
                && session.getOpponentUserId() != null
                && session.getOpponentUserId().equals(userId);
        if (!isCreator && !isOpponent)
            throw new BusinessException("无权操作此对战");
        if (session.getSessionStatus() == SessionStatus.FINISHED)
            throw new BusinessException("对战已结束");
        // 检查当前行动方
        if (session.getMode() == BattleMode.PVP
                && session.getCurrentPlayer() != null
                && !session.getCurrentPlayer().equals(userId)) {
            throw new BusinessException("当前不是你的回合");
        }
        return session;
    }

    /** 判断当前用户是否为"视角上的玩家"（即 session creator / playerUserId） */
    private boolean isViewingAsPlayer(GameSession session, Long userId) {
        return session.getPlayerUserId().equals(userId);
    }

    private int getAttackPower(SlotData slot) {
        if (slot.attack == null) return 0;
        if (Boolean.TRUE.equals(slot.isSpecialAttack)) return 0; // 特殊攻击力暂不处理
        return slot.attack;
    }

    private boolean hasSigil(SlotData slot, String sigilName) {
        if (slot.sigils == null || slot.sigils.isBlank()) return false;
        return Arrays.stream(slot.sigils.split(",")).map(String::trim).anyMatch(sigilName::equals);
    }

    private BoardStateResponse buildBoardStateFor(GameSession session, BoardData board, boolean asPlayer) {
        List<Long> myHand = parseJsonList(asPlayer ? session.getPlayerHand() : session.getOpponentHand());
        int oppHandCount = parseJsonList(asPlayer ? session.getOpponentHand() : session.getPlayerHand()).size();
        List<SlotData> mySlots = asPlayer ? board.playerSlots : board.opponentSlots;
        List<SlotData> oppSlots = asPlayer ? board.opponentSlots : board.playerSlots;

        List<BoardStateResponse.SlotInfo> mySlotInfos = new ArrayList<>();
        List<BoardStateResponse.SlotInfo> oppSlotInfos = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            mySlotInfos.add(toSlotInfo(i, mySlots.get(i)));
            oppSlotInfos.add(toSlotInfo(i, oppSlots.get(i)));
        }

        BoardStateResponse.BoardStateResponseBuilder builder = BoardStateResponse.builder()
                .turnNumber(session.getTurnNumber())
                .turnPhase(session.getTurnPhase().name())
                .sessionStatus(session.getSessionStatus().name())
                .playerSlots(mySlotInfos)
                .opponentSlots(oppSlotInfos)
                .playerHand(myHand.stream().map(this::cardToDetail).collect(Collectors.toList()))
                .opponentHandCount(oppHandCount)
                .playerBones(asPlayer ? session.getPlayerBones() : session.getOpponentBones())
                .opponentBones(asPlayer ? session.getOpponentBones() : session.getPlayerBones())
                .playerHp(asPlayer ? session.getPlayerHp() : session.getOpponentHp())
                .opponentHp(asPlayer ? session.getOpponentHp() : session.getPlayerHp())
                .playerItems(parseJsonStringList(asPlayer ? session.getPlayerItems() : session.getOpponentItems()))
                .opponentItems(parseJsonStringList(asPlayer ? session.getOpponentItems() : session.getPlayerItems()));

        // 对战结束时附加结算信息
        if (session.getSessionStatus() == SessionStatus.FINISHED) {
            // 血量 >0 的一方胜利（投降时已将投降方血量置 0）
            boolean creatorWon = session.getPlayerHp() > 0;
            boolean callerWon = asPlayer ? creatorWon : !creatorWon;
            builder.gameOver(true)
                    .winner(callerWon ? "PLAYER" : "OPPONENT")
                    .goldReward(callerWon ? WIN_GOLD_REWARD : LOSE_GOLD_REWARD)
                    .pointsChange(callerWon ? WIN_POINTS : LOSE_POINTS);
        } else {
            builder.gameOver(false);
        }

        return builder.build();
    }

    private BoardStateResponse.SlotInfo toSlotInfo(int index, SlotData slot) {
        if (slot == null) {
            return BoardStateResponse.SlotInfo.builder()
                    .index(index).card(null).isEmpty(true).build();
        }
        CardDetailResponse detail = cardToDetail(slot.cardId);
        // 覆盖运行时血量
        detail.setHealth(slot.currentHealth);
        return BoardStateResponse.SlotInfo.builder()
                .index(index).card(detail).isEmpty(false).build();
    }

    private CardDetailResponse cardToDetail(Long cardId) {
        Card c = cardDao.findById(cardId).orElse(null);
        if (c == null) return null;
        return CardDetailResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .attack(c.getAttack())
                .isSpecialAttack(c.getIsSpecialAttack())
                .health(c.getHealth())
                .bloodCost(c.getBloodCost())
                .boneCost(c.getBoneCost())
                .sigils(c.getSigils())
                .sigilList(c.getSigilList().stream().map(Sigil::getDisplayName).collect(Collectors.toList()))
                .races(c.getRaces())
                .raceList(c.getRaceList().stream().map(Race::getDisplayName).collect(Collectors.toList()))
                .maxDeckCount(c.getMaxDeckCount())
                .isLegendary(c.getIsLegendary())
                .canShuffle(c.getCanShuffle())
                .canSacrifice(c.getCanSacrifice())
                .price(c.getPrice())
                .sacrificeDesc(c.getSacrificeDesc())
                .briefDesc(c.getBriefDesc())
                .build();
    }

    // ==================== JSON 序列化 ====================

    /** 牌桌运行时卡牌数据 */
    static class SlotData {
        public Long cardId;
        public Integer currentHealth;
        public Integer attack;
        public String sigils;
        public Boolean isSpecialAttack;

        public SlotData() {}
        public SlotData(Long cardId, Integer currentHealth, Integer attack, String sigils, Boolean isSpecialAttack) {
            this.cardId = cardId;
            this.currentHealth = currentHealth;
            this.attack = attack;
            this.sigils = sigils;
            this.isSpecialAttack = isSpecialAttack;
        }
    }

    /** 棋盘数据 */
    static class BoardData {
        public List<SlotData> playerSlots;
        public List<SlotData> opponentSlots;

        public BoardData() {}
        public BoardData(List<SlotData> playerSlots, List<SlotData> opponentSlots) {
            this.playerSlots = playerSlots;
            this.opponentSlots = opponentSlots;
        }
    }

    private String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); }
        catch (JsonProcessingException e) { throw new RuntimeException("JSON序列化失败", e); }
    }

    private List<Long> parseJsonList(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try { return objectMapper.readValue(json, new TypeReference<List<Long>>() {}); }
        catch (JsonProcessingException e) { return new ArrayList<>(); }
    }

    private List<String> parseJsonStringList(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try { return objectMapper.readValue(json, new TypeReference<List<String>>() {}); }
        catch (JsonProcessingException e) { return new ArrayList<>(); }
    }

    private BoardData parseBoard(String json) {
        if (json == null || json.isBlank()) {
            return new BoardData(
                    new ArrayList<>(Arrays.asList(null, null, null, null)),
                    new ArrayList<>(Arrays.asList(null, null, null, null))
            );
        }
        try { return objectMapper.readValue(json, BoardData.class); }
        catch (JsonProcessingException e) {
            return new BoardData(
                    new ArrayList<>(Arrays.asList(null, null, null, null)),
                    new ArrayList<>(Arrays.asList(null, null, null, null))
            );
        }
    }
}
