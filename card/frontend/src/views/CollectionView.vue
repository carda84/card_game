<template>
  <div class="collection-view">
    <NavBar />
    <div class="page-content">
      <!-- 装饰背景元素 -->
      <div class="bg-decor">
        <span class="bg-icon i1">🐾</span>
        <span class="bg-icon i2">🌲</span>
        <span class="bg-icon i3">🌙</span>
        <span class="bg-icon i4">🐺</span>
        <span class="bg-icon i5">🦌</span>
        <span class="bg-icon i6">🐾</span>
      </div>

      <h2>🃏 我的卡牌收藏</h2>

      <div v-if="cardStore.loading" class="loading">加载中...</div>
      <div v-else class="card-grid">
        <CardItem
          v-for="card in cardStore.cards"
          :key="card.id"
          :name="card.name"
          :card="card"
          @preview="openPreview(card)"
        />
      </div>
    </div>

    <CardImageModal
      v-model="showModal"
      :card-name="previewCard?.name"
      :card="previewCard"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useCardStore } from '../store/card'
import NavBar from '../components/common/NavBar.vue'
import CardItem from '../components/card/CardItem.vue'
import CardImageModal from '../components/card/CardImageModal.vue'

const cardStore = useCardStore()
const showModal = ref(false)
const previewCard = ref(null)

onMounted(() => cardStore.fetchAllCards())

function openPreview(card) {
  previewCard.value = card
  showModal.value = true
}
</script>

<style scoped>
.collection-view {
  min-height: 100vh;
  background: transparent;
  position: relative;
  overflow: hidden;
}
/* 背景装饰层 */
.bg-decor {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}
.bg-icon {
  position: absolute;
  font-size: 4rem;
  opacity: 0.06;
  filter: blur(1px);
  animation: float 20s ease-in-out infinite;
}
.bg-icon.i1 { top: 8%; left: 5%; font-size: 5rem; animation-delay: 0s; }
.bg-icon.i2 { top: 20%; right: 10%; font-size: 6rem; animation-delay: -4s; }
.bg-icon.i3 { bottom: 30%; left: 15%; font-size: 4.5rem; animation-delay: -8s; }
.bg-icon.i4 { top: 55%; right: 20%; font-size: 5rem; animation-delay: -12s; }
.bg-icon.i5 { bottom: 15%; right: 5%; font-size: 5.5rem; animation-delay: -16s; }
.bg-icon.i6 { top: 40%; left: 45%; font-size: 4rem; animation-delay: -6s; }
@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  25% { transform: translateY(-12px) rotate(3deg); }
  50% { transform: translateY(8px) rotate(-2deg); }
  75% { transform: translateY(-6px) rotate(1deg); }
}
.page-content {
  position: relative;
  z-index: 1;
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}
.page-content h2 {
  color: #e0e0e0;
  margin-bottom: 16px;
  font-size: 1.4rem;
}
.loading { color: #888; padding: 20px; }
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 14px;
}
</style>
