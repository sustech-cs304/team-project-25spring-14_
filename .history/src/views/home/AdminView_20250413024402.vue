<template>
  <div class="admin-view">
    <div class="sidebar">
      <!-- 左侧 Sidebar 内容 -->
    </div>
    <div class="main-content">
      <div class="search-bar">
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
export default {
  data() {
    return {
      users: [] // 用户列表数据
    };
  },
  methods: {
    // 方法定义
  }
};
</script>

<style scoped>
.admin-view {
  display: flex;
}

.sidebar {
  width: 250px;
  /* Sidebar 样式 */
}

.main-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.search-bar {
  /* 搜索栏样式 */
}

.user-list {
  flex-grow: 1;
  /* 用户列表样式 */
}

.user-item {
  /* 用户项样式 */
}

.report-list {
  /* 举报信息列表样式 */
}
</style>
