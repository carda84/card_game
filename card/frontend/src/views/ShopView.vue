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

      <div v-if="shopStore.loading" class="loading">加载中...</div>
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

onMounted(() => shopStore.fetchItems())

async function handleBuy(item) {
  buying.value = true
  await shopStore.buy(item.cardId)
  buying.value = false
}

function openPreview(item) {
  previewCard.value = item
  showModal.value = true
}
</script>

<style scoped>
.shop-view { min-height: 100vh; background: transparent; }
.page-content { padding: 20px; max-width: 1400px; margin: 0 auto; }
.shop-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px; }
.shop-header h2 { color: #e0e0e0; }
.gold-display { color: #ffd700; font-size: 1.1rem; font-weight: bold; }
.hint { color: #888; font-size: 0.85rem; margin-bottom: 16px; }
.error-toast { background: #3a1010; color: #ff6b6b; padding: 10px 16px; border-radius: 6px; margin-bottom: 12px; font-size: 0.9rem; border: 1px solid #5a1a1a; }
.loading { color: #888; padding: 20px; }
.shop-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 14px;
}

.price { color: #ffd700; font-weight: bold; }
.owned-badge { color: #4ade80; font-size: 0.82rem; font-weight: 500; }
.btn-buy {
  padding: 5px 16px;
  background: #e94560;
  border: none;
  border-radius: 4px;
  color: white;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 500;
  transition: background 0.2s;
}
.btn-buy:hover { background: #c93050; }
.btn-buy:disabled { opacity: 0.5; cursor: not-allowed; }
</style>
