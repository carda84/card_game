<template>
  <div class="records-view">
    <NavBar />
    <div class="page-content">
      <div class="page-header">
        <h2>📊 对战记录</h2>
        <p class="page-sub">查看你的历史对战表现</p>
      </div>
      <div v-if="recordStore.loading" class="loading-state">
        <div class="spinner"></div><span>加载中...</span>
      </div>
      <div v-else-if="recordStore.records.length === 0" class="empty-state">
        <div class="empty-icon">📭</div>
        <h3>暂无对战记录</h3>
        <p>开始你的第一场对战吧</p>
        <router-link to="/battle/prepare" class="btn-action">前往对战</router-link>
      </div>
      <div v-else class="record-list">
        <div v-for="r in recordStore.records" :key="r.id" class="record-card">
          <div class="rc-result" :class="r.result?.toLowerCase()">
            <span class="result-badge">{{ resultLabel(r.result) }}</span>
          </div>
          <div class="rc-info">
            <div class="rc-top">
              <span class="rc-mode">{{ modeLabel(r.mode) }}</span>
              <span class="rc-vs">vs {{ r.opponentName || 'AI' }}</span>
            </div>
            <div class="rc-meta">
              <span>⏱ {{ r.turns }}回合</span>
              <span v-if="r.reward">🎁 +{{ r.reward }}</span>
            </div>
          </div>
          <div class="rc-time">{{ formatTime(r.createdAt) }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRecordStore } from '../store/record'
import NavBar from '../components/common/NavBar.vue'

const recordStore = useRecordStore()
onMounted(() => recordStore.fetchRecords())

function resultLabel(r) {
  if (r === 'WIN') return '胜利'
  if (r === 'LOSE') return '失败'
  if (r === 'SURRENDER') return '投降'
  return r
}
function modeLabel(m) { return m === 'PVE' ? '人机' : m === 'PVP' ? 'PvP' : m }
function formatTime(t) { return t ? t.replace('T', ' ').substring(0, 16) : '' }
</script>

<style scoped>
.records-view { min-height: 100vh; background: transparent; }
.page-content { padding: 24px; max-width: 800px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-header h2 {
  color: var(--text-primary);
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.page-sub { color: var(--text-muted); font-size: 0.88rem; }

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 60px 0;
  color: var(--text-muted);
}
.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid var(--border);
  border-top-color: #e94560;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: var(--text-muted);
  background: var(--bg-card);
  border: 1px dashed var(--border);
  border-radius: var(--radius-lg);
}
.empty-icon {
  font-size: 3.5rem;
  margin-bottom: 16px;
  opacity: 0.4;
}
.empty-state h3 { color: var(--text-secondary); margin-bottom: 8px; font-size: 1.1rem; }
.empty-state p { margin-bottom: 20px; }
.btn-action {
  display: inline-block;
  margin-top: 16px;
  padding: 12px 28px;
  background: linear-gradient(135deg, #e94560, #c23152);
  border-radius: 50px;
  color: white;
  font-weight: 600;
  transition: all 0.25s;
  box-shadow: 0 4px 16px rgba(233, 69, 96, 0.3);
}
.btn-action:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(233, 69, 96, 0.4);
}

.record-list { display: flex; flex-direction: column; gap: 10px; }
.record-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 14px 18px;
  transition: all 0.25s;
  animation: fadeIn 0.3s ease;
}
.record-card:hover {
  border-color: var(--border-hover);
  transform: translateX(6px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
  background: var(--bg-card-hover);
}

.rc-result { flex-shrink: 0; width: 60px; text-align: center; }
.result-badge {
  display: inline-block;
  padding: 5px 12px;
  border-radius: 50px;
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.5px;
}
.rc-result.win .result-badge { background: rgba(74, 222, 128, 0.15); color: var(--green); border: 1px solid rgba(74, 222, 128, 0.3); }
.rc-result.lose .result-badge { background: rgba(244, 67, 54, 0.12); color: var(--red); border: 1px solid rgba(244, 67, 54, 0.25); }
.rc-result.surrender .result-badge { background: rgba(255, 165, 0, 0.12); color: var(--orange); border: 1px solid rgba(255, 165, 0, 0.25); }

.rc-info { flex: 1; min-width: 0; }
.rc-top { display: flex; align-items: center; gap: 10px; margin-bottom: 4px; }
.rc-mode {
  padding: 3px 10px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 50px;
  font-size: 0.75rem;
  color: var(--text-secondary);
  border: 1px solid rgba(255, 255, 255, 0.08);
}
.rc-vs { font-weight: 500; color: var(--text-primary); font-size: 0.92rem; }
.rc-meta { display: flex; gap: 12px; font-size: 0.8rem; color: var(--text-muted); }
.rc-time { font-size: 0.78rem; color: var(--text-dim); flex-shrink: 0; white-space: nowrap; }

@keyframes fadeIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
</style>
