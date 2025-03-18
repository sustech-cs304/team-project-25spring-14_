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
        <p>角色: <span>{{ user.rolename }}</span></p>
        <p>状态: <span class="status">{{ user.status }}</span></p>
        <p>创建时间: <span>{{ formatDate(user.createdAt) }}</span></p>
        <p>上次登录: <span>{{ user.lastLogin ? formatDate(user.lastLogin) : '未登录' }}</span></p>
        <p>已使用存储: <span>{{ user.storageUsed }} MB</span></p>
        <p>邮箱: <span>{{ user.email || '未设置' }}</span></p>
      </div>
    </div>
  </template>