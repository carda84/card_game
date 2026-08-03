# 野兽牌 — 邪恶冥刻风格策略卡牌对战

> AI4SE 期末项目 · B · 非 harness 应用类

基于《邪恶冥刻》(Inscryption) 的 Web 端策略卡牌对战游戏，支持人机对战、玩家实时对战、好友社交、卡组构建等功能。

---

## 功能概览

| 模块 | 功能 |
|------|------|
| 认证 | 邮箱注册 + 验证码 + JWT 登录 |
| 卡牌 | 68 张卡牌模板，35 种印记，5 种族 |
| 人物 | 不同血量(20-40)、卡组数(10-20)、主动/被动技能 |
| 商店 | 浏览/购买卡牌（解锁制，统一售价） |
| 卡组 | 创建/编辑/校验（传奇≤3、重复上限、数量匹配、≤20个卡组） |
| 人机对战 | 4v4 棋盘，完整回合制（献祭→出牌→攻击） |
| PvP 对战 | WebSocket 实时对战，30 秒回合时限，积分排名 |
| 好友 | ID 搜索、私信聊天、查看数据、发起对局邀请 |
| 统计 | 卡牌使用率占比、PvP 胜率（两位小数）、最近 20 场记录 |

---

## 技术栈

| 层 | 技术 |
|-----|------|
| 后端 | Spring Boot 3.3.5 + Spring Security + Spring Data JPA |
| 语言 | Java 21 |
| 认证 | JWT (jjwt 0.12.6) + BCrypt |
| 数据库 | MySQL 8.0 |
| 邮件 | Spring Mail (163 SMTP) |
| 实时通信 | WebSocket |
| 前端 | Vue 3 + Vite 6 + Pinia + Vue Router |
| HTTP | Axios (带 Token 拦截器) |
| 构建 | Maven (后端) + npm (前端) |
| 容器 | Docker + docker-compose |
| CI | GitHub Actions |

---

## 快速开始

### 方式一：Docker Compose（推荐）

```bash
# 1. 克隆仓库
git clone <repo-url>
cd card_game

# 2. 创建 .env 文件（必须配置以下变量）
cat > .env << 'EOF'
DB_PASSWORD=your-strong-password
JWT_SECRET=your-jwt-secret-at-least-32-chars-long
MAIL_USERNAME=your-email@example.com
MAIL_PASSWORD=your-email-password-or-auth-code
EOF

# 3. 一键启动
docker-compose up -d --build

# 4. 访问
# 前端: http://localhost
# 后端: http://localhost:8080
```

### 方式二：本地开发

**环境要求：**
- JDK 21+
- Maven 3.8+
- Node.js 20+
- MySQL 8.0

```bash
# 1. 创建 MySQL 数据库
mysql -u root -p -e "CREATE DATABASE cardgame CHARACTER SET utf8mb4"

# 2. 配置环境变量
export DB_URL="jdbc:mysql://localhost:3306/cardgame?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8"
export DB_USERNAME=root
export DB_PASSWORD=your-password
export JWT_SECRET=your-jwt-secret-at-least-32-chars-long
export MAIL_USERNAME=your-email@example.com
export MAIL_PASSWORD=your-email-auth-code

# 3. 启动后端
cd card/backend
mvn spring-boot:run
# 后端启动在 http://localhost:8080

# 4. 启动前端（新终端）
cd card/frontend
npm install
npm run dev
# 前端启动在 http://localhost:5173
# API 请求自动代理到后端 8080
```

### 方式三：Makefile

```bash
make build          # 构建前后端
make run-docker     # Docker 一键启动
make test           # 运行测试
make clean          # 清理构建产物
```

---

## 安全配置

### 环境变量

| 变量 | 说明 | 必须 |
|------|------|------|
| `JWT_SECRET` | JWT 签名密钥（至少 32 字符） | ✅ |
| `DB_PASSWORD` | MySQL 密码 | ✅ |
| `DB_URL` | MySQL 连接 URL | 生产环境 |
| `DB_USERNAME` | MySQL 用户名 | 生产环境 |
| `MAIL_USERNAME` | 发件邮箱地址 | ✅（注册功能） |
| `MAIL_PASSWORD` | 发件邮箱密码/授权码 | ✅（注册功能） |
| `MAIL_HOST` | SMTP 服务器（默认 smtp.163.com） | 可选 |
| `CORS_ALLOWED_ORIGINS` | CORS 允许的前端域名 | 生产环境 |

### 凭据安全说明

- 所有敏感凭据通过环境变量注入，**绝不硬编码或提交仓库**
- 开发环境使用 `.env` 文件（已加入 `.gitignore`）
- 生产环境通过容器环境变量或密钥管理服务注入
- 密码使用 BCrypt 哈希存储，不可逆
- JWT Token 有效期 24 小时
- **`.env` 文件中的凭据为明文，进程环境对同一用户可见，请注意安全风险**

### 本地开发 .env 示例

```env
# 仅用于本地开发，切勿提交到仓库
DB_PASSWORD=dev-password
JWT_SECRET=dev-secret-key-at-least-32-chars-long
MAIL_USERNAME=test@example.com
MAIL_PASSWORD=dev-auth-code
```

### 在目标机器上安全配置 Key

1. **Docker 部署**：通过 `docker-compose.yml` 的环境变量注入，或 Docker Secrets
2. **原生部署**：通过 `.env` 文件加载（确保文件权限 `chmod 600`）或系统环境变量
3. **密钥管理**：生产环境推荐使用 HashiCorp Vault / AWS Secrets Manager 等密钥管理服务

---

## API 端点

### 公开接口（无需认证）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/send-code` | 发送验证码到邮箱 |
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录（返回 JWT） |
| POST | `/api/auth/logout` | 登出 |
| GET | `/api/characters` | 获取人物列表 |
| GET | `/api/cards` | 获取卡牌列表 |

### 受保护接口（需 `Authorization: Bearer <token>`）

#### 商店

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/shop` | 获取商店商品 |
| POST | `/api/shop/buy` | 购买卡牌 |

#### 卡组

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/decks` | 获取用户卡组列表 |
| POST | `/api/decks` | 创建卡组 |
| GET | `/api/decks/{id}` | 获取卡组详情 |
| DELETE | `/api/decks/{id}` | 删除卡组 |
| POST | `/api/decks/add-card` | 添加卡牌到卡组 |
| POST | `/api/decks/remove-card` | 从卡组移除卡牌 |
| POST | `/api/decks/rename` | 重命名卡组 |
| GET | `/api/decks/owned-cards` | 获取已拥有卡牌 |

#### 对战

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/battle/start` | 开始人机对战 |
| POST | `/api/battle/draw` | 抽牌 |
| POST | `/api/battle/select-card` | 选牌 |
| POST | `/api/battle/sacrifice` | 献祭 |
| POST | `/api/battle/play-card` | 出牌 |
| POST | `/api/battle/end-turn` | 结束回合 |
| POST | `/api/battle/surrender` | 投降 |
| GET | `/api/battle/state/{sessionId}` | 获取战斗状态 |

#### PvP

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/pvp/start-match` | 开始匹配 |
| POST | `/api/pvp/cancel-match` | 取消匹配 |
| GET | `/api/pvp/match-status` | 查询匹配状态 |

#### 好友

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/friends` | 好友列表 |
| POST | `/api/friends/add` | 添加好友 |
| DELETE | `/api/friends/{id}` | 删除好友 |
| POST | `/api/friends/message` | 发送私信 |
| GET | `/api/friends/messages/{friendId}` | 获取聊天记录 |

#### 统计

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/statistics` | 获取个人统计 |
| GET | `/api/records` | 获取对战记录 |

---

## 测试

```bash
# 后端单元测试（一键运行）
make test

# 或手动运行
cd card/backend
mvn test

# 前端构建验证
cd card/frontend
npm run build
```

---

## 项目结构

```
card_game/
├── .github/workflows/ci.yml   # CI 配置（GitHub Actions）
├── Makefile                    # 构建命令
├── docker-compose.yml          # 容器编排
├── card/
│   ├── docs/                   # 交付物文档
│   │   ├── SPEC.md             # 设计文档
│   │   ├── PLAN.md             # 实现计划
│   │   ├── SPEC_PROCESS.md     # 规约生成过程
│   │   ├── AGENT_LOG.md        # 开发日志
│   │   └── REFLECTION.md       # 反思报告（学生本人撰写）
│   ├── backend/                # Spring Boot 后端
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── src/
│   │       ├── main/java/com/cardgame/
│   │       │   ├── config/     # Security + CORS + JWT + WebSocket
│   │       │   ├── controller/ # REST API 控制器（13个）
│   │       │   ├── service/    # 业务逻辑接口 + 实现（20个）
│   │       │   ├── model/      # 实体(13) + DTO(24) + 枚举(7)
│   │       │   ├── dao/        # 数据访问层（12个）
│   │       │   ├── util/       # JWT + ID 生成器 + 常量
│   │       │   └── exception/  # 统一异常处理（6个）
│   │       └── main/resources/
│   │           ├── application.yml
│   │           ├── data.sql    # 68 张卡牌初始数据
│   │           └── character.sql # 人物初始数据
│   └── frontend/               # Vue 3 前端
│       ├── package.json
│       ├── vite.config.js
│       ├── Dockerfile
│       ├── nginx.conf
│       └── src/
│           ├── api/            # Axios 实例 + API 调用
│           ├── store/          # Pinia 状态管理
│           ├── router/         # Vue Router + 登录守卫
│           ├── views/          # 页面组件（15个）
│           ├── components/     # 公共组件（NavBar/CardItem/CardImageModal）
│           ├── utils/          # 工具函数（cardImages.js）
│           └── assets/cards/   # 68张卡牌图片
```

---

## 已知限制

- 验证码在生产环境需配置真实 SMTP 服务
- PvP 对战匹配采用简单轮询，高并发场景需优化
- 35 种印记效果分批实现，部分复杂印记的战斗逻辑尚未完成
- 前端生产部署需配合 Nginx 做 HTTPS 配置

---

## 许可证

本项目为课程期末项目，仅供学术用途。
# 卡牌游戏 — 邪恶冥刻风格策略卡牌对战

> AI4SE 期末项目 · B · 非 harness 应用类

基于《邪恶冥刻》(Inscryption) 的 Web 端策略卡牌对战游戏，支持人机对战、玩家实时对战、好友社交、卡组构建等功能。

---

## 功能概览

| 模块 | 功能 |
|------|------|
| 认证 | 邮箱注册 + 验证码 + JWT 登录 |
| 卡牌 | 68 张卡牌模板，35 种印记，5 种族 |
| 人物 | 不同血量(20-40)、卡组数(10-20)、主动/被动技能 |
| 商店 | 浏览/购买卡牌（金币经济系统） |
| 卡组 | 创建/编辑/校验（传奇≤3、重复上限、数量匹配） |
| 人机对战 | 4v4 棋盘，完整回合制（献祭→出牌→攻击） |
| PvP 对战 | WebSocket 实时对战，30 秒回合时限，积分排名 |
| 好友 | ID 搜索、私信聊天、查看数据、发起对局邀请 |
| 统计 | 卡牌使用率占比、PvP 胜率（两位小数）、最近 20 场记录 |

---

## 技术栈

| 层 | 技术 |
|-----|------|
| 后端 | Spring Boot 3.3 + Spring Security + Spring Data JPA |
| 认证 | JWT (jjwt 0.12.6) + BCrypt |
| 数据库 | H2 (开发) / MySQL (生产) |
| 邮件 | Spring Mail |
| 实时通信 | WebSocket |
| 前端 | Vue 3 + Vite + Pinia + Vue Router |
| HTTP | Axios (带 Token 拦截器) |
| 构建 | Maven (后端) + npm (前端) |
| 容器 | Docker + docker-compose |
| CI | GitHub Actions |

---

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- npm 9+

### 后端启动

```bash
cd card/backend

# 开发模式（H2 内存数据库，无需配置）
mvn spring-boot:run

# 服务启动在 http://localhost:8080
# H2 控制台: http://localhost:8080/h2-console
```

### 前端启动

```bash
cd card/frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 前端启动在 http://localhost:5173
# API 请求自动代理到后端 8080
```

---

## 安全配置

### 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `JWT_SECRET` | JWT 签名密钥（至少 32 字符） | 内置开发密钥 |
| `MAIL_USER` | 发件邮箱地址 | `noreply@example.com` |
| `MAIL_PASSWORD` | 发件邮箱密码/授权码 | 空 |
| `DB_HOST` | MySQL 主机 | `localhost` |
| `DB_PORT` | MySQL 端口 | `3306` |
| `DB_NAME` | 数据库名 | `cardgame` |
| `DB_USER` | 数据库用户名 | - |
| `DB_PASSWORD` | 数据库密码 | - |

### 凭据安全说明

- 所有敏感凭据通过环境变量注入，**绝不硬编码或提交仓库**
- 开发环境使用 `.env` 文件（已加入 `.gitignore`）
- 生产环境通过容器环境变量或密钥管理服务注入
- 密码使用 BCrypt 哈希存储，不可逆

### 本地开发 .env 示例

```env
# 仅用于本地开发，切勿提交
JWT_SECRET=your-dev-secret-key-at-least-32-chars
MAIL_USER=test@example.com
MAIL_PASSWORD=dev-password
```

---

## Docker 部署

```bash
# 构建镜像
docker-compose build

# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止
docker-compose down
```

---

## 项目结构

```
card/
├── docs/                  # 交付物文档
│   ├── SPEC.md            # 设计文档
│   ├── PLAN.md            # 实现计划
│   ├── SPEC_PROCESS.md    # 规约生成过程
│   ├── AGENT_LOG.md       # 开发日志
│   └── REFLECTION.md      # 反思报告
├── backend/               # Spring Boot 后端
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│       ├── main/java/com/cardgame/
│       │   ├── config/    # Security + CORS + JWT 过滤器
│       │   ├── controller/# REST API 控制器
│       │   ├── service/   # 业务逻辑接口 + 实现
│       │   ├── model/     # 实体 + DTO + 枚举
│       │   ├── dao/       # 数据访问层
│       │   ├── util/      # JWT + ID 生成器 + 常量
│       │   ├── exception/ # 统一异常处理
│       │   └── websocket/ # WebSocket 处理器
│       └── main/resources/
│           ├── application*.yml
│           └── data.sql   # 68 张卡牌初始数据
├── frontend/              # Vue 3 前端
│   ├── package.json
│   ├── vite.config.js
│   ├── Dockerfile
│   └── src/
│       ├── api/           # Axios 实例 + API 调用
│       ├── store/         # Pinia 状态管理
│       ├── router/        # Vue Router + 登录守卫
│       ├── views/         # 页面组件
│       ├── components/    # 业务组件
│       └── websocket/     # WebSocket 封装
├── docker-compose.yml     # 容器编排
├── .github/workflows/     # CI 配置
└── Makefile               # 构建命令
```

---

## API 端点

### 公开接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/send-code` | 发送验证码到邮箱 |
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录（返回 JWT） |
| POST | `/api/auth/logout` | 登出 |

### 受保护接口（需 Bearer Token）

后续模块完成后的完整 API 列表见 SPEC.md。

---

## 测试

```bash
# 后端测试
cd card/backend
mvn test

# 前端测试（待配置）
cd card/frontend
npm test
```

---

## 已知限制

- 验证码在开发环境使用 MailHog 模拟，需安装本地 SMTP 服务
- PvP 对战匹配采用简单轮询，高并发场景需优化
- 35 种印记效果分批实现，部分印记的战斗逻辑尚未完成
- 前端暂未部署到 CDN，需配合 Nginx 做生产构建

---

## 许可证

本项目为课程期末项目，仅供学术用途。
