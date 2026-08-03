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
.char-select-page { min-height: 100vh; background: #1a1a2e; }
.page-content { padding: 2rem; max-width: 1000px; margin: 0 auto; }
.page-content h2 { color: #e0e0e0; margin-bottom: 4px; }
.subtitle { color: #888; margin-bottom: 20px; font-size: 0.9rem; }

.loading { text-align: center; padding: 40px; color: #888; }
.spinner {
  width: 40px; height: 40px;
  border: 4px solid #0f3460; border-top-color: #e94560;
  border-radius: 50%; animation: spin 1s linear infinite;
  margin: 0 auto 12px;
}
@keyframes spin { to { transform: rotate(360deg); } }

.character-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}
.character-card {
  background: #16213e;
  border: 2px solid #0f3460;
  border-radius: 10px;
  padding: 16px;
  cursor: pointer;
  transition: border-color 0.2s, transform 0.2s;
}
.character-card:hover { border-color: #e94560; transform: translateY(-3px); }
.character-card.selected { border-color: #ffd700; box-shadow: 0 0 20px rgba(255,215,0,0.15); }
.character-card h3 { color: #e94560; margin: 0 0 8px; }
.char-stats { display: flex; gap: 12px; color: #ccc; font-size: 0.9rem; margin-bottom: 6px; }
.desc { color: #a0a0a0; font-size: 0.82rem; margin: 6px 0; line-height: 1.4; }
.char-badges { margin-top: 8px; }
.badge-free { background: #28a745; padding: 2px 8px; border-radius: 4px; font-size: 0.78rem; color: white; }
.badge-price { color: #ffd700; font-size: 0.85rem; }

.back-row { margin-top: 24px; text-align: center; }
.btn {
  padding: 8px 18px; border: none; border-radius: 6px;
  color: white; cursor: pointer; font-size: 0.9rem;
  text-decoration: none; display: inline-block; transition: all 0.2s;
}
.btn-ghost { background: transparent; border: 1px solid #0f3460; color: #ccc; }
.btn-ghost:hover { border-color: #ffd700; color: #ffd700; }
</style>
