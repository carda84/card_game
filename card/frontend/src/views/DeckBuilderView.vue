<template>
  <div class="deck-page">
    <NavBar />
    <main class="main-content">
      <div class="bg-decor">
        <span class="bg-icon i1">📋</span>
        <span class="bg-icon i2">🃏</span>
      </div>

      <div class="page-wrap">
        <!-- 顶部标题栏 -->
        <div class="top-bar">
          <div>
            <h2>📋 卡组管理</h2>
            <p class="subtitle">
              已有 {{ deckStore.decks.length }}/20 个卡组
              <span v-if="filterCharId"> · 筛选：{{ filterCharName }}</span>
            </p>
          </div>
          <div class="top-actions">
            <button
              class="btn btn-primary"
              :disabled="deckStore.decks.length >= 20"
              @click="showCreateDialog = true"
            >+ 创建卡组</button>
            <router-link to="/battle/prepare" class="btn btn-ghost">返回</router-link>
          </div>
        </div>

        <div v-if="deckStore.loading" class="loading">
          <div class="spinner"></div>
          <p>加载卡组中...</p>
        </div>

        <div v-else class="deck-layout">
          <!-- 左列：卡组列表 -->
          <div class="deck-list-col">
            <div v-if="!filteredDecks.length" class="empty-hint">
              <p>暂无卡组</p>
              <p class="small">点击上方按钮创建第一个卡组</p>
            </div>
            <div
              v-for="deck in filteredDecks"
              :key="deck.id"
              class="deck-card"
              :class="{ active: selectedDeckId === deck.id, valid: deck.isValid, invalid: !deck.isValid }"
              @click="selectDeck(deck.id)"
            >
              <div class="dc-header">
                <h3>{{ deck.name }}</h3>
                <div class="dc-badges">
                  <span v-if="deck.isValid" class="badge valid-badge">✓</span>
                  <span v-else class="badge invalid-badge">✗</span>
                </div>
              </div>
              <div class="dc-meta">
                <span>{{ deck.characterName }}</span>
                <span>{{ deck.cardCount }}/{{ deck.maxCardCount }} 张</span>
              </div>
              <div v-if="deck.validationMessage" class="dc-warn">{{ deck.validationMessage }}</div>
              <div class="dc-actions" @click.stop>
                <button class="btn-icon" title="编辑卡组" @click="selectDeck(deck.id)">✏️</button>
                <button class="btn-icon" title="重命名" @click="openRename(deck)">✍️</button>
                <button class="btn-icon btn-danger-icon" title="删除" @click="openDeleteConfirm(deck)">🗑️</button>
              </div>
            </div>
          </div>

          <!-- 右列：卡组编辑器 -->
          <div class="editor-col">
            <div v-if="!deckStore.currentDeck" class="empty-hint">
              <p>← 选择左侧卡组进行编辑</p>
              <p class="small">或创建新卡组</p>
            </div>

            <div v-else class="editor">
              <div class="editor-header">
                <h3>{{ deckStore.currentDeck.name }}</h3>
                <div class="header-badges">
                  <span class="count-badge">{{ deckStore.currentDeck.cardCount }}/{{ deckStore.currentDeck.maxCardCount }}</span>
                  <span class="count-badge legendary-badge" :class="{ 'legendary-full': legendaryCountInDeck() >= 3 }">
                    传奇 {{ legendaryCountInDeck() }}/3
                  </span>
                </div>
              </div>

              <!-- 卡组中的卡牌 -->
              <div class="section-title">卡组中的卡牌</div>
              <div v-if="!deckStore.currentDeck.cards?.length" class="mini-empty">卡组为空，请添加卡牌</div>
              <div v-else class="card-grid compact">
                <div
                  v-for="(card, idx) in deckStore.currentDeck.cards"
                  :key="'in-' + card.id + '-' + idx"
                  class="mini-card"
                  @click="previewCard = card; showPreview = true"
                >
                  <div class="mc-img">
                    <img v-if="getImg(card.name)" :src="getImg(card.name)" />
                    <span v-else class="mc-ph">🃏</span>
                  </div>
                  <div class="mc-info">
                    <div class="mc-name">{{ card.name }}</div>
                    <div class="mc-stats">
                      <span>❤️{{ card.health }}</span>
                      <span>⚔️{{ card.attack ?? '-' }}</span>
                    </div>
                  </div>
                  <button
                    class="remove-btn"
                    title="移除"
                    @click.stop="doRemoveCard(card.id)"
                  >×</button>
                </div>
              </div>

              <!-- 分隔 -->
              <div class="divider"></div>

              <!-- 可添加的卡牌 -->
              <div class="section-title">已拥有的卡牌 <span class="hint-text">（点击图片添加到卡组）</span></div>
              <div v-if="!deckStore.ownedCards.length" class="mini-empty">暂无卡牌，请在商店购买</div>
              <div v-else class="card-grid compact">
                <div
                  v-for="card in deckStore.ownedCards"
                  :key="'own-' + card.id"
                  class="mini-card own-card"
                  :class="{ disabled: !canAddCard(card) }"
                >
                  <div
                    class="mc-img clickable-img"
                    :title="canAddCard(card) ? '点击添加到卡组' : '已达上限'"
                    @click="canAddCard(card) && doAddCard(card.id)"
                  >
                    <img v-if="getImg(card.name)" :src="getImg(card.name)" />
                    <span v-else class="mc-ph">🃏</span>
                    <span v-if="countInDeck(card.id) > 0" class="img-count-badge">{{ countInDeck(card.id) }}</span>
                  </div>
                  <div class="mc-info" @click="previewCard = card; showPreview = true">
                    <div class="mc-name">{{ card.name }}</div>
                    <div class="mc-stats">
                      <span>❤️{{ card.health }}</span>
                      <span>⚔️{{ card.attack ?? '-' }}</span>
                    </div>
                    <div class="mc-extra">
                      <span v-if="card.isLegendary" class="legendary-tag">传奇</span>
                      <span class="count-tag">{{ countInDeck(card.id) }}/{{ card.maxDeckCount || '∞' }}</span>
                    </div>
                  </div>
                  <button
                    class="add-btn"
                    :disabled="!canAddCard(card)"
                    title="添加到卡组"
                    @click.stop="doAddCard(card.id)"
                  >+</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 卡牌预览弹窗 -->
    <CardImageModal v-model="showPreview" :card="previewCard" :card-name="previewCard?.name" />

    <!-- 创建卡组弹窗 -->
    <Teleport to="body">
      <div v-if="showCreateDialog" class="modal-overlay" @click.self="showCreateDialog = false">
        <div class="modal-card">
          <h3>创建新卡组</h3>
          <div class="form-group">
            <label>卡组名称</label>
            <input v-model="newDeckName" class="form-input" placeholder="输入卡组名称" maxlength="100" />
          </div>
          <div class="form-group">
            <label>关联人物</label>
            <select v-model="newDeckCharId" class="form-input">
              <option :value="null" disabled>选择人物</option>
              <option v-for="c in charStore.characters" :key="c.id" :value="c.id">
                {{ c.name }} (卡组 {{ c.deckSize }} 张)
              </option>
            </select>
          </div>
          <div class="form-actions">
            <button class="btn btn-primary" :disabled="!newDeckName || !newDeckCharId" @click="doCreateDeck">创建</button>
            <button class="btn btn-ghost" @click="showCreateDialog = false">取消</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 重命名弹窗 -->
    <Teleport to="body">
      <div v-if="showRenameDialog" class="modal-overlay" @click.self="showRenameDialog = false">
        <div class="modal-card">
          <h3>重命名卡组</h3>
          <div class="form-group">
            <label>新名称</label>
            <input v-model="renameName" class="form-input" placeholder="输入新名称" maxlength="100" @keyup.enter="doRename" />
          </div>
          <div class="form-actions">
            <button class="btn btn-primary" :disabled="!renameName" @click="doRename">确认</button>
            <button class="btn btn-ghost" @click="showRenameDialog = false">取消</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 删除确认弹窗 -->
    <Teleport to="body">
      <div v-if="showDeleteConfirm" class="modal-overlay" @click.self="showDeleteConfirm = false">
        <div class="modal-card">
          <h3>确认删除</h3>
          <p>确定要删除卡组「<b>{{ deleteTarget?.name }}</b>」吗？此操作不可撤销。</p>
          <div class="form-actions">
            <button class="btn btn-danger" @click="doDeleteDeck">删除</button>
            <button class="btn btn-ghost" @click="showDeleteConfirm = false">取消</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 提示条 -->
    <Transition name="slide">
      <div v-if="toastMsg" class="toast">{{ toastMsg }}</div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useDeckStore } from '../store/deck'
import { useCharacterStore } from '../store/character'
import NavBar from '../components/common/NavBar.vue'
import CardImageModal from '../components/card/CardImageModal.vue'
import { getCardImage } from '../utils/cardImages'

const route = useRoute()
const deckStore = useDeckStore()
const charStore = useCharacterStore()

const selectedDeckId = ref(null)
const showCreateDialog = ref(false)
const showRenameDialog = ref(false)
const showDeleteConfirm = ref(false)
const showPreview = ref(false)
const previewCard = ref(null)
const newDeckName = ref('')
const newDeckCharId = ref(null)
const renameName = ref('')
const renameTarget = ref(null)
const deleteTarget = ref(null)
const toastMsg = ref('')

const filterCharId = computed(() => route.query.characterId ? Number(route.query.characterId) : null)
const filterCharName = computed(() => {
  if (!filterCharId.value) return ''
  const c = charStore.characters.find(ch => ch.id === filterCharId.value)
  return c?.name || ''
})
const filteredDecks = computed(() => {
  if (!filterCharId.value) return deckStore.decks
  return deckStore.decks.filter(d => d.characterId === filterCharId.value)
})

onMounted(async () => {
  await charStore.fetchCharacters()
  await deckStore.fetchDecks()
  // 如果从人物选择页跳转过来，预选人物
  if (filterCharId.value) {
    newDeckCharId.value = filterCharId.value
  }
  // 如果只有一个卡组，自动选中
  if (filteredDecks.value.length === 1) {
    selectDeck(filteredDecks.value[0].id)
  }
})

watch(showCreateDialog, (v) => {
  if (v) {
    newDeckName.value = ''
    if (filterCharId.value) newDeckCharId.value = filterCharId.value
  }
})

function getImg(name) { return name ? getCardImage(name) : null }
function showToast(msg) {
  toastMsg.value = msg
  setTimeout(() => toastMsg.value = '', 2000)
}

async function selectDeck(deckId) {
  selectedDeckId.value = deckId
  try {
    await deckStore.loadDeckDetail(deckId)
    await deckStore.fetchOwnedCards()
  } catch (e) {
    showToast('加载卡组失败: ' + (e.message || e))
  }
}

function countInDeck(cardId) {
  if (!deckStore.currentDeck?.cards) return 0
  return deckStore.currentDeck.cards.filter(c => c.id === cardId).length
}

function legendaryCountInDeck() {
  if (!deckStore.currentDeck?.cards) return 0
  return deckStore.currentDeck.cards.filter(c => c.isLegendary).length
}

function canAddCard(card) {
  if (!deckStore.currentDeck) return false
  if (deckStore.currentDeck.cardCount >= deckStore.currentDeck.maxCardCount) return false
  const max = card.maxDeckCount
  if (max && max > 0 && countInDeck(card.id) >= max) return false
  if (card.isLegendary && legendaryCountInDeck() >= 3) return false
  return true
}

async function doAddCard(cardId) {
  if (!deckStore.currentDeck) return
  try {
    await deckStore.addCard(deckStore.currentDeck.id, cardId)
    showToast('已添加')
  } catch (e) {
    showToast(e.message || '添加失败')
  }
}

async function doRemoveCard(cardId) {
  if (!deckStore.currentDeck) return
  try {
    await deckStore.removeCard(deckStore.currentDeck.id, cardId)
    showToast('已移除')
  } catch (e) {
    showToast(e.message || '移除失败')
  }
}

async function doCreateDeck() {
  if (!newDeckName.value || !newDeckCharId.value) return
  try {
    const deck = await deckStore.createNewDeck({ name: newDeckName.value, characterId: newDeckCharId.value })
    showCreateDialog.value = false
    selectDeck(deck.id)
    showToast('卡组创建成功')
  } catch (e) {
    showToast(e.message || '创建失败')
  }
}

function openRename(deck) {
  renameTarget.value = deck
  renameName.value = deck.name
  showRenameDialog.value = true
}
async function doRename() {
  if (!renameTarget.value || !renameName.value) return
  try {
    await deckStore.rename(renameTarget.value.id, renameName.value)
    showRenameDialog.value = false
    showToast('重命名成功')
  } catch (e) {
    showToast(e.message || '重命名失败')
  }
}

function openDeleteConfirm(deck) {
  deleteTarget.value = deck
  showDeleteConfirm.value = true
}
async function doDeleteDeck() {
  if (!deleteTarget.value) return
  try {
    await deckStore.removeDeck(deleteTarget.value.id)
    if (selectedDeckId.value === deleteTarget.value.id) {
      selectedDeckId.value = null
    }
    showDeleteConfirm.value = false
    showToast('已删除')
  } catch (e) {
    showToast(e.message || '删除失败')
  }
}
</script>

<style scoped>
.deck-page { min-height: 100vh; background: transparent; }
.main-content { position: relative; min-height: calc(100vh - 56px); z-index: 1; }
.bg-decor { position: fixed; inset: 0; pointer-events: none; z-index: 0; }
.bg-icon { position: absolute; font-size: 7rem; opacity: 0.04; animation: float 20s ease-in-out infinite; }
.bg-icon.i1 { top: 15%; left: 10%; }
.bg-icon.i2 { bottom: 20%; right: 12%; animation-delay: -10s; }
@keyframes float { 0%,100% { transform: translateY(0); } 50% { transform: translateY(-15px); } }

.page-wrap { position: relative; z-index: 1; padding: 1.5rem 2rem; max-width: 1300px; margin: 0 auto; }

.top-bar { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 12px; }
.top-bar h2 { color: #e0e0e0; margin: 0 0 4px; font-size: 1.5rem; }
.subtitle { color: #888; font-size: 0.85rem; margin: 0; }
.top-actions { display: flex; gap: 10px; align-items: center; }

.loading { text-align: center; padding: 60px; color: #888; }
.spinner { width: 40px; height: 40px; border: 4px solid #0f3460; border-top-color: #e94560; border-radius: 50%; animation: spin 1s linear infinite; margin: 0 auto 12px; }
@keyframes spin { to { transform: rotate(360deg); } }

.empty-hint { text-align: center; color: #666; padding: 40px 20px; }
.empty-hint .small { font-size: 0.82rem; color: #555; margin-top: 6px; }

/* ===== 两列布局 ===== */
.deck-layout { display: grid; grid-template-columns: 320px 1fr; gap: 1.5rem; min-height: 500px; }
@media (max-width: 900px) { .deck-layout { grid-template-columns: 1fr; } }

/* 左列 - 卡组列表 */
.deck-list-col { display: flex; flex-direction: column; gap: 10px; max-height: 70vh; overflow-y: auto; padding-right: 6px; }
.deck-list-col::-webkit-scrollbar { width: 4px; }
.deck-list-col::-webkit-scrollbar-thumb { background: #0f3460; border-radius: 4px; }

.deck-card {
  background: #16213e; border: 1px solid #0f3460; border-radius: 10px;
  padding: 14px 16px; cursor: pointer; transition: border-color 0.2s, box-shadow 0.2s;
}
.deck-card:hover { border-color: #e94560; }
.deck-card.active { border-color: #ffd700; box-shadow: 0 0 16px rgba(255,215,0,0.12); }
.deck-card.valid { border-left: 3px solid #28a745; }
.deck-card.invalid { border-left: 3px solid #dc3545; }

.dc-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.dc-header h3 { color: #e0e0e0; margin: 0; font-size: 1rem; }
.badge { width: 22px; height: 22px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 0.75rem; font-weight: bold; }
.valid-badge { background: #28a745; color: white; }
.invalid-badge { background: #dc3545; color: white; }
.dc-meta { display: flex; justify-content: space-between; color: #888; font-size: 0.82rem; }
.dc-warn { color: #ffa500; font-size: 0.78rem; margin-top: 4px; }
.dc-actions { display: flex; gap: 6px; margin-top: 8px; }
.btn-icon { background: none; border: 1px solid #0f3460; border-radius: 6px; padding: 4px 8px; cursor: pointer; font-size: 0.9rem; transition: all 0.2s; }
.btn-icon:hover { border-color: #ffd700; background: rgba(255,215,0,0.08); }
.btn-danger-icon:hover { border-color: #dc3545; background: rgba(220,53,69,0.08); }

/* 右列 - 编辑器 */
.editor-col { background: #0d1b2a; border: 1px solid #0f3460; border-radius: 12px; padding: 20px; max-height: 70vh; overflow-y: auto; }
.editor-col::-webkit-scrollbar { width: 4px; }
.editor-col::-webkit-scrollbar-thumb { background: #0f3460; border-radius: 4px; }

.editor-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.editor-header h3 { color: #ffd700; margin: 0; }
.count-badge { background: #16213e; border: 1px solid #0f3460; border-radius: 20px; padding: 4px 12px; color: #e0e0e0; font-size: 0.85rem; }
.count-badge.legendary-badge { border-color: #e94560; color: #e94560; }
.count-badge.legendary-full { background: #e94560; color: white; }
.header-badges { display: flex; gap: 8px; }

.section-title { color: #aaa; font-size: 0.88rem; margin: 12px 0 8px; padding-bottom: 4px; border-bottom: 1px solid #0f3460; }
.section-title .hint-text { color: #666; font-size: 0.75rem; }
.mini-empty { color: #555; font-size: 0.85rem; padding: 12px; text-align: center; }
.divider { height: 1px; background: #0f3460; margin: 16px 0; }

/* 卡牌迷你卡片 */
.card-grid.compact { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 8px; }
.mini-card {
  display: flex; align-items: center; gap: 8px;
  background: #16213e; border: 1px solid #0f3460; border-radius: 8px;
  padding: 8px 10px; cursor: pointer; transition: border-color 0.2s;
  position: relative;
}
.mini-card:hover { border-color: #e94560; }
.mini-card.disabled { opacity: 0.4; cursor: not-allowed; }
.mini-card.disabled:hover { border-color: #0f3460; }
.mini-card.disabled .mc-img.clickable-img { cursor: not-allowed; opacity: 0.4; }
.mini-card.disabled .mc-img.clickable-img:hover { transform: none; box-shadow: none; }

.mc-img { width: 40px; height: 52px; border-radius: 4px; overflow: hidden; background: #0d1b2a; flex-shrink: 0; display: flex; align-items: center; justify-content: center; }
.mc-img.clickable-img { cursor: pointer; transition: transform 0.15s, box-shadow 0.15s; }
.mc-img.clickable-img:hover { transform: scale(1.15); box-shadow: 0 0 8px rgba(233,69,96,0.5); }
.mc-img.clickable-img:active { transform: scale(0.95); }
.mc-img { position: relative; }
.img-count-badge {
  position: absolute; bottom: -2px; right: -2px;
  background: #e94560; color: white; font-size: 0.65rem;
  min-width: 16px; height: 16px; border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  font-weight: bold; padding: 0 3px; border: 1px solid #0d1b2a;
}
.mc-img img { width: 100%; height: 100%; object-fit: contain; }
.mc-ph { font-size: 1.4rem; opacity: 0.3; }
.mc-info { flex: 1; min-width: 0; }
.mc-name { color: #e0e0e0; font-size: 0.85rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.mc-stats { display: flex; gap: 8px; color: #888; font-size: 0.75rem; margin-top: 2px; }
.mc-extra { display: flex; gap: 6px; margin-top: 2px; }
.legendary-tag { background: #e94560; color: white; font-size: 0.65rem; padding: 1px 5px; border-radius: 3px; }
.count-tag { color: #666; font-size: 0.72rem; }

.remove-btn, .add-btn {
  flex-shrink: 0; width: 26px; height: 26px; border-radius: 50%;
  border: none; cursor: pointer; font-size: 1rem; font-weight: bold;
  display: flex; align-items: center; justify-content: center; transition: all 0.2s;
}
.remove-btn { background: #dc3545; color: white; }
.remove-btn:hover { background: #ff4757; transform: scale(1.1); }
.add-btn { background: #28a745; color: white; }
.add-btn:hover:not(:disabled) { background: #2ecc71; transform: scale(1.1); }
.add-btn:disabled { opacity: 0.3; cursor: not-allowed; }

/* ===== 弹窗 ===== */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.6); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-card { background: #16213e; border: 1px solid #0f3460; border-radius: 12px; padding: 24px; width: 90%; max-width: 420px; }
.modal-card h3 { color: #e0e0e0; margin: 0 0 16px; }
.modal-card p { color: #ccc; font-size: 0.9rem; line-height: 1.5; }

.form-group { margin-bottom: 14px; }
.form-group label { display: block; color: #888; font-size: 0.82rem; margin-bottom: 4px; }
.form-input {
  width: 100%; padding: 10px 12px; background: #0d1b2a; border: 1px solid #0f3460;
  border-radius: 6px; color: #e0e0e0; font-size: 0.9rem; box-sizing: border-box;
}
.form-input:focus { outline: none; border-color: #e94560; }
.form-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 20px; }

/* ===== 通用按钮 ===== */
.btn { padding: 8px 18px; border: none; border-radius: 6px; color: white; cursor: pointer; font-size: 0.88rem; text-decoration: none; display: inline-block; transition: all 0.2s; }
.btn:hover:not(:disabled) { opacity: 0.85; transform: translateY(-1px); }
.btn:disabled { opacity: 0.4; cursor: not-allowed; }
.btn-primary { background: linear-gradient(135deg, #e94560, #c93050); }
.btn-danger { background: #dc3545; }
.btn-ghost { background: transparent; border: 1px solid #0f3460; color: #ccc; }
.btn-ghost:hover { border-color: #ffd700; color: #ffd700; }

/* ===== 提示条 ===== */
.toast { position: fixed; bottom: 30px; left: 50%; transform: translateX(-50%); background: #16213e; border: 1px solid #0f3460; color: #ffd700; padding: 10px 24px; border-radius: 8px; font-size: 0.88rem; z-index: 2000; box-shadow: 0 4px 20px rgba(0,0,0,0.4); }
.slide-enter-active, .slide-leave-active { transition: all 0.3s ease; }
.slide-enter-from, .slide-leave-to { opacity: 0; transform: translate(-50%, 10px); }
</style>
