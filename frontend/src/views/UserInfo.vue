<template>
  <div class="user-info-container">
    <div class="user-card">
      <div class="user-header">
        <div class="avatar">
          {{ userInfo.username?.charAt(0)?.toUpperCase() }}
        </div>
        <h2>{{ userInfo.username }}</h2>
      </div>
      
      <div class="info-section">
        <div class="info-item">
          <i class="fas fa-id-card"></i>
          <span>ID: {{ userInfo.id }}</span>
        </div>
        
        <div class="info-item">
          <i class="fas fa-clock"></i>
          <span>注册时间: {{ formatDate() }}</span>
        </div>
      </div>

      <div class="action-buttons">
        <button class="edit-btn">
          <i class="fas fa-edit"></i>
          修改密码
        </button>
        <button class="logout-btn" @click="handleLogout">
          <i class="fas fa-sign-out-alt"></i>
          退出登录
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userInfo: {
        id: '',
        username: '',
      }, 
      user: {}
    }
  },
  mounted() {
    const userData = localStorage.getItem('userInfo');
    if (userData) {
      this.userInfo = JSON.parse(userData);
      this.user = JSON.parse(userData);
    } else {
      this.$router.push('/login');
    }
  },
  methods: {
    handleLogout() {
      localStorage.removeItem('userInfo')
      this.$router.push('/login')
    },
    formatDate() {
      return new Date().toLocaleDateString()
    }
  }
}
</script>

<style scoped>
.user-info-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.user-card {
  background: white;
  border-radius: 15px;
  padding: 30px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.user-header {
  text-align: center;
  margin-bottom: 30px;
}

.avatar {
  width: 100px;
  height: 100px;
  background: linear-gradient(45deg, #4a90e2, #357abd);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  color: white;
  margin: 0 auto 15px;
}

.info-section {
  margin: 30px 0;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 10px;
}

.info-item i {
  margin-right: 15px;
  color: #4a90e2;
  font-size: 20px;
}

.action-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-top: 30px;
}

.edit-btn, .logout-btn {
  padding: 12px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 16px;
  transition: all 0.3s;
}

.edit-btn {
  background: #4a90e2;
  color: white;
}

.edit-btn:hover {
  background: #357abd;
}

.logout-btn {
  background: #dc3545;
  color: white;
}

.logout-btn:hover {
  background: #c82333;
}
</style>