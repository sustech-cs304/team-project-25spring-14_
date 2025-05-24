<template>
  <!-- 左侧导航栏 -->
  <nav class="sidebar">
    <div class="logo">
      <i class="album-icons icon-xiangji"></i>
      <span>智能相册</span>
    </div>
    <ul class="nav-list">
      <li
        v-for="item in filteredNavItems"
        :key="item.name"
        class="nav-item"
        :class="{ active: activeNav === item.name }"
        @click="navigateTo(item.path, item.name)"
      >
        <i :class="['album-icons', item.icon]"></i>
        <span>{{ item.label }}</span>
      </li>
    </ul>
    <div class="user-avatar" @click="showUserCard">
      <img
        :src="userInfo.avatarurl || require('@/assets/images/touxiang.svg')"
        alt="Avatar"
      />
    </div>
    <UserInfoCard ref="userCard" />
  </nav>
  <el-dialog v-model="showAppealDialog" title="账户申诉" width="400px">
    <p>
      您的账户已被封禁，请完成整改后点击下方按钮提交申诉请求，管理员将进行审核。
    </p>
    <template #footer>
      <el-button @click="showAppealDialog = false">取消</el-button>
      <el-button type="primary" @click="submitAppeal">提交申诉</el-button>
    </template>
  </el-dialog>
</template>

<script>
import UserInfoCard from "./UserInfoCard.vue";
import apiClient from "@/apiClient";

export default {
  name: "SideBar",
  components: { UserInfoCard },
  data() {
    return {
      userInfo: {},
      activeNav: localStorage.getItem("activeNav") || "discover",
      navItems: [
        {
          name: "discover",
          icon: "icon-u138",
          label: "发现",
          path: "/discover",
        },
        {
          name: "albums",
          icon: "icon-xiangceji",
          label: "相册集",
          path: "/albums",
        },
        {
          name: "allPhotos",
          icon: "icon-quanbuzhaopian1",
          label: "全部照片",
          path: "/allPhotos",
        },
        {
          name: "share",
          icon: "icon-gongxiangkongjian",
          label: "共享空间",
          path: "/share",
        },
        {
          name: "admin",
          icon: "icon-shezhi",
          label: "管理",
          path: "/admin",
        },
      ],
      showAppealDialog: false,
    };
  },
  computed: {
    filteredNavItems() {
      return this.navItems
        .filter((item) => {
          if (item.name === "admin") {
            return this.userInfo.rolename === "admin";
          }
          if (item.name === "share") {
            return this.userInfo.status !== "disabled";
          }
          return true;
        })
        .concat(
          this.userInfo.status === "disabled"
            ? [
                {
                  name: "appeal",
                  icon: "icon-shenqing",
                  label: "申诉",
                  path: "#appeal",
                },
              ]
            : []
        );
    },
  },
  mounted() {
    this.fetchUserInfo();
  },
  methods: {
    navigateTo(path, name) {
      if (path === "#appeal") {
        this.showAppealDialog = true;
        return;
      }
      this.activeNav = name;
      this.$router.push(path);
      localStorage.setItem("activeNav", name);
    },
    logout() {
      localStorage.removeItem("jwtToken");
      this.$router.push("/");
    },
    showUserCard(event) {
      const rect = event.currentTarget.getBoundingClientRect();

      const padding = 10;
      const cardWidth = 350;
      const cardHeight = 200;

      const isRight = rect.left + cardWidth + padding < window.innerWidth;
      const isBottom = rect.top + cardHeight + padding < window.innerHeight;

      const left = isRight
        ? rect.right + padding
        : rect.left - cardWidth - padding;
      const top = isBottom
        ? rect.top + padding
        : rect.bottom - cardHeight - padding;

      this.$refs.userCard.showCard(this.userInfo.userId, { top, left });
    },
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
        if (this.userInfo.status === "disabled") {
          alert("您的账号已被禁用，请整改后向管理员申诉");
        }
      } catch (error) {
        console.error("用户信息获取失败:", error);
        this.$router.push("/home/dicover");
      }
    },
    async submitAppeal() {
      try {
        await apiClient.put("/reports/user/mend");
        this.$message.success("申诉请求已提交");
        this.showAppealDialog = false;
      } catch (error) {
        console.error("申诉失败：", error);
        this.$message.error("申诉失败，请稍后再试");
      }
    },
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

/* 侧边导航 */
.sidebar {
  width: 200px;
  background: white;
  box-shadow: 4px 0 12px rgba(0, 0, 0, 0.05);
  position: relative; /* 固定导航栏 */
  top: 0;
  left: 0;
  height: 100%;
  z-index: 1000; /* 确保导航栏在内容之上 */
  overflow-y: auto; /* 使左侧导航栏可滚动 */
  padding: 24px;
  display: flex;
  flex-direction: column;
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
  margin-left: 200px; /* 为右侧内容区域留出左侧导航栏的空间 */
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
