<template>
  <div class="container">
    <!-- 用户信息展示 -->
    <div class="user-info" @click="goToUserInfo">
      <span class="username">{{ userInfo.username }}</span>
      <div class="avatar">
        {{ userInfo.username?.charAt(0)?.toUpperCase() }}
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-section">
      <div class="search-bar">
        <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索商家（火锅、奶茶等）"
            @keyup.enter="searchMerchants"
        >
        <button @click="searchMerchants">
          <i class="fas fa-search"></i>
        </button>
      </div> <!-- 在搜索栏下方添加历史记录区域 -->
      <div class="search-history" v-if="searchHistory.length > 0">
        <h4>搜索历史：</h4>
        <div class="history-tags">
      <span
          v-for="(item, index) in searchHistory"
          :key="index"
          class="history-tag"
          @click="selectHistory(item)"
      >
        {{ item }}
        <i class="fas fa-times" @click.stop="removeHistory(index)"></i>
      </span>
        </div>
      </div>

      <div class="filters">
        <div class="filter-group">
          <label>评分：</label>
          <select v-model="minScore">
            <option value="">全部</option>
            <option value="4.0">4.0分以上</option>
            <option value="4.5">4.5分以上</option>
          </select>
        </div>

        <div class="filter-group">
          <label>人均消费：</label>
          <input type="number" v-model="maxAvgConsume" placeholder="最高价格">
        </div>

        <div class="filter-group">
          <label>排序：</label>
          <select v-model="sortBy">
            <option value="default">综合排序</option>
            <option value="score">评分最高</option>
            <option value="consumption">人均最低</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 商家列表 -->
    <div class="merchant-list">
      <div v-if="loading" class="loading">加载中...</div>

      <div v-for="merchant in merchants" :key="merchant.id"
           class="merchant-card" @click="showMerchantDetail(merchant.id)">
        <img :src="merchant.photoUrl" class="merchant-photo">
        <div class="merchant-info">
          <h3>{{ merchant.merchantName }}</h3>
          <div class="rating">
            <span class="score">{{ merchant.score.toFixed(1) }}</span>
            <div class="stars">
              <i v-for="n in 5" :key="n"
                 class="fas fa-star"
                 :class="{ 'active': n <= Math.floor(merchant.score) }"></i>
            </div>
          </div>
          <p class="location">{{ merchant.location }}</p>
          <p class="consumption">人均￥{{ merchant.averageConsumption.toFixed(0) }}</p>
        </div>
      </div>
    </div>

    <!-- 退出按钮 -->
    <button class="logout-btn" @click="handleLogout">
      <i class="fas fa-sign-out-alt"></i>
      退出登录
    </button>
  </div>
</template>

<script>
import {ref, watch} from 'vue'
import {useRouter} from 'vue-router'
import axios from 'axios'

export default {
  setup() {
    const router = useRouter()
    const userInfo = ref({
      id: '',
      username: '未登录用户'
    })
    const searchHistory = ref(JSON.parse(localStorage.getItem('searchHistory')) || [])

    // 搜索相关状态
    const searchKeyword = ref('')
    const minScore = ref(null)
    const maxAvgConsume = ref(null)
    const sortBy = ref('default')
    const merchants = ref([])
    const loading = ref(false)

    // 初始化用户数据
    const initUser = () => {
      const userData = localStorage.getItem('userInfo')
      if (!userData) router.push('/login')
      userInfo.value = JSON.parse(userData)
    }

    // 搜索商家
    const searchMerchants = async () => {
      if (searchKeyword.value.trim()) {
        // 去重并保留最近5条
        searchHistory.value = [
          searchKeyword.value.trim(),
          ...searchHistory.value.filter(item => item !== searchKeyword.value.trim())
        ].slice(0, 5)

        localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value))
      }
      try {
        loading.value = true
        const response = await axios.get('/api/merchants/search', {
          params: {keyword: searchKeyword.value}
        })
        merchants.value = response.data
      } catch (error) {
        console.error('搜索失败:', error)
      } finally {
        loading.value = false
      }
    }
    const selectHistory = (keyword) => {
      searchKeyword.value = keyword
      searchMerchants()
    }

    const removeHistory = (index) => {
      searchHistory.value.splice(index, 1)
      localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value))
    }
    // 组合筛选和排序
    const filterAndSort = async () => {
      try {
        loading.value = true
        const params = {
          minScore: minScore.value,
          maxAvgConsume: maxAvgConsume.value,
          sortBy: sortBy.value
        }
        const response = await axios.get('/api/merchants', {params})
        merchants.value = response.data
      } catch (error) {
        console.error('获取数据失败:', error)
      } finally {
        loading.value = false
      }
    }

    // 监听筛选条件变化
    watch([minScore, maxAvgConsume, sortBy], () => {
      filterAndSort()
    })

    // 查看详情
    const showMerchantDetail = (id) => {
      router.push(`/merchants/${id}`)
    }

    // 退出登录
    const handleLogout = () => {
      localStorage.removeItem('userInfo')
      router.push('/login')
    }

    initUser()

    return {
      userInfo,
      searchKeyword,
      minScore,
      maxAvgConsume,
      sortBy,
      merchants,
      loading,
      searchMerchants,
      showMerchantDetail,
      handleLogout
    }
  }
}
</script>

<style scoped>
.search-history {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.history-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.history-tag {
  background: #f0f0f0;
  padding: 8px 15px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.history-tag:hover {
  background: #e0e0e0;
}

.fa-times {
  font-size: 12px;
  color: #999;
}

.fa-times:hover {
  color: #666;
}
.search-section {
  max-width: 1200px;
  margin: 40px auto;
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-bar input {
  flex: 1;
  padding: 12px 20px;
  border: 2px solid #eee;
  border-radius: 25px;
  font-size: 16px;
}

.filters {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.merchant-list {
  max-width: 1200px;
  margin: 0 auto;
}

.merchant-card {
  display: flex;
  gap: 20px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s;
}

.merchant-card:hover {
  transform: translateY(-3px);
}

.merchant-photo {
  width: 200px;
  height: 150px;
  border-radius: 8px;
  object-fit: cover;
}

.merchant-info h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.score {
  color: #ff6b6b;
  font-weight: bold;
  font-size: 18px;
}

.stars {
  color: #ddd;
}

.stars .active {
  color: #ffd700;
}

.location {
  color: #666;
  margin: 8px 0;
}

.consumption {
  color: #4ecdc4;
  font-weight: bold;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #666;
}
</style>