<template>
  <div class="pvp-battle-view">
    <div v-if="!battleStore.sessionId" class="init-screen">
      <h2>PvP 对战</h2>
      <p>请先通过匹配页面进入对战</p>
      <router-link to="/pvp/match" class="btn btn-primary">前往匹配</router-link>
    </div>
    <div v-else class="redirecting">
      <div class="spinner"></div>
      <p>正在跳转到战斗界面...</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useBattleStore } from '../store/battle'

const battleStore = useBattleStore()
const router = useRouter()

onMounted(() => {
  if (battleStore.sessionId) {
    router.replace({
      name: 'Battle',
      query: { mode: 'PVP', sessionId: battleStore.sessionId }
    })
  }
})
</script>

<style scoped>
.pvp-battle-view {
  min-height: 100vh;
  background: #1a1a2e;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  text-align: center;
}
.init-screen h2 { color: #e0e0e0; margin-bottom: 12px; }
.init-screen p { color: #888; margin-bottom: 20px; }
.spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #0f3460;
  border-top-color: #e94560;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}
@keyframes spin { to { transform: rotate(360deg); } }
.btn {
  padding: 10px 24px;
  border: none;
  border-radius: 6px;
  color: white;
  cursor: pointer;
  text-decoration: none;
  display: inline-block;
}
.btn-primary { background: linear-gradient(135deg, #e94560, #c93050); }
</style>
