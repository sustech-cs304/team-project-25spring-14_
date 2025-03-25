<template>
  <div class="main">
    <div class="usercenter-top">
      <div class="user-avator">
        <img src="src/assets/images/老大.jpg" alt="avator" />
      </div>
      <div class="user-name"></div>
      <div class="user-id"></div>
      <div class="user-status"></div>
      <div class="user-storage"></div>
    </div>
    <div class="usercenter-body">
      <div class="usercenter-left">
        <div class="usercenter-nav">
          <nav class="nav-item">
            <div class="icon">
              <i class=""></i>
              <span>个人中心</span>
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
          </nav>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "UsercenterView",
  data() {
    return {
      activateNav: "userinfo",
      navItems: [
        {
          name: "userinfo",
          icon: "icon-u138",
          label: "个人信息",
    }
      ],
  },
};
</script>
