<template>
  <div class="page-container">
    <h1>用户信息</h1>
    <div class="my-page">
      <h1>欢迎来到个人中心</h1>
      <p>用户名：{{ userInfo.username }}</p>
      <p>ID: {{ userInfo.id }}</p>
      <button @click="handleLogout">退出登录</button>
    </div>
    <!-- 此处添加用户信息展示 -->
  </div>
</template>

<script>
export default {
  data() {
    return {
      userInfo: {
        id: '',
        username: '',
      }, user: {}
    }
  },
  mounted() {// 从本地存储加载用户信息
    const userData = localStorage.getItem('userInfo');
    if (userData) {
      this.userInfo = JSON.parse(userData);
      this.user = JSON.parse(userData);
    } else {
      this.$router.push('/login');
    }
  }, methods: {
    handleLogout() {
      localStorage.removeItem('userInfo')
      this.$router.push('/login')
    }
  }
}
</script>