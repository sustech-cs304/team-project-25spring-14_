<template>
  <div
    v-if="visible"
    class="card"
    :style="{ top: top + 'px', left: left + 'px' }"
    @click.self="closeCard"
  >
    <template v-if="!showDetailForm && !showPasswordForm">
      <div class="header">
        <img class="avatar" :src="avatarUrl" alt="avatar" />
        <div class="info">
          <div class="name">{{ name }}</div>
          <div class="role-status">{{ status }} - {{ role }}</div>
          <div class="email">{{ email }}</div>
        </div>
      </div>
      <div class="photos">
        <img v-for="url in photos" :key="url" :src="url" class="photo" />
      </div>
      <div class="album-link" @click="goToAlbum">
        访问 {{ name }} 的相册 -&gt;
      </div>
      <div class="album-link" @click="goToSpace">
        访问 {{ name }} 的空间 -&gt;
      </div>
      <div v-if="isSelf">
        <div class="button-row">
          <el-button type="primary" plain @click="goToDetail"
            >修改信息</el-button
          >
          <el-button type="warning" plain @click="goToPassword"
            >修改密码</el-button
          >
        </div>
        <div class="logout-row">
          <el-button type="danger" plain @click="logout">登出</el-button>
        </div>
      </div>
    </template>

    <template v-else-if="showDetailForm">
      <div class="detail-form">
        <label>
          用户名：
          <input type="text" v-model="name" />
        </label>
        <label>
          邮箱：
          <input type="text" v-model="email" />
        </label>
        <div class="upload-row">
          <label>头像：</label>
          <input type="file" accept="image/*" @change="handleAvatarUpload" />
        </div>
        <div class="confirm-row">
          <el-button @click="cancelEdit">取消</el-button>
          <el-button type="primary" @click="confirmEdit">确定</el-button>
        </div>
      </div>
    </template>

    <template v-else-if="showPasswordForm">
      <div class="detail-form">
        <label
          >原密码：
          <input type="password" v-model="oldPassword" />
        </label>
        <label
          >修改密码：
          <input type="password" v-model="newPassword" />
        </label>
        <label
          >确认密码：
          <input type="password" v-model="confirmPassword" />
        </label>
        <div class="confirm-row">
          <el-button @click="cancelEdit">取消</el-button>
          <el-button type="primary" @click="confirmPasswordEdit"
            >确定</el-button
          >
        </div>
      </div>
    </template>

    <button class="close-button" @click="closeCard">×</button>
  </div>
</template>

<script setup>
import { ref, computed, defineExpose } from "vue";
import { useRouter } from "vue-router";
import apiClient from "@/apiClient";
import { ElButton } from "element-plus";

const router = useRouter();

const visible = ref(false);
const top = ref(0);
const left = ref(0);
const showDetailForm = ref(false);
const showPasswordForm = ref(false);

const avatarUrl = ref("");
const name = ref("");
const email = ref("");
const role = ref("");
const status = ref("");
const oldPassword = ref("");
const newPassword = ref("");
const confirmPassword = ref("");
const albumOwnerId = ref("");

const isSelf = computed(() => {
  return albumOwnerId.value === localStorage.getItem("userId");
});
cons
console.log(localStorage.getItem("userId"));

async function showCard(userId, position) {
  albumOwnerId.value = userId;
  top.value = position.top - 100;
  left.value = position.left;
  visible.value = true;

  try {
    const response = await apiClient.get(`/user/getUser/${userId}`);
    console.log(userId);
    avatarUrl.value =
      response.data.data.avatarUrl ||
      new URL("@/assets/images/touxiang.svg", import.meta.url).href;
    console.log(userId);
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

function goToAlbum() {
  router.push({
    path: "/albums",
    query: { albumOwnerId: albumOwnerId.value },
  });
}

function goToSpace() {
  router.push(`/space?user=${encodeURIComponent(name.value)}`);
}

function goToDetail() {
  showDetailForm.value = true;
  showPasswordForm.value = false;
}

function goToPassword() {
  showPasswordForm.value = true;
  showDetailForm.value = false;
}

function logout() {
  localStorage.removeItem("jwtToken");
  router.push("/login");
}

function handleAvatarUpload(event) {
  const file = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = () => {
      avatarUrl.value = reader.result;
    };
    reader.readAsDataURL(file);
  }
}

function cancelEdit() {
  showDetailForm.value = false;
  showPasswordForm.value = false;
}

function confirmEdit() {
  // 提交逻辑待接入
  showDetailForm.value = false;
}

async function confirmPasswordEdit() {
  if (newPassword.value !== confirmPassword.value) {
    alert("两次输入的新密码不一致！");
    return;
  }
  // TODO: 提交密码更新
  try {
    const response = await apiClient.patch("/user/updatePwd", {
      old_pwd: oldPassword.value,
      new_pwd: newPassword.value,
      re_pwd: confirmPassword.value,
    });
    if (response.data.code === 0) {
      alert("密码修改成功！");
    } else {
      alert(`密码修改失败：${response.data.message}`);
    }
  } catch (error) {
    console.error("Error updating password:", error);
    alert("密码修改失败，请稍后重试！");
  }
  showPasswordForm.value = false;
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

.info .role-status {
  font-size: 14px;
  font-style: italic;
  color: #666;
}

.info .email {
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

.album-link {
  margin-top: 12px;
  padding: 12px;
  text-align: center;
  background-color: #f9f9f9;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-weight: 500;
}
.album-link:hover {
  background-color: #e0e0e0;
}

.button-row {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 16px;
}

.logout-row {
  display: flex;
  justify-content: center;
  margin-top: 12px;
}

.button-row .el-button {
  width: 120px;
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

.detail-form label {
  display: block;
  margin-bottom: 10px;
  font-size: 14px;
}
.detail-form input {
  width: 100%;
  padding: 6px;
  margin-top: 4px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.upload-row {
  margin-top: 16px;
  text-align: center;
}
.upload-row label {
  display: block;
  text-align: left;
  margin-bottom: 4px;
  font-size: 14px;
}
.confirm-row {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 16px;
}
</style>
