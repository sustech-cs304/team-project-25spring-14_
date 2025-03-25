const { defineConfig } = require("@vue/cli-service");

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      // 匹配以 /api 开头的请求路径
      "/": {
        target: "http://localhost:8080", // 后端接口地址（示例）
        changeOrigin: fa, // 是否改变请求头中的 Host
      },
    },
  },
});
