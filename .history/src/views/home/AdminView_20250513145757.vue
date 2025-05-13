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
        <div v-if="currentTab === 'users'">
          <div class="user-search">
            <input
              type="text"
              v-model="searchQuery"
              placeholder="请输入用户ID"
            />
          </div>
          <div class="user-list">
            <div class="user-item" v-for="user in users" :key="user.userId">
              用户ID：{{ user.userId }}，用户名：{{ user.name }}
            </div>
          </div>
        </div>
        <div v-if="currentTab === 'reports'">
          <!-- 举报信息内容 -->
          <p>这是举报信息内容</p>
        </div>
        <div v-if="currentTab === 'statistics'">
          <!-- 详细情况内容 -->
          <p>这是详细情况内容</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import SideBar from "@/components/SideBar.vue";
import apiClient from "@/apiClient";

export default {
  name: "AdminView",
  components: {
    SideBar,
  },
  data() {
    return {
      users: [],
      currentTab: "users",
      searchQuery: "",
    };
  },
  methods: {
    selectTab(tab) {
      this.currentTab = tab;
    },
    async fetchUsers() {
      try {
        const response = await apiClient.get("/user/all");
        this.users = response.data.data;
        
      } catch (error) {
        console.error("获取用户失败:", error);
      }
    },
  },
  mounted() {
    this.fetchUsers();
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
  background-color: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-search {
  margin-bottom: 12px;
}

.user-search input {
  width: 100%;
  padding: 8px 12px;
  font-size: 14px;
  border-radius: 6px;
  border: 1px solid #ccc;
}

.user-list {
  max-height: 300px;
  overflow-y: auto;
  background-color: #fafafa;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px;
}

.user-item {
  padding: 8px;
  border-bottom: 1px solid #eee;
}

.user-item:last-child {
  border-bottom: none;
}
</style>
