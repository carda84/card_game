<template>
  <div class="shop-view">
    <NavBar />
    <div class="page-content">
      <div class="shop-header">
        <h2>🛒 卡牌商店</h2>
        <p class="gold-display">💰 {{ userStore.gold }} 金币</p>
      </div>
      <p class="hint">所有卡牌统一售价 20 金币，购买后可无限次编入卡组。点击图片查看大图。</p>

      <div v-if="shopStore.error" class="error-toast">❌ {{ shopStore.error }}</div>

      <Transition name="toast-slide">
        <div v-if="buySuccessMsg" class="success-toast">✅ {{ buySuccessMsg }}</div>
      </Transition>

      <div v-if="shopStore.loading" class="loading">
        <div class="loading-spinner"></div>
        <span>加载中...</span>
      </div>
      <div v-else class="shop-grid">
        <CardItem
          v-for="item in shopStore.items"
          :key="item.cardId"
          :name="item.cardName"
          :card="item.card"
          :owned="item.owned"
          @preview="openPreview(item)"
        >
          <template #action>
            <span class="price">💰 {{ item.price }}</span>
            <span v-if="item.owned" class="owned-badge">✅ 已解锁</span>
            <button v-else @click="handleBuy(item)" class="btn-buy" :disabled="buying">购买</button>
          </template>
        </CardItem>
      </div>
    </div>

    <CardImageModal
      v-model="showModal"
      :card-name="previewCard?.cardName"
      :card="previewCard?.card"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useShopStore } from '../store/shop'
import { useUserStore } from '../store/user'
import NavBar from '../components/common/NavBar.vue'
import CardItem from '../components/card/CardItem.vue'
import CardImageModal from '../components/card/CardImageModal.vue'

const shopStore = useShopStore()
const userStore = useUserStore()
const buying = ref(false)
const showModal = ref(false)
const previewCard = ref(null)
const buySuccessMsg = ref('')

onMounted(() => shopStore.fetchItems())

async function handleBuy(item) {
  buying.value = true
  const success = await shopStore.buy(item.cardId)
  buying.value = false
  if (success) {
    buySuccessMsg.value = `已解锁「${item.cardName}」`
    setTimeout(() => buySuccessMsg.value = '', 2500)
  }
}

function openPreview(item) {
  previewCard.value = item
  showModal.value = true
}
</script>

<style scoped>
.shop-view { min-height: 100vh; background: transparent; }
.page-content { padding: 24px; max-width: 1400px; margin: 0 auto; }

.shop-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border, #1e3a5f);
}
.shop-header h2 {
  color: var(--text-primary);
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  gap: 8px;
}
.gold-display {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.12), rgba(255, 215, 0, 0.05));
  border: 1px solid rgba(255, 215, 0, 0.25);
  border-radius: 50px;
  color: #ffd700;
  font-size: 1.05rem;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(255, 215, 0, 0.1);
}
.hint {
  color: var(--text-muted, #718096);
  font-size: 0.85rem;
  margin-bottom: 18px;
  padding: 10px 14px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: var(--radius-sm, 6px);
  border-left: 3px solid var(--brand, #e94560);
}
.error-toast {
  background: rgba(244, 67, 54, 0.1);
  color: var(--red, #f44336);
  padding: 10px 16px;
  border-radius: var(--radius-sm, 6px);
  margin-bottom: 12px;
  font-size: 0.9rem;
  border: 1px solid rgba(244, 67, 54, 0.2);
}
.success-toast {
  background: linear-gradient(135deg, rgba(74, 222, 128, 0.12), rgba(74, 222, 128, 0.05));
  color: var(--green, #4ade80);
  padding: 12px 18px;
  border-radius: var(--radius-md, 10px);
  margin-bottom: 12px;
  font-size: 0.92rem;
  font-weight: 600;
  border: 1px solid rgba(74, 222, 128, 0.25);
  box-shadow: 0 2px 12px rgba(74, 222, 128, 0.15);
}
.toast-slide-enter-active, .toast-slide-leave-active { transition: all 0.3s ease; }
.toast-slide-enter-from, .toast-slide-leave-to { opacity: 0; transform: translateY(-10px); }

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 60px;
  color: var(--text-muted);
}
.loading-spinner {
  width: 36px;
  height: 36px;
  border: 3px solid var(--border);
  border-top-color: #e94560;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.shop-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(190px, 1fr));
  gap: 16px;
}

.price {
  color: #ffd700;
  font-weight: 700;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 4px;
}
.owned-badge {
  color: var(--green, #4ade80);
  font-size: 0.82rem;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: rgba(74, 222, 128, 0.08);
  border-radius: 50px;
}
.btn-buy {
  padding: 7px 20px;
  background: linear-gradient(135deg, #e94560, #c23152);
  border: none;
  border-radius: var(--radius-sm, 6px);
  color: white;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 600;
  transition: all 0.25s;
  box-shadow: 0 2px 8px rgba(233, 69, 96, 0.25);
  position: relative;
  overflow: hidden;
}
.btn-buy::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.15), transparent);
  opacity: 0;
  transition: opacity 0.2s;
}
.btn-buy:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(233, 69, 96, 0.4);
}
.btn-buy:hover::before { opacity: 1; }
.btn-buy:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(233, 69, 96, 0.3);
}
.btn-buy:disabled { opacity: 0.4; cursor: not-allowed; transform: none; box-shadow: none; }
</style>
