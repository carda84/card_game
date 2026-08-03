import request from './request'

export function joinQueue(data) {
  return request.post('/pvp/queue', data)
}

export function leaveQueue() {
  return request.delete('/pvp/queue')
}

export function findMatch() {
  return request.get('/pvp/match')
}
