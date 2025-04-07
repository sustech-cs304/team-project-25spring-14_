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
        },
      ],
    };
  },
  methods: {
    navigateTo(path, name) {
      this.activateNav = name;
      this.$router.push(path);
    },
  },
};
</script>
