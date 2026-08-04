<template>
  <div class="match-page">
    <NavBar />
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
          <!-- 旋转图案 -->
          <div class="orbit-container">
            <div class="orbit-ring">
              <span class="orbit-dot d1"></span>
              <span class="orbit-dot d2"></span>
              <span class="orbit-dot d3"></span>
              <span class="orbit-dot d4"></span>
            </div>
            <span class="orbit-center">⚔️</span>
          </div>

          <h3>正在寻找对手...</h3>
          <p class="wait-time">已等待 <span class="time-num">{{ formatTime(elapsedSeconds) }}</span></p>
          <p class="hint">请保持页面开启，匹配成功后将自动进入对战</p>

          <div class="wait-actions">
            <button class="btn btn-ghost" @click="exitMatch">🏠 返回首页</button>
            <button class="btn btn-danger" @click="pvpStore.cancelQueue()">✖ 取消匹配</button>
          </div>
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
const elapsedSeconds = ref(0)
let pollTimer = null
let tickTimer = null

function startTick() {
  elapsedSeconds.value = 0
  tickTimer = setInterval(() => { elapsedSeconds.value++ }, 1000)
}
function stopTick() {
  if (tickTimer) { clearInterval(tickTimer); tickTimer = null }
  elapsedSeconds.value = 0
}
function formatTime(s) {
  const m = String(Math.floor(s / 60)).padStart(2, '0')
  const sec = String(s % 60).padStart(2, '0')
  return `${m}:${sec}`
}

function exitMatch() {
  pvpStore.cancelQueue()
  stopPoll()
  stopTick()
  router.push('/')
}

onMounted(async () => {
  await deckStore.fetchDecks()
  if (deckStore.decks.length) {
    selectedDeckId.value = deckStore.decks[0].id
  }
  // 如果进入页面时已在队列中（刷新恢复），启动计时
  if (pvpStore.inQueue) { startTick(); startPoll() }
})

watch(() => pvpStore.inQueue, (inQ) => {
  if (inQ) { startTick() } else { stopTick() }
})

watch(() => pvpStore.matchResult, (res) => {
  if (res && res.matched) {
    stopPoll()
    stopTick()
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
  stopTick()
})
</script>

<style scoped>
.match-page {
  min-height: 100vh;
  background: transparent;
  position: relative;
  overflow: hidden;
}
.bg-decor { position: fixed; inset: 0; pointer-events: none; z-index: 0; overflow: hidden; }
.bg-icon { position: absolute; font-size: 6rem; opacity: 0.05; animation: float 18s ease-in-out infinite; }
.bg-icon.i1 { top: 10%; left: 8%; }
.bg-icon.i2 { top: 30%; right: 12%; animation-delay: -6s; }
.bg-icon.i3 { bottom: 20%; left: 20%; animation-delay: -12s; }
@keyframes float { 0%,100% { transform: translateY(0) rotate(0); } 50% { transform: translateY(-15px) rotate(4deg); } }

.main-content {
  position: relative;
  z-index: 2;
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
  background: rgba(22, 33, 62, 0.95);
  border: 1px solid rgba(15, 52, 96, 0.8);
  border-radius: 12px;
  padding: 24px;
  text-align: left;
  position: relative;
  z-index: 3;
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
/* ===== 旋转轨道图案 ===== */
.orbit-container {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto 20px;
}
.orbit-ring {
  position: absolute;
  inset: 0;
  border: 2px solid rgba(255, 215, 0, 0.25);
  border-radius: 50%;
  animation: orbit-spin 2.4s linear infinite;
}
.orbit-dot {
  position: absolute;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #e94560;
  box-shadow: 0 0 8px rgba(233,69,96,0.7);
}
.orbit-dot.d1 { top: -7px;  left: calc(50% - 7px); }
.orbit-dot.d2 { bottom: -7px; left: calc(50% - 7px); background: #ffd700; box-shadow: 0 0 8px rgba(255,215,0,0.7); }
.orbit-dot.d3 { left: -7px;  top: calc(50% - 7px); background: #4fc3f7; box-shadow: 0 0 8px rgba(79,195,247,0.7); }
.orbit-dot.d4 { right: -7px; top: calc(50% - 7px); background: #81c784; box-shadow: 0 0 8px rgba(129,199,132,0.7); }
.orbit-center {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.4rem;
  animation: pulse-glow 1.6s ease-in-out infinite;
}
@keyframes orbit-spin { to { transform: rotate(360deg); } }
@keyframes pulse-glow {
  0%,100% { transform: scale(1); opacity: 1; }
  50%       { transform: scale(1.15); opacity: 0.75; }
}

/* ===== 等待时间 ===== */
.wait-time {
  color: #888;
  font-size: 1rem;
  margin: 8px 0 4px;
}
.time-num {
  color: #ffd700;
  font-size: 1.6rem;
  font-weight: bold;
  font-variant-numeric: tabular-nums;
  letter-spacing: 2px;
}

/* ===== 操作按钮 ===== */
.wait-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 20px;
}
.wait-actions .btn { flex: 1; max-width: 160px; }

.waiting-card { text-align: center; }
.waiting-card h3 { color: #ffd700; margin-top: 4px; }

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
