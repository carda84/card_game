import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getFriends, addFriend, removeFriend, sendMessage, getMessages, searchUser } from '../api/friend'

export const useFriendStore = defineStore('friend', () => {
  const friends = ref([])
  const messages = ref([])
  const loading = ref(false)

  async function fetchFriends() {
    loading.value = true
    try { friends.value = await getFriends() } finally { loading.value = false }
  }

  async function add(targetId) {
    await addFriend(targetId)
    await fetchFriends()
  }

  async function remove(friendUserId) {
    await removeFriend(friendUserId)
    await fetchFriends()
  }

  async function sendMsg(friendUserId, content) {
    await sendMessage({ friendUserId, content })
  }

  async function loadMessages(friendUserId) {
    messages.value = await getMessages(friendUserId)
  }

  async function search(tag) {
    return await searchUser(tag)
  }

  return { friends, messages, loading, fetchFriends, add, remove, sendMsg, loadMessages, search }
})
