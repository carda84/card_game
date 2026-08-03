<template>
  <div>
    <NavBar />
    <div class="page-content">
      <h2>卡牌统计</h2>
      <p class="placeholder">卡牌使用率和 PvP 胜率统计（开发中）</p>
    </div>
  </div>
</template>

<script setup>
import NavBar from '../components/common/NavBar.vue'
</script>

<style scoped>
.page-content { padding: 20px; }
.placeholder { color: #a0a0a0; margin-top: 20px; }
</style>
