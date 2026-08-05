import request from './request'

/** 获取个人简介（含对战统计） */
export function getProfile() {
  return request.get('/user/profile')
}
