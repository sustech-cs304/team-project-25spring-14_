import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import HomeView from "../views/HomeView.vue";
import LoginView from "../views/LoginView.vue";
import MainView from "../views/MainView.vue";
import TryView from "@/views/TryView.vue";
import UserInfoView from "@/views/UserInfoView.vue";
import DiscoverView from "@/views/DiscoverView.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "login",
    component: LoginView,
  },
  {
    path: "/main", // 主界面路径
    name: "main", // 路由名称
    component: MainView, // 路由指向的主界面组件
  },
  {
    path: "/try", // 主界面路径
    name: "try", // 路由名称
    component: TryView, // 路由指向的主界面组件
  },
  {
    path: "/home"
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

router.beforeEach((to, from, next) => {
  const jwtToken = localStorage.getItem("jwtToken");
  if (to.name == "login" && jwtToken) {
    next({ name: "try" });
  } else if (to.name !== "try" && !jwtToken) {
    next({ name: "login" });
  } else {
    next();
  }
});

export default router;
