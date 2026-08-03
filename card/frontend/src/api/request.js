import axios from 'axios'
import { useUserStore } from '../store/user'
import router from '../router'

/**
 * Axios 实例
 * - 自动附加 JWT Token
 * - 统一处理 401 跳转登录
 */
const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器：自动携带 Token
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一错误处理
request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const status = error.response?.status
    const message = error.response?.data?.message || '请求失败'

    if (status === 401) {
      // Token 失效，清除登录态并跳转登录页
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
    }

    return Promise.reject({ status, message })
  }
)

export default request
