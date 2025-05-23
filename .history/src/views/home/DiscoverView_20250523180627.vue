<template>
  
</template>

<script>
import SideBar from "@/components/SideBar.vue";
export default {
  name: "TryView",
  components: {
    SideBar,
  },
  data() {
    return {
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
/** 
     * AI-generated-content 
     * tool: DeepSeek 
     * version: latest
     * usage: I let the ai help me beautify the look of the interface
     */
/* 基础样式 */
:root {
  --primary-color: #7b61ff;
  --secondary-color: #4b70e2;
  --text-primary: #1a1a1a;
  --text-secondary: #666;
  --background: #f8fafc;
  --card-radius: 16px;
}
/* 内容区域 */
.main-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
  width: 100vw;
}
.content-wrapper {
  flex: 1;
  margin: 10px;
  padding: 20px;
  overflow-y: auto;
}
/* 数据卡片 */
.overview-cards {
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
