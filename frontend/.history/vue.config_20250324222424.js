const { defineConfig } = require("@vue/cli-service");

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      // 匹配以 /api 开头的请求路径
      '/api': {
        target: 'http://localhost:8080',  // 后端接口地址（示例）
        changeOrigin: true,              // 是否改变请求头中的 Host
        pathRewrite: {
          '^/api': ''                    // 将 /api 重写为空，实际调用是 http://localhost:8080/xxx
        }
      }
    }
  }
});