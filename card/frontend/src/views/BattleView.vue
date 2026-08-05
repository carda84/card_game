<template>
  <div class="battle-view">
    <!-- 装饰背景 -->
    <div class="bg-decor">
      <span class="bg-icon i1">⚔️</span>
      <span class="bg-icon i2">🐾</span>
      <span class="bg-icon i3">💀</span>
      <span class="bg-icon i4">🦴</span>
    </div>

    <!-- 初始化中 -->
    <div v-if="!battleStore.sessionId" class="init-screen">
      <div class="spinner"></div>
      <p>正在准备战斗...</p>
    </div>

    <!-- 战斗主体 -->
    <div v-else class="battle-wrap">
      <!-- 左侧：对手信息 -->
      <aside class="side-panel left-side">
        <div class="side-title">👹 对手</div>
        <div class="side-name">{{ battleStore.opponentName || '对手' }}</div>
        <div class="side-sub" v-if="battleStore.opponentCharacterName">
          {{ battleStore.opponentCharacterName }}
        </div>

        <div class="stat-grid">
          <div class="stat hp" :class="{ low: battleStore.opponentHp <= 10 }">
            <span class="stat-icon">❤️</span>
            <span class="stat-val">{{ battleStore.opponentHp }}</span>
          </div>
          <div class="stat">
            <span class="stat-icon">🃏</span>
            <span class="stat-val">{{ battleStore.opponentHandCount }}</span>
          </div>
          <div class="stat">
            <span class="stat-icon">🦴</span>
            <span class="stat-val">{{ battleStore.opponentBones }}</span>
          </div>
        </div>

        <div class="side-label">可用道具</div>
        <div class="item-list">
          <span v-if="!opponentItems.length" class="empty-hint">无</span>
          <span v-for="(it, idx) in opponentItems" :key="idx" class="item-tag">{{ it }}</span>
        </div>
      </aside>

      <!-- 中央：牌桌 -->
      <main class="board-area">
        <!-- 牌桌背景图 -->
        <img :src="deskImg" class="desk-bg" alt="" />

        <!-- 对手格位 (绝对定位，贴合背景图上方卡槽) -->
        <div class="slots-row opponent-row">
          <div
            v-for="(slot, idx) in battleStore.opponentSlots"
            :key="'opp-' + idx"
            class="slot opponent-slot"
            :class="{ filled: !slot.isEmpty }"
          >
            <div v-if="!slot.isEmpty" class="card-on-board" @click="previewCard(slot.card)">
              <img v-if="getImg(slot.card)" :src="getImg(slot.card)" class="card-img" />
              <div v-else class="card-ph"><span>🃏</span></div>
              <div class="hp-badge" v-if="slot.card">
                <span>❤️{{ slot.card.health }}</span>
              </div>
              <div class="sigil-badges" v-if="slot.card?.sigilList?.length">
                <span v-for="(sig, si) in slot.card.sigilList.slice(0, 3)" :key="si" class="sigil-tag" :title="sig">{{ sig }}</span>
              </div>
            </div>
            <div v-else class="slot-empty">·</div>
          </div>
        </div>

        <!-- 我方格位 (绝对定位，贴合背景图下方卡槽) -->
        <div class="slots-row player-row">
          <div
            v-for="(slot, idx) in battleStore.playerSlots"
            :key="'me-' + idx"
            class="slot player-slot"
            :class="{
              filled: !slot.isEmpty,
              empty: slot.isEmpty,
              'can-play': canPlayToSlot(idx),
              'sac-target': isSacTarget(idx),
              'sac-selectable': sacSelecting && !slot.isEmpty && canBeSac(slot)
            }"
            @click="onPlayerSlotClick(idx)"
          >
            <div v-if="!slot.isEmpty" class="card-on-board" @click.stop="onPlayerSlotClick(idx)">
              <img v-if="getImg(slot.card)" :src="getImg(slot.card)" class="card-img" />
              <div v-else class="card-ph"><span>🃏</span></div>
              <div class="hp-badge" v-if="slot.card">
                <span>❤️{{ slot.card.health }}</span>
              </div>
              <div class="sigil-badges" v-if="slot.card?.sigilList?.length">
                <span v-for="(sig, si) in slot.card.sigilList.slice(0, 3)" :key="si" class="sigil-tag" :title="sig">{{ sig }}</span>
              </div>
              <div class="card-preview-overlay" @click.stop="previewCard(slot.card)">🔍</div>
            </div>
            <div v-else class="slot-empty">
              <span v-if="canPlayToSlot(idx)" class="play-hint">点击出牌</span>
              <span v-else>·</span>
            </div>
          </div>
        </div>

        <!-- 回合信息浮层 -->
        <div class="turn-bar">
          <span>回合 {{ battleStore.turnNumber }}</span>
          <span class="phase-tag">{{ phaseLabel }}</span>
          <span v-if="battleStore.mode === 'PVP'" class="mode-tag">PvP</span>
          <span v-else class="mode-tag">PvE</span>
        </div>

        <!-- PvP 等待提示 -->
        <div v-if="battleStore.mode === 'PVP' && !battleStore.isMyTurn && !battleStore.gameOver" class="opponent-turn-banner">
          <div class="otb-spinner"></div>
          <span>对手回合，请等待...</span>
        </div>

        <!-- 操作按钮浮层 -->
        <div class="action-bar">
          <button
            class="btn btn-primary"
            :disabled="battleStore.loading || !canEndTurn || !battleStore.isMyTurn"
            @click="handleEndTurn"
          >结束回合</button>
          <button
            class="btn btn-skill"
            :disabled="battleStore.loading || !isPlayPhase || !battleStore.isMyTurn"
            @click="handleUseSkill"
            v-if="battleStore.mode !== null"
          >主动技能</button>
          <button
            class="btn btn-danger"
            :disabled="battleStore.loading"
            @click="handleSurrender"
          >投降</button>
        </div>

        <!-- 攻击结果浮层 -->
        <Transition name="fade">
          <div v-if="showAttackLog" class="attack-log">
            <div class="log-title">⚔️ 攻击结算</div>
            <div v-for="(atk, i) in battleStore.lastAttacks" :key="i" class="log-line">
              格位 {{ atk.attackerSlot + 1 }} → 格位 {{ atk.defenderSlot + 1 }} 造成 {{ atk.damage }} 伤害
              <span v-if="atk.defenderDied" class="tag-kill">击杀</span>
              <span v-if="atk.attackerDied" class="tag-dead">阵亡</span>
            </div>
            <button class="btn btn-ghost" @click="showAttackLog = false">关闭</button>
          </div>
        </Transition>
      </main>

      <!-- 右侧：我方信息 -->
      <aside class="side-panel right-side">
        <div class="side-title">🐾 我方</div>
        <div class="stat-grid">
          <div class="stat hp" :class="{ low: battleStore.playerHp <= 10 }">
            <span class="stat-icon">❤️</span>
            <span class="stat-val">{{ battleStore.playerHp }}</span>
          </div>
          <div class="stat">
            <span class="stat-icon">🦴</span>
            <span class="stat-val">{{ battleStore.playerBones }}</span>
          </div>
          <div class="stat">
            <span class="stat-icon">🃏</span>
            <span class="stat-val">{{ battleStore.playerHand.length }}</span>
          </div>
        </div>

        <div class="side-label">可用道具</div>
        <div class="item-list">
          <span v-if="!battleStore.playerItems.length" class="empty-hint">无</span>
          <div
            v-for="(it, idx) in battleStore.playerItems"
            :key="idx"
            class="item-tag clickable"
            @click="onUseItem(idx)"
          >{{ it }}</div>
        </div>

        <div class="side-label">操作</div>
        <div class="action-col">
          <button
            v-if="selectedHandIdx !== null && selectedCard?.bloodCost > 0"
            class="btn btn-sac"
            :disabled="!canStartSac"
            @click="startSacrifice"
          >
            {{ sacSelecting ? '取消献祭' : `献祭(${selectedCard.bloodCost})` }}
          </button>
          <button
            v-if="sacSelecting"
            class="btn btn-ghost"
            @click="cancelSacrifice"
          >撤销</button>
          <button
            v-if="selectedHandIdx !== null && selectedCard?.boneCost > 0"
            class="btn btn-bone"
            :disabled="battleStore.playerBones < selectedCard.boneCost"
            @click="playBoneCard"
          >骨头出牌(🦴{{ selectedCard.boneCost }})</button>
        </div>
      </aside>
    </div>

    <!-- 底部：手牌区 -->
    <div class="hand-area" v-if="battleStore.sessionId">
      <div class="hand-title">手牌 ({{ battleStore.playerHand.length }})</div>
      <div class="hand-scroll">
        <div
          v-for="(card, idx) in battleStore.playerHand"
          :key="idx"
          class="hand-card"
          :class="{ selected: selectedHandIdx === idx, disabled: !isPlayPhase }"
          @click="onHandCardClick(idx)"
        >
          <div class="hc-img">
            <img v-if="getImg(card)" :src="getImg(card)" />
            <div v-else class="card-ph small"><span>🃏</span></div>
          </div>
          <div class="hc-info">
            <div class="hc-name">{{ card.name }}</div>
            <div class="hc-costs">
              <span v-if="card.bloodCost > 0" class="cost blood"><img :src="bloodImg" class="icon-sm" />{{ card.bloodCost }}</span>
              <span v-if="card.boneCost > 0" class="cost bone">🦴{{ card.boneCost }}</span>
              <span v-if="card.bloodCost === 0 && card.boneCost === 0" class="cost free">免费</span>
            </div>
          </div>
          <button class="preview-btn" @click.stop="previewCard(card)">详情</button>
        </div>
        <div v-if="!battleStore.playerHand.length" class="hand-empty">暂无手牌</div>
      </div>
    </div>

    <!-- 抽牌选择弹窗 -->
    <Teleport to="body">
      <div v-if="showDrawDialog" class="modal-overlay">
        <div class="modal-card draw-dialog">
          <h3>抽牌阶段</h3>
          <p class="sub">选择本回合抽取的卡牌</p>
          <div class="draw-options">
            <button class="draw-opt" @click="doDraw('SQUIRREL')">
              <div class="opt-img">
                <img v-if="squirrelImg" :src="squirrelImg" />
                <span v-else>🐿️</span>
              </div>
              <div class="opt-name">松鼠牌</div>
              <div class="opt-desc">无限供应</div>
            </button>
            <button class="draw-opt" @click="doDraw('DECK')">
              <div class="opt-img">
                <img v-if="wolfImg" :src="wolfImg" />
                <span v-else>🐺</span>
              </div>
              <div class="opt-name">牌组抽牌</div>
              <div class="opt-desc">抽取牌组中的卡牌</div>
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 卡牌预览弹窗 -->
    <CardImageModal v-model="previewVisible" :card="previewCardData" :card-name="previewCardData?.name" />

    <!-- 道具使用确认弹窗 -->
    <Teleport to="body">
      <div v-if="showItemConfirm" class="modal-overlay">
        <div class="modal-card confirm-dialog">
          <h3>使用道具</h3>
          <p>确认使用 <b>{{ selectedItemName }}</b> ？</p>
          <div class="confirm-actions">
            <button class="btn btn-primary" @click="confirmUseItem">确认</button>
            <button class="btn btn-ghost" @click="showItemConfirm = false">取消</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 投降确认 -->
    <Teleport to="body">
      <div v-if="showSurrenderConfirm" class="modal-overlay">
        <div class="modal-card confirm-dialog">
          <h3>确认投降</h3>
          <p>投降将判负，是否继续？</p>
          <div class="confirm-actions">
            <button class="btn btn-danger" @click="confirmSurrender">确认投降</button>
            <button class="btn btn-ghost" @click="showSurrenderConfirm = false">取消</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- AI 行动日志弹窗 -->
    <Teleport to="body">
      <div v-if="showAiLog" class="modal-overlay">
        <div class="modal-card ai-log-dialog">
          <h3>🤖 AI 对手行动</h3>
          <p class="sub">AI 回合执行了以下操作：</p>
          <div class="ai-log-list">
            <div v-for="(action, idx) in battleStore.aiActions" :key="idx" class="ai-log-item">
              <span class="ai-log-type">{{ aiActionIcon(action.type) }}</span>
              <span class="ai-log-detail">{{ action.detail }}</span>
            </div>
            <div v-if="!battleStore.aiActions.length" class="ai-log-empty">AI 本回合未执行任何操作</div>
          </div>
          <div class="confirm-actions">
            <button class="btn btn-primary" @click="closeAiLog">确认</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 提示条 -->
    <Transition name="slide">
      <div v-if="battleStore.message" class="toast">{{ battleStore.message }}</div>
    </Transition>

    <!-- 结算跳转 -->
    <Teleport to="body">
      <div v-if="battleStore.gameOver && battleStore.result" class="modal-overlay">
        <div class="modal-card game-over">
          <h2 :class="battleStore.result.result">
            {{ battleStore.result.result === 'WIN' ? '🎉 胜利！' : '💀 战败' }}
          </h2>
          <div class="go-stats">
            <div>回合数：{{ battleStore.result.turns }}</div>
            <div>金币奖励：+{{ battleStore.result.goldReward }}</div>
            <div>积分变化：{{ battleStore.result.pointsChange > 0 ? '+' : '' }}{{ battleStore.result.pointsChange }}</div>
          </div>
          <div class="confirm-actions">
            <button class="btn btn-primary" @click="goToResult">查看结算</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter, onBeforeRouteLeave } from 'vue-router'
import { useBattleStore } from '../store/battle'
import CardImageModal from '../components/card/CardImageModal.vue'
import { getCardImage } from '../utils/cardImages'
import deskImg from '../assets/images/desk.jpg'
import bloodImg from '../assets/images/blood.png'

const route = useRoute()
const router = useRouter()
const battleStore = useBattleStore()

const selectedHandIdx = ref(null)
const sacrificeSlots = ref([])
const sacSelecting = ref(false)
const showDrawDialog = ref(false)
const showAttackLog = ref(false)
const showAiLog = ref(false)
const showItemConfirm = ref(false)
const showSurrenderConfirm = ref(false)
const pendingItemIdx = ref(null)
const previewVisible = ref(false)
const previewCardData = ref(null)

const selectedCard = computed(() => {
  if (selectedHandIdx.value === null) return null
  return battleStore.playerHand[selectedHandIdx.value] || null
})

const isPlayPhase = computed(() => battleStore.turnPhase === 'PLAY_CARD')
const canEndTurn = computed(() => battleStore.turnPhase === 'PLAY_CARD' && battleStore.isMyTurn)
const canStartSac = computed(() => {
  if (!selectedCard.value) return false
  const needed = selectedCard.value.bloodCost
  const onBoard = battleStore.playerSlots.filter(s => !s.isEmpty && canBeSac(s)).length
  return onBoard >= needed
})
const selectedItemName = computed(() => {
  if (pendingItemIdx.value === null) return ''
  return battleStore.playerItems[pendingItemIdx.value] || ''
})

const opponentItems = computed(() => battleStore.board?.opponentItems || [])
const squirrelImg = computed(() => getCardImage('松鼠'))
const wolfImg = computed(() => getCardImage('狼'))

const phaseLabel = computed(() => {
  const p = battleStore.turnPhase
  if (p === 'DRAW') return '抽牌阶段'
  if (p === 'PLAY_CARD') return '出牌阶段'
  if (p === 'END_TURN') return '结算中'
  return p || '-'
})

function getImg(card) {
  if (!card?.name) return null
  return getCardImage(card.name)
}

function canBeSac(slot) {
  return slot?.card?.canSacrifice !== false
}
function isSacTarget(idx) {
  return sacSelecting.value && sacrificeSlots.value.includes(idx)
}
function canPlayToSlot(idx) {
  if (!isPlayPhase.value || !battleStore.isMyTurn || selectedHandIdx.value === null) return false
  const slot = battleStore.playerSlots[idx]
  if (!slot || !slot.isEmpty) return false
  const card = selectedCard.value
  if (!card) return false
  if (card.bloodCost > 0) {
    return sacrificeSlots.value.length === card.bloodCost && !sacSelecting.value
  }
  if (card.boneCost > 0) {
    return battleStore.playerBones >= card.boneCost
  }
  return true
}

function onHandCardClick(idx) {
  if (!isPlayPhase.value) {
    battleStore.message = '当前不是出牌阶段'
    setTimeout(() => battleStore.message = '', 1500)
    return
  }
  if (!battleStore.isMyTurn) {
    battleStore.message = '当前不是你的回合'
    setTimeout(() => battleStore.message = '', 1500)
    return
  }
  if (selectedHandIdx.value === idx) {
    selectedHandIdx.value = null
    cancelSacrifice()
    return
  }
  selectedHandIdx.value = idx
  sacrificeSlots.value = []
  sacSelecting.value = false
}

function onPlayerSlotClick(idx) {
  const slot = battleStore.playerSlots[idx]

  if (sacSelecting.value) {
    if (!slot || slot.isEmpty) return
    if (!canBeSac(slot)) {
      battleStore.message = '该卡牌不可被献祭'
      setTimeout(() => battleStore.message = '', 1500)
      return
    }
    const pos = sacrificeSlots.value.indexOf(idx)
    if (pos >= 0) {
      sacrificeSlots.value.splice(pos, 1)
    } else {
      const needed = selectedCard.value?.bloodCost || 0
      if (sacrificeSlots.value.length < needed) {
        sacrificeSlots.value.push(idx)
      }
    }
    if (sacrificeSlots.value.length === needed) {
      sacSelecting.value = false
    }
    return
  }

  // 非献祭模式：点击有卡的格位 -> 预览卡牌
  if (slot && !slot.isEmpty) {
    previewCard(slot.card)
    return
  }

  if (canPlayToSlot(idx)) {
    doPlayCard(idx)
  }
}

function startSacrifice() {
  if (!selectedCard.value || selectedCard.value.bloodCost <= 0) return
  if (sacSelecting.value) {
    cancelSacrifice()
    return
  }
  sacSelecting.value = true
  sacrificeSlots.value = []
  battleStore.message = `请从场上选择 ${selectedCard.value.bloodCost} 张卡牌进行献祭`
  setTimeout(() => battleStore.message = '', 2000)
}
function cancelSacrifice() {
  sacSelecting.value = false
  sacrificeSlots.value = []
}

async function playBoneCard() {
  if (selectedHandIdx.value === null || !selectedCard.value) return
  if (battleStore.playerBones < selectedCard.value.boneCost) {
    battleStore.message = '骨头不足'
    setTimeout(() => battleStore.message = '', 1500)
    return
  }
  battleStore.message = '请点击一个空位出牌'
  setTimeout(() => battleStore.message = '', 2000)
}

async function doPlayCard(slotIndex) {
  try {
    await battleStore.playCard(
      selectedHandIdx.value,
      slotIndex,
      sacrificeSlots.value
    )
    selectedHandIdx.value = null
    sacrificeSlots.value = []
    sacSelecting.value = false
    battleStore.message = '出牌成功'
    setTimeout(() => battleStore.message = '', 1200)
  } catch (e) {
    battleStore.message = e.message || '出牌失败'
    setTimeout(() => battleStore.message = '', 2500)
  }
}

async function doDraw(drawType) {
  showDrawDialog.value = false
  try {
    await battleStore.drawCard(drawType)
    battleStore.message = drawType === 'SQUIRREL' ? '抽到松鼠牌' : '从牌组抽牌'
    setTimeout(() => battleStore.message = '', 1200)
  } catch (e) {
    battleStore.message = e.message || '抽牌失败'
    setTimeout(() => battleStore.message = '', 2500)
  }
}

async function handleEndTurn() {
  selectedHandIdx.value = null
  cancelSacrifice()
  try {
    const res = await battleStore.endTurn()
    // PvE: 优先显示 AI 行动日志
    if (battleStore.aiActions.length > 0) {
      showAiLog.value = true
    } else if (res.attacks?.length) {
      showAttackLog.value = true
    }
  } catch (e) {
    battleStore.message = e.message || '结束回合失败'
    setTimeout(() => battleStore.message = '', 2500)
  }
}

function handleSurrender() {
  showSurrenderConfirm.value = true
}
async function confirmSurrender() {
  showSurrenderConfirm.value = false
  try {
    await battleStore.doSurrender()
  } catch (e) {
    battleStore.message = e.message || '投降失败'
    setTimeout(() => battleStore.message = '', 2000)
  }
}

function onUseItem(idx) {
  pendingItemIdx.value = idx
  showItemConfirm.value = true
}
async function confirmUseItem() {
  showItemConfirm.value = false
  try {
    await battleStore.useItem(pendingItemIdx.value)
    battleStore.message = '道具使用成功'
    setTimeout(() => battleStore.message = '', 1200)
  } catch (e) {
    battleStore.message = e.message || '道具使用失败'
    setTimeout(() => battleStore.message = '', 2000)
  }
  pendingItemIdx.value = null
}

function previewCard(card) {
  previewCardData.value = card
  previewVisible.value = true
}

async function handleUseSkill() {
  try {
    await battleStore.useSkill(0)
    battleStore.message = '主动技能使用成功：己方场上卡牌恢复1点血量'
    setTimeout(() => battleStore.message = '', 2000)
  } catch (e) {
    battleStore.message = e.message || '技能使用失败'
    setTimeout(() => battleStore.message = '', 2000)
  }
}

function aiActionIcon(type) {
  const icons = {
    'DRAW': '🃏',
    'PLAY_CARD': '▶️',
    'SACRIFICE': '💀',
    'USE_ITEM': '🎒',
    'ATTACK': '⚔️'
  }
  return icons[type] || '•'
}

function closeAiLog() {
  showAiLog.value = false
  battleStore.aiActions = []
}

function goToResult() {
  router.push('/battle/result')
}

onMounted(async () => {
  // PvP 模式：从 query 读取 sessionId
  if (route.query.mode === 'PVP' && route.query.sessionId) {
    try {
      await battleStore.attachSession(Number(route.query.sessionId), {
        mode: 'PVP',
        opponentName: route.query.opponentName || '对手',
        opponentCharacterName: route.query.opponentCharacterName || ''
      })
    } catch (e) {
      battleStore.message = '加载对战失败：' + (e.message || '未知错误')
    }
    return
  }
  // PvE 模式：来自关卡选择
  if (route.query.levelId && route.query.deckId && !battleStore.sessionId) {
    try {
      const res = await battleStore.startBattle({
        mode: 'PVE',
        levelId: Number(route.query.levelId),
        deckId: Number(route.query.deckId)
      })
      // AI 先手时显示首回合行动日志
      if (res && res.aiFirstTurnActions?.length > 0) {
        showAiLog.value = true
      }
    } catch (e) {
      battleStore.message = e.message || '开始对战失败'
    }
  }
})

watch(() => battleStore.turnPhase, (phase) => {
  // 抽牌弹窗：仅在“抽牌阶段”且是调用者的回合时显示
  if (phase === 'DRAW' && battleStore.sessionId && !battleStore.gameOver && battleStore.isMyTurn) {
    showDrawDialog.value = true
  } else {
    showDrawDialog.value = false
  }
}, { immediate: true })

watch(() => battleStore.lastAttacks, (arr) => {
  if (arr && arr.length) showAttackLog.value = true
})

onUnmounted(() => {
  battleStore.stopPolling()
  window.removeEventListener('beforeunload', handleBeforeUnload)
})

// PvP 模式：离开页面自动投降
function autoSurrenderOnLeave() {
  if (
    battleStore.mode === 'PVP' &&
    battleStore.sessionId &&
    !battleStore.gameOver
  ) {
    // 使用 navigator.sendBeacon 确保在页面卸载时仍能发出请求
    const token = localStorage.getItem('token') || ''
    const url = '/api/battle/surrender'
    const body = JSON.stringify({ sessionId: battleStore.sessionId })
    // 使用 fetch + keepalive 确保在页面卸载时仍能发出请求
    fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: body,
      keepalive: true
    }).catch(() => {})
  }
}

// 路由内导航离开（点击链接、router.push 等）
onBeforeRouteLeave((to, from, next) => {
  autoSurrenderOnLeave()
  next()
})

// 页面关闭 / 刷新 / 跳转其他网站
window.addEventListener('beforeunload', handleBeforeUnload)
function handleBeforeUnload() {
  autoSurrenderOnLeave()
}
</script>

<style scoped>
.battle-view {
  min-height: 100vh;
  background: transparent;
  position: relative;
  overflow-x: hidden;
  padding: 16px;
  display: flex;
  flex-direction: column;
}

.bg-decor { position: fixed; inset: 0; pointer-events: none; z-index: 0; display: none; }
.bg-icon { position: absolute; font-size: 6rem; opacity: 0.04; animation: float 20s ease-in-out infinite; }
.bg-icon.i1 { top: 8%; left: 5%; }
.bg-icon.i2 { top: 20%; right: 8%; animation-delay: -6s; }
.bg-icon.i3 { bottom: 15%; left: 10%; animation-delay: -12s; }
.bg-icon.i4 { bottom: 25%; right: 5%; animation-delay: -16s; }
@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0); }
  50% { transform: translateY(-20px) rotate(6deg); }
}

.init-screen {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #888;
  z-index: 1;
}
.init-screen .spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #0f3460;
  border-top-color: #e94560;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}
@keyframes spin { to { transform: rotate(360deg); } }

.battle-wrap {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 220px 1fr 220px;
  gap: 14px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.side-panel {
  background: rgba(22, 33, 62, 0.7);
  border: 1px solid rgba(255, 215, 0, 0.15);
  border-radius: 12px;
  padding: 14px;
  backdrop-filter: blur(8px);
  display: flex;
  flex-direction: column;
  gap: 12px;
  height: fit-content;
}
.side-title {
  font-size: 0.9rem;
  color: #ffd700;
  letter-spacing: 1px;
  border-bottom: 1px solid #0f3460;
  padding-bottom: 6px;
}
.side-name { color: #e0e0e0; font-size: 1rem; font-weight: 600; }
.side-sub { color: #888; font-size: 0.82rem; }
.side-label {
  color: #a78bfa;
  font-size: 0.8rem;
  margin-top: 4px;
  letter-spacing: 0.5px;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 6px;
}
.stat {
  background: #0d1b2a;
  border: 1px solid #0f3460;
  border-radius: 8px;
  padding: 6px 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}
.stat.hp { border-color: #c93050; }
.stat.hp.low { background: rgba(220, 53, 69, 0.15); border-color: #dc3545; }
.stat-icon { font-size: 0.95rem; }
.stat-val { color: #e0e0e0; font-size: 0.95rem; font-weight: 600; }

.item-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  min-height: 24px;
}
.empty-hint { color: #555; font-size: 0.78rem; }
.item-tag {
  background: #0f3460;
  color: #e0e0e0;
  font-size: 0.78rem;
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid #0f3460;
}
.item-tag.clickable {
  cursor: pointer;
  transition: all 0.2s;
}
.item-tag.clickable:hover {
  background: #e94560;
  border-color: #e94560;
  color: white;
}

.action-col {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.board-area {
  border: 2px solid rgba(255, 215, 0, 0.3);
  border-radius: 16px;
  position: relative;
  overflow: hidden;
  background: #0a1a12;
  aspect-ratio: 4 / 3;
}

.desk-bg {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  pointer-events: none;
  z-index: 0;
}

/* 牌桌内容层 - 所有子元素绝对定位 */
.board-area > *:not(.desk-bg) {
  position: relative;
  z-index: 1;
}

/* 对手卡槽行 - 贴合背景图上方矩形区域 */
.opponent-row {
  position: absolute;
  top: 12%;
  left: 15%;
  right: 15%;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2.5%;
  z-index: 2;
}

/* 我方卡槽行 - 贴合背景图下方矩形区域 */
.player-row {
  position: absolute;
  bottom: 12%;
  left: 15%;
  right: 15%;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2.5%;
  z-index: 2;
}

/* 回合信息 - 顶部居中浮层 */
.turn-bar {
  position: absolute;
  top: 2%;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  align-items: center;
  color: #e0e0e0;
  font-size: 0.8rem;
  padding: 4px 14px;
  background: rgba(0, 0, 0, 0.65);
  border-radius: 20px;
  backdrop-filter: blur(6px);
  z-index: 3;
  white-space: nowrap;
}

/* 操作按钮 - 底部居中浮层 */
.action-bar {
  position: absolute;
  bottom: 2%;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  background: rgba(0, 0, 0, 0.65);
  border-radius: 20px;
  padding: 6px 16px;
  backdrop-filter: blur(6px);
  z-index: 3;
  white-space: nowrap;
}

/* PvP 等待提示 - 居中浮层 */
.opponent-turn-banner {
  position: absolute;
  top: 46%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 20px;
  background: rgba(233, 69, 96, 0.35);
  border: 1px solid rgba(233, 69, 96, 0.6);
  border-radius: 12px;
  color: #ff8a9e;
  font-size: 0.95rem;
  backdrop-filter: blur(6px);
  z-index: 4;
  white-space: nowrap;
}
.phase-tag {
  background: #e94560;
  color: white;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 0.78rem;
}
.mode-tag {
  background: #0f3460;
  color: #ffd700;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 0.78rem;
}

.slots-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2.5%;
}
.slot {
  aspect-ratio: 3 / 4;
  background: transparent;
  border: 2px solid transparent;
  border-radius: 8px;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}
.slot.filled {
  border-color: rgba(255, 215, 0, 0.4);
  background: rgba(0, 0, 0, 0.15);
  border-radius: 8px;
}
.slot.opponent-slot:not(.filled) {
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(0, 0, 0, 0.1);
}
.slot.player-slot.empty.can-play {
  border-color: #ffd700;
  background: rgba(255, 215, 0, 0.12);
  cursor: pointer;
  animation: pulse 1.2s ease-in-out infinite;
}
@keyframes pulse {
  0%, 100% { box-shadow: 0 0 0 0 rgba(255,215,0,0.3); }
  50% { box-shadow: 0 0 0 6px rgba(255,215,0,0); }
}
.slot.player-slot.sac-target {
  border-color: #dc3545;
  background: rgba(220, 53, 69, 0.2);
  box-shadow: 0 0 12px rgba(220, 53, 69, 0.3);
}
.slot.player-slot.sac-selectable {
  border-color: #ffd700;
  background: rgba(255, 215, 0, 0.1);
  cursor: pointer;
  animation: sacGlow 1s ease-in-out infinite;
}
@keyframes sacGlow {
  0%, 100% { box-shadow: 0 0 4px rgba(255,215,0,0.3); }
  50% { box-shadow: 0 0 14px rgba(255,215,0,0.5); }
}
.slot.player-slot.sac-selectable:hover {
  border-color: #fff;
  transform: translateY(-3px);
  box-shadow: 0 0 18px rgba(255, 215, 0, 0.5);
}
.slot-empty {
  color: rgba(255,255,255,0.15);
  font-size: 1.2rem;
}
.play-hint {
  color: #ffd700;
  font-size: 0.8rem;
  letter-spacing: 1px;
  text-shadow: 0 0 6px rgba(255,215,0,0.5);
}

.card-on-board {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
}
.card-on-board:hover .card-preview-overlay {
  opacity: 1;
}
.card-preview-overlay {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 22px;
  height: 22px;
  background: rgba(0, 0, 0, 0.7);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.65rem;
  opacity: 0;
  transition: opacity 0.2s;
  cursor: pointer;
  z-index: 5;
}
.card-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  border-radius: 6px;
}
.card-ph {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(13,27,42,0.6), rgba(22,33,62,0.6));
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.5rem;
  opacity: 0.5;
  border-radius: 6px;
}
.card-ph.small { font-size: 1.5rem; }
.hp-badge {
  position: absolute;
  bottom: 2px;
  right: 2px;
  background: rgba(0, 0, 0, 0.75);
  color: #ff6b6b;
  font-size: 0.72rem;
  padding: 1px 5px;
  border-radius: 8px;
  border: 1px solid #dc3545;
  font-weight: bold;
}
.sigil-badges {
  position: absolute;
  top: 2px;
  left: 2px;
  display: flex;
  flex-wrap: wrap;
  gap: 1px;
  max-width: 90%;
}
.sigil-tag {
  background: rgba(167, 139, 250, 0.85);
  color: #fff;
  font-size: 0.58rem;
  padding: 0px 3px;
  border-radius: 3px;
  line-height: 1.3;
  white-space: nowrap;
}

.attack-log {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(13, 27, 42, 0.98);
  border: 2px solid #e94560;
  border-radius: 12px;
  padding: 16px 20px;
  min-width: 280px;
  max-width: 420px;
  z-index: 10;
  backdrop-filter: blur(10px);
}
.log-title {
  color: #ffd700;
  font-size: 1rem;
  margin-bottom: 8px;
  text-align: center;
}
.log-line {
  color: #e0e0e0;
  font-size: 0.82rem;
  padding: 3px 0;
  border-bottom: 1px solid rgba(15,52,96,0.5);
}
.tag-kill, .tag-dead {
  display: inline-block;
  font-size: 0.7rem;
  padding: 1px 6px;
  border-radius: 4px;
  margin-left: 4px;
}
.tag-kill { background: #2a5a3a; color: #90ee90; }
.tag-dead { background: #dc3545; color: white; }

.hand-area {
  position: relative;
  z-index: 1;
  max-width: 1400px;
  margin: 14px auto 0;
  width: 100%;
  background: rgba(22, 33, 62, 0.75);
  border: 1px solid rgba(255, 215, 0, 0.15);
  border-radius: 12px;
  padding: 10px 14px;
  backdrop-filter: blur(8px);
}
.hand-title {
  color: #ffd700;
  font-size: 0.85rem;
  margin-bottom: 6px;
}
.hand-scroll {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 6px;
}
.hand-scroll::-webkit-scrollbar { height: 6px; }
.hand-scroll::-webkit-scrollbar-thumb { background: #0f3460; border-radius: 3px; }
.hand-empty { color: #555; padding: 20px; }

.hand-card {
  flex-shrink: 0;
  width: 110px;
  background: #0d1b2a;
  border: 2px solid #0f3460;
  border-radius: 10px;
  padding: 6px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.hand-card:hover { border-color: #e94560; transform: translateY(-3px); }
.hand-card.selected {
  border-color: #ffd700;
  box-shadow: 0 0 0 3px rgba(255, 215, 0, 0.3);
  transform: translateY(-6px);
}
.hand-card.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.hc-img {
  width: 100%;
  aspect-ratio: 3 / 4;
  background: #1a1a2e;
  border-radius: 6px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.hc-img img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
.hc-info { padding: 2px 2px 0; }
.hc-name { color: #ffd700; font-size: 0.78rem; line-height: 1.2; }
.hc-costs { display: flex; gap: 4px; font-size: 0.72rem; margin-top: 2px; }
.cost.blood { color: #ff6b6b; display: inline-flex; align-items: center; gap: 2px; }
.cost.bone { color: #ffa07a; }
.cost.free { color: #90ee90; }
.icon-sm { width: 12px; height: 12px; object-fit: contain; vertical-align: middle; }
.preview-btn {
  background: transparent;
  border: 1px solid #0f3460;
  color: #888;
  font-size: 0.68rem;
  padding: 2px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 2px;
}
.preview-btn:hover { color: #ffd700; border-color: #ffd700; }

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.75);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s;
}
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.modal-card {
  background: #1a1a2e;
  border: 2px solid #ffd700;
  border-radius: 16px;
  padding: 24px;
  max-width: 480px;
  width: 90%;
  text-align: center;
  animation: scaleIn 0.25s;
}
@keyframes scaleIn { from { transform: scale(0.9); opacity: 0; } to { transform: scale(1); opacity: 1; } }

.modal-card h3 { color: #ffd700; margin: 0 0 8px; }
.modal-card p { color: #ccc; margin: 0 0 16px; }
.modal-card p.sub { color: #888; font-size: 0.85rem; }
.modal-card b { color: #ffd700; }

.draw-options {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  margin-top: 12px;
}
.draw-opt {
  background: #16213e;
  border: 2px solid #0f3460;
  border-radius: 12px;
  padding: 14px;
  cursor: pointer;
  transition: all 0.2s;
  color: #e0e0e0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}
.draw-opt:hover { border-color: #e94560; transform: translateY(-3px); }
.opt-img {
  width: 70px;
  height: 90px;
  background: #0d1b2a;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
}
.opt-img img { width: 100%; height: 100%; object-fit: contain; }
.opt-name { color: #ffd700; font-size: 0.95rem; font-weight: 600; }
.opt-desc { color: #888; font-size: 0.75rem; }

.confirm-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-top: 8px;
}

.game-over h2 {
  font-size: 2rem;
  margin: 0 0 12px;
}
.game-over h2.WIN { color: #ffd700; }
.game-over h2.LOSE, .game-over h2.SURRENDER { color: #dc3545; }
.go-stats {
  color: #ccc;
  font-size: 0.95rem;
  line-height: 1.8;
  margin-bottom: 14px;
}

.ai-log-dialog {
  max-width: 520px;
}
.ai-log-list {
  max-height: 300px;
  overflow-y: auto;
  margin: 10px 0;
  text-align: left;
}
.ai-log-item {
  display: flex;
  gap: 8px;
  padding: 6px 8px;
  border-bottom: 1px solid rgba(15, 52, 96, 0.5);
  font-size: 0.85rem;
}
.ai-log-type {
  flex-shrink: 0;
  font-size: 1rem;
}
.ai-log-detail {
  color: #e0e0e0;
  line-height: 1.4;
}
.ai-log-empty {
  color: #555;
  text-align: center;
  padding: 20px;
  font-size: 0.85rem;
}
.ai-log-list::-webkit-scrollbar { width: 5px; }
.ai-log-list::-webkit-scrollbar-thumb { background: #0f3460; border-radius: 3px; }

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  color: white;
  cursor: pointer;
  font-size: 0.88rem;
  transition: all 0.2s;
  background: #0f3460;
}
.btn:hover:not(:disabled) { opacity: 0.85; transform: translateY(-1px); }
.btn:disabled { opacity: 0.4; cursor: not-allowed; }
.btn-primary { background: linear-gradient(135deg, #e94560, #c93050); }
.btn-danger { background: #dc3545; }
.btn-ghost { background: transparent; border: 1px solid #0f3460; color: #ccc; }
.btn-ghost:hover { border-color: #ffd700; color: #ffd700; }
.btn-sac { background: linear-gradient(135deg, #8b0000, #5c0000); }
.btn-bone { background: linear-gradient(135deg, #8b5e3c, #5c3d26); }
.btn-skill { background: linear-gradient(135deg, #6a5acd, #483d8b); }

.otb-spinner {
  width: 18px;
  height: 18px;
  border: 2px solid #e94560;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.toast {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(22, 33, 62, 0.95);
  border: 1px solid #ffd700;
  color: #ffd700;
  padding: 8px 18px;
  border-radius: 20px;
  font-size: 0.88rem;
  z-index: 2000;
  box-shadow: 0 4px 16px rgba(0,0,0,0.4);
}
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
.slide-enter-active, .slide-leave-active { transition: all 0.3s; }
.slide-enter-from { transform: translate(-50%, -20px); opacity: 0; }
.slide-leave-to { transform: translate(-50%, -20px); opacity: 0; }

@media (max-width: 960px) {
  .battle-wrap { grid-template-columns: 1fr; }
  .side-panel { flex-direction: row; flex-wrap: wrap; }
  .stat-grid { flex: 1; min-width: 200px; }
}
</style>
