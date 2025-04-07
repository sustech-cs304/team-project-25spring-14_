import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import HomeView from "../views/HomeView.vue";
import LoginView from "../views/LoginView.vue";
import SettingsView from "@/views/home/SettingsView.vue";
import DiscoverView from "@/views/home/DiscoverView.vue";
import PhotosView from "@/views/home/PhotosView.vue";

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
        path: "se",
        name: "userinfo",
        component: UserInfoView,
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
