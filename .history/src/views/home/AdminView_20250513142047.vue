<template>
  <div class="main-container">
    <SideBar />
    <div class="container">
      <div class="top">
        <!-- 顶部为用户搜索栏 -->
      </div>
      <div class="user-list">
        <!-- 中部为用户列表 -->
        <div class="user-item" v-for="user in users" :key="user.id">
          {{ user.name }}
        </div>
      </div>
      <div class="report-list">
        <!-- 底部为举报信息列表 -->
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
    };
  },
  methods: {
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

.search-bar {
  background-color: #f9f9f9;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.user-list {
  flex-grow: 1;
  overflow-y: auto;
  padding: 10px;
}

.user-item {
  background-color: #ffffff;
  padding: 15px;
  margin-bottom: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s;
}

.user-item:hover {
  background-color: #f0f0f0;
}

.report-list {
  background-color: #f9f9f9;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-top: 20px;
}
</style>
