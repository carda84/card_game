import { defineStore } from 'pinia'
import { ref } from 'vue'
import { joinQueue, leaveQueue, findMatch } from '../api/pvp'

export const usePvpStore = defineStore('pvp', () => {
  const inQueue = ref(false)
  const matchResult = ref(null)

  async function queue(deckId) {
    await joinQueue({ deckId })
    inQueue.value = true
  }

  async function cancelQueue() {
    await leaveQueue()
    inQueue.value = false
  }

  async function checkMatch() {
    const res = await findMatch()
    if (res.matched) {
      matchResult.value = res
      inQueue.value = false
    }
    return res
  }

  function reset() {
    inQueue.value = false
    matchResult.value = null
  }

  return { inQueue, matchResult, queue, cancelQueue, checkMatch, reset }
})
