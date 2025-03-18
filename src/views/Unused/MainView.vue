<template>
  <div class="main-container">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <div class="logo">相册</div>
      <ul class="nav-list">
        <li class="nav-item active">
          <i class="iconfont icon-home"></i> <span>主页</span>
        </li>
        <li class="nav-item">
          <i class="iconfont icon-image"></i> <span>全部照片</span>
        </li>
        <li class="nav-item">
          <i class="iconfont icon-group"></i> <span>相册集</span>
        </li>
        <li class="nav-item">
          <i class="iconfont icon-share"></i> <span>分享圈</span>
        </li>
        <li class="nav-item">
          <i class="iconfont icon-recycle"></i> <span>回收站</span>
        </li>
        <li class="nav-item">
          <i class="iconfont icon-settings"></i> <span>设置</span>
        </li>
        <li class="nav-item">
          <i class="iconfont icon-user"></i> <span>用户管理</span>
        </li>
        <li class="nav-item">
          <i class="iconfont icon-announce"></i> <span>公告管理</span>
        </li>
      </ul>
    </div>

    <!-- 右侧内容区域 -->
    <div class="content">
      <!-- 用户信息 -->
      <div class="user-info">
        <div class="user-avatar">
          <img src="@/assets/images/kobe.jpg" alt="Avatar" />
        </div>
        <div class="user-stats">
          <h3>欢迎回来, 老大</h3>
          <div class="stats">
            <div class="stat-item">
              <strong>6</strong>
              <p>照片数量</p>
            </div>
            <div class="stat-item">
              <strong>6</strong>
              <p>相册数量</p>
            </div>
            <div class="stat-item">
              <strong>6</strong>
              <p>创作数量</p>
            </div>
            <div class="stat-item">
              <strong>6</strong>
              <p>拍摄地点数量</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 公告 -->
      <div class="announcement">
        <img src="@/assets/images/老大.jpg" alt="公告图片" />
        <p>2024-06-17 22:05:21</p>
      </div>

      <!-- 照片统计饼图 -->
      <div class="photo-stats">
        <h3>照片统计</h3>
        <!-- 这里的饼图你可以用图表库实现，如 ECharts 或 Chart.js -->
        <div class="chart-placeholder">
          <p>饼图会显示在这里</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "MainView",
};
</script>

<style scoped>
.main-container {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 200px;
  background-color: #2f3c4f;
  color: #fff;
  padding-top: 20px;
  display: flex;
  flex-direction: column;
}

.logo {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 30px;
}

.nav-list {
  list-style-type: none;
  padding: 0;
}

.nav-item {
  padding: 10px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: background-color 0.3s;
}

.nav-item i {
  margin-right: 10px;
}

.nav-item:hover {
  background-color: #4b70e2;
}

.nav-item.active {
  background-color: #4b70e2;
}

.content {
  flex: 1;
  padding: 20px;
  background-color: #f4f5f7;
}

.user-info {
  display: flex;
  margin-bottom: 30px;
}

.user-avatar {
  margin-right: 20px;
}

.user-avatar img {
  border-radius: 50%;
  width: 100px;
  height: 100px;
}

.user-stats {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  text-align: center;
}

.stat-item strong {
  font-size: 24px;
  display: block;
}

.announcement {
  margin-bottom: 30px;
}

.announcement h3 {
  font-size: 18px;
  margin-bottom: 10px;
}

.photo-stats h3 {
  margin-bottom: 10px;
}

.chart-placeholder {
  background-color: #e1e1e1;
  height: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #666;
  font-size: 18px;
}
</style>
