<template>
  <Teleport to="body">
    <div v-if="modelValue" class="card-modal-overlay" @click="close">
      <div class="card-modal-content" @click.stop>
        <button class="close-btn" @click="close">✕</button>
        <div class="modal-image-wrap">
          <img
            v-if="imageUrl"
            :src="imageUrl"
            :alt="cardName"
            class="modal-full-image"
          />
          <div v-else class="modal-placeholder">
            <span>🃏</span>
            <p>暂无图片</p>
          </div>
        </div>
        <div class="modal-card-info">
          <h3 class="modal-card-name">{{ cardName }}</h3>
          <div class="modal-stats" v-if="card">
            <span class="stat health">❤️ {{ card.health }}</span>
            <span class="stat attack">⚔️ {{ card.attack ?? '特殊' }}</span>
            <span class="stat cost">🩸{{ card.bloodCost }}</span>
            <span v-if="card.boneCost > 0" class="stat cost">🦴{{ card.boneCost }}</span>
          </div>
          <p v-if="card?.sigils" class="modal-sigils">✨ {{ card.sigils }}</p>
          <p v-if="card?.races" class="modal-races">🏷️ {{ card.races }}</p>
          <p v-if="card?.description" class="modal-desc">{{ card.description }}</p>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { computed } from 'vue'
import { getCardImage } from '../../utils/cardImages'

const props = defineProps({
  modelValue: Boolean,
  cardName: String,
  card: Object
})
const emit = defineEmits(['update:modelValue'])

const imageUrl = computed(() => getCardImage(props.cardName))

function close() {
  emit('update:modelValue', false)
}
</script>

<style scoped>
.card-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.85);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 0.2s ease;
}
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.card-modal-content {
  background: #1a1a2e;
  border: 2px solid #ffd700;
  border-radius: 16px;
  max-width: 480px;
  width: 90vw;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  animation: scaleIn 0.25s ease;
}
@keyframes scaleIn { from { transform: scale(0.9); opacity: 0; } to { transform: scale(1); opacity: 1; } }

.close-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(0,0,0,0.6);
  border: none;
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  font-size: 1rem;
  cursor: pointer;
  z-index: 10;
  transition: background 0.2s;
}
.close-btn:hover { background: #e94560; }

.modal-image-wrap {
  width: 100%;
  aspect-ratio: 3 / 4;
  overflow: hidden;
  border-radius: 14px 14px 0 0;
  background: #0d1b2a;
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal-full-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}
.modal-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0d1b2a 0%, #16213e 100%);
  color: #555;
  font-size: 1.2rem;
}
.modal-placeholder span { font-size: 4rem; }

.modal-card-info { padding: 16px 20px 20px; }
.modal-card-name {
  color: #ffd700;
  font-size: 1.3rem;
  margin: 0 0 10px;
}
.modal-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 8px;
}
.stat { font-size: 0.95rem; }
.stat.health { color: #ff6b6b; }
.stat.attack { color: #ffa07a; }
.stat.cost { color: #aaa; }
.modal-sigils { color: #a78bfa; margin: 4px 0; font-size: 0.9rem; }
.modal-races { color: #60a5fa; margin: 4px 0; font-size: 0.9rem; }
.modal-desc { color: #888; font-size: 0.85rem; margin-top: 8px; font-style: italic; }
</style>
