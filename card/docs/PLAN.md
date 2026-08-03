# PLAN.md — 野兽牌 实现计划

> 基于 SPEC.md 的任务拆分，每个 task 由一个 subagent 在一次会话内完成

---

## 阶段总览

| 阶段 | 内容 | 状态 |
|------|------|------|
| Phase 1 | 项目初始化 + 基础设施 | ✅ 完成 |
| Phase 2 | 认证模块（注册/登录/JWT） | ✅ 完成 |
| Phase 3 | 卡牌数据层 | ✅ 完成 |
| Phase 4 | 人物模块 | ✅ 完成 |
| Phase 5 | 商店模块 | ✅ 完成 |
| Phase 6 | 卡组构建模块 | ✅ 完成 |
| Phase 7 | 战斗核心引擎 | ✅ 完成 |
| Phase 8 | PvP 对战 + WebSocket | ✅ 完成 |
| Phase 9 | 好友系统 | ✅ 完成 |
| Phase 10 | 统计 + 对战记录 | ✅ 完成 |
| Phase 11 | 前端完整页面 | ✅ 完成 |
| Phase 12 | CI/CD + Docker + 部署 | ✅ 完成 |

---

## Phase 1：项目初始化 + 基础设施 ✅

### Task 1.1 — 后端项目骨架
- **目标**：创建 Spring Boot 项目结构
- **文件**：`pom.xml`, `CardGameApplication.java`, `application.yml`
- **验证**：`mvn spring-boot:run` 启动成功
- **状态**：✅ 完成

### Task 1.2 — 安全与跨域配置
- **目标**：配置 Spring Security + JWT + CORS
- **文件**：`SecurityConfig.java`, `JwtAuthenticationFilter.java`, `CorsConfig.java`, `JwtUtil.java`
- **验证**：公开接口无需Token；受保护接口需Bearer Token
- **状态**：✅ 完成

### Task 1.3 — 异常处理与工具类
- **目标**：统一异常处理 + 常量管理 + ID生成器
- **文件**：`BusinessException.java`, `GlobalExceptionHandler.java`, `GameConstants.java`, `IdGenerator.java`
- **验证**：异常返回统一JSON格式
- **状态**：✅ 完成

### Task 1.4 — 前端项目骨架
- **目标**：创建 Vue 3 + Vite 项目 + 路由 + Axios
- **文件**：`package.json`, `vite.config.js`, `index.html`, `App.vue`, `main.js`, `router/index.js`, `request.js`
- **验证**：`npm run dev` 启动，页面可访问
- **状态**：✅ 完成

---

## Phase 2：认证模块 ✅

### Task 2.1 — User 实体 + DAO
- **目标**：用户数据模型
- **文件**：`User.java`, `UserDao.java`
- **验证**：JPA 自动建表；findByEmail 查询正常
- **状态**：✅ 完成

### Task 2.2 — 认证服务实现
- **目标**：验证码发送/校验 + 注册 + 登录
- **文件**：`AuthService.java`, `AuthServiceImpl.java`, `EmailService.java`, `EmailServiceImpl.java`
- **验证**：注册→登录→获取Token 全链路
- **状态**：✅ 完成

### Task 2.3 — 认证控制器
- **目标**：REST API 暴露
- **文件**：`AuthController.java`, DTO（`SendCodeRequest`, `RegisterRequest`, `LoginRequest` 等）
- **验证**：curl 调用 send-code / register / login 返回正确响应
- **状态**：✅ 完成

### Task 2.4 — 前端登录注册页面
- **目标**：登录/注册表单 + Pinia 状态管理
- **文件**：`LoginView.vue`, `LoginForm.vue`, `RegisterForm.vue`, `HomeView.vue`, `store/user.js`, `api/auth.js`
- **验证**：注册成功后切换登录，登录后跳转大厅
- **状态**：✅ 完成

---

## Phase 3：卡牌数据层 ✅

### Task 3.1 — 枚举定义
- **目标**：35种印记 + 5种族 枚举
- **文件**：`Sigil.java`, `Race.java`
- **验证**：所有印记名称与 card_design.txt 一致
- **状态**：✅ 完成

### Task 3.2 — Card 实体
- **目标**：卡牌数据模型（15字段 + 辅助方法）
- **文件**：`Card.java`
- **验证**：字段与 carda.txt 要求完全对应
- **状态**：✅ 完成

### Task 3.3 — 卡牌初始数据
- **目标**：68张卡牌 SQL 数据
- **文件**：`data.sql`
- **验证**：启动后 SELECT COUNT(*) FROM cards = 68
- **状态**：✅ 完成

---

## Phase 4：人物模块 ✅

### Task 4.1 — Character 实体 + DAO
- **目标**：人物数据模型（血量20-40、卡组数10-20、技能描述）
- **文件**：`Character.java`, `CharacterDao.java`, `character.sql`
- **验证**：人物数据正确加载
- **状态**：✅ 完成

### Task 4.2 — CharacterService + Controller
- **目标**：人物查询 API
- **文件**：`CharacterService.java`, `CharacterServiceImpl.java`, `CharacterController.java`
- **验证**：GET /api/characters 返回人物列表
- **状态**：✅ 完成

### Task 4.3 — 前端人物选择页
- **目标**：人物展示 + 选择后跳转卡组管理
- **文件**：`CharacterSelectView.vue`, `store/character.js`, `api/character.js`
- **验证**：点击人物→自动跳转卡组页
- **状态**：✅ 完成

---

## Phase 5：商店模块 ✅

### Task 5.1 — ShopItem/PlayerCard 实体 + DAO
- **目标**：商店商品和玩家卡牌数据模型
- **文件**：`ShopItem.java`, `ShopItemDao.java`, `PlayerCard.java`, `PlayerCardDao.java`
- **状态**：✅ 完成

### Task 5.2 — ShopService + Controller
- **目标**：商店浏览 + 购买卡牌（扣金币 + 解锁卡牌）
- **文件**：`ShopService.java`, `ShopServiceImpl.java`, `ShopController.java`
- **验证**：购买成功→金币扣除→PlayerCard解锁；金币不足返回错误
- **状态**：✅ 完成

### Task 5.3 — 前端商店 + 收藏页
- **目标**：商店购买界面 + 卡牌收藏展示
- **文件**：`ShopView.vue`, `CollectionView.vue`, `store/shop.js`, `store/card.js`, `api/shop.js`, `api/card.js`
- **验证**：卡牌图片展示，购买后收藏更新
- **状态**：✅ 完成

### Task 5.4 — 卡牌图片资源
- **目标**：68张卡牌图片 + cardImages.js 映射
- **文件**：`assets/cards/*.png`, `utils/cardImages.js`
- **状态**：✅ 完成

---

## Phase 6：卡组构建模块 ✅

### Task 6.1 — Deck 实体 + DAO
- **目标**：卡组数据模型 + 关联关系
- **文件**：`Deck.java`, `DeckDao.java`, `DeckCard.java`, `DeckCardDao.java`
- **状态**：✅ 完成

### Task 6.2 — DeckService + Controller
- **目标**：创建/编辑/删除卡组 + 添加/移除卡牌 + 校验规则
- **校验规则**：
  - 卡牌数量 = 人物要求的 deckSize
  - 传奇卡 ≤ 3
  - 每张卡 ≤ maxDeckCount
  - 用户最多 20 个卡组
  - 只能编入已拥有的卡牌
- **文件**：`DeckService.java`, `DeckServiceImpl.java`, `DeckController.java`
- **状态**：✅ 完成

### Task 6.3 — 前端卡组管理页
- **目标**：卡组列表 + 编辑器（点击图片添加、多次点击多次添加）
- **文件**：`DeckBuilderView.vue`, `store/deck.js`, `api/deck.js`
- **验证**：选人物→创建卡组→编辑→校验通过
- **状态**：✅ 完成

---

## Phase 7：战斗核心引擎 ✅

### Task 7.1 — GameSession 模型
- **目标**：运行时战斗状态（棋盘4v4、手牌、抽牌堆、骨头等）
- **文件**：`GameSession.java`, `GameSessionDao.java`
- **状态**：✅ 完成

### Task 7.2 — 战斗服务
- **目标**：开局（随机先后手、抽5张、保底1费血献祭卡）
- **文件**：`BattleService.java`, `BattleServiceImpl.java`, `BattleController.java`
- **状态**：✅ 完成

### Task 7.3 — 回合流程
- **目标**：抽牌→选牌→献祭→出牌→结算 完整流程
- **文件**：`TurnService.java`, `TurnServiceImpl.java`
- **状态**：✅ 完成

### Task 7.4 — 献祭系统
- **目标**：血献祭精确匹配 + 骨头献祭扣除 + 不可献祭卡牌检查
- **文件**：`SacrificeService.java`, `SacrificeServiceImpl.java`
- **状态**：✅ 完成

### Task 7.5 — 印记效果
- **目标**：核心印记的战斗效果实现
- **文件**：`CardLogicService.java`, `CardLogicServiceImpl.java`
- **状态**：✅ 完成

### Task 7.6 — 洗牌系统
- **目标**：永久死亡排除 + 牌组空时重洗
- **文件**：`ShuffleService.java`, `ShuffleServiceImpl.java`
- **状态**：✅ 完成

### Task 7.7 — AI 决策
- **目标**：人机对战AI出牌逻辑
- **文件**：`AIService.java`, `AIServiceImpl.java`
- **状态**：✅ 完成

### Task 7.8 — 随机事件
- **目标**：环境随机事件（双方效果相同）
- **文件**：`RandomEventService.java`, `RandomEventServiceImpl.java`
- **状态**：✅ 完成

### Task 7.9 — 关卡系统
- **目标**：PvE 关卡选择 + 难度配置
- **文件**：`Level.java`, `LevelDao.java`, `LevelService.java`, `LevelServiceImpl.java`, `LevelController.java`
- **状态**：✅ 完成

### Task 7.10 — 战斗前端
- **目标**：人机对战界面 + 战斗结果页
- **文件**：`BattleView.vue`, `BattleResultView.vue`, `BattlePrepareView.vue`, `LevelSelectView.vue`
- **状态**：✅ 完成

---

## Phase 8：PvP 对战 + WebSocket ✅

### Task 8.1 — WebSocket 配置
- **文件**：`WebSocketConfig.java`
- **状态**：✅ 完成

### Task 8.2 — 匹配系统
- **文件**：`MatchService.java`, `MatchServiceImpl.java`
- **状态**：✅ 完成

### Task 8.3 — PvP 对战逻辑（30秒计时）
- **文件**：`PvpBattleService.java`, `PvpBattleServiceImpl.java`, `PvpController.java`
- **状态**：✅ 完成

### Task 8.4 — PvP 前端
- **文件**：`MatchView.vue`, `PvpBattleView.vue`, `store/pvp.js`, `api/pvp.js`
- **状态**：✅ 完成

---

## Phase 9：好友系统 ✅

### Task 9.1 — 好友实体 + DAO
- **文件**：`Friend.java`, `FriendDao.java`, `FriendMessage.java`, `FriendMessageDao.java`
- **状态**：✅ 完成

### Task 9.2 — 好友服务 + 控制器
- **文件**：`FriendService.java`, `FriendServiceImpl.java`, `FriendController.java`
- **状态**：✅ 完成

### Task 9.3 — 好友前端
- **文件**：`FriendListView.vue`, `ChatView.vue`, `store/friend.js`, `api/friend.js`
- **状态**：✅ 完成

---

## Phase 10：统计 + 对战记录 ✅

### Task 10.1 — 对战记录
- **文件**：`BattleRecord.java`, `BattleRecordDao.java`, `RecordService.java`, `RecordServiceImpl.java`, `RecordController.java`
- **状态**：✅ 完成

### Task 10.2 — 卡牌统计
- **文件**：`CardUsageStat.java`, `CardUsageStatDao.java`, `StatisticsService.java`, `StatisticsServiceImpl.java`, `StatisticsController.java`
- **状态**：✅ 完成

### Task 10.3 — 统计前端
- **文件**：`StatisticsView.vue`, `RecordView.vue`, `store/statistics.js`, `store/record.js`
- **状态**：✅ 完成

---

## Phase 11：前端完整页面 ✅

### Task 11.1 — 公共组件
- **文件**：`NavBar.vue`, `CardItem.vue`, `CardImageModal.vue`
- **状态**：✅ 完成

### Task 11.2 — 对战准备页
- **文件**：`BattlePrepareView.vue`（4步骤导航：选人物→选关卡→选卡组→开始）
- **状态**：✅ 完成

---

## Phase 12：CI/CD + Docker + 部署 ✅

### Task 12.1 — Dockerfile + docker-compose
- **文件**：`backend/Dockerfile`, `frontend/Dockerfile`, `frontend/nginx.conf`, `docker-compose.yml`
- **验证**：`docker-compose up -d` 三条命令启动全部服务
- **状态**：✅ 完成

### Task 12.2 — CI 配置（GitHub Actions）
- **文件**：`.github/workflows/ci.yml`（含 unit-test job + docker-build job）
- **状态**：✅ 完成

### Task 12.3 — Makefile
- **文件**：`Makefile`（test / build / run / clean 命令）
- **状态**：✅ 完成

### Task 12.4 — 凭据安全修复
- **目标**：移除所有硬编码凭据
- **文件**：`application.yml`, `docker-compose.yml`
- **状态**：✅ 完成

---

## 依赖关系

```
Phase 1 → Phase 2 → Phase 3 → Phase 4
                              → Phase 5
                              → Phase 6 (依赖 4+5)
                              → Phase 7 (依赖 3+6)
                              → Phase 8 (依赖 7)
                              → Phase 9 (并行)
                              → Phase 10 (依赖 7+8)
Phase 2-10 → Phase 11
Phase 11 → Phase 12
```

可并行：Phase 4 和 Phase 5 可并行；Phase 9 可与其他阶段并行
# PLAN.md — 卡牌游戏实现计划

> 基于 SPEC.md 的任务拆分，每个 task 可由一个 subagent 在一次会话内完成

---

## 阶段总览

| 阶段 | 内容 | 状态 |
|------|------|------|
| Phase 1 | 项目初始化 + 基础设施 | ✅ 完成 |
| Phase 2 | 认证模块（注册/登录/JWT） | ✅ 完成 |
| Phase 3 | 卡牌数据层 | ✅ 完成 |
| Phase 4 | 人物模块 | ⬜ 待做 |
| Phase 5 | 商店模块 | ⬜ 待做 |
| Phase 6 | 卡组构建模块 | ⬜ 待做 |
| Phase 7 | 战斗核心引擎 | ⬜ 待做 |
| Phase 8 | PvP 对战 + WebSocket | ⬜ 待做 |
| Phase 9 | 好友系统 | ⬜ 待做 |
| Phase 10 | 统计 + 对战记录 | ⬜ 待做 |
| Phase 11 | 前端完整页面 | ⬜ 待做 |
| Phase 12 | CI/CD + Docker + 部署 | ⬜ 待做 |

---

## Phase 1：项目初始化 + 基础设施 ✅

### Task 1.1 — 后端项目骨架
- **目标**：创建 Spring Boot 项目结构
- **文件**：`pom.xml`, `CardGameApplication.java`, `application.yml`, `application-dev.yml`, `application-prod.yml`
- **验证**：`mvn spring-boot:run` 启动成功
- **状态**：✅ `commit: feat: 初始化Spring Boot项目`

### Task 1.2 — 安全与跨域配置
- **目标**：配置 Spring Security + JWT + CORS
- **文件**：`SecurityConfig.java`, `JwtAuthenticationFilter.java`, `CorsConfig.java`, `JwtUtil.java`
- **验证**：公开接口无需Token；受保护接口需Bearer Token
- **状态**：✅

### Task 1.3 — 异常处理与工具类
- **目标**：统一异常处理 + 常量管理 + ID生成器
- **文件**：`BusinessException.java`, `GlobalExceptionHandler.java`, `GameConstants.java`, `IdGenerator.java`
- **验证**：异常返回统一JSON格式
- **状态**：✅

### Task 1.4 — 前端项目骨架
- **目标**：创建 Vue 3 + Vite 项目 + 路由 + Axios
- **文件**：`package.json`, `vite.config.js`, `index.html`, `App.vue`, `main.js`, `router/index.js`, `request.js`
- **验证**：`npm run dev` 启动，页面可访问
- **状态**：✅

---

## Phase 2：认证模块 ✅

### Task 2.1 — User 实体 + DAO
- **目标**：用户数据模型
- **文件**：`User.java`, `UserDao.java`
- **验证**：JPA 自动建表；findByEmail 查询正常
- **状态**：✅

### Task 2.2 — 认证服务实现
- **目标**：验证码发送/校验 + 注册 + 登录
- **文件**：`AuthService.java`, `AuthServiceImpl.java`, `EmailService.java`, `EmailServiceImpl.java`
- **验证**：注册→登录→获取Token 全链路
- **状态**：✅

### Task 2.3 — 认证控制器
- **目标**：REST API 暴露
- **文件**：`AuthController.java`, DTO（`SendCodeRequest`, `RegisterRequest`, `LoginRequest`, `RegisterResponse`, `LoginResponse`）
- **验证**：curl 调用 send-code / register / login 返回正确响应
- **状态**：✅

### Task 2.4 — 前端登录注册页面
- **目标**：登录/注册表单 + Pinia 状态管理
- **文件**：`LoginForm.vue`, `RegisterForm.vue`, `LoginView.vue`, `HomeView.vue`, `store/user.js`, `api/auth.js`
- **验证**：注册成功后切换登录，登录后跳转大厅
- **状态**：✅

---

## Phase 3：卡牌数据层 ✅

### Task 3.1 — 枚举定义
- **目标**：35种印记 + 5种族 枚举
- **文件**：`Sigil.java`, `Race.java`
- **验证**：所有印记名称与 card_design.txt 一致
- **状态**：✅

### Task 3.2 — Card 实体
- **目标**：卡牌数据模型（15字段 + 辅助方法）
- **文件**：`Card.java`
- **验证**：字段与 carda.txt 要求完全对应
- **状态**：✅

### Task 3.3 — 卡牌初始数据
- **目标**：68张卡牌 SQL 数据
- **文件**：`data.sql`
- **验证**：启动后 SELECT COUNT(*) FROM cards = 68
- **状态**：✅

---

## Phase 4：人物模块 ⬜

### Task 4.1 — Character 实体 + DAO
- **目标**：人物数据模型（血量20-40、卡组数10-20、主动/被动技能）
- **文件**：`Character.java`, `CharacterDao.java`, `character.sql`
- **验证**：人物数据正确加载

### Task 4.2 — CharacterService + Controller
- **目标**：人物查询 API
- **文件**：`CharacterService.java`, `CharacterServiceImpl.java`, `CharacterController.java`
- **验证**：GET /api/characters 返回人物列表

---

## Phase 5：商店模块 ⬜

### Task 5.1 — ShopItem 实体 + DAO
- **文件**：`ShopItem.java`, `ShopItemDao.java`, `PlayerCard.java`, `PlayerCardDao.java`

### Task 5.2 — ShopService + Controller
- **目标**：商店浏览 + 购买卡牌（扣金币 + 加库存）
- **文件**：`ShopService.java`, `ShopServiceImpl.java`, `ShopController.java`, `BuyCardRequest.java`
- **验证**：购买成功→金币扣除→PlayerCard+1；金币不足返回错误

---

## Phase 6：卡组构建模块 ⬜

### Task 6.1 — Deck 实体 + DAO
- **文件**：`Deck.java`, `DeckDao.java`, `DeckCard.java`, `DeckCardDao.java`

### Task 6.2 — DeckService + Controller
- **目标**：创建卡组 + 添加/移除卡牌 + 校验规则
- **校验规则**：
  - 卡牌数量 = 人物要求的 deckSize
  - 传奇卡 ≤ 3
  - 每张卡 ≤ maxDeckCount
  - maxDeckCount=0 的卡不能入组
- **文件**：`DeckService.java`, `DeckServiceImpl.java`, `DeckController.java`, `DeckValidationException.java`
- **验证**：各种不合法场景被正确拒绝

---

## Phase 7：战斗核心引擎 ⬜

### Task 7.1 — GameSession 模型
- **目标**：运行时战斗状态（棋盘4v4、手牌、抽牌堆、骨头等）
- **文件**：`GameSession.java`

### Task 7.2 — 战斗服务
- **目标**：开局（随机先后手、抽5张、保底1费血献祭卡）
- **文件**：`BattleService.java`, `BattleServiceImpl.java`

### Task 7.3 — 回合流程
- **目标**：抽牌→选牌→献祭→出牌→结算 完整流程
- **文件**：`TurnService.java`, `TurnServiceImpl.java`

### Task 7.4 — 献祭系统
- **目标**：血献祭精确匹配 + 骨头献祭扣除 + 不可献祭卡牌检查
- **文件**：`SacrificeService.java`, `SacrificeServiceImpl.java`

### Task 7.5 — 印记效果
- **目标**：35种印记的战斗效果实现
- **文件**：`CardLogicService.java`, `CardLogicServiceImpl.java`

### Task 7.6 — 洗牌系统
- **目标**：永久死亡排除 + 牌组空时重洗
- **文件**：`ShuffleService.java`, `ShuffleServiceImpl.java`

### Task 7.7 — AI 决策
- **目标**：人机对战AI出牌逻辑
- **文件**：`AIService.java`, `AIServiceImpl.java`

### Task 7.8 — 随机事件
- **目标**：环境随机事件（双方效果相同）
- **文件**：`RandomEventService.java`, `RandomEventServiceImpl.java`

---

## Phase 8：PvP 对战 + WebSocket ⬜

### Task 8.1 — WebSocket 配置
- **文件**：`WebSocketConfig.java`

### Task 8.2 — 匹配系统
- **文件**：`MatchService.java`, `MatchServiceImpl.java`, `MatchWebSocketHandler.java`

### Task 8.3 — PvP 对战逻辑（30秒计时）
- **文件**：`PvpBattleService.java`, `PvpBattleServiceImpl.java`, `BattleWebSocketHandler.java`, `PvpController.java`

---

## Phase 9：好友系统 ⬜

### Task 9.1 — 好友实体 + DAO
- **文件**：`Friend.java`, `FriendDao.java`

### Task 9.2 — 好友服务 + 控制器
- **文件**：`FriendService.java`, `FriendServiceImpl.java`, `FriendController.java`

### Task 9.3 — 私信系统
- **文件**：`FriendMessage.java`, `FriendMessageDao.java`, `ChatWebSocketHandler.java`

---

## Phase 10：统计 + 对战记录 ⬜

### Task 10.1 — 对战记录
- **文件**：`BattleRecord.java`, `BattleRecordDao.java`, `RecordService.java`, `RecordServiceImpl.java`, `RecordController.java`

### Task 10.2 — 卡牌统计
- **文件**：`CardUsageStat.java`, `CardUsageStatDao.java`, `StatisticsService.java`, `StatisticsServiceImpl.java`, `StatisticsController.java`

---

## Phase 11：前端完整页面 ⬜

### Task 11.1 — 商店 + 卡牌收藏页
- **文件**：`ShopView.vue`, `CollectionView.vue`, `shop.js`, `card.js`

### Task 11.2 — 人物选择 + 卡组编辑页
- **文件**：`CharacterSelectView.vue`, `DeckBuilderView.vue`, `DeckValidator.vue`

### Task 11.3 — 对战界面（人机+PvP）
- **文件**：`BattleView.vue`, `PvpBattleView.vue`, `BattleBoard.vue`, `HandArea.vue`, `SacrificePanel.vue`, `TurnTimer.vue` 等

### Task 11.4 — 好友 + 统计 + 记录页
- **文件**：`FriendListView.vue`, `ChatView.vue`, `StatisticsView.vue`, `RecordView.vue`

---

## Phase 12：CI/CD + Docker + 部署 ⬜

### Task 12.1 — Dockerfile + docker-compose
- **文件**：`backend/Dockerfile`, `frontend/Dockerfile`, `docker-compose.yml`

### Task 12.2 — CI 配置（GitHub Actions）
- **文件**：`.github/workflows/ci.yml`（含 unit-test job）

### Task 12.3 — 测试用例
- **文件**：`AuthControllerTest.java`, `DeckServiceTest.java`, `BattleServiceTest.java` 等

### Task 12.4 — Makefile
- **文件**：`Makefile`（test / build / run 命令）

---

## 依赖关系

```
Phase 1 → Phase 2 → Phase 3 → Phase 4
                              → Phase 5
                              → Phase 6 (依赖 4+5)
                              → Phase 7 (依赖 3+6)
                              → Phase 8 (依赖 7)
                              → Phase 9
                              → Phase 10 (依赖 7+8)
Phase 2-10 → Phase 11
Phase 11 → Phase 12
```

可并行：Phase 4 和 Phase 5 可并行；Phase 9 可与其他阶段并行
