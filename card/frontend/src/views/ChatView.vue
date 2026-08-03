<template>
  <div>
    <NavBar />
    <div class="chat-page">
      <h2>私信</h2>
      <div class="messages">
        <div v-for="msg in friendStore.messages" :key="msg.id"
             :class="['msg', msg.fromUserId === currentUserId ? 'mine' : 'theirs']">
          <p>{{ msg.content }}</p>
          <span class="time">{{ msg.createdAt }}</span>
        </div>
      </div>
      <div class="input-bar">
        <input v-model="newMessage" @keyup.enter="send" placeholder="输入消息..." />
        <button @click="send">发送</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useFriendStore } from '../store/friend'
import { useUserStore } from '../store/user'
import NavBar from '../components/common/NavBar.vue'

const route = useRoute()
const friendStore = useFriendStore()
const userStore = useUserStore()
const newMessage = ref('')
const currentUserId = ref(null) // TODO: 从 JWT 解析

onMounted(async () => {
  const friendId = Number(route.params.friendId)
  await friendStore.loadMessages(friendId)
})

async function send() {
  if (!newMessage.value.trim()) return
  const friendId = Number(route.params.friendId)
  await friendStore.sendMsg(friendId, newMessage.value)
  newMessage.value = ''
  await friendStore.loadMessages(friendId)
}
</script>

<style scoped>
.chat-page { padding: 20px; max-width: 600px; margin: 0 auto; }
.messages { height: 400px; overflow-y: auto; margin: 16px 0; }
.msg { padding: 8px 12px; border-radius: 8px; margin-bottom: 8px; max-width: 70%; }
.mine { background: #0f3460; margin-left: auto; }
.theirs { background: #16213e; }
.time { font-size: 0.75rem; color: #a0a0a0; }
.input-bar { display: flex; gap: 8px; }
.input-bar input { flex: 1; padding: 8px 12px; background: #16213e; border: 1px solid #0f3460; border-radius: 4px; color: white; }
.input-bar button { padding: 8px 16px; background: #e94560; border: none; border-radius: 4px; color: white; cursor: pointer; }
</style>
