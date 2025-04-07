<template>
  <div
    v-if="visible"
    class="card"
    :style="{ top: top + 'px', left: left + 'px' }"
    @click.self="closeCard"
  >
    <div class="header">
      <img class="avatar" :src="avatarUrl" alt="avatar" />
      <div class="info">
        <div class="name">{{ name }}</div>
        <div class="id">状态: {{ wechatId }}</div>
        <div class="location">注册信息: {{ location }}</div>
      </div>
    </div>
    <div class="photos">
      <img v-for="url in photos" :key="url" :src="url" class="photo" />
    </div>
    <button class="close-button" @click="closeCard">×</button>
  </div>
</template>

<script setup>
import { ref, defineExpose } from "vue";

const visible = ref(false);
const top = ref(0);
const left = ref(0);

const avatarUrl = ref("");
const name = ref("");
const wechatId = ref("");
const location = ref("");
const photos = ref([]);

async function showCard(position) {
  top.value = position.top;
  left.value = position.left;
  visible.value = true;

  try {
    const res = await fetch("/api/userinfo");
    const data = await res.json();
    avatarUrl.value = data.avatarUrl || require("@/assets/images/kobe.jpg");
    name.value = data.username;
    wechatId.value = data.status;
    location.value = `注册时间: ${data.createdAt?.slice(0, 10) || ""}${
      data.lastLogin ? " / 上次登录: " + data.lastLogin.slice(0, 10) : " / 从未登录"
    }`;
    photos.value = [
      require("@/assets/images/老大.jpg"),
      require("@/assets/images/老大.jpg"),
      require("@/assets/images/老大.jpg"),
    ];
  } catch (error) {
    console.error("获取用户信息失败:", error);
  }
}

function closeCard() {
  visible.value = false;
}

defineExpose({ showCard });
</script>

<style scoped>
.card {
  position: fixed;
  background-color: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  z-index: 99999;
  padding: 20px;
  border-radius: 12px;
  width: 350px;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  margin-right: 12px;
}

.info .name {
  font-weight: bold;
  font-size: 18px;
}

.info .id,
.info .location {
  font-size: 14px;
  color: #666;
}

.photos {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.photo {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.close-button {
  position: absolute;
  top: 8px;
  right: 12px;
  border: none;
  background: none;
  font-size: 24px;
  cursor: pointer;
}
</style>
