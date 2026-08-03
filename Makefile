# ===== Makefile — 卡牌游戏构建命令 =====

.PHONY: help test build run stop clean

help: ## 显示帮助
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'

# ===== 测试 =====

test: test-backend test-frontend ## 运行所有测试

test-backend: ## 运行后端测试
	cd card/backend && mvn test -B

test-frontend: ## 运行前端构建验证
	cd card/frontend && npm run build

# ===== 构建 =====

build: build-backend build-frontend ## 构建前后端

build-backend: ## 构建后端 JAR
	cd card/backend && mvn package -DskipTests -B

build-frontend: ## 构建前端生产包
	cd card/frontend && npm install && npm run build

# ===== 运行 =====

run-dev: ## 本地开发模式启动（需先配置 .env）
	cd card/backend && mvn spring-boot:run &
	cd card/frontend && npm run dev

run-docker: ## Docker Compose 一键启动
	docker-compose up -d --build

stop: ## 停止 Docker 服务
	docker-compose down

logs: ## 查看 Docker 日志
	docker-compose logs -f

# ===== 清理 =====

clean: ## 清理构建产物
	cd card/backend && mvn clean
	rm -rf card/frontend/dist
	rm -rf card/frontend/node_modules

# ===== Docker =====

docker-build: ## 构建 Docker 镜像
	docker-compose build

docker-push: ## 推送镜像到 Registry（需先登录）
	@echo "请配置 Registry 地址后手动推送"
