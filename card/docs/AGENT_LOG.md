# AGENT_LOG.md — 开发日志

> 按时间顺序记录关键开发节点

---

## 2026-07-30 · Phase 1-3 · 项目初始化 + 认证模块 + 卡牌数据

### 节点 1：需求分析与架构设计

- **时间**：2026-07-30
- **触发技能**：brainstorming（需求梳理）
- **关键决策**：
  - 确定技术栈：Spring Boot 3.3.5 + Java 21 + Vue 3 + Vite + Pinia
  - 确定分层架构：Controller → Service → Model → DAO
  - 卡牌数据格式：Excel → SQL 初始化脚本
- **人工干预**：根据 carda.txt 49 条需求逐条核对 context 文件，确保无遗漏

### 节点 2：Context 文件重构

- **时间**：2026-07-30
- **触发技能**：writing-plans
- **输出**：完整的 context 文件（343 行），覆盖全部模块
- **关键变更**：
  - 扩展 Card 实体字段（bloodCost/boneCost/canShuffle/canSacrifice）
  - 新增 GameSession 字段（cardsPlayedThisTurn, guaranteedBloodCardDrawn）
  - 新增 Level 实体和关卡选择流程
  - 补充 WebSocket 层（PvP/私信/匹配）
- **教训**：首次修改时遗漏了 `canSacrifice` 属性和关卡选择组件，需要二次对照检查

### 节点 3：后端项目初始化

- **时间**：2026-07-30
- **创建文件**：`pom.xml`, `CardGameApplication.java`, `application.yml`
- **关键配置**：
  - Spring Security 无状态 + JWT
  - MySQL 数据库
  - data.sql + character.sql 延迟执行（`defer-datasource-initialization: true`）

### 节点 4：认证模块实现

- **时间**：2026-07-30
- **创建文件**：22 个后端文件 + 8 个前端文件
- **设计决策**：
  - 验证码存储使用 ConcurrentHashMap（开发阶段），生产环境替换为 Redis
  - JWT 载荷包含 userId + email
  - 用户唯一标识（6位数字）通过 IdGenerator 循环查重保证唯一
  - 前端 Token 存储在 localStorage，Axios 拦截器自动附加

### 节点 5：卡牌实体与数据

- **时间**：2026-07-30
- **创建文件**：`Card.java`, `Sigil.java`(35种), `Race.java`(5种), `data.sql`(68张)
- **关键设计**：
  - `attack` 为 null 时标记 `isSpecialAttack=true`（蚂蚁、蚁后等特殊攻击力）
  - 印记/种族存为逗号分隔中文字符串，提供 `getSigilList()` 解析方法
  - `canShuffle=false` 的 7 张卡：松鼠、蜜蜂、兔子、尾巴、破碎的蛋、铃铛、堤坝
  - `canSacrifice=false` 的 3 张卡：铃铛、堤坝、破碎的蛋
- **数据校验**：逐行对照 Excel 数据，修正了响尾蛇的种族归类（虫族→爬行族）

---

## 2026-07-30 · Phase 4-5 · 人物模块 + 商店模块

### 节点 6：人物模块

- **时间**：2026-07-30
- **创建文件**：`Character.java`, `CharacterDao.java`, `CharacterService.java`, `CharacterServiceImpl.java`, `CharacterController.java`, `character.sql`
- **设计决策**：
  - 人物属性包含 maxHp(20-40), deckSize(10-20), specialAbilityDesc, isDefault, price
  - 默认人物(isDefault=true)免费解锁，其他需用金币购买
  - character.sql 初始化数据通过 application.yml 的 sql.init.data-locations 加载

### 节点 7：商店系统重构

- **时间**：2026-07-30
- **触发技能**：subagent-driven-development
- **创建文件**：`ShopService.java`, `ShopServiceImpl.java`, `ShopController.java`, `PlayerCard.java`, `PlayerCardDao.java`
- **关键变更**：
  - 购买逻辑从消耗品改为解锁制：购买一次即可无限编入卡组
  - 统一售价由 GameConstants 配置
  - PlayerCard 表记录解锁状态（userId + cardId）
- **人工干预**：修正了购买逻辑中的重复购买检查，确保已解锁卡牌不会再次扣费

### 节点 8：前端商店 + 收藏页

- **时间**：2026-07-30
- **创建文件**：`ShopView.vue`, `CollectionView.vue`, `store/shop.js`, `store/card.js`, `CardItem.vue`, `CardImageModal.vue`
- **关键设计**：
  - CardItem 组件统一卡牌展示样式（传奇金边、已解锁绿边）
  - CardImageModal 全屏预览弹窗
  - 68张卡牌图片资源 + cardImages.js 映射

---

## 2026-07-30 · Phase 6 · 卡组构建模块

### 节点 9：卡组管理后端

- **时间**：2026-07-30
- **创建文件**：`Deck.java`, `DeckDao.java`, `DeckCard.java`, `DeckCardDao.java`, `DeckService.java`, `DeckServiceImpl.java`, `DeckController.java`
- **设计决策**：
  - 20 卡组上限（MAX_DECKS_PER_USER = 20）
  - addCard 完整校验：卡牌拥有权、卡组容量、同卡数量上限、传奇卡上限（3张）
  - 所有写操作都校验卡组归属（deck.userId == request.userId）
  - 新增 getOwnedCards() 和 renameDeck() 接口
- **人工干预**：修复了 DeckServiceImpl 构造函数中缺少 PlayerCardDao 注入的问题

### 节点 10：卡组管理前端

- **时间**：2026-07-30
- **创建文件**：`DeckBuilderView.vue`(501行), `store/deck.js`, `api/deck.js`
- **关键设计**：
  - 两栏布局：左侧卡组列表 + 右侧卡组编辑器
  - 点击图片添加卡牌：点击图片直接加入卡组，多次点击多次加入
  - 图片角标显示已编入数量
  - 达到上限后图片变灰不可点击
  - 支持创建/重命名/删除弹窗
- **人工干预**：
  - 修复了 CharacterSelectView 跳转逻辑，选人物后自动跳转卡组页
  - 添加了前端传奇卡数量限制校验（legendaryCountInDeck >= 3）

---

## 2026-07-30 · Phase 7-8 · 战斗引擎 + PvP 对战

### 节点 11：战斗核心引擎

- **时间**：2026-07-30
- **创建文件**：`BattleService.java`, `BattleServiceImpl.java`, `TurnService.java`, `TurnServiceImpl.java`, `SacrificeService.java`, `SacrificeServiceImpl.java`, `CardLogicService.java`, `CardLogicServiceImpl.java`, `ShuffleService.java`, `ShuffleServiceImpl.java`, `AIService.java`, `AIServiceImpl.java`, `RandomEventService.java`, `RandomEventServiceImpl.java`
- **设计决策**：
  - 4v4 棋盘布局
  - 回合制 6 阶段：DRAW → SELECT_CARD → SACRIFICE → PLAY_CARD → END_TURN → AUTO_ATTACK
  - 献祭系统：血献祭精确匹配 + 骨头献祭扣除
  - 洗牌系统：永久死亡排除 + 牌组空时重洗
  - AI 决策：评估出牌优先级（攻击力/血量/费用）

### 节点 12：PvP 对战系统

- **时间**：2026-07-30
- **创建文件**：`MatchService.java`, `MatchServiceImpl.java`, `PvpBattleService.java`, `PvpBattleServiceImpl.java`, `PvpController.java`, `WebSocketConfig.java`
- **关键设计**：
  - 匹配系统采用轮询方式（MatchService + 内存队列）
  - PvP 30秒回合时限，超时强制结束
  - WebSocket 实时通信
- **前端文件**：`MatchView.vue`, `PvpBattleView.vue`, `BattleView.vue`, `BattleResultView.vue`, `BattlePrepareView.vue`, `LevelSelectView.vue`

### 节点 13：战斗逻辑完善

- **时间**：2026-07-30
- **关键实现**：
  - 完整的双人对战流程：匹配 → 战斗 → 结算
  - PvE 关卡选择 + 难度配置
  - 战斗结果页面展示详细数据

---

## 2026-07-30 · Phase 9-10 · 好友系统 + 统计

### 节点 14：好友系统

- **时间**：2026-07-30
- **创建文件**：`Friend.java`, `FriendDao.java`, `FriendMessage.java`, `FriendMessageDao.java`, `FriendService.java`, `FriendServiceImpl.java`, `FriendController.java`
- **前端文件**：`FriendListView.vue`, `ChatView.vue`, `store/friend.js`, `api/friend.js`
- **功能**：搜索添加好友、查看数据、私信聊天、对局邀请

### 节点 15：统计 + 对战记录

- **时间**：2026-07-30
- **创建文件**：`BattleRecord.java`, `BattleRecordDao.java`, `CardUsageStat.java`, `CardUsageStatDao.java`, `RecordService.java`, `StatisticsService.java`
- **前端文件**：`StatisticsView.vue`, `RecordView.vue`
- **功能**：卡牌使用率、PvP胜率、最近20场对战记录

---

## 2026-07-30 · Phase 11-12 · 前端完善 + CI/CD + Docker

### 节点 16：前端公共组件与导航

- **时间**：2026-07-30
- **创建文件**：`NavBar.vue`（品牌导航栏）、`CardItem.vue`（可复用卡牌组件）、`CardImageModal.vue`（全屏预览）
- **设计决策**：统一暗黑主题，所有页面使用相同配色方案

### 节点 17：Docker + CI 配置

- **时间**：2026-07-30
- **创建文件**：`backend/Dockerfile`, `frontend/Dockerfile`, `frontend/nginx.conf`, `docker-compose.yml`, `.github/workflows/ci.yml`, `Makefile`
- **关键配置**：
  - 多阶段构建（Maven build → JRE alpine run）
  - Nginx 反向代理 API + WebSocket
  - CI 包含 unit-test job + docker-build job
- **人工干预**：
  - 修复 Dockerfile 重复内容
  - 修复 docker-compose.yml 硬编码凭据问题
  - 修复 application.yml 中所有硬编码的敏感凭据

### 节点 18：编码修复与 UTF-8 问题

- **时间**：2026-07-30
- **问题**：多个前端 Vue 文件出现 UTF-8 编码损坏（中文变乱码）
- **根因**：PowerShell `Set-Content -Encoding UTF8` 输出带 BOM 的 UTF-8
- **解决方案**：使用 Node.js `fs.writeFileSync()` + `UTF8Encoding $false` 写入
- **修复文件**：`BattleResultView.vue`, `MatchView.vue`, `PvpBattleView.vue`
- **教训**：在 PowerShell 中处理中文文件时，永远不要用 `Set-Content`，改用 `[System.IO.File]::WriteAllLines()` 或 Node.js

---

## 经验总结

### 最有效的 Prompt/Context 策略
- 提供完整的 context 文件（包含所有模块的接口定义和数据结构）让 subagent 理解全局
- 给出 carda.txt 原始需求作为约束，减少 AI 的自由发挥空间

### 最大的坑
- PowerShell 编码问题导致大量中文文件损坏
- Write 工具在此环境中会追加旧文件内容，需要手动截断
- Dockerfile 和 docker-compose.yml 中的硬编码凭据是严重安全隐患

### 如果重做
- 一开始就使用 `.env` 文件管理所有敏感配置
- 更早引入 CI/CD 配置，在每次 push 时自动验证
- 为前端组件编写单元测试，减少回归风险

> 后续开发阶段的日志将在实现过程中持续记录
# AGENT_LOG.md — 开发日志

> 按时间顺序记录关键开发节点

---

## 2026-07-30 · Phase 1-3 · 项目初始化 + 认证模块 + 卡牌数据

### 节点 1：需求分析与架构设计

- **时间**：2026-07-30
- **触发技能**：brainstorming（需求梳理）
- **关键决策**：
  - 确定技术栈：Spring Boot 3.3 + Vue 3 + Vite + Pinia
  - 确定分层架构：Controller → Service → Model → DAO
  - 卡牌数据格式：Excel → SQL 初始化脚本
- **人工干预**：根据 carda.txt 49 条需求逐条核对 context 文件，确保无遗漏

### 节点 2：Context 文件重构

- **时间**：2026-07-30
- **触发技能**：writing-plans
- **输出**：完整的 context 文件（343 行），覆盖全部模块
- **关键变更**：
  - 扩展 Card 实体字段（bloodCost/boneCost/canShuffle/canSacrifice）
  - 新增 GameSession 字段（cardsPlayedThisTurn, guaranteedBloodCardDrawn）
  - 新增 Level 实体和关卡选择流程
  - 补充 WebSocket 层（PvP/私信/匹配）
- **教训**：首次修改时遗漏了 `canSacrifice` 属性和关卡选择组件，需要二次对照检查

### 节点 3：后端项目初始化

- **时间**：2026-07-30
- **创建文件**：`pom.xml`, `CardGameApplication.java`, 三个 profile 配置文件
- **关键配置**：
  - Spring Security 无状态 + JWT
  - H2 内存数据库（开发）/ MySQL（生产）
  - data.sql 延迟执行（`defer-datasource-initialization: true`）

### 节点 4：认证模块实现

- **时间**：2026-07-30
- **创建文件**：22 个后端文件 + 8 个前端文件
- **设计决策**：
  - 验证码存储使用 ConcurrentHashMap（开发阶段），生产环境替换为 Redis
  - JWT 载荷包含 userId + email
  - 用户唯一标识（6位数字）通过 IdGenerator 循环查重保证唯一
  - 前端 Token 存储在 localStorage，Axios 拦截器自动附加

### 节点 5：卡牌实体与数据

- **时间**：2026-07-30
- **创建文件**：`Card.java`, `Sigil.java`(35种), `Race.java`(5种), `data.sql`(68张)
- **关键设计**：
  - `attack` 为 null 时标记 `isSpecialAttack=true`（蚂蚁、蚁后等特殊攻击力）
  - 印记/种族存为逗号分隔中文字符串，提供 `getSigilList()` 解析方法
  - `canShuffle=false` 的 7 张卡：松鼠、蜜蜂、兔子、尾巴、破碎的蛋、铃铛、堤坝
  - `canSacrifice=false` 的 3 张卡：铃铛、堤坝、破碎的蛋
- **数据校验**：逐行对照 Excel 数据，修正了响尾蛇的种族归类（虫族→爬行族）

---

##  节点6 商店逻辑

> 后续开发阶段的日志将在实现过程中持续记录
