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
        <div class="role">{{ role }}</div>
        <div class="status">{{ status }}</div>
        <div class="email">{{ email }}</div>
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
import apiClient from "@/apiClient";

const visible = ref(false);
const top = ref(0);
const left = ref(0);

const avatarUrl = ref("");
const name = ref("");
const email = ref("");
const role = ref("");
const status = ref("");
const photos = ref([]);

async function showCard(user, position) {
  top.value = position.top;
  left.value = position.left;
  visible.value = true;

  try {
    const response = await apiClient.get("/user/userInfo");
    avatarUrl.value =
      response.data.data.avatarUrl ||
      new URL("@/assets/images/touxiang.svg", import.meta.url).href;
    name.value = response.data.data.username;
    email.value = response.data.data.email;
    role.value = response.data.data.rolename;
    status.value = response.data.data.status;
  } catch (error) {
    console.error("Error fetching user data:", error);
  }
  console.log(name.value);
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
  font-size: 22px;
}

.info .role,
.info . {
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
