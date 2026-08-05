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
  gap: 16px;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.form-group label {
  font-size: 0.82rem;
  font-weight: 600;
  color: var(--text-secondary, #a0aec0);
  letter-spacing: 0.3px;
}
.form-group input {
  padding: 11px 14px;
  border: 1px solid var(--border, #1e3a5f);
  border-radius: var(--radius-sm, 6px);
  background: var(--bg-input, #0d1b2a);
  color: var(--text-primary, #f0f0f0);
  font-size: 0.95rem;
  outline: none;
  transition: all 0.2s;
}
.form-group input::placeholder {
  color: var(--text-dim, #4a5568);
}
.btn-primary {
  padding: 12px;
  border: none;
  border-radius: var(--radius-sm, 6px);
  background: linear-gradient(135deg, #e94560, #c23152);
  color: white;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s;
  box-shadow: 0 2px 8px rgba(233, 69, 96, 0.3);
}
.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}
.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(233, 69, 96, 0.4);
}
.error {
  color: var(--red, #f44336);
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 6px;
}
.error::before {
  content: '⚠';
}
.switch-link {
  text-align: center;
  font-size: 0.85rem;
  color: var(--text-muted, #718096);
}
.switch-link a {
  color: #e94560;
  cursor: pointer;
  font-weight: 600;
  transition: color 0.2s;
}
.switch-link a:hover {
  color: #ff6b8a;
}
</style>
