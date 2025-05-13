<template>
  <div class="main-container">
    <SideBar />
    <div class="container">
      <div class="nav-buttons">
        <button
          class="nav-button"
          :class="{ active: currentTab === 'users' }"
          @click="selectTab('users')"
        >
          用户管理
        </button>
        <button
          class="nav-button"
          :class="{ active: currentTab === 'reports' }"
          @click="selectTab('reports')"
        >
          举报信息
        </button>
        <button
          class="nav-button"
          :class="{ active: currentTab === 'statistics' }"
          @click="selectTab('statistics')"
        >
          详细情况
        </button>
      </div>
      <div class="content-area">
        <div class="user-list" v-if="currentTab === 'users'">
          <!-- 中部为用户列表 -->
          <div class="user-item" v-for="user in users" :key="user.id">
            {{ user.name }}
          </div>
        </div>
        <div class="report-list" v-if="currentTab === 'reports'">
          <!-- 报告信息列表内容 -->
        </div>
        <div class="statistics-content" v-if="currentTab === 'statistics'">
          <!-- 数据统计内容 -->
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import SideBar from "@/components/SideBar.vue";

export default {
  name: "AdminView",
  components: {
    SideBar,
  },
  data() {
    return {
      users: [], // 用户列表数据
      currentTab: "users",
    };
  },
  methods: {
    selectTab(tab) {
      this.currentTab = tab;
    },
    // 方法定义
  },
};
</script>

<style scoped>
.main-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
  width: 100vw;
}

.container {
  flex: 1;
  margin: 10px;
  padding: 20px;
  background-color: #f3f4f6;
  min-height: 300px;
  box-shadow: inset 0 4px 6px rgba(0, 0, 0, 0.1),
    inset 0 -4px 6px rgba(255, 255, 255, 0.7);
  border-radius: 32px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.nav-buttons {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}
.nav-button {
  flex: 1;
  padding: 10px;
  border-radius: 8px;
  border: none;
  background-color: #ddd;
  cursor: pointer;
  font-weight: bold;
}
.nav-button:hover {
  background-color: #ccc;
}
.nav-button.active {
  background-color: #aaa;
  color: white;
}
.content-area {
  flex: 1;
  overflow-y: auto;
}
</style>
