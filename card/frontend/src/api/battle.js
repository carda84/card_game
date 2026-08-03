import request from './request'

export function startBattle(data) {
  return request.post('/battle/start', data)
}

export function drawCard(data) {
  return request.post('/battle/draw', data)
}

export function playCard(data) {
  return request.post('/battle/play-card', data)
}

export function sacrificeCards(data) {
  return request.post('/battle/sacrifice', data)
}

export function endTurn(data) {
  return request.post('/battle/end-turn', data)
}

export function surrender(data) {
  return request.post('/battle/surrender', data)
}

export function getBoardState(sessionId) {
  return request.get(`/battle/board/${sessionId}`)
}

export function useSkill(data) {
  return request.post('/battle/skill', data)
}

export function useItem(data) {
  return request.post('/battle/item', data)
}
