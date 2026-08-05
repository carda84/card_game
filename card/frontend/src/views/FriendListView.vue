<template>
  <div class="friends-view">
    <NavBar />
    <div class="page-content">
      <div class="page-header">
        <h2>👥 好友列表</h2>
        <p class="page-sub">添加好友，一起对战</p>
      </div>

      <div class="search-section">
        <div class="search-bar">
          <span class="search-icon">🔍</span>
          <input v-model="searchTag" placeholder="输入用户标识搜索（如 #138992）" @keyup.enter="doSearch" />
          <button @click="doSearch" class="btn-search" :disabled="searching">
            {{ searching ? '搜索中...' : '搜索' }}
          </button>
        </div>
        <p v-if="searchMsg" class="search-msg" :class="searchMsgType">{{ searchMsg }}</p>
      </div>

      <div v-if="friendStore.loading" class="loading-state">
        <div class="spinner"></div><span>加载中...</span>
      </div>
      <div v-else-if="friendStore.friends.length === 0" class="empty-state">
        <div class="empty-icon">👤</div>
        <h3>暂无好友</h3>
        <p>通过搜索用户标识添加好友</p>
      </div>
      <div v-else class="friend-list">
        <div v-for="f in friendStore.friends" :key="f.userId" class="friend-card">
          <div class="fc-avatar">{{ f.nickname?.charAt(0) || '?' }}</div>
          <div class="fc-info">
            <h4>{{ f.nickname }}<span class="tag">#{{ f.uniqueTag }}</span></h4>
            <div class="fc-meta"><span class="fc-points">⭐ {{ f.points }} 积分</span></div>
          </div>
          <div class="fc-actions">
            <router-link :to="`/friends/chat/${f.userId}`" class="btn-chat">💬 私信</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useFriendStore } from '../store/friend'
import NavBar from '../components/common/NavBar.vue'

const friendStore = useFriendStore()
const searchTag = ref('')
const searching = ref(false)
const searchMsg = ref('')
const searchMsgType = ref('success')

onMounted(() => friendStore.fetchFriends())

async function doSearch() {
  if (!searchTag.value.trim()) return
  searching.value = true
  searchMsg.value = ''
  try {
    const user = await friendStore.search(searchTag.value)
    if (user) {
      await friendStore.add(searchTag.value)
      searchMsg.value = `已添加 ${user.nickname}#${user.uniqueTag}`
      searchMsgType.value = 'success'
      searchTag.value = ''
    } else {
      searchMsg.value = '未找到该用户'
      searchMsgType.value = 'error'
    }
  } catch (e) {
    searchMsg.value = e.message || '搜索失败'
    searchMsgType.value = 'error'
  } finally {
    searching.value = false
    setTimeout(() => searchMsg.value = '', 3000)
  }
}
</script>

<style scoped>
.friends-view { min-height: 100vh; background: transparent; }
.page-content { padding: 24px; max-width: 700px; margin: 0 auto; }
.page-header { margin-bottom: 20px; }
.page-header h2 {
  color: var(--text-primary);
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.page-sub { color: var(--text-muted); font-size: 0.88rem; }

.search-section { margin-bottom: 24px; }
.search-bar {
  display: flex;
  align-items: center;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.25s;
}
.search-bar:focus-within {
  border-color: #e94560;
  box-shadow: 0 0 0 3px rgba(233, 69, 96, 0.15);
}
.search-icon {
  padding: 0 14px;
  font-size: 1rem;
  opacity: 0.5;
}
.search-bar input {
  flex: 1;
  padding: 14px 12px 14px 0;
  background: transparent;
  border: none;
  color: var(--text-primary);
  font-size: 0.92rem;
  outline: none;
}
.search-bar input::placeholder { color: var(--text-dim); }
.btn-search {
  padding: 14px 24px;
  background: linear-gradient(135deg, #e94560, #c23152);
  border: none;
  color: white;
  font-weight: 600;
  font-size: 0.88rem;
  cursor: pointer;
  transition: all 0.25s;
  white-space: nowrap;
}
.btn-search:hover:not(:disabled) { opacity: 0.9; }
.btn-search:disabled { opacity: 0.5; cursor: not-allowed; }

.search-msg {
  margin-top: 10px;
  font-size: 0.85rem;
  padding: 10px 14px;
  border-radius: 8px;
  font-weight: 500;
}
.search-msg.success {
  color: var(--green);
  background: rgba(74, 222, 128, 0.1);
  border: 1px solid rgba(74, 222, 128, 0.2);
}
.search-msg.error {
  color: var(--red);
  background: rgba(244, 67, 54, 0.1);
  border: 1px solid rgba(244, 67, 54, 0.2);
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 60px 0;
  color: var(--text-muted);
}
.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid var(--border);
  border-top-color: #e94560;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: var(--text-muted);
  background: var(--bg-card);
  border: 1px dashed var(--border);
  border-radius: var(--radius-lg);
}
.empty-icon {
  font-size: 3.5rem;
  margin-bottom: 16px;
  opacity: 0.4;
}

.friend-list { display: flex; flex-direction: column; gap: 12px; }
.friend-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 16px 20px;
  transition: all 0.25s;
  animation: fadeIn 0.3s ease;
}
.friend-card:hover {
  border-color: var(--border-hover);
  transform: translateX(6px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
  background: var(--bg-card-hover);
}

.fc-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #e94560, #c23152);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  font-weight: 700;
  color: white;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(233, 69, 96, 0.3);
}

.fc-info { flex: 1; min-width: 0; }
.fc-info h4 {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}
.fc-info .tag {
  color: var(--text-dim);
  font-weight: 400;
  font-size: 0.85rem;
}
.fc-meta { display: flex; gap: 12px; }
.fc-points {
  font-size: 0.85rem;
  color: var(--blue);
  padding: 2px 8px;
  background: rgba(0, 191, 255, 0.08);
  border-radius: 50px;
}

.fc-actions { flex-shrink: 0; }
.btn-chat {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 8px 18px;
  background: rgba(0, 191, 255, 0.1);
  border: 1px solid rgba(0, 191, 255, 0.25);
  border-radius: 50px;
  color: var(--blue);
  font-size: 0.85rem;
  font-weight: 500;
  transition: all 0.25s;
  text-decoration: none;
}
.btn-chat:hover {
  background: rgba(0, 191, 255, 0.18);
  border-color: rgba(0, 191, 255, 0.4);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 191, 255, 0.2);
}

@keyframes fadeIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
</style>
