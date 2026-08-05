<template>
  <div class="char-select-page">
    <NavBar />
    <main class="page-content">
      <h2>👤 选择人物</h2>
      <p class="subtitle">选择你的对战角色，将自动跳转至卡组管理</p>

      <div v-if="charStore.loading" class="loading">
        <div class="spinner"></div>
        <p>加载中...</p>
      </div>

      <div v-else class="character-grid">
        <div
          v-for="char in charStore.characters"
          :key="char.id"
          class="character-card"
          :class="{ selected: charStore.selectedCharacter?.id === char.id }"
          @click="selectAndGo(char)"
        >
          <h3>{{ char.name }}</h3>
          <div class="char-stats">
            <span>❤️ {{ char.maxHp }}</span>
            <span>🃏 {{ char.deckSize }} 张</span>
          </div>
          <p class="desc">{{ char.specialAbilityDesc }}</p>
          <div class="char-badges">
            <span v-if="char.isDefault" class="badge-free">免费</span>
            <span v-else class="badge-price">💰{{ char.price }}</span>
          </div>
        </div>
      </div>

      <div class="back-row">
        <router-link to="/battle/prepare" class="btn btn-ghost">返回</router-link>
      </div>
    </main>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCharacterStore } from '../store/character'
import NavBar from '../components/common/NavBar.vue'

const charStore = useCharacterStore()
const router = useRouter()

onMounted(() => charStore.fetchCharacters())

function selectAndGo(char) {
  charStore.selectCharacter(char)
  router.push({ name: 'DeckBuilder', query: { characterId: char.id } })
}
</script>

<style scoped>
.char-select-page { min-height: 100vh; background: transparent; }
.page-content { padding: 2rem; max-width: 1000px; margin: 0 auto; }
.page-content h2 { color: var(--text-primary); margin-bottom: 4px; }
.subtitle { color: var(--text-muted); margin-bottom: 24px; font-size: 0.9rem; }

.loading { text-align: center; padding: 40px; color: var(--text-muted); }
.spinner {
  width: 40px; height: 40px;
  border: 4px solid var(--border, #1e3a5f); border-top-color: #e94560;
  border-radius: 50%; animation: spin 1s linear infinite;
  margin: 0 auto 12px;
}
@keyframes spin { to { transform: rotate(360deg); } }

.character-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}
.character-card {
  background: var(--bg-card, #16213e);
  border: 2px solid var(--border, #1e3a5f);
  border-radius: var(--radius-md, 10px);
  padding: 18px;
  cursor: pointer;
  transition: all 0.25s;
  position: relative;
  overflow: hidden;
}
.character-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.02), transparent);
  pointer-events: none;
}
.character-card:hover {
  border-color: #e94560;
  transform: translateY(-4px);
  box-shadow: var(--shadow-brand, 0 4px 20px rgba(233, 69, 96, 0.2));
}
.character-card.selected {
  border-color: var(--gold, #ffd700);
  box-shadow: 0 0 24px rgba(255, 215, 0, 0.2);
}
.character-card h3 {
  color: #e94560;
  margin: 0 0 10px;
  font-size: 1.05rem;
}
.char-stats {
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
}
.char-stats span {
  padding: 3px 8px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
  font-size: 0.85rem;
  color: var(--text-secondary, #a0aec0);
}
.desc {
  color: var(--text-muted, #718096);
  font-size: 0.82rem;
  margin: 8px 0;
  line-height: 1.5;
}
.char-badges { margin-top: 10px; }
.badge-free {
  background: rgba(74, 222, 128, 0.15);
  color: var(--green, #4ade80);
  border: 1px solid rgba(74, 222, 128, 0.3);
  padding: 3px 10px;
  border-radius: 50px;
  font-size: 0.78rem;
  font-weight: 600;
}
.badge-price {
  color: var(--gold, #ffd700);
  font-size: 0.85rem;
  font-weight: 600;
}

.back-row { margin-top: 28px; text-align: center; }
.btn {
  padding: 9px 20px; border: none; border-radius: var(--radius-sm, 6px);
  color: white; cursor: pointer; font-size: 0.9rem;
  text-decoration: none; display: inline-block; transition: all 0.2s;
}
.btn-ghost {
  background: transparent;
  border: 1px solid var(--border, #1e3a5f);
  color: var(--text-secondary, #a0aec0);
}
.btn-ghost:hover {
  border-color: var(--gold, #ffd700);
  color: var(--gold);
  background: rgba(255, 215, 0, 0.05);
}
</style>
