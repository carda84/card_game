import request from './request'

export function addFriend(targetId) {
  return request.post('/friends', { targetId })
}

export function removeFriend(friendUserId) {
  return request.delete(`/friends/${friendUserId}`)
}

export function getFriends() {
  return request.get('/friends')
}

export function searchUser(tag) {
  return request.get('/friends/search', { params: { tag } })
}

export function sendMessage(data) {
  return request.post('/friends/message', data)
}

export function getMessages(friendUserId) {
  return request.get(`/friends/messages/${friendUserId}`)
}
