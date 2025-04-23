<template>
  <div class="page-container">
    <div class="orders-container">
      <div class="page-header">
        <div class="back-link" @click="goBack">
          <i class="fas fa-arrow-left"></i> 返回
        </div>
        <h1>我的订单</h1>
      </div>
      
      <div v-if="loading" class="loading-container">
        <i class="fas fa-spinner fa-spin"></i>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="orders.length === 0" class="empty-state">
        <div class="empty-icon">
          <i class="fas fa-receipt"></i>
        </div>
        <h2>暂无订单</h2>
        <p>您还没有购买任何套餐</p>
        <button class="explore-btn" @click="$router.push('/home')">去逛逛</button>
      </div>
      
      <div v-else class="orders-list">
        <div 
          v-for="order in orders" 
          :key="order.id" 
          class="order-card"
          @click="goToOrderDetail(order.id)"
        >
          <div class="order-header">
            <div class="order-time">{{ formatDateTime(order.createTime) }}</div>
            <div class="order-status">{{ getOrderStatus(order) }}</div>
          </div>
          
          <div class="order-content">
            <div class="order-business">{{ order.businessName }}</div>
            <div class="order-title">{{ order.packageTitle }}</div>
            <div class="order-price">￥{{ order.finalPrice }}</div>
          </div>
          
          <div class="order-footer">
            <button class="view-code-btn">查看券码</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      loading: true,
      orders: [],
      userInfo: null
    }
  },
  mounted() {
    this.userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    this.fetchOrders();
  },
  methods: {
    async fetchOrders() {
      try {
        const response = await axios.get('/api/orders/user', {
          headers: {
            'UserId': this.userInfo.id
          }
        });
        this.orders = response.data;
      } catch (error) {
        console.error('获取订单列表失败:', error);
        this.orders = [];
      } finally {
        this.loading = false;
      }
    },
    formatDateTime(dateStr) {
      if (!dateStr) return '';
      
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      
      return `${year}-${month}-${day} ${hours}:${minutes}`;
    },
    getOrderStatus(order) {
      // 这里可以根据实际业务扩展更多状态
      return '未使用';
    },
    goToOrderDetail(orderId) {
      this.$router.push(`/coupon-code/${orderId}`);
    },
    goBack() {
      this.$router.push('/my');
    }
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.orders-container {
  max-width: 800px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.back-link {
  display: inline-flex;
  align-items: center;
  color: #4a90e2;
  margin-bottom: 10px;
  cursor: pointer;
  font-size: 1rem;
}

.back-link i {
  margin-right: 8px;
}

.page-header h1 {
  font-size: 1.8rem;
  margin: 0;
  color: #333;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 0;
  color: #666;
}

.loading-container i {
  font-size: 2rem;
  margin-bottom: 15px;
  color: #4a90e2;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 50px 0;
  text-align: center;
}

.empty-icon {
  font-size: 4rem;
  color: #ddd;
  margin-bottom: 20px;
}

.empty-state h2 {
  color: #333;
  margin-bottom: 10px;
}

.empty-state p {
  color: #999;
  margin-bottom: 20px;
}

.explore-btn {
  padding: 10px 20px;
  background: #4a90e2;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.explore-btn:hover {
  background: #357abd;
  transform: translateY(-2px);
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.order-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  cursor: pointer;
  transition: all 0.3s;
}

.order-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.order-time {
  color: #666;
  font-size: 0.9rem;
}

.order-status {
  color: #4a90e2;
  font-weight: 500;
}

.order-content {
  margin-bottom: 15px;
}

.order-business {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 5px;
}

.order-title {
  font-size: 1.2rem;
  color: #333;
  margin-bottom: 10px;
}

.order-price {
  font-size: 1.3rem;
  font-weight: bold;
  color: #e53935;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.view-code-btn {
  padding: 8px 15px;
  background: #4a90e2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.view-code-btn:hover {
  background: #357abd;
}
</style>
