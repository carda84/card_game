<template>
  <form class="auth-form" @submit.prevent="handleRegister">
    <div class="form-group">
      <label>邮箱</label>
      <input v-model="email" type="email" placeholder="请输入邮箱" required />
    </div>
    <div class="code-row">
      <div class="form-group code-input">
        <label>验证码</label>
        <input v-model="code" type="text" placeholder="6位验证码" maxlength="6" required />
      </div>
      <button type="button" class="btn-code" :disabled="cooldown > 0" @click="handleSendCode">
        {{ cooldown > 0 ? `${cooldown}s` : '发送验证码' }}
      </button>
    </div>
    <div class="form-group">
      <label>昵称</label>
      <input v-model="nickname" type="text" placeholder="请输入昵称" required />
    </div>
    <div class="form-group">
      <label>密码</label>
      <input v-model="password" type="password" placeholder="请输入密码（至少6位）" minlength="6" required />
    </div>
    <div class="form-group">
      <label>确认密码</label>
      <input v-model="confirmPassword" type="password" placeholder="再次输入密码" required />
    </div>
    <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    <p v-if="successMsg" class="success">{{ successMsg }}</p>
    <button type="submit" class="btn-primary" :disabled="loading">
      {{ loading ? '注册中...' : '注册' }}
    </button>
    <p class="switch-link">
      已有账号？<a @click="$emit('switch-to-login')">返回登录</a>
    </p>
  </form>
</template>

<script setup>
import { ref } from 'vue'
import { sendCode, register } from '../../api/auth'

const emit = defineEmits(['register-success', 'switch-to-login'])

const email = ref('')
const code = ref('')
const nickname = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const cooldown = ref(0)
const errorMsg = ref('')
const successMsg = ref('')

let cooldownTimer = null

async function handleSendCode() {
  if (!email.value) {
    errorMsg.value = '请先输入邮箱'
    return
  }
  errorMsg.value = ''
  try {
    await sendCode(email.value)
    successMsg.value = '验证码已发送到邮箱'
    cooldown.value = 60
    cooldownTimer = setInterval(() => {
      cooldown.value--
      if (cooldown.value <= 0) clearInterval(cooldownTimer)
    }, 1000)
  } catch (err) {
    errorMsg.value = err.message || '发送失败'
  }
}

async function handleRegister() {
  errorMsg.value = ''
  successMsg.value = ''

  if (password.value !== confirmPassword.value) {
    errorMsg.value = '两次密码不一致'
    return
  }
  if (password.value.length < 6) {
    errorMsg.value = '密码至少6位'
    return
  }

  loading.value = true
  try {
    await register({
      email: email.value,
      verificationCode: code.value,
      password: password.value,
      nickname: nickname.value
    })
    successMsg.value = '注册成功，请登录'
    emit('register-success')
  } catch (err) {
    errorMsg.value = err.message || '注册失败'
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
.code-row {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}
.code-input {
  flex: 1;
}
.btn-code {
  padding: 10px 14px;
  border: 1px solid #e94560;
  border-radius: 6px;
  background: transparent;
  color: #e94560;
  cursor: pointer;
  white-space: nowrap;
  font-size: 0.85rem;
}
.btn-code:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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
.error {
  color: #ef4444;
  font-size: 0.85rem;
}
.success {
  color: #22c55e;
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
