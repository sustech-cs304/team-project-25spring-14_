import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import axios from "axios";
import qs from "qs";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";

const app = createApp(App);
app.use(router);
app.use(ElementPlus);
app.config.globalProperties.$axios = axios;

app.mount("#app");
