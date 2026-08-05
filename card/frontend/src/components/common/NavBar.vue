<template>
  <nav class="navbar">
    <div class="navbar-brand">
      <router-link to="/" class="brand-link">
        <span class="brand-icon">🐾</span>
        <span class="brand-text">野兽牌</span>
      </router-link>
    </div>
    <div class="navbar-links">
      <router-link v-for="link in navLinks" :key="link.to" :to="link.to" class="nav-link">
        <span class="nav-icon">{{ link.icon }}</span>
        <span class="nav-label">{{ link.label }}</span>
      </router-link>
    </div>
    <div class="navbar-user">
      <router-link to="/profile" class="user-chip" title="个人简介">
        <span class="user-avatar">{{ userStore.nickname?.charAt(0) || '?' }}</span>
        <span class="user-name">{{ userStore.fullId }}</span>
      </router-link>
      <div class="resource-pills">
        <span class="pill pill-gold">
          <span class="pill-icon">💰</span>
          <span>{{ userStore.gold }}</span>
        </span>
        <span class="pill pill-blue">
          <span class="pill-icon">⭐</span>
          <span>{{ userStore.points }}</span>
        </span>
      </div>
      <button @click="handleLogout" class="btn-logout">退出</button>
    </div>
  </nav>
</template>

<script setup>
import { useUserStore } from '../../store/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const navLinks = [
  { to: '/collection', icon: '🃏', label: '收藏' },
  { to: '/decks', icon: '📋', label: '卡组' },
  { to: '/shop', icon: '🛒', label: '商店' },
  { to: '/battle/prepare', icon: '⚔️', label: '对战' },
  { to: '/friends', icon: '👥', label: '好友' },
  { to: '/records', icon: '📊', label: '战绩' },
]

async function handleLogout() {
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 24px; height: 60px;
  background: rgba(17, 24, 39, 0.85);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-bottom: 1px solid rgba(30, 58, 95, 0.6);
  position: sticky; top: 0; z-index: 100;
}

.brand-link { display: flex; align-items: center; gap: 8px; font-weight: 800; font-size: 1.25rem; transition: transform 0.2s; }
.brand-link:hover { transform: scale(1.05); }
.brand-icon { font-size: 1.4rem; filter: drop-shadow(0 0 4px rgba(233,69,96,0.5)); }
.brand-text { background: linear-gradient(135deg, #e94560, #ff6b8a); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }

.navbar-links { display: flex; gap: 4px; }
.nav-link {
  display: flex; align-items: center; gap: 5px;
  padding: 7px 14px; border-radius: 8px;
  font-size: 0.88rem; font-weight: 500;
  color: var(--text-secondary, #a0aec0);
  transition: all 0.2s; position: relative;
}
.nav-link:hover { color: var(--text-primary, #f0f0f0); background: rgba(255,255,255,0.06); }
.nav-link.router-link-exact-active { color: #fff; background: rgba(233,69,96,0.15); }
.nav-link.router-link-exact-active::after {
  content: ''; position: absolute; bottom: -1px; left: 20%; right: 20%; height: 2px;
  background: linear-gradient(90deg, transparent, #e94560, transparent); border-radius: 2px;
}
.nav-icon { font-size: 0.9rem; }

.navbar-user { display: flex; align-items: center; gap: 12px; }
.user-chip {
  display: flex; align-items: center; gap: 8px;
  padding: 4px 12px 4px 4px;
  background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.08);
  border-radius: 50px; transition: all 0.2s; cursor: pointer;
}
.user-chip:hover { background: rgba(233,69,96,0.12); border-color: rgba(233,69,96,0.3); }

.user-avatar {
  width: 28px; height: 28px; border-radius: 50%;
  background: linear-gradient(135deg, #e94560, #c23152);
  display: flex; align-items: center; justify-content: center;
  font-size: 0.85rem; font-weight: 700; color: white; flex-shrink: 0;
}
.user-name {
  font-size: 0.82rem; color: var(--text-secondary, #a0aec0);
  max-width: 120px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}

.resource-pills { display: flex; gap: 6px; }
.pill { display: flex; align-items: center; gap: 4px; padding: 4px 10px; border-radius: 50px; font-size: 0.8rem; font-weight: 600; }
.pill-icon { font-size: 0.85rem; }
.pill-gold { background: rgba(255,215,0,0.1); color: #ffd700; border: 1px solid rgba(255,215,0,0.2); }
.pill-blue { background: rgba(0,191,255,0.1); color: #00bfff; border: 1px solid rgba(0,191,255,0.2); }

.btn-logout {
  padding: 6px 14px; background: transparent;
  border: 1px solid rgba(233,69,96,0.3); border-radius: 8px;
  color: #e94560; cursor: pointer; font-size: 0.82rem; font-weight: 500; transition: all 0.2s;
}
.btn-logout:hover { background: rgba(233,69,96,0.12); border-color: #e94560; }

@media (max-width: 900px) {
  .navbar { padding: 0 12px; }
  .nav-label { display: none; }
  .nav-link { padding: 7px 10px; }
  .user-name { display: none; }
}
</style>
