<template>
  <router-view />
</template>

<script setup>
</script>

<style>
/* 全局基础样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  /* 品牌色 */
  --brand: #e94560;
  --brand-dark: #c23152;
  --brand-glow: rgba(233, 69, 96, 0.35);
  
  /* 背景层次 */
  --bg-deep: #0a0e17;
  --bg-base: #111827;
  --bg-card: #16213e;
  --bg-card-hover: #1a2744;
  --bg-elevated: #1e2a4a;
  --bg-input: #0d1b2a;
  
  /* 边框 */
  --border: #1e3a5f;
  --border-hover: #2a4a6f;
  --border-brand: rgba(233, 69, 96, 0.4);
  
  /* 文字 */
  --text-primary: #f0f0f0;
  --text-secondary: #a0aec0;
  --text-muted: #718096;
  --text-dim: #4a5568;
  
  /* 语义色 */
  --gold: #ffd700;
  --gold-glow: rgba(255, 215, 0, 0.2);
  --blue: #00bfff;
  --blue-glow: rgba(0, 191, 255, 0.2);
  --green: #4ade80;
  --green-dark: #28a745;
  --red: #f44336;
  --orange: #ffa500;
  --purple: #a78bfa;
  
  /* 圆角 */
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 16px;
  --radius-xl: 20px;
  
  /* 阴影 */
  --shadow-sm: 0 2px 8px rgba(0,0,0,0.3);
  --shadow-md: 0 4px 16px rgba(0,0,0,0.4);
  --shadow-lg: 0 8px 32px rgba(0,0,0,0.5);
  --shadow-brand: 0 4px 20px rgba(233, 69, 96, 0.2);
  
  /* 过渡 */
  --ease: cubic-bezier(0.4, 0, 0.2, 1);
}

body {
  font-family: 'Inter', 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background:
    linear-gradient(rgba(10, 10, 20, 0.65), rgba(10, 10, 20, 0.65)),
    url('./assets/images/background.jpg');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  background-repeat: no-repeat;
  color: var(--text-primary);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  line-height: 1.5;
}

a {
  color: inherit;
  text-decoration: none;
}

/* 全局选中样式 */
::selection {
  background: var(--brand);
  color: white;
}

/* 全局滚动条 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: var(--border);
  border-radius: 3px;
}
::-webkit-scrollbar-thumb:hover {
  background: var(--border-hover);
}

/* 全局输入框聚焦光晕 */
input:focus, select:focus, textarea:focus {
  outline: none;
  border-color: var(--brand) !important;
  box-shadow: 0 0 0 3px var(--brand-glow);
}

/* 全局按钮基类 */
button {
  font-family: inherit;
}

/* 页面过渡 */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.25s var(--ease);
}
.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}
</style>
