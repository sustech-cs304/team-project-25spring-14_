<template>
    <div class="main-container">
      <!-- 左侧导航栏 -->
      <nav class="sidebar">
        <div class="logo">
          <i class="album-icons icon-xiangji"></i>
          <span>智能相册</span>
        </div>
        <ul class="nav-list">
          <li
            v-for="item in navItems"
            :key="item.name"
            class="nav-item"
            :class="{ active: activeNav === item.name }"
            @click="navigateTo(item.path, item.name)"
          >
            <i :class="['album-icons', item.icon]"></i>
            <span>{{ item.label }}</span>
          </li>
        </ul>
        <div class="user-avatar" @click="logout">
          <img src="@/assets/images/kobe.jpg" alt="Avatar" />
        </div>
      </nav>
  
      <!-- 右侧内容区域 -->
      <main class="content">
        <router-view></router-view>
      </main>
    </div>
  </template>