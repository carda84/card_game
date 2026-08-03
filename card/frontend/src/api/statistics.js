import request from './request'

export function getMyStats() {
  return request.get('/statistics/my')
}

export function getGlobalStats() {
  return request.get('/statistics/global')
}
