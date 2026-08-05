import request from './request'

/** 获取个人简介（含对战统计） */
export function getProfile() {
  return request.get('/user/profile')
}

/** 上传头像 */
export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/user/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
