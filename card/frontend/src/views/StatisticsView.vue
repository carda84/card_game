<template>
  <div class="stats-view">
    <NavBar />
    <div class="page-content">
      <div class="page-header">
        <h2>📈 卡牌统计</h2>
        <p class="page-sub">深入了解你的卡牌使用情况</p>
      </div>
      <div class="coming-soon">
        <div class="cs-icon-wrap"><span class="cs-icon">📊</span></div>
        <h3>统计分析功能</h3>
        <p>卡牌使用率、胜率趋势、热门卡组分析等功能正在开发中</p>
        <div class="cs-features">
          <div class="cs-feat"><span class="feat-icon">📉</span><span>胜率趋势图</span></div>
          <div class="cs-feat"><span class="feat-icon">🃏</span><span>卡牌使用率</span></div>
          <div class="cs-feat"><span class="feat-icon">🏆</span><span>热门卡组排行</span></div>
        </div>
        <router-link to="/records" class="btn-back">查看对战记录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import NavBar from '../components/common/NavBar.vue'
</script>

<style scoped>
.stats-view { min-height: 100vh; background: transparent; }
.page-content { padding: 24px; max-width: 700px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-header h2 { color: var(--text-primary); margin-bottom: 4px; }
.page-sub { color: var(--text-muted); font-size: 0.88rem; }

.coming-soon {
  text-align: center; padding: 48px 24px;
  background: var(--bg-card); border: 1px solid var(--border);
  border-radius: 16px; color: var(--text-secondary);
}
.cs-icon-wrap {
  display: inline-flex; align-items: center; justify-content: center;
  width: 80px; height: 80px; border-radius: 50%;
  background: rgba(0,191,255,0.08); border: 2px solid rgba(0,191,255,0.2);
  margin-bottom: 20px;
}
.cs-icon { font-size: 2.2rem; }
.coming-soon h3 { color: var(--text-primary); font-size: 1.3rem; margin-bottom: 8px; }
.coming-soon > p { color: var(--text-muted); font-size: 0.9rem; margin-bottom: 28px; max-width: 400px; margin-left: auto; margin-right: auto; }

.cs-features { display: flex; justify-content: center; gap: 20px; margin-bottom: 28px; }
.cs-feat {
  display: flex; flex-direction: column; align-items: center; gap: 6px;
  padding: 14px 18px; background: rgba(255,255,255,0.03);
  border: 1px solid var(--border); border-radius: 10px;
  font-size: 0.82rem; color: var(--text-secondary); min-width: 100px;
}
.feat-icon { font-size: 1.4rem; }

.btn-back {
  display: inline-block; padding: 10px 24px;
  background: transparent; border: 1px solid var(--border);
  border-radius: 6px; color: var(--text-secondary); font-size: 0.88rem; transition: all 0.2s;
}
.btn-back:hover { border-color: #e94560; color: #e94560; background: rgba(233,69,96,0.05); }
</style>
