<template>
  <div class="login-view">
    <div class="deco-layer">
      <div class="deco-orb orb1"></div>
      <div class="deco-orb orb2"></div>
      <div class="deco-orb orb3"></div>
    </div>
    <div class="auth-card">
      <div class="card-glow"></div>
      <div class="logo">
        <div class="logo-icon-wrap"><span class="logo-icon">🐾</span></div>
        <h1>野兽牌</h1>
        <p class="subtitle">策略卡牌对战</p>
      </div>
      <div class="form-container">
        <LoginForm v-if="mode === 'login'" @login-success="onLoginSuccess" @switch-to-register="mode = 'register'" />
        <RegisterForm v-else @register-success="onRegisterSuccess" @switch-to-login="mode = 'login'" />
      </div>
      <div class="card-footer">
        <span class="footer-dot"></span>
        <span>野兽牌 v1.0</span>
      </div>
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

function onLoginSuccess() { router.push('/') }
function onRegisterSuccess() { mode.value = 'login' }
</script>

<style scoped>
.login-view { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: transparent; position: relative; overflow: hidden; }

.deco-layer { position: fixed; inset: 0; pointer-events: none; z-index: 0; }
.deco-orb { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.4; animation: orbFloat 15s ease-in-out infinite; }
.orb1 { width: 300px; height: 300px; background: radial-gradient(circle, rgba(233,69,96,0.4), transparent 70%); top: -100px; right: -50px; }
.orb2 { width: 250px; height: 250px; background: radial-gradient(circle, rgba(0,191,255,0.3), transparent 70%); bottom: -80px; left: -60px; animation-delay: -5s; }
.orb3 { width: 200px; height: 200px; background: radial-gradient(circle, rgba(167,139,250,0.25), transparent 70%); top: 40%; left: 60%; animation-delay: -10s; }
@keyframes orbFloat { 0%,100% { transform: translate(0,0); } 33% { transform: translate(20px,-15px); } 66% { transform: translate(-15px,10px); } }

.auth-card {
  width: 100%; max-width: 440px;
  background: rgba(22,33,62,0.92); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(30,58,95,0.6); border-radius: 20px;
  padding: 2.5rem 2rem 1.5rem; box-shadow: 0 8px 32px rgba(0,0,0,0.5);
  position: relative; z-index: 1; overflow: hidden;
}
.card-glow {
  position: absolute; top: -2px; left: 20%; right: 20%; height: 3px;
  background: linear-gradient(90deg, transparent, #e94560, #ff6b8a, transparent);
  border-radius: 3px; animation: glowPulse 3s ease-in-out infinite;
}
@keyframes glowPulse { 0%,100% { opacity: 0.6; } 50% { opacity: 1; } }

.logo { text-align: center; margin-bottom: 1.5rem; }
.logo-icon-wrap {
  display: inline-flex; align-items: center; justify-content: center;
  width: 64px; height: 64px; border-radius: 50%;
  background: linear-gradient(135deg, rgba(233,69,96,0.2), rgba(194,49,82,0.3));
  border: 2px solid rgba(233,69,96,0.3); margin-bottom: 12px;
  animation: iconPulse 2s ease-in-out infinite;
}
@keyframes iconPulse { 0%,100% { box-shadow: 0 0 0 0 rgba(233,69,96,0.3); } 50% { box-shadow: 0 0 0 12px rgba(233,69,96,0); } }
.logo-icon { font-size: 2rem; }
.logo h1 { font-size: 1.8rem; font-weight: 800; background: linear-gradient(135deg, #fff, #e94560); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; margin: 0; }
.subtitle { color: var(--text-muted, #718096); font-size: 0.88rem; margin-top: 4px; letter-spacing: 2px; }

.form-container { margin-top: 1rem; }

.card-footer {
  display: flex; align-items: center; justify-content: center; gap: 6px;
  margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid rgba(30,58,95,0.4);
  font-size: 0.75rem; color: var(--text-dim, #4a5568);
}
.footer-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--green, #4ade80); animation: dotBlink 2s infinite; }
@keyframes dotBlink { 0%,100% { opacity: 1; } 50% { opacity: 0.3; } }
</style>
