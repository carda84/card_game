# SPEC_PROCESS.md — 规约生成过程文档

> 记录与 AI 协作生成 SPEC 和 PLAN 的过程

---

## 1. Brainstorming 关键节点

### 智能体追问的好问题

1. **"卡牌的攻击力为什么有些是'特殊'？如何处理？"**
   - 促使我们设计了 `isSpecialAttack` 标记字段，区分固定攻击力和运行时计算攻击力（蚂蚁、蚁后等）

2. **"印记和种族在数据库中如何存储？枚举数组还是字符串？"**
   - 考虑到多印记/多种族的灵活性和 SQL 兼容性，最终选择逗号分隔字符串 + 辅助解析方法

3. **"PvP 对战的实时通信方案：轮询还是 WebSocket？"**
   - 明确需要 WebSocket 支撑 PvP 对战、好友私信、匹配等待三个场景

4. **"商店购买是消耗品还是解锁制？"**
   - 最终确定为解锁制：购买一次即可无限编入卡组，符合 Inscryption 原作理念

### 促使修正原设想的问题

1. **原始设想**：使用 username 作为登录凭证
   - **修正**：根据 carda.txt 要求改为邮箱注册 + 验证码模式，唯一标识（#138992）仅作为展示和好友搜索用途

2. **原始设想**：卡牌稀有度使用 COMMON/RARE/EPIC/LEGENDARY
   - **修正**：根据 carda.txt 改为 `isLegendary` 布尔字段，传奇只是卡牌的一个属性而非稀有度等级

3. **原始设想**：战斗结算采用同时出牌模式
   - **修正**：根据 carda.txt 确认为严格的回合制，选牌→献祭→出牌必须连贯，被打断则全部回退

4. **原始设想**：卡组数量不限
   - **修正**：设定每用户最多 20 个卡组上限，防止数据膨胀

---

## 2. 关键迭代

### 迭代 1：Context 文件首次重构

**AI 建议**：将 TurnResolveService（同时出牌结算）拆分为 TurnService + CardLogicService + ShuffleService

**我的决策**：采纳。因为 carda.txt 明确要求：
- 回合流程有 6 个阶段（TurnService）
- 35种印记需要独立的效果解析逻辑（CardLogicService）
- 洗牌有特殊规则（ShuffleService）

### 迭代 2：卡牌实体字段设计

**AI 建议**：将印记和种族存为 JSON 数组

**我的决策**：推翻。改为逗号分隔字符串，原因：
- H2/MySQL 的 JSON 支持不一致
- 简单的 LIKE 查询即可实现筛选
- 前端展示直接 split 即可

### 迭代 3：验证码存储方案

**AI 建议**：使用 Redis 存储验证码

**我的决策**：暂时使用 ConcurrentHashMap，因为：
- 开发阶段避免引入额外依赖
- 接口层面保持 EmailService 抽象，后续可无缝替换

### 迭代 4：数据库从 H2 迁移到 MySQL

**AI 建议**：继续使用 H2 内存数据库开发

**我的决策**：推翻。迁移到 MySQL，因为：
- H2 重启丢数据，开发体验差
- MySQL 更接近生产环境
- Docker 一键启动 MySQL 很方便

### 迭代 5：卡组管理交互设计

**AI 建议**：传统的拖拽式卡组编辑

**我的决策**：改为点击图片添加模式：
- 点击已拥有卡牌的图片直接加入卡组
- 多次点击代表多次加入
- 达到上限后图片变灰不可点击
- 更符合移动端和桌面端统一体验

---

## 3. AI 建议的处理

### 采纳的建议
- 使用 Spring Boot 3.3.5（最新稳定版）而非 3.1
- 使用 Pinia 而非 Vuex（Vue 3 官方推荐）
- 在 Card 实体中添加辅助方法（`hasSigil`, `getSigilList` 等）
- 使用多阶段 Docker 构建减小镜像体积
- 统一异常处理返回 JSON 格式

### 推翻/修正的建议
- 推翻了 JSON 存储印记/种族的方案（见上）
- 推翻了使用 Spring Session 的方案（JWT 无状态不需要）
- 修正了卡牌数据中响尾蛇的种族归类（AI 初版误放入虫族）
- 推翻了 H2 继续开发的方案，迁移到 MySQL
- 修正了 application.yml 中的硬编码凭据问题

---

## 4. 反思

### Brainstorming 做得好的地方
- 需求覆盖全面：68 张卡牌数据逐条核对 Excel
- 分层架构清晰：每个模块的 Service/DAO/Controller 边界明确
- 安全考量到位：凭据通过环境变量注入

### 让人不满的地方
- 初始 context 文件遗漏了关卡选择功能和 canSacrifice 属性
- 对 PvP 30 秒超时的回退逻辑描述不够具体
- 印记效果的实现优先级未明确排序
- 商店购买逻辑从消耗品改为解锁制时，没有及时更新 SPEC

---

## 5. 冷启动验证

### 验证过程

按照通用要求 §4.5，使用一个与主开发智能体不同的 agent（全新 session，不导入对话历史），仅凭 SPEC.md + PLAN.md 尝试实现 Task 4.1（Character 实体 + DAO）。

### 暴露的 SPEC 缺陷

1. **Character 实体字段不完整**：SPEC 中只列了 `name, maxHp, deckSize, activeSkill, passiveSkill`，但实际还需要 `specialAbilityDesc, isDefault, price` 字段
   - **修订**：在 SPEC 6.数据模型中补充完整字段列表

2. **缺少 character.sql 文件名约定**：PLAN 中只提到 `character.sql`，但没说明它应放在 `src/main/resources/` 下以及如何在 `application.yml` 的 `sql.init.data-locations` 中引用
   - **修订**：在 PLAN Task 4.1 中补充文件路径和配置引用说明

3. **枚举值与实际代码不一致**：SPEC 中 `TurnPhase` 枚举使用英文大写，但实际代码中某些阶段名称有差异
   - **修订**：统一 SPEC 中的枚举定义与实际代码保持一致

### 产出与预期差距

第二个 agent 成功创建了 Character.java 实体和 CharacterDao.java 接口，基本符合预期。主要差距在于遗漏了 `isDefault` 和 `price` 字段，导致后续商店中的人物购买功能无法关联。

### 据此对 SPEC/PLAN 的修订

- SPEC §6 数据模型：补充 Character 的 `specialAbilityDesc`, `isDefault`, `price` 字段
- PLAN Task 4.1：明确 `character.sql` 放在 `src/main/resources/` 下
- PLAN Task 4.1：补充 `application.yml` 的 `sql.init.data-locations` 需包含 `classpath:character.sql`
# SPEC_PROCESS.md — 规约生成过程文档

> 记录与 AI 协作生成 SPEC 和 PLAN 的过程

---

## 1. Brainstorming 关键节点

### 智能体追问的好问题

1. **"卡牌的攻击力为什么有些是'特殊'？如何处理？"**
   - 促使我们设计了 `isSpecialAttack` 标记字段，区分固定攻击力和运行时计算攻击力（蚂蚁、蚁后等）

2. **"印记和种族在数据库中如何存储？枚举数组还是字符串？"**
   - 考虑到多印记/多种族的灵活性和 SQL 兼容性，最终选择逗号分隔字符串 + 辅助解析方法

3. **"PvP 对战的实时通信方案：轮询还是 WebSocket？"**
   - 明确需要 WebSocket 支撑 PvP 对战、好友私信、匹配等待三个场景

### 促使修正原设想的问题

1. **原始设想**：使用 username 作为登录凭证
   - **修正**：根据 carda.txt 要求改为邮箱注册 + 验证码模式，唯一标识（#138992）仅作为展示和好友搜索用途

2. **原始设想**：卡牌稀有度使用 COMMON/RARE/EPIC/LEGENDARY
   - **修正**：根据 carda.txt 改为 `isLegendary` 布尔字段，传奇只是卡牌的一个属性而非稀有度等级

3. **原始设想**：战斗结算采用同时出牌模式
   - **修正**：根据 carda.txt 确认为严格的回合制，选牌→献祭→出牌必须连贯，被打断则全部回退

---

## 2. 关键迭代

### 迭代 1：Context 文件首次重构

**AI 建议**：将 TurnResolveService（同时出牌结算）拆分为 TurnService + CardLogicService + ShuffleService

**我的决策**：采纳。因为 carda.txt 明确要求：
- 回合流程有 6 个阶段（TurnService）
- 35种印记需要独立的效果解析逻辑（CardLogicService）
- 洗牌有特殊规则（ShuffleService）

### 迭代 2：卡牌实体字段设计

**AI 建议**：将印记和种族存为 JSON 数组

**我的决策**：推翻。改为逗号分隔字符串，原因：
- H2/MySQL 的 JSON 支持不一致
- 简单的 LIKE 查询即可实现筛选
- 前端展示直接 split 即可

### 迭代 3：验证码存储方案

**AI 建议**：使用 Redis 存储验证码

**我的决策**：暂时使用 ConcurrentHashMap，因为：
- 开发阶段避免引入额外依赖
- 接口层面保持 EmailService 抽象，后续可无缝替换

---

## 3. AI 建议的处理

### 采纳的建议
- 使用 Spring Boot 3.3（最新稳定版）而非 3.1
- 使用 Pinia 而非 Vuex（Vue 3 官方推荐）
- 在 Card 实体中添加辅助方法（`hasSigil`, `getSigilList` 等）

### 推翻/修正的建议
- 推翻了 JSON 存储印记/种族的方案（见上）
- 推翻了使用 Spring Session 的方案（JWT 无状态不需要）
- 修正了卡牌数据中响尾蛇的种族归类（AI 初版误放入虫族）

---

## 4. 反思

### Brainstorming 做得好的地方
- 需求覆盖全面：68 张卡牌数据逐条核对 Excel
- 分层架构清晰：每个模块的 Service/DAO/Controller 边界明确
- 安全考量到位：凭据通过环境变量注入

### 让人不满的地方
- 初始 context 文件遗漏了关卡选择功能和 canSacrifice 属性
- 对 PvP 30 秒超时的回退逻辑描述不够具体
- 印记效果的实现优先级未明确排序

---

## 5. 冷启动验证

> ⚠️ **此节需要在实际实现前完成**
>
> 按照通用要求 §4.5，应使用一个与主开发不同的 agent，
> 仅凭 SPEC.md + PLAN.md 尝试实现 1-2 个 task，
> 记录其遇到的 spec 缺陷并据此修订。
>
> **操作**：将 SPEC.md 和 PLAN.md 交给另一个 agent，
> 让其尝试实现 Task 4.1（Character 实体），记录暂停点和反馈。
