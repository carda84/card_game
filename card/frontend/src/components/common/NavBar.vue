<template>
  <nav class="navbar">
    <div class="navbar-brand">
      <router-link to="/">🐾 野兽牌</router-link>
    </div>
    <div class="navbar-links">
      <router-link to="/collection">收藏</router-link>
      <router-link to="/decks">卡组</router-link>
      <router-link to="/shop">商店</router-link>
      <router-link to="/battle/prepare">对战</router-link>
      <router-link to="/friends">好友</router-link>
      <router-link to="/records">战绩</router-link>
    </div>
    <div class="navbar-user">
      <span class="user-id">{{ userStore.fullId }}</span>
      <span class="gold">💰 {{ userStore.gold }}</span>
      <span class="points">⭐ {{ userStore.points }}</span>
      <button @click="handleLogout" class="btn-logout">退出</button>
    </div>
  </nav>
</template>

<script setup>
import { useUserStore } from '../../store/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

async function handleLogout() {
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 56px;
  background: #16213e;
  border-bottom: 1px solid #0f3460;
}
.navbar-brand a {
  font-size: 1.3rem;
  font-weight: bold;
  color: #e94560;
}
.navbar-links {
  display: flex;
  gap: 16px;
}
.navbar-links a {
  padding: 6px 12px;
  border-radius: 4px;
  transition: background 0.2s;
}
.navbar-links a:hover,
.navbar-links a.router-link-active {
  background: #0f3460;
}
.navbar-user {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 0.9rem;
}
.user-id { color: #a0a0a0; }
.gold { color: #ffd700; }
.points { color: #00bfff; }
.btn-logout {
  padding: 4px 10px;
  background: #e94560;
  border: none;
  border-radius: 4px;
  color: white;
  cursor: pointer;
}
</style>
