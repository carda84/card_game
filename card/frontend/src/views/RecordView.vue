<template>
  <div>
    <NavBar />
    <div class="page-content">
      <h2>对战记录</h2>
      <div v-if="recordStore.loading">加载中...</div>
      <div v-else-if="recordStore.records.length === 0">暂无对战记录</div>
      <div v-else class="record-list">
        <div v-for="r in recordStore.records" :key="r.id" class="record-card">
          <span class="mode">{{ r.mode }}</span>
          <span :class="r.result">{{ r.result }}</span>
          <span>vs {{ r.opponentName || 'AI' }}</span>
          <span>{{ r.turns }}回合</span>
          <span>{{ r.createdAt }}</span>
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
</script>

<style scoped>
.page-content { padding: 20px; }
.record-list { margin-top: 16px; }
.record-card { display: flex; gap: 16px; padding: 12px; background: #16213e; border-radius: 6px; margin-bottom: 8px; align-items: center; }
.WIN { color: #ffd700; }
.LOSE { color: #dc3545; }
.mode { color: #a0a0a0; }
</style>
