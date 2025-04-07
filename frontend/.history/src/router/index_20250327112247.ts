import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import HomeView from "../views/HomeView.vue";
import LoginView from "../views/LoginView.vue";
import UsercenterView from "@/views/home/UsercenterView.vue";
import DiscoverView from "@/views/home/DiscoverView.vue";
import PhotosView from "@/views/home/PhotosView.vue";
import ImageEditor from "@/views/ImageEditor.vue";
import AlbumsView from "@/views/home/AlbumsView.vue";
import AlbumDetailView from "@/views/home/AlbumDetailView.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "login",
    component: LoginView,
  },
  {
    path: "/discover",
    name: "discover",
    component: DiscoverView,
  },
  {
    path: "/photos",
    name: "photos",
    component: PhotosView,
  },
  {
    path: "/usercenter",
    name: "usercenter",
    component: UsercenterView,
  },
  {
    path: "/edit",
    name: "edit",
    component: ImageEditor,
  },
  {
    path: "/albums",
    name: "albums",
    component: AlbumsView,
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem("jwtToken");

  if (!isAuthenticated && to.path !== "/") {
    next("/");
  } else if (isAuthenticated && to.path === "/") {
    next("/home");
  } else {
    next();
  }
});
export default router;
