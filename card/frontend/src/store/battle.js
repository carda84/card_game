import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as battleApi from '../api/battle'

export const useBattleStore = defineStore('battle', () => {
  // ====== 状态 ======
  const sessionId = ref(null)
  const mode = ref(null)            // 'PVE' | 'PVP'
  const opponentName = ref('')
  const opponentCharacterName = ref('')
  const isPlayerFirst = ref(true)
  const board = ref(null)           // BoardStateResponse
  const loading = ref(false)
  const gameOver = ref(false)
  const result = ref(null)          // BattleEndResponse
  const lastAttacks = ref([])       // 上一次攻击结果
  const aiActions = ref([])         // AI 行动日志
  const message = ref('')           // 提示信息
  const pollingTimer = ref(null)    // PvP 轮询定时器

  // ====== 计算属性 ======
  const turnPhase = computed(() => board.value?.turnPhase || null)
  const playerHp = computed(() => board.value?.playerHp ?? 0)
  const opponentHp = computed(() => board.value?.opponentHp ?? 0)
  const playerBones = computed(() => board.value?.playerBones ?? 0)
  const opponentBones = computed(() => board.value?.opponentBones ?? 0)
  const playerHand = computed(() => board.value?.playerHand || [])
  const playerSlots = computed(() => board.value?.playerSlots || [])
  const opponentSlots = computed(() => board.value?.opponentSlots || [])
  const playerItems = computed(() => board.value?.playerItems || [])
  const opponentHandCount = computed(() => board.value?.opponentHandCount ?? 0)
  const turnNumber = computed(() => board.value?.turnNumber ?? 0)
  const currentPlayerId = computed(() => board.value?.currentPlayerId ?? null)
  const isMyTurn = computed(() => board.value?.isMyTurn ?? true)

  // ====== 动作 ======

  /** 开始 PvE 对战 */
  async function startBattle(data) {
    loading.value = true
    try {
      const res = await battleApi.startBattle(data)
      sessionId.value = res.sessionId
      mode.value = data.mode || 'PVE'
      opponentName.value = res.opponentName || '对手'
      isPlayerFirst.value = res.isPlayerFirst
      // 保存 AI 先手首回合行动
      aiActions.value = res.aiFirstTurnActions || []
      await refreshBoard()
      return res
    } finally {
      loading.value = false
    }
  }

  /** 设置已有会话（PvP 匹配后调用） */
  async function attachSession(sid, meta = {}) {
    sessionId.value = sid
    mode.value = meta.mode || 'PVP'
    opponentName.value = meta.opponentName || '对手'
    opponentCharacterName.value = meta.opponentCharacterName || ''
    gameOver.value = false
    result.value = null
    await refreshBoard()
    startPolling()
  }

  /** 刷新棋盘状态 */
  async function refreshBoard() {
    if (!sessionId.value) return
    try {
      board.value = await battleApi.getBoardState(sessionId.value)

      // 检查是否游戏结束（优先用后端返回的 gameOver，兑底用血量判断）
      const serverOver = board.value.gameOver === true
      const hpOver = board.value.playerHp <= 0 || board.value.opponentHp <= 0

      if (serverOver || hpOver) {
        gameOver.value = true
        stopPolling()
        if (!result.value) {
          if (serverOver && board.value.winner) {
            // 后端已提供结算信息
            result.value = {
              result: board.value.winner === 'PLAYER' ? 'WIN' : 'LOSE',
              turns: board.value.turnNumber || 0,
              goldReward: board.value.goldReward ?? (board.value.winner === 'PLAYER' ? 100 : 30),
              pointsChange: board.value.pointsChange ?? (board.value.winner === 'PLAYER' ? 10 : -5)
            }
          } else {
            // 兑底：用血量判断
            const iWon = board.value.opponentHp <= 0
            result.value = {
              result: iWon ? 'WIN' : 'LOSE',
              turns: board.value.turnNumber || 0,
              goldReward: iWon ? 100 : 30,
              pointsChange: iWon ? 10 : -5
            }
          }
        }
      }
    } catch (e) {
      message.value = '获取棋盘状态失败：' + (e.message || '未知错误')
    }
  }

  /** 抽牌 */
  async function drawCard(drawType) {
    loading.value = true
    try {
      const res = await battleApi.drawCard({ sessionId: sessionId.value, drawType })
      await refreshBoard()
      return res
    } finally {
      loading.value = false
    }
  }

  /** 出牌（含献祭） */
  async function playCard(handCardIndex, slotIndex, sacrificeSlotIndices = []) {
    loading.value = true
    try {
      const res = await battleApi.playCard({
        sessionId: sessionId.value,
        handCardIndex,
        slotIndex,
        sacrificeSlotIndices
      })
      await refreshBoard()
      return res
    } finally {
      loading.value = false
    }
  }

  /** 结束回合 */
  async function endTurn() {
    loading.value = true
    try {
      const res = await battleApi.endTurn({ sessionId: sessionId.value })
      lastAttacks.value = res.attacks || []
      // 保存 AI 行动日志
      aiActions.value = res.aiActions || []
      await refreshBoard()
      if (res.isGameOver) {
        gameOver.value = true
        stopPolling()
        // 构造结算数据
        result.value = {
          result: res.winner === 'PLAYER' ? 'WIN' : 'LOSE',
          turns: turnNumber.value,
          goldReward: res.winner === 'PLAYER' ? 100 : 30,
          pointsChange: res.winner === 'PLAYER' ? 10 : -5
        }
      }
      return res
    } finally {
      loading.value = false
    }
  }

  /** 投降 */
  async function doSurrender() {
    if (!sessionId.value) return
    loading.value = true
    try {
      const res = await battleApi.surrender({ sessionId: sessionId.value })
      result.value = res
      gameOver.value = true
      stopPolling()
      return res
    } finally {
      loading.value = false
    }
  }

  /** 使用道具 */
  async function useItem(itemIndex) {
    loading.value = true
    try {
      await battleApi.useItem({ sessionId: sessionId.value, itemIndex })
      await refreshBoard()
    } finally {
      loading.value = false
    }
  }

  /** 使用技能 */
  async function useSkill(skillId) {
    loading.value = true
    try {
      await battleApi.useSkill({ sessionId: sessionId.value, skillId })
      await refreshBoard()
    } finally {
      loading.value = false
    }
  }

  /** 重置 */
  function resetBattle() {
    stopPolling()
    sessionId.value = null
    mode.value = null
    opponentName.value = ''
    opponentCharacterName.value = ''
    board.value = null
    gameOver.value = false
    result.value = null
    lastAttacks.value = []
    aiActions.value = []
    message.value = ''
  }

  /** PvP 轮询 */
  function startPolling() {
    stopPolling()
    pollingTimer.value = setInterval(async () => {
      if (mode.value === 'PVP' && !gameOver.value && sessionId.value) {
        await refreshBoard()
      }
    }, 2000)
  }
  function stopPolling() {
    if (pollingTimer.value) {
      clearInterval(pollingTimer.value)
      pollingTimer.value = null
    }
  }

  return {
    // state
    sessionId, mode, opponentName, opponentCharacterName, isPlayerFirst,
    board, loading, gameOver, result, lastAttacks, aiActions, message,
    // computed
    turnPhase, playerHp, opponentHp, playerBones, opponentBones,
    playerHand, playerSlots, opponentSlots, playerItems,
    opponentHandCount, turnNumber, currentPlayerId, isMyTurn,
    // actions
    startBattle, attachSession, refreshBoard, drawCard, playCard,
    endTurn, doSurrender, useItem, useSkill, resetBattle,
    startPolling, stopPolling
  }
})
