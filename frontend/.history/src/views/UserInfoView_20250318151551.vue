<template>
  <div class="user-info-container">
    <!-- 用户头像 -->
    <div class="user-avatar">
      <img v-if="user.avatarUrl" :src="user.avatarUrl" alt="User Avatar" />
      <div v-else class="default-avatar">用户头像</div>
    </div>

    <!-- 用户信息 -->
    <div class="user-details">
      <h2>{{ user.username }}</h2>
      <p>
        角色: <span>{{ user.rolename }}</span>
      </p>
      <p>
        状态: <span class="status">{{ user.status }}</span>
      </p>
      <p>
        创建时间: <span>{{ formatDate(user.createdAt) }}</span>
      </p>
      <p>
        上次登录:
        <span>{{
          user.lastLogin ? formatDate(user.lastLogin) : "未登录"
        }}</span>
      </p>
      <p>
        已使用存储: <span>{{ user.storageUsed }} MB</span>
      </p>
      <p>
        邮箱: <span>{{ user.email || "未设置" }}</span>
      </p>
    </div>
  </div>
</template>

<script>
export default {
  name: "UserInfo",
  data() {
    return {
      user: {
        userId: 1,
        rolename: "user",
        username: "123456",
        email: null,
        avatarUrl: null,
        status: "active",
        storageUsed: 0,
        createdAt: "2025-03-18T13:28:35.297615",
        lastLogin: null
      }
    };
  },
  methods: {
    // 格式化日期
    formatDate(date) {
      const options = {
        year: "numeric",
        month: "long",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit"
      };
      return new Date(date).toLocaleDateString("zh-CN", options);
    }
  }
};
</script>
