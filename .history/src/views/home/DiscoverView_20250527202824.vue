<template>
  <div class="main-container">
    <SideBar />
    <div class="content-wrapper">
      <div class="overview-cards">
        <div class="stats-card">
          <i class="album-icons icon-xiangce"></i>
          <div>
            <h3>{{ this.myPhotoCounts }}</h3>
            <p>照片数量</p>
          </div>
        </div>
        <div class="stats-card">
          <i class="album-icons icon-tubiaozhizuomoban"></i>
          <div>
            <h3>{{ this.myAlbumCounts }}</h3>
            <p>相册数量</p>
          </div>
        </div>
      </div>

      <!-- 公告卡片 -->
      <div class="announcement-card">
        <div class="announcement-image">
          <img
            v-if="this.latestPhoto"
            :src="this.latestPhoto.fileUrl"
            alt="公告"
          />
          <div v-else class="no-photo-placeholder">暂无照片</div>
          <div class="announcement-time">
            <i class="album-icons icon-shizhongclock73"></i>
            <span v-if="latestPhoto && latestPhoto.capturedAt">
              {{ latestPhoto.capturedAt.split("T")[0] }}
            </span>
          </div>
        </div>
        <div class="announcement-content">
          <h3>最新照片</h3>
          <p
            v-if="latestPhoto"
            @click="
              this.$router.push({
                path: `/albums/${this.latestPhoto.albumId}`,
                query: { isSelf: true },
              })
            "
          >
            去看看吧 →
          </p>
        </div>
      </div>

      <!-- 照片统计 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>照片分布</h3>
          <el-button size="small" @click="toggleChartMode">
            切换为 {{ chartMode === 'tag' ? '地点' : '标签' }} 统计
          </el-button>
          <div class="chart-legend">
            <span v-for="item in chartData" :key="item.name">
              <i :style="{ backgroundColor: item.color }"></i>
              {{ item.name }}
            </span>
          </div>
        </div>
        <div class="chart-container">
          <!-- 这里接入实际图表 -->
          <div ref="chart" class="chart-placeholder"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import SideBar from "@/components/SideBar.vue";
import apiClient from "@/apiClient";
import * as echarts from "echarts";
export default {
  name: "TryView",
  components: {
    SideBar,
  },
  data() {
    return {
      chartData: [],
      chartInstance: null,
      userInfo: {},
      myPhotoCounts: 0,
      myAlbumCounts: 0,
      latestPhoto: {},
      chartMode: "tag",
    };
  },
  methods: {
    async fetchUserInfo() {
      try {
        await apiClient
          .get("/user/userInfo")
          .then((response) => {
            this.userInfo = response.data.data;
          })
          .catch((error) => {
            console.error(error);
          });
        console.log("用户信息获取成功，ID:", this.userInfo.userId);
        localStorage.setItem("userId", this.userInfo.userId);
        this.currentUserId = this.userInfo.userId;
      } catch (error) {
        console.error("用户信息获取失败:", error);
        this.$router.push("/");
      }
    },
    async fetchData() {
      try {
        const photosRes = await apiClient.get("/photos/my");
        const albumsRes = await apiClient.get(
          `/albums/user/${this.userInfo.userId}`,
          {
            params: { currentUserId: this.userInfo.userId },
          }
        );
        this.myPhotoCounts = photosRes.data.data.count;
        this.myAlbumCounts = albumsRes.data.data.count;
        this.latestPhoto = photosRes.data.data.photos[0];

        const categoryCounts = {};
        photosRes.data.data.photos.forEach((photo) => {
          const key =
            this.chartMode === "tag"
              ? (photo.tag || "其他").trim()
              : (photo.location || "未知").trim();
          categoryCounts[key] = (categoryCounts[key] || 0) + 1;
        });

        // 为每个标签分配随机颜色（或从固定色卡中选取）
        const colors = [
          "#7B61FF",
          "#4B70E2",
          "#FF7E5C",
          "#FFD600",
          "#00C9A7",
          "#FFB6B9",
          "#E2E8F0",
          "#98FB98",
          "#DDA0DD",
          "#87CEEB",
        ];
        const colorMap = {};
        Object.keys(categoryCounts).forEach((tag, index) => {
          colorMap[tag] = colors[index % colors.length];
        });

        this.chartData = Object.entries(categoryCounts).map(([name, value]) => ({
          name,
          value,
          color: colorMap[name],
        }));

        this.$nextTick(this.renderChart);
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    },
    renderChart() {
      const dom = this.$refs.chart;
      if (!dom) return;
      if (!this.chartInstance) {
        this.chartInstance = echarts.init(dom);
      }
      const option = {
        tooltip: { trigger: "item" },
        legend: { orient: "vertical", left: "left" },
        series: [
          {
            name: "照片分类",
            type: "pie",
            radius: "50%",
            data: this.chartData.map((i) => ({ name: i.name, value: i.value })),
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)",
              },
            },
          },
        ],
        color: this.chartData.map((i) => i.color),
      };
      this.chartInstance.setOption(option);
    },
    toggleChartMode() {
      this.chartMode = this.chartMode === "tag" ? "location" : "tag";
      this.fetchData();
    },
  },
  mounted() {
    this.fetchUserInfo().then(() => {
      this.fetchData();
    });
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

.no-photo-placeholder {
  width: 100%;
  height: 100%;
  background-color: #f0f0f0;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #999;
  font-size: 16px;
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
