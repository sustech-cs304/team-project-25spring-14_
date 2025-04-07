<template>
  <div
    v-if="visible"
    class="card"
    :style="{ top: top + 'px', left: left + 'px' }"
    @click.self="closeCard"
  >
    <div class="header">
      <label class="avatar-label">
        <img class="avatar" :src="avatarUrl" alt="avatar" />
        <input type="file" style="display: none" @change="changeAvatar" />
      </label>
      <div class="info">
        <div
          v-if="!editingName"
          class="name"
          @click="enableEdit('name')"
        >{{ name }}</div>
        <div
          v-else
          class="name editable"
          contenteditable="true"
          @blur="saveEdit('name')"
          @keydown.enter.prevent="saveEdit('name')"
          ref="nameEditor"
        >{{ editableName }}</div>

        <div
          v-if="!editingEmail"
          class="email"
          @click="enableEdit('email')"
        >{{ email }}</div>
        <div
          v-else
          class="email editable"
          contenteditable="true"
          @blur="saveEdit('email')"
          @keydown.enter.prevent="saveEdit('email')"
          ref="emailEditor"
        >{{ editableEmail }}</div>

        <div
          v-if="!editingRole"
          class="role"
          @click="enableEdit('role')"
        >{{ role }}</div>
        <div
          v-else
          class="role editable"
          contenteditable="true"
          @blur="saveEdit('role')"
          @keydown.enter.prevent="saveEdit('role')"
          ref="roleEditor"
        >{{ editableRole }}</div>
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
const photos = ref([]);

const editingName = ref(false);
const editingEmail = ref(false);
const editingRole = ref(false);
const editableName = ref("");
const editableEmail = ref("");
const editableRole = ref("");

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
    editableName.value = name.value;
    editableEmail.value = email.value;
    editableRole.value = role.value;
  } catch (error) {
    console.error("Error fetching user data:", error);
  }
  console.log(name.value);
}

function closeCard() {
  visible.value = false;
}

function enableEdit(field) {
  if (field === "name") {
    editableName.value = name.value;
    editingName.value = true;
  } else if (field === "email") {
    editableEmail.value = email.value;
    editingEmail.value = true;
  } else if (field === "role") {
    editableRole.value = role.value;
    editingRole.value = true;
  }
}

function saveEdit(field) {
  if (field === "name") {
    name.value = editableName.value;
    editingName.value = false;
  } else if (field === "email") {
    email.value = editableEmail.value;
    editingEmail.value = false;
  } else if (field === "role") {
    role.value = editableRole.value;
    editingRole.value = false;
  }
}

function changeAvatar(event) {
  const file = event.target.files[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = () => {
    avatarUrl.value = reader.result;
  };
  reader.readAsDataURL(file);
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

.avatar-label {
  cursor: pointer;
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

.editable {
  border-bottom: 1px dashed #9ca3af;
  outline: none;
}

.editable:focus {
  border-color: #4b70e2;
}
</style>
