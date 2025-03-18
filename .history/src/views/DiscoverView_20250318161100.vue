<template>
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
</template>

<script>
export default {
  name: "TryView",
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
