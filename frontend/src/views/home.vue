<template>
  <div class="container">
    <!-- 新增搜索区域 -->
    <div class="search-section">
      <div class="search-box">
        <input
            v-model="searchKeyword"
            @keyup.enter="handleSearch"
            placeholder="请输入商家名称..."
        />
        <button @click="handleSearch" class="search-btn">搜索</button>
      </div>

      <!-- 搜索历史 -->
      <div class="history-panel">
        <h3>搜索历史</h3>
        <div>
          <button v-if="showExpand" @click="toggleExpand" class="expand-btn">
            {{ isExpanded ? '收起' : '展开' }}
          </button>
          <button
              v-if="searchHistory.length > 0"
              @click="clearAllHistory"
              class="clear-all"
          >
            清空全部
          </button>
        </div>
      </div>

      <ul v-if="searchHistory.length > 0">
        <li v-for="(item, index) in displayedHistory" :key="item.id">
          <span class="keyword" @click="quickSearch(item.keyword)">{{ item.keyword }}</span>
          <button @click="deleteHistory(item.id)" class="delete-btn">×</button>
        </li>
      </ul>
      <p v-else class="empty-tip">暂无搜索记录</p>
    </div>
  </div>

  <!-- 原有用户信息 -->
  <div class="user-info" @click="goToUserInfo">
    <span class="username">{{ userInfo.username }}</span>
    <div class="avatar">
      {{ userInfo.username?.charAt(0)?.toUpperCase() }}
    </div>
  </div>

  <button class="logout-btn" @click="handleLogout">
    <i class="fas fa-sign-out-alt"></i>
    退出登录
  </button>

</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      userInfo: {
        id: '',
        username: '未登录用户'
      },
      isExpanded: false,  // 新增展开状态
      displayLimit: 5,     // 默认显示历史记录数量
      searchKeyword: '',
      searchHistory: []
    }
  },
  computed: {
    // 计算属性
    displayedHistory() {
      return this.isExpanded
          ? this.searchHistory
          : this.searchHistory.slice(0, this.displayLimit)
    },
    // 控制展开按钮显示
    showExpand() {
      return this.searchHistory.length > this.displayLimit
    }
  },
  mounted() {
    const userData = localStorage.getItem('userInfo')
    if (userData) {
      this.userInfo = JSON.parse(userData)
      this.loadSearchHistory()
    } else {
      this.$router.push('/login')
    }
  },
  methods: {
    // 展开/收起切换方法
    toggleExpand() {
      this.isExpanded = !this.isExpanded
    },
    async loadSearchHistory() {
      try {
        const response = await axios.get('/api/search', {
          headers: {
            'UserId': this.userInfo.id
          }
        })
        this.searchHistory = response.data
      } catch (error) {
        console.error('加载搜索历史失败:', error)
      }
    },
    async handleSearch() {
      if (!this.searchKeyword.trim()) return

      try {
        // 保存搜索记录
        await axios.post('/api/search', null, {
          params: {keyword: this.searchKeyword},
          headers: {
            'UserId': this.userInfo.id
          }
        })
        this.searchKeyword = ''
        await this.loadSearchHistory()
      } catch (error) {
        console.error('搜索失败:', error)
      }
    },
    quickSearch(keyword) {
      this.searchKeyword = keyword
      this.handleSearch()
    },
    async deleteHistory(id) {
      try {
        await axios.delete(`/api/search/${id}`, {
          headers: {
            'UserId': this.userInfo.id
          }
        })
        await this.loadSearchHistory()
      } catch (error) {
        console.error('删除失败:', error)
      }
    },
    async clearAllHistory() {
      try {
        await axios.delete('/api/search', {
          headers: {
            'UserId': this.userInfo.id
          }
        })
        this.searchHistory = []
      } catch (error) {
        console.error('清空失败:', error)
      }
    },
    handleLogout() {
      localStorage.removeItem('userInfo')
      this.$router.push('/login')
    },
    goToUserInfo() {
      this.$router.push('/user-info')
    }
  }
}
</script>

<style scoped>
.expand-btn {
  background: #4a90e2;
  color: white;
  padding: 6px 12px;
  border-radius: 15px;
  margin-right: 10px;
  transition: all 0.3s;
}

.expand-btn:hover {
  background: #357abd;
}

.history-header div {
  display: flex;
  align-items: center;
}

.search-section {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.search-box {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
}

.search-box input {
  flex: 1;
  padding: 12px;
  border: 2px solid #4a90e2;
  border-radius: 25px;
  font-size: 16px;
}

.search-btn {
  padding: 12px 30px;
  background: #4a90e2;
  border-radius: 25px;
  transition: all 0.3s;
}

.search-btn:hover {
  background: #357abd;
  transform: scale(1.05);
}

.history-panel {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.history-header h3 {
  color: #333;
  margin: 0;
}

.clear-all {
  background: #e74c3c;
  padding: 6px 12px;
  border-radius: 15px;
}

ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.keyword {
  cursor: pointer;
  transition: color 0.2s;
}

.keyword:hover {
  color: #4a90e2;
}

.delete-btn {
  background: none;
  border: none;
  color: #999;
  font-size: 20px;
  cursor: pointer;
  padding: 0 8px;
}

.delete-btn:hover {
  color: #e74c3c;
}

.empty-tip {
  color: #999;
  text-align: center;
  margin: 20px 0;
}

/* 调整原有用户信息位置 */
.user-info {
  top: 20px;
  right: 30px;
}

.container {
  padding: 2rem;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.user-info {
  position: fixed;
  top: 20px;
  right: 30px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.user-info:hover {
  transform: scale(1.05);
}

.avatar {
  width: 40px;
  height: 40px;
  background: #4a90e2;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
}

.username {
  font-size: 1.1rem;
  color: #333;
  font-weight: 500;
}

.logout-btn {
  position: fixed;
  bottom: 30px;
  right: 30px;
  padding: 12px 25px;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
}

.logout-btn:hover {
  background: #c0392b;
  transform: scale(1.05);
}

.fas {
  font-size: 2.5rem;
}
</style>