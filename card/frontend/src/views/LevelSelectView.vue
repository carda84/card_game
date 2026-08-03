<template>
  <div class="level-select-page">
    <NavBar />
    <main class="main-content">
      <div class="center-wrap">
        <h2>🎯 关卡选择</h2>
        <p class="subtitle">选择关卡和卡组，开始 PvE 对战</p>

        <div class="deck-picker">
          <label class="hint">选择你的卡组：</label>
          <select v-model="selectedDeckId" class="deck-select">
            <option :value="null" disabled>请选择卡组</option>
            <option v-for="d in deckStore.decks" :key="d.id" :value="d.id">
              {{ d.name }} ({{ d.cardCount || 0 }} 张)
            </option>
          </select>
        </div>

        <div v-if="!levels.length" class="loading">
          <div class="spinner"></div>
          <p>加载关卡中...</p>
        </div>

        <div v-else class="level-list">
          <div v-for="level in levels" :key="level.id" class="level-card">
            <div class="level-head">
              <h3>{{ level.name }}</h3>
              <div class="stars">{{ '★'.repeat(level.difficulty) }}{{ '☆'.repeat(Math.max(0, 5 - level.difficulty)) }}</div>
            </div>
            <p class="desc">{{ level.description }}</p>
            <div class="meta">
              <span>奖励倍率：x{{ level.rewardMultiplier }}</span>
            </div>
            <button
              class="btn btn-primary"
              :disabled="!selectedDeckId"
              @click="startLevel(level)"
            >开始挑战</button>
          </div>
        </div>

        <div class="back-row">
          <router-link to="/battle/prepare" class="btn btn-ghost">返回</router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../components/common/NavBar.vue'
import request from '../api/request'
import { useDeckStore } from '../store/deck'

const router = useRouter()
const deckStore = useDeckStore()
const levels = ref([])
const selectedDeckId = ref(null)

onMounted(async () => {
  try {
    levels.value = await request.get('/levels')
  } catch (e) {
    console.warn('关卡加载失败', e)
  }
  await deckStore.fetchDecks()
  if (deckStore.decks.length) {
    selectedDeckId.value = deckStore.decks[0].id
  }
})

function startLevel(level) {
  if (!selectedDeckId.value) return
  router.push({
    name: 'Battle',
    query: { levelId: level.id, deckId: selectedDeckId.value, mode: 'PVE' }
  })
}
</script>

<style scoped>
.level-select-page { min-height: 100vh; background: #1a1a2e; }
.main-content { padding: 2rem; max-width: 760px; margin: 0 auto; }
.center-wrap h2 { color: #e0e0e0; margin-bottom: 6px; }
.subtitle { color: #888; margin-bottom: 20px; }

.deck-picker { margin-bottom: 20px; }
.hint { color: #888; font-size: 0.88rem; display: block; margin-bottom: 6px; }
.deck-select {
  width: 100%;
  padding: 10px 12px;
  background: #0d1b2a;
  border: 1px solid #0f3460;
  border-radius: 6px;
  color: #e0e0e0;
  font-size: 0.95rem;
}

.loading { text-align: center; padding: 40px; color: #888; }
.spinner {
  width: 40px; height: 40px;
  border: 4px solid #0f3460;
  border-top-color: #e94560;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 12px;
}
@keyframes spin { to { transform: rotate(360deg); } }

.level-list { display: grid; gap: 12px; }
.level-card {
  background: #16213e;
  border: 1px solid #0f3460;
  border-radius: 10px;
  padding: 16px;
  transition: border-color 0.2s;
}
.level-card:hover { border-color: #e94560; }
.level-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.level-head h3 { color: #e94560; margin: 0; }
.stars { color: #ffd700; letter-spacing: 2px; }
.desc { color: #ccc; font-size: 0.88rem; margin: 6px 0; }
.meta { color: #888; font-size: 0.82rem; margin-bottom: 10px; }

.back-row { margin-top: 20px; text-align: center; }

.btn {
  padding: 8px 18px;
  border: none;
  border-radius: 6px;
  color: white;
  cursor: pointer;
  font-size: 0.9rem;
  text-decoration: none;
  display: inline-block;
  transition: all 0.2s;
}
.btn:hover:not(:disabled) { opacity: 0.85; transform: translateY(-1px); }
.btn:disabled { opacity: 0.4; cursor: not-allowed; }
.btn-primary { background: linear-gradient(135deg, #e94560, #c93050); }
.btn-ghost { background: transparent; border: 1px solid #0f3460; color: #ccc; }
.btn-ghost:hover { border-color: #ffd700; color: #ffd700; }
</style>
