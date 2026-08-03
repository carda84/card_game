import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as apiLogin, logout as apiLogout } from '../api/auth'

/**
 * 用户状态管理
 * 管理登录态、Token、用户基本信息
 */
export const useUserStore = defineStore('user', () => {
  // ===== 状态 =====
  const token = ref(localStorage.getItem('token') || '')
  const nickname = ref(localStorage.getItem('nickname') || '')
  const uniqueTag = ref(localStorage.getItem('uniqueTag') || '')
  const gold = ref(Number(localStorage.getItem('gold')) || 0)
  const points = ref(Number(localStorage.getItem('points')) || 0)

  // ===== 计算属性 =====
  const isLoggedIn = computed(() => !!token.value)
  const fullId = computed(() => nickname.value ? `${nickname.value}#${uniqueTag.value}` : '')

  // ===== 方法 =====

  /** 登录并保存状态到 localStorage */
  async function login(credentials) {
    const data = await apiLogin(credentials)
    setUserData(data)
    return data
  }

  /** 保存登录数据到 state + localStorage */
  function setUserData(data) {
    token.value = data.token
    nickname.value = data.nickname
    uniqueTag.value = data.uniqueTag
    gold.value = data.gold
    points.value = data.points

    localStorage.setItem('token', data.token)
    localStorage.setItem('nickname', data.nickname)
    localStorage.setItem('uniqueTag', data.uniqueTag)
    localStorage.setItem('gold', String(data.gold))
    localStorage.setItem('points', String(data.points))
  }

  /** 更新金币（用于商店购买等场景） */
  function updateGold(newGold) {
    gold.value = newGold
    localStorage.setItem('gold', String(newGold))
  }

  /** 更新积分（用于对战结算） */
  function updatePoints(newPoints) {
    points.value = newPoints
    localStorage.setItem('points', String(newPoints))
  }

  /** 登出，清除所有状态 */
  async function logout() {
    try {
      await apiLogout()
    } catch {
      // 即使接口失败也清除本地状态
    }
    clearUserData()
  }

  /** 清除本地状态 */
  function clearUserData() {
    token.value = ''
    nickname.value = ''
    uniqueTag.value = ''
    gold.value = 0
    points.value = 0

    localStorage.removeItem('token')
    localStorage.removeItem('nickname')
    localStorage.removeItem('uniqueTag')
    localStorage.removeItem('gold')
    localStorage.removeItem('points')
  }

  return {
    // state
    token,
    nickname,
    uniqueTag,
    gold,
    points,
    // computed
    isLoggedIn,
    fullId,
    // methods
    login,
    setUserData,
    updateGold,
    updatePoints,
    logout,
    clearUserData
  }
})
