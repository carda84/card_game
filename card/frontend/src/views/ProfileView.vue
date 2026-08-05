<template>
  <div class="profile-view">
    <NavBar />
    <main class="profile-main">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <template v-else-if="profile">
        <!-- 用户信息卡片 -->
        <div class="profile-card hero-card">
          <div class="avatar-section">
            <div class="avatar-circle">{{ profile.nickname?.charAt(0) || '?' }}</div>
          </div>
          <div class="hero-info">
            <h2 class="hero-name">{{ profile.fullId }}</h2>
            <p class="hero-email">{{ profile.email }}</p>
            <p class="hero-join">注册于 {{ profile.createdAt }}</p>
          </div>
          <div class="hero-resources">
            <div class="res-item gold">
              <span class="res-icon">💰</span>
              <span class="res-value">{{ profile.gold }}</span>
              <span class="res-label">金币</span>
            </div>
            <div class="res-item points">
              <span class="res-icon">⭐</span>
              <span class="res-value">{{ profile.points }}</span>
              <span class="res-label">积分</span>
            </div>
            <div class="res-item rank">
              <span class="res-icon">🏆</span>
              <span class="res-value">#{{ profile.rank }}</span>
              <span class="res-label">排名</span>
            </div>
          </div>
        </div>

        <!-- 对战统计 -->
        <div class="section">
          <h3 class="section-title">📊 对战统计</h3>
          <div class="stats-grid">
            <div class="stat-box">
              <span class="stat-value">{{ profile.totalBattles }}</span>
              <span class="stat-label">总场次</span>
            </div>
            <div class="stat-box win">
              <span class="stat-value">{{ profile.wins }}</span>
              <span class="stat-label">胜利</span>
            </div>
            <div class="stat-box lose">
              <span class="stat-value">{{ profile.losses }}</span>
              <span class="stat-label">失败</span>
            </div>
            <div class="stat-box surrender">
              <span class="stat-value">{{ profile.surrenders }}</span>
              <span class="stat-label">投降</span>
            </div>
            <div class="stat-box rate">
              <span class="stat-value">{{ profile.winRate }}%</span>
              <span class="stat-label">胜率</span>
            </div>
          </div>
        </div>

        <!-- PvE / PvP 分开统计 -->
        <div class="section">
          <h3 class="section-title">⚔️ 模式详情</h3>
          <div class="mode-grid">
            <div class="mode-card pve">
              <div class="mode-header">🤖 人机对战</div>
              <div class="mode-body">
                <div class="mode-stat">
                  <span class="ms-val">{{ profile.pveBattles }}</span>
                  <span class="ms-label">场次</span>
                </div>
                <div class="mode-stat">
                  <span class="ms-val">{{ profile.pveWins }}</span>
                  <span class="ms-label">胜利</span>
                </div>
                <div class="mode-stat">
                  <span class="ms-val">{{ pveWinRate }}%</span>
                  <span class="ms-label">胜率</span>
                </div>
              </div>
            </div>
            <div class="mode-card pvp">
              <div class="mode-header">👥 玩家对战</div>
              <div class="mode-body">
                <div class="mode-stat">
                  <span class="ms-val">{{ profile.pvpBattles }}</span>
                  <span class="ms-label">场次</span>
                </div>
                <div class="mode-stat">
                  <span class="ms-val">{{ profile.pvpWins }}</span>
                  <span class="ms-label">胜利</span>
                </div>
                <div class="mode-stat">
                  <span class="ms-val">{{ pvpWinRate }}%</span>
                  <span class="ms-label">胜率</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 账号资产 -->
        <div class="section">
          <h3 class="section-title">🎒 账号资产</h3>
          <div class="asset-grid">
            <div class="asset-item">
              <span class="asset-icon">📋</span>
              <span class="asset-value">{{ profile.deckCount }}</span>
              <span class="asset-label">卡组</span>
            </div>
            <div class="asset-item">
              <span class="asset-icon">👤</span>
              <span class="asset-value">{{ profile.characterCount }}</span>
              <span class="asset-label">已解锁人物</span>
            </div>
          </div>
        </div>

        <!-- 快捷导航 -->
        <div class="section">
          <h3 class="section-title">🚀 快捷导航</h3>
          <div class="quick-nav">
            <router-link to="/collection" class="nav-btn">🃏 卡牌收藏</router-link>
            <router-link to="/decks" class="nav-btn">📋 卡组管理</router-link>
            <router-link to="/shop" class="nav-btn">🛒 商店</router-link>
            <router-link to="/battle/prepare" class="nav-btn">⚔️ 开始对战</router-link>
            <router-link to="/records" class="nav-btn">📊 战绩记录</router-link>
            <router-link to="/friends" class="nav-btn">👥 好友</router-link>
          </div>
        </div>
      </template>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getProfile } from '../api/user'
import NavBar from '../components/common/NavBar.vue'

const loading = ref(true)
const profile = ref(null)

const pveWinRate = computed(() => {
  if (!profile.value || profile.value.pveBattles === 0) return '0.0'
  return (profile.value.pveWins / profile.value.pveBattles * 100).toFixed(1)
})

const pvpWinRate = computed(() => {
  if (!profile.value || profile.value.pvpBattles === 0) return '0.0'
  return (profile.value.pvpWins / profile.value.pvpBattles * 100).toFixed(1)
})

onMounted(async () => {
  try {
    profile.value = await getProfile()
  } catch (e) {
    console.error('加载个人简介失败', e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.profile-view {
  min-height: 100vh;
  color: #e0e0e0;
}

.profile-main {
  max-width: 720px;
  margin: 0 auto;
  padding: 24px 16px 60px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 60px 0;
  color: #888;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #0f3460;
  border-top-color: #e94560;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===== 英雄卡片 ===== */
.hero-card {
  display: flex;
  align-items: center;
  gap: 20px;
  background: linear-gradient(135deg, #16213e, #0f3460);
  border: 1px solid #0f3460;
  border-radius: 14px;
  padding: 28px 24px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.avatar-circle {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #e94560, #c23152);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
  font-weight: bold;
  color: #fff;
  flex-shrink: 0;
  box-shadow: 0 0 16px rgba(233, 69, 96, 0.4);
}

.hero-info {
  flex: 1;
  min-width: 160px;
}

.hero-name {
  font-size: 1.5rem;
  font-weight: 700;
  color: #fff;
  margin: 0 0 4px;
}

.hero-email {
  font-size: 0.82rem;
  color: #7a8ba8;
  margin: 0 0 2px;
}

.hero-join {
  font-size: 0.75rem;
  color: #556;
  margin: 0;
}

.hero-resources {
  display: flex;
  gap: 20px;
  flex-shrink: 0;
}

.res-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.res-icon {
  font-size: 1.3rem;
}

.res-value {
  font-size: 1.1rem;
  font-weight: 700;
}

.res-item.gold .res-value { color: #ffd700; }
.res-item.points .res-value { color: #00bfff; }
.res-item.rank .res-value { color: #ffa500; }

.res-label {
  font-size: 0.7rem;
  color: #888;
}

/* ===== 通用段落 ===== */
.section {
  margin-bottom: 22px;
}

.section-title {
  font-size: 1rem;
  color: #a0a0a0;
  margin: 0 0 12px;
  padding-bottom: 6px;
  border-bottom: 1px solid #0f3460;
}

/* ===== 对战统计网格 ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
}

.stat-box {
  background: #16213e;
  border: 1px solid #0f3460;
  border-radius: 10px;
  padding: 14px 8px;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 1.3rem;
  font-weight: 700;
  color: #fff;
}

.stat-label {
  font-size: 0.75rem;
  color: #888;
}

.stat-box.win .stat-value { color: #4caf50; }
.stat-box.lose .stat-value { color: #f44336; }
.stat-box.surrender .stat-value { color: #ff9800; }
.stat-box.rate .stat-value { color: #00e5ff; }

/* ===== 模式统计 ===== */
.mode-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.mode-card {
  background: #16213e;
  border: 1px solid #0f3460;
  border-radius: 10px;
  overflow: hidden;
}

.mode-header {
  padding: 10px 14px;
  font-size: 0.9rem;
  font-weight: 600;
  border-bottom: 1px solid #0f3460;
}

.mode-card.pve .mode-header { background: rgba(76, 175, 80, 0.1); color: #81c784; }
.mode-card.pvp .mode-header { background: rgba(33, 150, 243, 0.1); color: #64b5f6; }

.mode-body {
  display: flex;
  justify-content: space-around;
  padding: 14px 10px;
}

.mode-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.ms-val {
  font-size: 1.1rem;
  font-weight: 700;
  color: #fff;
}

.ms-label {
  font-size: 0.7rem;
  color: #888;
}

/* ===== 资产网格 ===== */
.asset-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.asset-item {
  background: #16213e;
  border: 1px solid #0f3460;
  border-radius: 10px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.asset-icon {
  font-size: 1.5rem;
}

.asset-value {
  font-size: 1.2rem;
  font-weight: 700;
  color: #fff;
}

.asset-label {
  font-size: 0.8rem;
  color: #888;
  margin-left: auto;
}

/* ===== 快捷导航 ===== */
.quick-nav {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.nav-btn {
  background: #16213e;
  border: 1px solid #0f3460;
  border-radius: 8px;
  padding: 12px 10px;
  text-align: center;
  font-size: 0.85rem;
  color: #e0e0e0;
  transition: border-color 0.2s, transform 0.15s;
}

.nav-btn:hover {
  border-color: #e94560;
  transform: translateY(-2px);
}

/* ===== 响应式 ===== */
@media (max-width: 600px) {
  .hero-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .quick-nav {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
