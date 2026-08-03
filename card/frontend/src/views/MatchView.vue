<template>
  <div class="match-page">
    <Nav🏆ar />
    <main class="main-content">
      <div class="bg-decor">
        <span class="bg-icon i1">⚔️</span>
        <span class="bg-icon i2">B</span>
        <span class="bg-icon i3">🐾</span>
      </div>

      <div class="center-wrap">
        <h2>🏆 PvP 匹配</h2>
        <p class="subtitle">与其他玩家进行实时对战</p>

        <div v-if="!pvpStore.inQueue" class="prepare-card">
          <p class="hint">请先选择要使用的卡组：</p>
          <select v-model="selectedDeckId" class="deck-select">
            <option :value="null" disabled>请选择卡组</option>
            <option v-for="d in deckStore.decks" :key="d.id" :value="d.id">
              {{ d.name }} ({{ d.cardCount || 0 }})
            </option>
          </select>
          <div class="actions">
            <button
              class="btn btn-primary"
              :disabled="!selectedDeckId"
              @click="startMatch"
            >开始匹配</button>
            <router-link to="/battle/prepare" class="btn btn-ghost">返回</router-link>
          </div>
        </div>

        <div v-else class="waiting-card">
          <div class="spinner"></div>
          <h3>正在寻找对手...</h3>
          <p class="hint">请保持页面开启</p>
          <button class="btn btn-danger" @click="pvpStore.cancelQueue()">取消匹配</button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { usePvpStore } from '../store/pvp'
import { useDeckStore } from '../store/deck'
import NavBar from '../components/common/NavBar.vue'

const pvpStore = usePvpStore()
const deckStore = useDeckStore()
const router = useRouter()

const selectedDeckId = ref(null)
let pollTimer = null

onMounted(async () => {
  await deckStore.fetchDecks()
  if (deckStore.decks.length) {
    selectedDeckId.value = deckStore.decks[0].id
  }
})

watch(() => pvpStore.matchResult, (res) => {
  if (res && res.matched) {
    stopPoll()
    router.push({
      name: 'Battle',
      query: {
        mode: 'PVP',
        sessionId: res.sessionId,
        opponentName: res.opponentNickname,
        opponentCharacterName: res.opponentCharacterName,
        opponentCharacterId: res.opponentCharacterId
      }
    })
  }
})

function startMatch() {
  if (!selectedDeckId.value) return
  pvpStore.queue(selectedDeckId.value)
  startPoll()
}

function startPoll() {
  stopPoll()
  pollTimer = setInterval(async () => {
    if (!pvpStore.inQueue) { stopPoll(); return }
    await pvpStore.checkMatch()
  }, 1500)
}
function stopPoll() {
  if (pollTimer) { clearInterval(pollTimer); pollTimer = null }
}

onUnmounted(() => {
  stopPoll()
})
</script>

<style scoped>
.match-page {
  min-height: 100vh;
  background: #1a1a2e;
  position: relative;
  overflow: hidden;
}
.bg-decor { position: fixed; inset: 0; pointer-events: none; z-index: 0; }
.bg-icon { position: absolute; font-size: 6rem; opacity: 0.05; animation: float 18s ease-in-out infinite; }
.bg-icon.i1 { top: 10%; left: 8%; }
.bg-icon.i2 { top: 30%; right: 12%; animation-delay: -6s; }
.bg-icon.i3 { bottom: 20%; left: 20%; animation-delay: -12s; }
@keyframes float { 0%,100% { transform: translateY(0) rotate(0); } 50% { transform: translateY(-15px) rotate(4deg); } }

.main-content {
  position: relative;
  z-index: 1;
  min-height: calc(100vh - 56px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}
.center-wrap {
  text-align: center;
  width: 100%;
  max-width: 460px;
}
.center-wrap h2 { color: #e0e0e0; font-size: 1.8rem; margin-bottom: 6px; }
.subtitle { color: #888; margin-bottom: 24px; }

.prepare-card, .waiting-card {
  background: #16213e;
  border: 1px solid #0f3460;
  border-radius: 12px;
  padding: 24px;
  text-align: left;
}
.hint { color: #888; font-size: 0.88rem; margin-bottom: 12px; }
.deck-select {
  width: 100%;
  padding: 10px 12px;
  background: #0d1b2a;
  border: 1px solid #0f3460;
  border-radius: 6px;
  color: #e0e0e0;
  font-size: 0.95rem;
  margin-bottom: 16px;
}
.actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}
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
.waiting-card { text-align: center; }
.waiting-card h3 { color: #ffd700; }

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  color: white;
  cursor: pointer;
  font-size: 0.95rem;
  text-decoration: none;
  display: inline-block;
  transition: all 0.2s;
}
.btn:hover:not(:disabled) { opacity: 0.85; transform: translateY(-1px); }
.btn:disabled { opacity: 0.4; cursor: not-allowed; }
.btn-primary { background: linear-gradient(135deg, #e94560, #c93050); }
.btn-danger { background: #dc3545; }
.btn-ghost { background: transparent; border: 1px solid #0f3460; color: #ccc; }
.btn-ghost:hover { border-color: #ffd700; color: #ffd700; }
</style>
