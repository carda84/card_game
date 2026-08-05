<template>
  <div class="home-view">
    <NavBar />
    <main class="main-content">
      <!-- 欢迎横幅 -->
      <div class="welcome-banner">
        <div class="banner-content">
          <h1 class="banner-title">
            <span class="title-greeting">欢迎回来，</span>
            <span class="title-name">{{ userStore.nickname }}</span>
          </h1>
          <p class="banner-sub">策略卡牌对战 · 收集 · 构筑 · 征战</p>
        </div>
        <div class="banner-stats">
          <div class="stat-pill gold">
            <span class="sp-icon">💰</span>
            <span class="sp-val">{{ userStore.gold }}</span>
            <span class="sp-label">金币</span>
          </div>
          <div class="stat-pill blue">
            <span class="sp-icon">⭐</span>
            <span class="sp-val">{{ userStore.points }}</span>
            <span class="sp-label">积分</span>
          </div>
        </div>
      </div>

      <!-- 导航网格 -->
      <div class="nav-grid">
        <router-link
          v-for="(item, idx) in navItems"
          :key="item.to"
          :to="item.to"
          class="nav-card"
          :class="item.class"
          :style="{ animationDelay: `${idx * 60}ms` }"
        >
          <div class="nc-icon-wrap" :style="{ background: item.iconBg }">
            <span class="nc-icon">{{ item.icon }}</span>
          </div>
          <div class="nc-info">
            <h3>{{ item.title }}</h3>
            <p>{{ item.desc }}</p>
          </div>
          <span class="nc-arrow">→</span>
        </router-link>
      </div>
    </main>
  </div>
</template>

<script setup>
import NavBar from '../components/common/NavBar.vue'
import { useUserStore } from '../store/user'

const userStore = useUserStore()

const navItems = [
  { to: '/profile', icon: '👤', title: '个人简介', desc: '查看账号信息与战绩', class: 'card-accent',
    iconBg: 'linear-gradient(135deg, rgba(233,69,96,0.2), rgba(233,69,96,0.05))' },
  { to: '/collection', icon: '🃏', title: '卡牌收藏', desc: '浏览你的卡牌库', class: '',
    iconBg: 'linear-gradient(135deg, rgba(167,139,250,0.2), rgba(167,139,250,0.05))' },
  { to: '/characters', icon: '🎭', title: '选择人物', desc: '选择你的对战角色', class: '',
    iconBg: 'linear-gradient(135deg, rgba(255,165,0,0.2), rgba(255,165,0,0.05))' },
  { to: '/decks', icon: '📋', title: '卡组管理', desc: '创建和编辑卡组', class: '',
    iconBg: 'linear-gradient(135deg, rgba(0,191,255,0.2), rgba(0,191,255,0.05))' },
  { to: '/shop', icon: '🛒', title: '商店', desc: '购买新卡牌', class: '',
    iconBg: 'linear-gradient(135deg, rgba(255,215,0,0.2), rgba(255,215,0,0.05))' },
  { to: '/battle/prepare', icon: '⚔️', title: '开始对战', desc: '人机 / PvP 对战', class: 'card-primary',
    iconBg: 'linear-gradient(135deg, rgba(233,69,96,0.25), rgba(233,69,96,0.08))' },
  { to: '/friends', icon: '👥', title: '好友', desc: '添加好友、私信', class: '',
    iconBg: 'linear-gradient(135deg, rgba(74,222,128,0.2), rgba(74,222,128,0.05))' },
  { to: '/records', icon: '📊', title: '战绩', desc: '查看对战记录', class: '',
    iconBg: 'linear-gradient(135deg, rgba(244,67,54,0.2), rgba(244,67,54,0.05))' },
  { to: '/statistics', icon: '📈', title: '统计', desc: '卡牌使用率/胜率', class: '',
    iconBg: 'linear-gradient(135deg, rgba(0,229,255,0.2), rgba(0,229,255,0.05))' },
]
</script>

<style scoped>
.home-view {
  min-height: 100vh;
  background: transparent;
  color: var(--text-primary);
}

.main-content {
  padding: 2rem 1.5rem 4rem;
  max-width: 900px;
  margin: 0 auto;
}

.welcome-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  background: linear-gradient(135deg, rgba(233, 69, 96, 0.12), rgba(22, 33, 62, 0.8));
  border: 1px solid rgba(233, 69, 96, 0.2);
  border-radius: var(--radius-lg);
  padding: 28px 32px;
  margin-bottom: 2rem;
  position: relative;
  overflow: hidden;
  animation: fadeInDown 0.5s ease;
}
.welcome-banner::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(233, 69, 96, 0.15), transparent 70%);
  border-radius: 50%;
  pointer-events: none;
}
.banner-title { font-size: 1.6rem; font-weight: 700; margin-bottom: 6px; }
.title-greeting { color: var(--text-secondary); font-weight: 500; }
.title-name {
  background: linear-gradient(135deg, #e94560, #ff6b8a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.banner-sub { color: var(--text-muted); font-size: 0.88rem; letter-spacing: 1px; }

.banner-stats { display: flex; gap: 10px; flex-shrink: 0; }
.stat-pill {
  display: flex; align-items: center; gap: 6px;
  padding: 8px 14px; border-radius: 50px;
  font-size: 0.85rem; font-weight: 600;
}
.stat-pill.gold { background: rgba(255,215,0,0.1); border: 1px solid rgba(255,215,0,0.2); color: var(--gold); }
.stat-pill.blue { background: rgba(0,191,255,0.1); border: 1px solid rgba(0,191,255,0.2); color: var(--blue); }
.sp-icon { font-size: 1rem; }

.nav-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 14px;
}

.nav-card {
  display: flex; align-items: center; gap: 14px;
  background: var(--bg-card); border: 1px solid var(--border);
  border-radius: var(--radius-md); padding: 16px 18px;
  transition: all 0.25s var(--ease); animation: fadeInUp 0.4s ease both;
  position: relative; overflow: hidden;
}
.nav-card::after {
  content: ''; position: absolute; inset: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.02), transparent);
  pointer-events: none;
}
.nav-card:hover { border-color: var(--border-hover); transform: translateY(-3px); box-shadow: var(--shadow-md); }
.nav-card.card-accent { border-color: rgba(233,69,96,0.25); }
.nav-card.card-accent:hover { border-color: rgba(233,69,96,0.5); box-shadow: var(--shadow-brand); }
.nav-card.card-primary { border-color: rgba(233,69,96,0.3); background: linear-gradient(135deg, rgba(233,69,96,0.08), var(--bg-card)); }
.nav-card.card-primary:hover { border-color: #e94560; box-shadow: var(--shadow-brand); }

.nc-icon-wrap {
  width: 44px; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.nc-icon { font-size: 1.4rem; }
.nc-info { flex: 1; min-width: 0; }
.nc-info h3 { font-size: 0.95rem; font-weight: 600; color: var(--text-primary); margin-bottom: 2px; }
.nc-info p { font-size: 0.8rem; color: var(--text-muted); margin: 0; }
.nc-arrow { color: var(--text-dim); font-size: 1rem; transition: transform 0.2s, color 0.2s; }
.nav-card:hover .nc-arrow { transform: translateX(4px); color: #e94560; }

@keyframes fadeInDown { from { opacity:0; transform:translateY(-16px); } to { opacity:1; transform:translateY(0); } }
@keyframes fadeInUp { from { opacity:0; transform:translateY(12px); } to { opacity:1; transform:translateY(0); } }

@media (max-width: 600px) {
  .welcome-banner { flex-direction: column; align-items: flex-start; padding: 20px; }
  .nav-grid { grid-template-columns: 1fr; }
}
</style>
