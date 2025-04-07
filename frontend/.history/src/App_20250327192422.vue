<template>
  <div class="app-container">
    <SideBar />
    <transition name="fade">
      <router-view />
    </transition>
  </div>
</template>

<script>
import SideBar from "@/components/SideBar.vue";

export default {
  components: {
    SideBar,
  },
};
</script>

<style>
*,
*::after,
*::before {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  user-select: none;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.app-container {
  display: flex;
}
</style>
