import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getRecentRecords } from '../api/record'

export const useRecordStore = defineStore('record', () => {
  const records = ref([])
  const loading = ref(false)

  async function fetchRecords() {
    loading.value = true
    try { records.value = await getRecentRecords() } finally { loading.value = false }
  }

  return { records, loading, fetchRecords }
})
