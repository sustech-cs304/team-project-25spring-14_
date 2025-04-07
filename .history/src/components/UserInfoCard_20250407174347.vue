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
        <div class="email">{{ email }}</div>
        <div class="role">{{ role }}</div>
        <div class="id">微信号: {{ wechatId }}</div>
        <div class="location">地区: {{ location }}</div>
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

function showCard(user, position) {
  avatarUrl.value = user.avatarUrl;
  name.value = user.name;
  wechatId.value = user.wechatId;
  location.value = user.location;
  photos.value = user.photos || [];
  top.value = position.top;
  left.value = position.left;
  visible.value = true;
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
