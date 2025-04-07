import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import axios from "axios";
import qs from "qs";

createApp(App).use(router).mount("#app");
