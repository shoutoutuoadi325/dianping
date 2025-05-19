<template>
  <div class="invitation-container">
    <button class="back-btn" @click="$router.back()">
      <i class="fas fa-arrow-left"></i>
      返回
    </button>

    <div class="invitation-card">
      <h2>我的邀请码</h2>
      <div class="code-display">
        {{ userInfo.invitationCode }}
      </div>
      <button class="copy-btn" @click="copyCode">
        <i class="fas fa-copy"></i>
        复制邀请码
      </button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userInfo: {}
    }
  },
  mounted() {
    const userData = localStorage.getItem('userInfo')
    if (userData) {
      this.userInfo = JSON.parse(userData)
    } else {
      this.$router.push('/auth')
    }
  },
  methods: {
    copyCode() {
      navigator.clipboard.writeText(this.userInfo.invitationCode)
        .then(() => alert('邀请码已复制到剪贴板'))
        .catch(err => console.error('复制失败:', err))
    }
  }
}
</script>

<style scoped>
.invitation-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.invitation-card {
  background: white;
  border-radius: 15px;
  padding: 30px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.back-btn {
  position: fixed;
  top: 20px;
  left: 20px;
  padding: 12px 25px;
  background: #666;
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  z-index: 1000;
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
}

.code-display {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 10px;
  font-size: 2rem;
  text-align: center;
  letter-spacing: 3px;
  margin-bottom: 20px;
  font-family: monospace;
  color: #4a90e2;
}

.copy-btn {
  width: 100%;
  padding: 12px;
  background: #4a90e2;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 30px;
}
h3 {
  color: #666;
  margin-bottom: 15px;
}

.share-buttons {
  display: flex;
  gap: 15px;
}

.share-btn {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: white;
}

.wechat {
  background: #07c160;
}

.qq {
  background: #12b7f5;
}
</style>
