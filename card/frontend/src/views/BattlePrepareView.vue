<template>
  <div class="battle-prepare-view">
    <NavBar />
    <main class="main-content">
      <!-- 装饰背景 -->
      <div class="bg-decor">
        <span class="bg-icon i1">⚔️</span>
        <span class="bg-icon i2">🛡️</span>
        <span class="bg-icon i3">🔥</span>
        <span class="bg-icon i4">💀</span>
      </div>

      <div class="center-wrap">
        <h2>⚔️ 准备对战</h2>
        <p class="subtitle">选择你的战斗方式</p>

        <div class="prepare-steps">
          <router-link to="/characters" class="step-card">
            <div class="step-num">1</div>
            <div class="step-info">
              <h3>选择人物</h3>
              <p>选择你的对战角色，不同角色拥有不同能力</p>
            </div>
          </router-link>
          <router-link to="/decks" class="step-card">
            <div class="step-num">2</div>
            <div class="step-info">
              <h3>组建卡组</h3>
              <p>创建或编辑你的战斗卡组</p>
            </div>
          </router-link>
          <router-link to="/levels" class="step-card">
            <div class="step-num">3</div>
            <div class="step-info">
              <h3>选择关卡</h3>
              <p>挑战 PvE 人机对战关卡</p>
            </div>
          </router-link>
          <router-link to="/pvp/match" class="step-card">
            <div class="step-num">4</div>
            <div class="step-info">
              <h3>PvP 匹配</h3>
              <p>与其他玩家进行实时对战</p>
            </div>
          </router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import NavBar from '../components/common/NavBar.vue'
</script>

<style scoped>
.battle-prepare-view {
  min-height: 100vh;
  background: #1a1a2e;
  position: relative;
  overflow: hidden;
}

/* 装饰背景 */
.bg-decor {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}
.bg-icon {
  position: absolute;
  font-size: 5rem;
  opacity: 0.05;
  filter: blur(1px);
  animation: float 18s ease-in-out infinite;
}
.bg-icon.i1 { top: 10%; left: 8%; animation-delay: 0s; }
.bg-icon.i2 { top: 25%; right: 12%; font-size: 4rem; animation-delay: -5s; }
.bg-icon.i3 { bottom: 20%; left: 18%; font-size: 4.5rem; animation-delay: -10s; }
.bg-icon.i4 { bottom: 30%; right: 8%; font-size: 5.5rem; animation-delay: -14s; }
@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-15px) rotate(4deg); }
}

.main-content {
  position: relative;
  z-index: 1;
  min-height: calc(100vh - 56px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.center-wrap {
  text-align: center;
  width: 100%;
  max-width: 560px;
}

.center-wrap h2 {
  color: #e0e0e0;
  font-size: 1.8rem;
  margin-bottom: 6px;
}
.subtitle {
  color: #888;
  font-size: 0.95rem;
  margin-bottom: 2rem;
}

.prepare-steps {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.step-card {
  display: flex;
  align-items: center;
  gap: 18px;
  background: #16213e;
  border: 1px solid #0f3460;
  border-radius: 12px;
  padding: 18px 24px;
  text-align: left;
  transition: border-color 0.25s, transform 0.2s, box-shadow 0.25s;
}
.step-card:hover {
  border-color: #e94560;
  transform: translateX(6px);
  box-shadow: 0 4px 20px rgba(233, 69, 96, 0.15);
}

.step-num {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #e94560, #c93050);
  color: white;
  font-weight: bold;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.step-info h3 {
  color: #e0e0e0;
  font-size: 1.05rem;
  margin-bottom: 3px;
}
.step-info p {
  color: #888;
  font-size: 0.85rem;
  margin: 0;
}
</style>
