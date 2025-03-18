<template>
  <div class="main-container">
    <!-- 左侧导航栏 -->
    <nav class="sidebar">
      <div class="logo">
        <i class="album-icons icon-xiangji"></i>
        <span>智能相册</span>
      </div>
      <ul class="nav-list">
        <li
          v-for="item in navItems"
          :key="item.name"
          class="nav-item"
          :class="{ active: activeNav === item.name }"
          @click="activeNav = item.name"
        >
          <i :class="['album-icons', item.icon]"></i>
          <span>{{ item.label }}</span>
        </li>
      </ul>
      <div class="user-avatar">
        <img src="@/assets/images/kobe.jpg" alt="Avatar" />
      </div>
    </nav>

    <!-- 右侧内容区域 -->
    <main class="content">
      <!-- 数据概览 -->
      <div class="overview-cards">
        <div class="stats-card">
          <i class="album-icons icon-xiangce"></i>
          <div>
            <h3>6,234</h3>
            <p>照片数量</p>
          </div>
        </div>
        <div class="stats-card">
          <i class="album-icons icon-tubiaozhizuomoban"></i>
          <div>
            <h3>12</h3>
            <p>相册数量</p>
          </div>
        </div>
        <div class="stats-card">
          <i class="album-icons icon-1"></i>
          <div>
            <h3>28</h3>
            <p>拍摄地点</p>
          </div>
        </div>
      </div>

      <!-- 公告卡片 -->
      <div class="announcement-card">
        <div class="announcement-image">
          <img src="@/assets/images/老大.jpg" alt="公告" />
          <div class="announcement-time">
            <i class="album-icons icon-shizhongclock73"></i>
            <span>2024-06-17 22:05:21</span>
          </div>
        </div>
        <div class="announcement-content">
          <h3>最新动态</h3>
          <p>夏日回忆精选相册已生成 →</p>
        </div>
      </div>

      <!-- 照片统计 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>照片分布</h3>
          <div class="chart-legend">
            <span v-for="item in chartData" :key="item.name">
              <i :style="{ backgroundColor: item.color }"></i>
              {{ item.name }}
            </span>
          </div>
        </div>
        <div class="chart-container">
          <!-- 这里接入实际图表 -->
          <div class="chart-placeholder"></div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
export default {
  name: "MainView",
  data() {
    return {
      activeNav: "home",
      navItems: [
        { name: "home", icon: "icon-u138", label: "发现" },
        { name: "photos", icon: "icon-quanbuzhaopian1", label: "全部照片" },
        { name: "albums", icon: "icon-xiangceji", label: "相册集" },
        { name: "share", icon: "icon-gongxiangkongjian", label: "共享空间" },
        { name: "recycle", icon: "icon-huishouzhan", label: "回收站" },
        { name: "settings", icon: "icon-shezhi", label: "设置" },
      ],
      chartData: [
        { name: "人物", color: "#7B61FF" },
        { name: "风景", color: "#4B70E2" },
        { name: "美食", color: "#FF7E5C" },
        { name: "其他", color: "#E2E8F0" },
      ],
    };
  },
};
</script>

<style scoped>
/* 基础样式 */
:root {
  --primary-color: #7b61ff;
  --secondary-color: #4b70e2;
  --text-primary: #1a1a1a;
  --text-secondary: #666;
  --background: #f8fafc;
  --card-radius: 16px;
}

body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

/* 主容器 */
.main-container {
  display: flex;
  min-height: 100vh;
  background: var(--background);
}

/* 侧边导航 */
.sidebar {
  width: 2px;
  background: white;
  box-shadow: 4px 0 12px rgba(0, 0, 0, 0.05);
  position: fixed; /* 固定导航栏 */
  top: 0;
  left: 0;
  height: 100%;
  z-index: 1000; /* 确保导航栏在内容之上 */
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  padding: 12px;
  margin-bottom: 24px;
}

.nav-list {
  flex: 1;
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  margin: 8px 0;
  border-radius: 8px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.nav-item:hover {
  background: #f3f4ff;
  color: var(--primary-color);
}

.nav-item.active {
  background: #f3f4ff;
  color: var(--primary-color);
  font-weight: 500;
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  margin-top: auto;
  align-self: center;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 内容区域 */
.content {
  margin-left: 240px; /* 为右侧内容区域留出左侧导航栏的空间 */
  overflow-y: auto; /* 使右侧内容区域可滚动 */
  width: calc(100% - 240px); /* 使内容区域宽度适应视口 */
  height: 100vh; /* 设置内容区域的高度 */
  padding: 32px;
  gap: 24px;
  align-content: start;
  display: grid;
}

/* 数据卡片 */
.overview-cards {
  grid-column: 1 / -1;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.stats-card {
  background: white;
  padding: 20px;
  border-radius: var(--card-radius);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  gap: 16px;
}

.stats-card i {
  font-size: 28px;
  padding: 12px;
  background: #f3f4ff;
  border-radius: 8px;
  color: var(--primary-color);
}

.stats-card h3 {
  margin: 0;
  font-size: 24px;
}

.stats-card p {
  margin: 4px 0 0;
  color: var(--text-secondary);
}

.gradient-bg {
  background: linear-gradient(
    135deg,
    var(--primary-color),
    var(--secondary-color)
  );
  color: white;
}

.gradient-bg i {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

/* 公告卡片 */
.announcement-card {
  background: white;
  border-radius: var(--card-radius);
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  width: 100%; /* 或者指定一个具体的宽度，如 400px */
  max-width: 600px; /* 最大宽度 */
  height: 365px; /* 设置高度 */
  padding: 24px;
}

.announcement-image {
  position: relative;
  height: 240px;
}

.announcement-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.announcement-time {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.announcement-content {
  padding: 20px;
}

.announcement-content h3 {
  margin: 0 0 12px;
}

.announcement-content p {
  margin: 0;
  color: var(--text-secondary);
}

/* 图表卡片 */
.chart-card {
  grid-column: 1 / -1;
  background: white;
  border-radius: var(--card-radius);
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.chart-legend span {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-left: 20px;
  color: var(--text-secondary);
}

.chart-legend i {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.chart-container {
  height: 400px;
}

.chart-placeholder {
  height: 100%;
  background: linear-gradient(135deg, #f3f4ff, #ffffff);
  border-radius: 12px;
}
</style>
