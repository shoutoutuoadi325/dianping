<template>
  <div class="page-container">
    <div class="coupons-container">
      <div class="page-header">
        <div class="back-link" @click="goBack">
          <i class="fas fa-arrow-left"></i> 返回
        </div>
        <h1>我的卡包</h1>
      </div>
      
      <div class="tab-controls">
        <button 
          v-for="tab in tabs" 
          :key="tab.value" 
          :class="['tab-btn', { active: currentTab === tab.value }]"
          @click="currentTab = tab.value"
        >
          {{ tab.label }} 
          <span v-if="getTabCount(tab.value) > 0" class="tab-count">{{ getTabCount(tab.value) }}</span>
        </button>
      </div>
      
      <div v-if="loading" class="loading-container">
        <i class="fas fa-spinner fa-spin"></i>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="getFilteredCoupons().length === 0" class="empty-state">
        <div class="empty-icon">
          <i class="fas fa-ticket-alt"></i>
        </div>
        <h2>暂无优惠券</h2>
        <p v-if="currentTab === 'unused'">您还没有可用的优惠券</p>
        <p v-else-if="currentTab === 'used'">您还没有已使用的优惠券</p>
        <p v-else>您还没有过期的优惠券</p>
        <div v-if="currentTab === 'unused'">
          <button class="explore-btn" @click="$router.push('/new-user-coupons')">领取优惠券</button>
        </div>
      </div>
      
      <div v-else class="coupons-list">
        <div 
          v-for="coupon in getFilteredCoupons()" 
          :key="coupon.id" 
          class="coupon-card"
          :class="[getCouponClass(coupon), 
                 { 'used': coupon.used, 'expired': isExpired(coupon) }]"
        >
          <div class="coupon-left">
            <div class="coupon-value">{{ formatCouponValue(coupon) }}</div>
            <div class="coupon-condition" v-if="coupon.minAmount > 0">
              满{{ coupon.minAmount }}元可用
            </div>
            <div class="coupon-condition" v-else>
              无门槛使用
            </div>
          </div>
          
          <div class="coupon-right">
            <div class="coupon-title">{{ coupon.title }}</div>
            <div class="coupon-desc">{{ getCouponDescription(coupon) }}</div>
            <div class="coupon-validity">{{ formatValidity(coupon) }}</div>
            
            <div class="coupon-status" v-if="coupon.used">
              <i class="fas fa-check-circle"></i> 已使用
            </div>
            <div class="coupon-status" v-else-if="isExpired(coupon)">
              <i class="fas fa-times-circle"></i> 已过期
            </div>
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
      coupons: [],
      userInfo: null,
      currentTab: 'unused', // 默认显示未使用券
      tabs: [
        { label: '未使用', value: 'unused' },
        { label: '已使用', value: 'used' },
        { label: '已过期', value: 'expired' }
      ]
    }
  },
  mounted() {
    this.userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    this.fetchCoupons();
  },
  methods: {
    async fetchCoupons() {
      try {
        const response = await axios.get('/api/coupons/user', {
          headers: {
            'UserId': this.userInfo.id
          }
        });
        this.coupons = response.data;
      } catch (error) {
        console.error('获取优惠券列表失败:', error);
        this.coupons = [];
      } finally {
        this.loading = false;
      }
    },
    getFilteredCoupons() {
      switch (this.currentTab) {
        case 'unused':
          return this.coupons.filter(coupon => !coupon.used && !this.isExpired(coupon));
        case 'used':
          return this.coupons.filter(coupon => coupon.used);
        case 'expired':
          return this.coupons.filter(coupon => !coupon.used && this.isExpired(coupon));
        default:
          return this.coupons;
      }
    },
    getTabCount(tabValue) {
      switch (tabValue) {
        case 'unused':
          return this.coupons.filter(coupon => !coupon.used && !this.isExpired(coupon)).length;
        case 'used':
          return this.coupons.filter(coupon => coupon.used).length;
        case 'expired':
          return this.coupons.filter(coupon => !coupon.used && this.isExpired(coupon)).length;
        default:
          return 0;
      }
    },
    isExpired(coupon) {
      if (coupon.endDate) {
        return new Date(coupon.endDate) < new Date();
      }
      return false;
    },
    formatCouponValue(coupon) {
      switch (coupon.type) {
        case 'FIXED_AMOUNT':
          return `￥${coupon.value}`;
        case 'FIX_TO_AMOUNT':
          return `￥${coupon.value}`;
        case 'PERCENTAGE':
          return `${coupon.value}折`;
        default:
          return '';
      }
    },
    getCouponDescription(coupon) {
      let description = [];
      
      // 适用范围
      if (coupon.applicableCategory) {
        description.push(`适用品类: ${coupon.applicableCategory}`);
      }
      
      if (coupon.applicableBusiness) {
        description.push(`适用商家: ${coupon.applicableBusiness}`);
      }
      
      // 最大抵扣
      if (coupon.maxDiscount > 0) {
        description.push(`最多减￥${coupon.maxDiscount}`);
      }
      
      return description.join(' | ');
    },
    formatValidity(coupon) {
      if (coupon.endDate) {
        return `有效期至: ${new Date(coupon.endDate).toLocaleDateString()}`;
      } else if (coupon.validDays) {
        return `领取后${coupon.validDays}天内有效`;
      } else {
        return '长期有效';
      }
    },
    getCouponClass(coupon) {
      switch (coupon.type) {
        case 'FIXED_AMOUNT':
          return 'fixed-amount';
        case 'FIX_TO_AMOUNT':
          return 'fix-to-amount';
        case 'PERCENTAGE':
          return 'percentage';
        default:
          return '';
      }
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

.coupons-container {
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

.tab-controls {
  display: flex;
  margin-bottom: 20px;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.tab-btn {
  flex: 1;
  padding: 15px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  color: #666;
  position: relative;
  transition: all 0.3s;
}

.tab-btn.active {
  color: #4a90e2;
  font-weight: 500;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 20%;
  right: 20%;
  height: 3px;
  background: #4a90e2;
  border-radius: 3px;
}

.tab-count {
  display: inline-block;
  background: #4a90e2;
  color: white;
  font-size: 0.8rem;
  width: 20px;
  height: 20px;
  line-height: 20px;
  text-align: center;
  border-radius: 50%;
  margin-left: 5px;
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

.coupons-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.coupon-card {
  display: flex;
  border-radius: 8px;
  overflow: hidden;
  height: 120px;
  position: relative;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.coupon-card::before {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0;
  left: 100px; /* 左边宽度 */
  width: 10px;
  background: white;
  background-image: radial-gradient(circle at 5px 10px, transparent 5px, white 5px);
  background-size: 10px 20px;
  background-repeat: repeat-y;
  z-index: 1;
}

.coupon-left {
  width: 100px;
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 15px;
}

.coupon-right {
  flex: 1;
  background: white;
  padding: 15px;
  display: flex;
  flex-direction: column;
  position: relative;
}

.coupon-value {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 5px;
}

.coupon-condition {
  font-size: 0.8rem;
  text-align: center;
}

.coupon-title {
  font-size: 1.2rem;
  font-weight: 500;
  margin-bottom: 10px;
  color: #333;
}

.coupon-desc {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 15px;
}

.coupon-validity {
  font-size: 0.8rem;
  color: #999;
}

.coupon-status {
  position: absolute;
  top: 15px;
  right: 15px;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 5px;
}

.coupon-status i {
  font-size: 1rem;
}

/* 不同优惠券类型的样式 */
.fixed-amount .coupon-left {
  background: #4a90e2;
}

.fix-to-amount .coupon-left {
  background: #e53935;
}

.percentage .coupon-left {
  background: #ff9800;
}

/* 已使用和已过期样式 */
.coupon-card.used .coupon-left,
.coupon-card.expired .coupon-left {
  background: #9e9e9e;
}

.coupon-card.used .coupon-status,
.coupon-card.expired .coupon-status {
  color: #9e9e9e;
}

.coupon-card.used::after,
.coupon-card.expired::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.5);
  pointer-events: none;
}
</style>
