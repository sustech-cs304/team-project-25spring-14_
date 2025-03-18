import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import HomeView from "../views/HomeView.vue";
import LoginView from "../views/LoginView.vue";
import MainView from "../views/MainView.vue";
import TryView from "@/views/TryView.vue";
import UserInfoView from "@/views/home/UserInfoView.vue";
import DiscoverView from "@/views/home/DiscoverView.vue";
import PhotosView from "@/views/PhotosView.vue";

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
    path: "/home",
    name: "home",
    component: HomeView,
    redirect: "/home/discover",
    children: [
      {
        path: "discover",
        name: "discover",
        component: DiscoverView,
      },
      {
        path: "photos",
        name: "photos",
        component: PhotosView,
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

router.beforeEach((to, from, next) => {
  const jwtToken = localStorage.getItem("jwtToken");
  if (to.name == "login" && jwtToken) {
    next({ name: "home" });
  } else if (to.name !== "home" && !jwtToken) {
    next({ name: "login" });
  } else {
    next();
  }
});

export default router;
