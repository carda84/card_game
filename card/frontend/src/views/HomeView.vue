<template>
  <div class="home-view">
    <NavBar />
    <main class="main-content">
      <h2>欢迎来到野兽牌</h2>
      <p class="subtitle">策略卡牌对战 · 收集 · 构筑 · 征战</p>
      <div class="nav-grid">
        <router-link to="/collection" class="nav-card">
          <h3>🃏 卡牌收藏</h3>
          <p>浏览你的卡牌库</p>
        </router-link>
        <router-link to="/characters" class="nav-card">
          <h3>👤 选择人物</h3>
          <p>选择你的对战角色</p>
        </router-link>
        <router-link to="/decks" class="nav-card">
          <h3>📋 卡组管理</h3>
          <p>创建和编辑卡组</p>
        </router-link>
        <router-link to="/shop" class="nav-card">
          <h3>🛒 商店</h3>
          <p>购买新卡牌</p>
        </router-link>
        <router-link to="/battle/prepare" class="nav-card">
          <h3>⚔️ 开始对战</h3>
          <p>人机/PvP 对战</p>
        </router-link>
        <router-link to="/friends" class="nav-card">
          <h3>👥 好友</h3>
          <p>添加好友、私信</p>
        </router-link>
        <router-link to="/records" class="nav-card">
          <h3>📊 战绩</h3>
          <p>查看对战记录</p>
        </router-link>
        <router-link to="/statistics" class="nav-card">
          <h3>📈 统计</h3>
          <p>卡牌使用率/胜率</p>
        </router-link>
      </div>
    </main>
  </div>
</template>

<script setup>
import NavBar from '../components/common/NavBar.vue'
</script>

<style scoped>
.home-view {
  min-height: 100vh;
  background: #1a1a2e;
  color: #e0e0e0;
}
.main-content {
  padding: 2rem;
  text-align: center;
}
.main-content h2 {
  font-size: 1.8rem;
  margin-bottom: 0.5rem;
}
.subtitle {
  color: #888;
  margin-bottom: 2rem;
}
.nav-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
  max-width: 800px;
  margin: 0 auto;
}
.nav-card {
  background: #16213e;
  border: 1px solid #0f3460;
  border-radius: 10px;
  padding: 20px;
  transition: border-color 0.2s, transform 0.2s;
}
.nav-card:hover {
  border-color: #e94560;
  transform: translateY(-2px);
}
.nav-card h3 {
  color: #e94560;
  margin-bottom: 6px;
}
.nav-card p {
  color: #a0a0a0;
  font-size: 0.85rem;
}
</style>
