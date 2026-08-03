import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getAllCards, getDeckableCards } from '../api/card'

export const useCardStore = defineStore('card', () => {
  const cards = ref([])
  const loading = ref(false)

  async function fetchAllCards() {
    loading.value = true
    try { cards.value = await getAllCards() } finally { loading.value = false }
  }

  async function fetchDeckableCards() {
    loading.value = true
    try { cards.value = await getDeckableCards() } finally { loading.value = false }
  }

  return { cards, loading, fetchAllCards, fetchDeckableCards }
})
