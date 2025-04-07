const { defineConfig } = require("@vue/cli-service");

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      // 匹配以 /api 开头的请求路径
      '/api': {
        target: 'http://localhost:8080',  
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''                  
      }
    }
  }
});