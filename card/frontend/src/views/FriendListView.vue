<template>
  <div>
    <NavBar />
    <div class="page-content">
      <h2>好友列表</h2>
      <div class="search-bar">
        <input v-model="searchTag" placeholder="输入用户标识搜索（如 #138992）" />
        <button @click="doSearch" class="btn-primary">搜索</button>
      </div>
      <div v-if="friendStore.loading">加载中...</div>
      <div v-else class="friend-list">
        <div v-for="f in friendStore.friends" :key="f.userId" class="friend-card">
          <h4>{{ f.nickname }}#{{ f.uniqueTag }}</h4>
          <p>⭐ {{ f.points }}</p>
          <router-link :to="`/friends/chat/${f.userId}`" class="btn-chat">私信</router-link>
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

onMounted(() => friendStore.fetchFriends())

async function doSearch() {
  const user = await friendStore.search(searchTag.value)
  if (user) {
    await friendStore.add(searchTag.value)
  }
}
</script>

<style scoped>
.page-content { padding: 20px; }
.search-bar { display: flex; gap: 8px; margin: 16px 0; }
.search-bar input { flex: 1; padding: 8px 12px; background: #16213e; border: 1px solid #0f3460; border-radius: 4px; color: white; }
.btn-primary { padding: 8px 16px; background: #e94560; border: none; border-radius: 4px; color: white; cursor: pointer; }
.friend-list { margin-top: 12px; }
.friend-card { display: flex; align-items: center; gap: 16px; padding: 12px; background: #16213e; border-radius: 6px; margin-bottom: 8px; }
.friend-card h4 { color: #e94560; }
.btn-chat { padding: 4px 10px; background: #0f3460; border-radius: 4px; }
</style>
