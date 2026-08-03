/**
 * 通用工具函数
 */

/** 格式化日期 */
export function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleString('zh-CN')
}

/** 格式化百分比（保留两位小数） */
export function formatPercent(value) {
  return (value * 100).toFixed(2) + '%'
}

/** 延迟函数 */
export function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

/** 从 JWT 中解析 payload */
export function decodeJwt(token) {
  try {
    const payload = token.split('.')[1]
    return JSON.parse(atob(payload))
  } catch {
    return null
  }
}
