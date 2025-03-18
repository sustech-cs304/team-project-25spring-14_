import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import HomeView from "../views/HomeView.vue";
import LoginView from "../views/LoginView.vue";
<<<<<<< HEAD
import SettingsView from "@/views/home/SettingsView.vue";
import DiscoverView from "@/views/home/DiscoverView.vue";
import PhotosView from "@/views/home/PhotosView.vue";
=====
import ImageEditor from "@/views/ImageEditor.vue";
>>>>>>> 5e3d76056e1c3d0f3c3af5a2c96bddabaa0fcdd4

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "login",
    component: LoginView,
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
      {
        path: "settings",
        name: "settings",
        component: SettingsView,
      },
    ],
  },
  {
    path: "/edit", // 主界面路径
    name: "edit", // 路由名称
    component: ImageEditor, // 路由指向的主界面组件
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
