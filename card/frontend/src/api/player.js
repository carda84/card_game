import request from './request'

export function getPlayerInfo() {
  return request.get('/players/me')
}

export function getLeaderboard(top = 20) {
  return request.get('/players/leaderboard', { params: { top } })
}
