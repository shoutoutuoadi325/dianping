<template>
  <div class="my-page">
    <h1>欢迎来到个人中心</h1>
    <p>用户名：{{ userInfo.username }}</p>
    <p>Id: {{userInfo.userId}}</p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userInfo: {
        userId: '',
        username: '',
      }
    }
  },
  mounted() {
    const userData = localStorage.getItem('userInfo');
    if (userData) {
      this.userInfo = JSON.parse(userData);
    } else {
      this.$router.push('/login');
    }
  }
}
</script>