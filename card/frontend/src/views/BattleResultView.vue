<template>
  <div class="result-page">
    <div class="bg-decor">
      <span class="bg-icon i1">🏆</span>
      <span class="bg-icon i2">⚔️</span>
      <span class="bg-icon i3">💀</span>
    </div>

    <main class="result-wrap">
      <div class="result-card" :class="resultClass">
        <div class="result-banner">
          <div class="result-icon">{{ resultIcon }}</div>
          <h2>{{ resultTitle }}</h2>
          <p class="sub">{{ resultSub }}</p>
        </div>

        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-label">回合数</div>
            <div class="stat-value">{{ battleStore.result?.turns ?? 0 }}</div>
          </div>
          <div class="stat-item gold">
            <div class="stat-label">金币奖励</div>
            <div class="stat-value">+{{ battleStore.result?.goldReward ?? 0 }}</div>
          </div>
          <div class="stat-item" :class="pointsClass">
            <div class="stat-label">积分变化</div>
            <div class="stat-value">
              {{ (battleStore.result?.pointsChange ?? 0) > 0 ? '+' : '' }}{{ battleStore.result?.pointsChange ?? 0 }}
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-label">对战模式</div>
            <div class="stat-value">{{ battleStore.mode || 'PVE' }}</div>
          </div>
        </div>

        <div class="actions">
          <router-link to="/battle/prepare" class="btn btn-primary">再来一局</router-link>
          <router-link to="/" class="btn btn-ghost">返回大厅</router-link>
          <router-link to="/records" class="btn btn-ghost">查看战绩</router-link>
        </div>
      </div>

      <div v-if="!battleStore.result" class="no-result">
        <h2>暂无结算数据</h2>
        <p>可能是通过直接访问页面进入</p>
        <router-link to="/battle/prepare" class="btn btn-primary">开始对战</router-link>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useBattleStore } from '../store/battle'

const battleStore = useBattleStore()

const resultClass = computed(() => {
  const r = battleStore.result?.result
  if (r === 'WIN') return 'win'
  if (r === 'LOSE') return 'lose'
  if (r === 'SURRENDER') return 'surrender'
  return ''
})
const resultIcon = computed(() => {
  const r = battleStore.result?.result
  if (r === 'WIN') return '🏆'
  if (r === 'SURRENDER') return '🏳️'
  return '💀'
})
const resultTitle = computed(() => {
  const r = battleStore.result?.result
  if (r === 'WIN') return '胜利！'
  if (r === 'SURRENDER') return '已投降'
  return '战败'
})
const resultSub = computed(() => {
  const r = battleStore.result?.result
  if (r === 'WIN') return '英勇的战士，你赢得了这场战斗'
  if (r === 'SURRENDER') return '战略性撤退，下次再战'
  return '战斗结束，继续努力'
})
const pointsClass = computed(() => {
  const p = battleStore.result?.pointsChange ?? 0
  return p >= 0 ? 'positive' : 'negative'
})

onMounted(() => {
  battleStore.stopPolling()
})
</script>

<style scoped>
.result-page {
  min-height: 100vh;
  background: #1a1a2e;
  position: relative;
  overflow: hidden;
  padding: 2rem;
}
.bg-decor { position: fixed; inset: 0; pointer-events: none; z-index: 0; }
.bg-icon { position: absolute; font-size: 8rem; opacity: 0.05; animation: float 20s ease-in-out infinite; }
.bg-icon.i1 { top: 10%; left: 10%; }
.bg-icon.i2 { top: 30%; right: 15%; animation-delay: -7s; }
.bg-icon.i3 { bottom: 15%; left: 20%; animation-delay: -13s; }
@keyframes float { 0%,100% { transform: translateY(0) rotate(0); } 50% { transform: translateY(-20px) rotate(6deg); } }

.result-wrap {
  position: relative;
  z-index: 1;
  max-width: 560px;
  margin: 0 auto;
}

.result-card {
  background: rgba(22, 33, 62, 0.9);
  border: 2px solid #0f3460;
  border-radius: 16px;
  overflow: hidden;
  backdrop-filter: blur(8px);
}
.result-card.win { border-color: #ffd700; box-shadow: 0 0 40px rgba(255,215,0,0.2); }
.result-card.lose { border-color: #dc3545; }
.result-card.surrender { border-color: #888; }

.result-banner {
  padding: 30px 20px;
  text-align: center;
  background: linear-gradient(180deg, rgba(15,52,96,0.5) 0%, transparent 100%);
}
.result-icon {
  font-size: 4rem;
  margin-bottom: 10px;
  filter: drop-shadow(0 4px 12px rgba(0,0,0,0.5));
}
.result-card.win .result-banner h2 { color: #ffd700; }
.result-card.lose .result-banner h2,
.result-card.surrender .result-banner h2 { color: #dc3545; }
.result-banner h2 {
  font-size: 2.2rem;
  margin: 0 0 8px;
  color: #e0e0e0;
}
.result-banner .sub { color: #888; margin: 0; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 20px;
}
.stat-item {
  background: #0d1b2a;
  border: 1px solid #0f3460;
  border-radius: 10px;
  padding: 12px;
  text-align: center;
}
.stat-item.gold { border-color: #ffd700; }
.stat-item.positive { border-color: #2a5a3a; }
.stat-item.negative { border-color: #dc3545; }
.stat-label {
  color: #888;
  font-size: 0.8rem;
  margin-bottom: 4px;
}
.stat-value {
  color: #e0e0e0;
  font-size: 1.3rem;
  font-weight: bold;
}
.stat-item.gold .stat-value { color: #ffd700; }
.stat-item.positive .stat-value { color: #90ee90; }
.stat-item.negative .stat-value { color: #ff6b6b; }

.actions {
  display: flex;
  gap: 10px;
  justify-content: center;
  padding: 16px 20px 24px;
  flex-wrap: wrap;
}

.no-result {
  text-align: center;
  color: #888;
  padding: 40px 20px;
}
.no-result h2 { color: #e0e0e0; margin-bottom: 8px; }

.btn {
  padding: 10px 22px;
  border: none;
  border-radius: 6px;
  color: white;
  cursor: pointer;
  font-size: 0.95rem;
  text-decoration: none;
  display: inline-block;
  transition: all 0.2s;
}
.btn:hover { opacity: 0.85; transform: translateY(-1px); }
.btn-primary { background: linear-gradient(135deg, #e94560, #c93050); }
.btn-ghost { background: transparent; border: 1px solid #0f3460; color: #ccc; }
.btn-ghost:hover { border-color: #ffd700; color: #ffd700; }
</style>
