import request from './request'

export function getRecentRecords() {
  return request.get('/records')
}

export function getRecordDetail(recordId) {
  return request.get(`/records/${recordId}`)
}
