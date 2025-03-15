export default defineConfig({
    server: {
        port: 8081,
        // 添加以下配置
        historyApiFallback: true
    }
})