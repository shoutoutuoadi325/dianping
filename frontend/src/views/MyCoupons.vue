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
        <h2>暂无{{ currentTabLabel }}优惠券</h2>
        <p v-if="currentTab === 'unused'">快去领券中心看看吧~</p>
        <div v-if="currentTab === 'unused'">
          <button class="explore-btn" @click="$router.push('/new-user-coupons')">去领券</button>
        </div>
      </div>

      <div v-else class="coupons-list">
        <div
          v-for="coupon in getFilteredCoupons()"
          :key="coupon.id"
          class="coupon-card"
          :class="[getCouponClass(coupon), coupon.status]"
        >
          <div class="coupon-left">
            <div class="coupon-value">{{ coupon.couponName }}</div>
          </div>

          <div class="coupon-right">
            <div class="coupon-info">
              <div class="coupon-title">{{ coupon.couponName }}</div>
              <div class="coupon-desc">
                <span>品类: {{ coupon.category || '全商家通用' }}</span>
                <span v-if="coupon.expireTime"> | 有效期至: {{ formatValidity(coupon) }}</span>
                <span v-else> | 长期有效</span>
              </div>
            </div>

            <div class="coupon-status-badge" :class="coupon.status">
              <img v-if="coupon.status === 'used'" src="@/assets/used-stamp.png" alt="已使用">
              <img v-if="coupon.status === 'expired'" src="@/assets/expired-stamp.png" alt="已过期">
            </div>

            <button v-if="coupon.status === 'unused'" class="use-btn" @click="goToUse(coupon)">
              去使用 <i class="fas fa-chevron-right"></i>
            </button>
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
      allCoupons: [],
      userInfo: null,
      currentTab: 'unused',
      tabs: [
        { label: '未使用', value: 'unused' },
        { label: '已使用', value: 'used' },
        { label: '已过期', value: 'expired' }
      ]
    };
  },
  computed: {
    processedCoupons() {
      const now = new Date();
      return this.allCoupons.map(coupon => {
        let status = coupon.couponAmount > 0 ? 'unused' : 'used';
        if (this.isCouponExpired(coupon, now)) {
          status = 'expired';
        }
        return { 
          ...coupon, 
          status, 
          category: coupon.category || '全商家通用' // 确保 category 有默认值
        };
      });
    },
    currentTabLabel() {
      const tab = this.tabs.find(t => t.value === this.currentTab);
      return tab ? tab.label : '';
    }
  },
  mounted() {
    const storedUserInfo = localStorage.getItem('userInfo');
    if (!storedUserInfo) {
      this.$router.push('/login');
      return;
    }
    this.userInfo = JSON.parse(storedUserInfo);
    this.fetchCoupons();
  },
  methods: {
    async fetchCoupons() {
      this.loading = true;
      try {
        const response = await axios.get('/api/coupons/all', { // 确保路径正确
          headers: {
            'UserId': this.userInfo.id
          }
        });
        this.allCoupons = response.data;
      } catch (error) {
        console.error('获取优惠券列表失败:', error);
        this.allCoupons = [];
      } finally {
        this.loading = false;
      }
    },
    getFilteredCoupons() {
      return this.processedCoupons.filter(coupon => coupon.status === this.currentTab);
    },
    getTabCount(tabValue) {
      return this.processedCoupons.filter(coupon => coupon.status === tabValue).length;
    },
    isCouponExpired(coupon, now = new Date()) {
      if (coupon.expireTime) {
        return new Date(coupon.expireTime) < now;
      }
      return false;
    },
    formatValidity(coupon) {
      if (coupon.expireTime) {
        return new Date(coupon.expireTime).toLocaleDateString();
      }
      return '长期有效';
    },
    getCouponClass(coupon) {
      return coupon.status;
    },
    goToUse(coupon) {
      this.$router.push('/nearby-food');
    },
    goBack() {
      this.$router.push('/my');
    }
  }
};
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
  box-sizing: border-box;
}

.coupons-container {
  max-width: 800px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
  padding: 0 10px;
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
  margin: 0 10px 20px 10px;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.tab-btn {
  flex: 1;
  padding: 14px 10px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  color: #666;
  position: relative;
  transition: all 0.3s;
  font-weight: 500;
}

.tab-btn.active {
  color: #4a90e2;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 25%;
  right: 25%;
  height: 3px;
  background: #4a90e2;
  border-radius: 3px;
}

.tab-count {
  display: inline-block;
  background: #e0e0e0;
  color: #666;
  font-size: 0.75rem;
  min-width: 18px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  border-radius: 9px;
  margin-left: 6px;
  padding: 0 5px;
  font-weight: normal;
}
.tab-btn.active .tab-count {
  background: #4a90e2;
  color: white;
}

.loading-container, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: #666;
}

.loading-container i {
  font-size: 2.5rem;
  margin-bottom: 20px;
  color: #4a90e2;
}
.loading-container span {
  font-size: 1rem;
}

.empty-icon {
  font-size: 4rem;
  color: #bdc3c7;
  margin-bottom: 25px;
}

.empty-state h2 {
  color: #333;
  font-size: 1.2rem;
  margin-bottom: 10px;
}

.empty-state p {
  color: #999;
  font-size: 0.9rem;
  margin-bottom: 25px;
}

.explore-btn {
  padding: 10px 25px;
  background: #4a90e2;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 0.95rem;
}
.explore-btn:hover {
  background: #357abd;
  transform: translateY(-2px);
}

.coupons-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 0 10px;
}

.coupon-card {
  display: flex;
  border-radius: 8px;
  overflow: hidden;
  min-height: 110px;
  position: relative;
  box-shadow: 0 3px 10px rgba(0,0,0,0.06);
  background: white;
  transition: opacity 0.3s;
}

.coupon-left {
  width: 90px;
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 15px 5px;
  text-align: center;
  position: relative;
  flex-shrink: 0;
}
.coupon-left::after {
    content: '';
    position: absolute;
    top: 10px;
    bottom: 10px;
    right: -1px;
    width: 2px;
    background: linear-gradient(to bottom, white 5px, transparent 5px, transparent 10px);
    background-size: 100% 15px;
}

.coupon-right {
  flex: 1;
  background: white;
  padding: 12px 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  min-width: 0;
}

.coupon-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 1;
  margin-right: 10px;
  min-width: 0;
}

.coupon-value {
  font-size: 1.4rem;
  font-weight: bold;
  margin-bottom: 4px;
  line-height: 1.2;
}

.coupon-condition {
  font-size: 0.8rem;
  opacity: 0.9;
}

.coupon-title {
  font-size: 1.05rem;
  font-weight: 500;
  margin-bottom: 6px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.coupon-desc {
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 8px;
  line-height: 1.3;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.coupon-validity {
  font-size: 0.8rem;
  color: #999;
}

.coupon-status-badge {
  position: absolute;
  top: 0;
  right: 0;
  width: 60px;
  height: 60px;
  overflow: hidden;
  opacity: 0.8;
}
.coupon-status-badge img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.use-btn {
  background: none;
  border: 1px solid #4a90e2;
  color: #4a90e2;
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
  flex-shrink: 0;
}
.use-btn:hover {
  background: #eaf4ff;
}
.use-btn i {
  margin-left: 4px;
  font-size: 0.7rem;
}

.fixed-amount .coupon-left { background: #4a90e2; }
.percentage .coupon-left { background: #ff9800; }

.coupon-card.used,
.coupon-card.expired {
  opacity: 0.6;
}

.coupon-card.used .coupon-left,
.coupon-card.expired .coupon-left {
  background: #bdc3c7;
}
.coupon-card.used .coupon-left::after,
.coupon-card.expired .coupon-left::after {
   background: linear-gradient(to bottom, #f5f7fa 5px, transparent 5px, transparent 10px);
   background-size: 100% 15px;
}
</style>
