<template>
  <div class="login-view">
    <div class="auth-card">
      <div class="logo">
        <h1>🐾 野兽牌</h1>
        <p class="subtitle">策略卡牌对战</p>
      </div>

      <LoginForm
        v-if="mode === 'login'"
        @login-success="onLoginSuccess"
        @switch-to-register="mode = 'register'"
      />
      <RegisterForm
        v-else
        @register-success="onRegisterSuccess"
        @switch-to-login="mode = 'login'"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import LoginForm from '../components/auth/LoginForm.vue'
import RegisterForm from '../components/auth/RegisterForm.vue'

const router = useRouter()
const mode = ref('login')

function onLoginSuccess() {
  router.push('/')
}

function onRegisterSuccess() {
  mode.value = 'login'
}
</script>

<style scoped>
.login-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
}

.auth-card {
  width: 100%;
  max-width: 460px;
  background: #16213e;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
}

.logo {
  text-align: center;
  margin-bottom: 1rem;
}

.logo h1 {
  color: #e0e0e0;
  font-size: 1.8rem;
  margin: 0;
}

.subtitle {
  color: #888;
  font-size: 0.85rem;
  margin-top: 0.3rem;
}
</style>
