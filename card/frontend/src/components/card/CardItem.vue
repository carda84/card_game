<template>
  <div class="card-item" :class="{ owned, legendary: card?.isLegendary }">
    <!-- 卡牌图片区（可点击放大） -->
    <div class="card-image" @click="$emit('preview')">
      <img
        v-if="imageUrl"
        :src="imageUrl"
        :alt="name"
        class="card-art"
      />
      <div v-else class="card-art-placeholder">
        <span class="placeholder-icon">🃏</span>
      </div>
      <span v-if="card?.isLegendary" class="legendary-badge">传奇</span>
    </div>

    <!-- 卡牌信息区 -->
    <div class="card-body">
      <h4 class="card-name">{{ name }}</h4>
      <div class="card-info" v-if="card">
        <span class="stat health">❤️ {{ card.health }}</span>
        <span class="stat attack">⚔️ {{ card.attack ?? '特殊' }}</span>
        <span class="stat cost">🩸{{ card.bloodCost }}</span>
        <span v-if="card.boneCost > 0" class="stat cost">🦴{{ card.boneCost }}</span>
      </div>
      <p v-if="card?.sigils" class="sigils">✨ {{ card.sigils }}</p>
      <p v-if="card?.races" class="races">🏷️ {{ card.races }}</p>
    </div>

    <!-- 操作区（可选） -->
    <div v-if="$slots.action" class="card-footer">
      <slot name="action" />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { getCardImage } from '../../utils/cardImages'

const props = defineProps({
  name: { type: String, required: true },
  card: { type: Object, default: null },
  owned: { type: Boolean, default: false }
})
defineEmits(['preview'])

const imageUrl = computed(() => getCardImage(props.name))
</script>

<style scoped>
.card-item {
  background: #16213e;
  border: 1px solid #0f3460;
  border-radius: 10px;
  overflow: hidden;
  transition: transform 0.2s, border-color 0.2s, box-shadow 0.2s;
  display: flex;
  flex-direction: column;
}
.card-item:hover { transform: translateY(-3px); border-color: #e94560; box-shadow: 0 4px 16px rgba(233, 69, 96, 0.2); }
.card-item.owned { border-color: #2a5a3a; }
.card-item.owned:hover { transform: none; box-shadow: none; }
.card-item.legendary { border-color: #8b5e3c; }
.card-item.legendary:hover { border-color: #ffd700; box-shadow: 0 4px 16px rgba(255, 215, 0, 0.25); }

.card-image {
  position: relative;
  width: 100%;
  aspect-ratio: 3 / 4;
  background: #0d1b2a;
  overflow: hidden;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
.card-image:hover .card-art { filter: brightness(1.15); }
.card-art {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
  transition: filter 0.2s;
}
.card-art-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0d1b2a 0%, #16213e 100%);
}
.placeholder-icon { font-size: 3rem; opacity: 0.3; }
.legendary-badge {
  position: absolute;
  top: 6px;
  right: 6px;
  background: #e94560;
  color: white;
  font-size: 0.65rem;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: bold;
  letter-spacing: 1px;
}

.card-body { padding: 10px 12px 6px; flex: 1; }
.card-name { color: #ffd700; margin: 0 0 6px; font-size: 0.95rem; }
.card-info { display: flex; flex-wrap: wrap; gap: 6px; font-size: 0.8rem; margin-bottom: 4px; }
.stat { white-space: nowrap; }
.stat.health { color: #ff6b6b; }
.stat.attack { color: #ffa07a; }
.stat.cost { color: #aaa; }
.sigils { color: #a78bfa; font-size: 0.78rem; margin: 2px 0; }
.races { color: #60a5fa; font-size: 0.78rem; margin: 2px 0; }

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px 10px;
  font-size: 0.85rem;
}
</style>
