import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import HomeView from "../views/HomeView.vue";
import LoginView from "../views/LoginView.vue";
import SettingsView from "@/views/home/SettingsView.vue";
import DiscoverView from "@/views/home/DiscoverView.vue";
import PhotosView from "@/views/home/PhotosView.vue";
import ImageEditor from "@/views/ImageEditor.vue";

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
  const isAuthenticated = localStorage.getItem("jwt_token");

  // 如果路由需要认证且用户没有登录，重定向到登录页面
  if (to.meta.requiresAuth && !isAuthenticated) {
    next("/login");
  } else {
    next();
  }
});
export default router;
