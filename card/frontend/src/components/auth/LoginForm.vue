<template>
  <form class="auth-form" @submit.prevent="handleLogin">
    <div class="form-group">
      <label>邮箱</label>
      <input v-model="email" type="email" placeholder="请输入邮箱" required />
    </div>
    <div class="form-group">
      <label>密码</label>
      <input v-model="password" type="password" placeholder="请输入密码" required />
    </div>
    <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    <button type="submit" class="btn-primary" :disabled="loading">
      {{ loading ? '登录中...' : '登录' }}
    </button>
    <p class="switch-link">
      还没有账号？<a @click="$emit('switch-to-register')">立即注册</a>
    </p>
  </form>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '../../store/user'

const emit = defineEmits(['login-success', 'switch-to-register'])
const userStore = useUserStore()

const email = ref('')
const password = ref('')
const loading = ref(false)
const errorMsg = ref('')

async function handleLogin() {
  loading.value = true
  errorMsg.value = ''
  try {
    await userStore.login({ email: email.value, password: password.value })
    emit('login-success')
  } catch (err) {
    errorMsg.value = err.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.form-group label {
  font-size: 0.85rem;
  color: #aaa;
}
.form-group input {
  padding: 10px 12px;
  border: 1px solid #333;
  border-radius: 6px;
  background: #0d1117;
  color: #e0e0e0;
  font-size: 0.95rem;
  outline: none;
  transition: border-color 0.2s;
}
.form-group input:focus {
  border-color: #e94560;
}
.btn-primary {
  padding: 10px;
  border: none;
  border-radius: 6px;
  background: #e94560;
  color: white;
  font-size: 1rem;
  cursor: pointer;
  transition: opacity 0.2s;
}
.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.btn-primary:hover:not(:disabled) {
  opacity: 0.85;
}
.error {
  color: #ef4444;
  font-size: 0.85rem;
}
.switch-link {
  text-align: center;
  font-size: 0.85rem;
  color: #888;
}
.switch-link a {
  color: #e94560;
  cursor: pointer;
}
</style>
