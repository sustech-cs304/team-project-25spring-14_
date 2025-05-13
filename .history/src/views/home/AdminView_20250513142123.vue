<template>
  <div class="main-container">
    <SideBar />
    <div class="container">
      <div class="top-bar">
        <!-- 顶部为gong n栏 -->
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
</style>
