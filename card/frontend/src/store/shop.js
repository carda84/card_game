import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getShopItems, buyCard as apiBuyCard } from '../api/shop'
import { useUserStore } from './user'

export const useShopStore = defineStore('shop', () => {
  const items = ref([])
  const loading = ref(false)
  const error = ref('')

  async function fetchItems() {
    loading.value = true
    error.value = ''
    try {
      const data = await getShopItems()
      items.value = data.items || []
    } catch (e) {
      error.value = e.message || '加载商店失败'
    } finally { loading.value = false }
  }

  async function buy(cardId) {
    error.value = ''
    try {
      const result = await apiBuyCard({ cardId })
      // 更新用户金币
      const userStore = useUserStore()
      if (result.remainingGold !== undefined) {
        userStore.updateGold(result.remainingGold)
      }
      // 直接更新本地状态，不重新加载整个列表（避免滚动跳动）
      const item = items.value.find(i => i.cardId === cardId)
      if (item) {
        item.owned = true
      }
      return true
    } catch (e) {
      error.value = e.message || '购买失败'
      return false
    }
  }

  return { items, loading, error, fetchItems, buy }
})
