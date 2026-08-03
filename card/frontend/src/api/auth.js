import request from './request'

/**
 * 认证相关 API
 */

/**
 * 发送验证码到邮箱
 * @param {string} email 邮箱地址
 */
export function sendCode(email) {
  return request.post('/auth/send-code', { email })
}

/**
 * 用户注册
 * @param {Object} data - { email, verificationCode, password, nickname }
 */
export function register(data) {
  return request.post('/auth/register', data)
}

/**
 * 用户登录
 * @param {Object} data - { email, password }
 */
export function login(data) {
  return request.post('/auth/login', data)
}

/**
 * 用户登出
 */
export function logout() {
  return request.post('/auth/logout')
}
