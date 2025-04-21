<template>
  <div class="page-container">
    <div class="new-user-coupons">
      <div class="page-header">
        <div class="back-link" @click="goBack">
          <i class="fas fa-arrow-left"></i> 返回
        </div>
        <h1>新人专享优惠</h1>
      </div>
      
      <div class="banner">
        <h2>欢迎加入小众点评!</h2>
        <p>您可以选择以下优惠券中的一张，开启美食探索旅程</p>
      </div>
      
      <div v-if="loading" class="loading-container">
        <i class="fas fa-spinner fa-spin"></i>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="coupons.length === 0" class="empty-state">
        <div class="empty-icon">
          <i class="fas fa-ticket-alt"></i>
        </div>
        <h2>暂无可领取的优惠券</h2>
        <p>您可能已经领取过新人券，或暂无可领取的优惠活动</p>
        <button class="explore-btn" @click="$router.push('/my-coupons')">查看我的卡包</button>
      </div>
      
      <div v-else class="coupons-grid">
        <div 
          v-for="coupon in coupons" 
          :key="coupon.id" 
          class="coupon-card"
          :class="[getCouponClass(coupon), { 'disabled': coupon.claimed || !coupon.available }]"
          @click="selectCoupon(coupon)"
        >
          <div class="coupon-header">
            <h3>{{ coupon.title }}</h3>
            <div class="coupon-badge" v-if="coupon.remainingQuantity <= 10 && coupon.remainingQuantity > 0">
              剩余{{ coupon.remainingQuantity }}张
            </div>
            <div class="coupon-badge sold-out" v-if="coupon.remainingQuantity === 0">
              已抢光
            </div>
          </div>
          
          <div class="coupon-value">{{ formatCouponValue(coupon) }}</div>
          
          <div class="coupon-conditions">
            <div class="condition-item" v-if="coupon.minAmount > 0">
              <i class="fas fa-tag"></i> 满{{ coupon.minAmount }}元可用
            </div>
            <div class="condition-item" v-else>
              <i class="fas fa-tag"></i> 无门槛使用
            </div>
            
            <div class="condition-item" v-if="coupon.applicableCategory">
              <i class="fas fa-th-list"></i> 限{{ coupon.applicableCategory }}品类
            </div>
            
            <div class="condition-item" v-if="coupon.applicableBusiness">
              <i class="fas fa-store"></i> 限{{ coupon.applicableBusiness }}使用
            </div>
            
            <div class="condition-item">
              <i class="fas fa-clock"></i> {{ formatValidity(coupon) }}
            </div>
          </div>
          
          <button 
            class="claim-btn" 
            :class="{ 'claimed': coupon.claimed, 'disabled': !coupon.available || coupon.remainingQuantity === 0 }"
            @click.stop="claimCoupon(coupon)"
          >
            {{ getCouponButtonText(coupon) }}
          </button>
        </div>
      </div>
      
      <!-- 领取成功弹窗 -->
      <div class="modal-overlay" v-if="showSuccessModal" @click="showSuccessModal = false">
        <div class="success-modal" @click.stop>
          <div class="success-icon">
            <i class="fas fa-check-circle"></i>
          </div>
          <h2>领取成功!</h2>
          <p>优惠券已添加到您的卡包中</p>
          <button class="primary-btn" @click="viewMyCoupons">查看我的卡包</button>
          <button class="secondary-btn" @click="showSuccessModal = false">继续浏览</button>
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
      showSuccessModal: false,
      selectedCoupon: null
    }
  },
  mounted() {
    this.userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    this.fetchNewUserCoupons();
  },
  methods: {
    async fetchNewUserCoupons() {
      try {
        const response = await axios.get('/api/coupons/new-user', {
          headers: {
            'UserId': this.userInfo.id
          }
        });
        this.coupons = response.data;
      } catch (error) {
        console.error('获取新人优惠券失败:', error);
        this.coupons = [];
      } finally {
        this.loading = false;
      }
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
    getCouponButtonText(coupon) {
      if (coupon.claimed) {
        return '已领取';
      } else if (coupon.remainingQuantity === 0) {
        return '已抢光';
      } else if (!coupon.available) {
        return '不可领取';
      } else {
        return '立即领取';
      }
    },
    selectCoupon(coupon) {
      if (!coupon.claimed && coupon.available && coupon.remainingQuantity > 0) {
        this.selectedCoupon = coupon;
      }
    },
    async claimCoupon(coupon) {
      if (coupon.claimed || !coupon.available || coupon.remainingQuantity === 0) {
        return;
      }
      
      try {
        const response = await axios.post(`/api/coupons/${coupon.id}/claim`, null, {
          headers: {
            'UserId': this.userInfo.id
          }
        });
        
        if (response.data.success) {
          // 更新券的状态
          coupon.claimed = true;
          coupon.remainingQuantity--;
          
          // 显示成功弹窗
          this.showSuccessModal = true;
        }
      } catch (error) {
        console.error('领取优惠券失败:', error);
        const errorMsg = error.response?.data?.message || '领取失败，请重试';
        alert(errorMsg);
      }
    },
    viewMyCoupons() {
      this.$router.push('/my-coupons');
    },
    goBack() {
      this.$router.push('/home');
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

.new-user-coupons {
  max-width: 1000px;
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

.banner {
  text-align: center;
  padding: 30px;
  background: linear-gradient(135deg, #4a90e2, #357abd);
  color: white;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 5px 15px rgba(74, 144, 226, 0.3);
}

.banner h2 {
  font-size: 1.8rem;
  margin-bottom: 10px;
}

.banner p {
  font-size: 1.1rem;
  opacity: 0.9;
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

.coupons-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.coupon-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.coupon-card:not(.disabled):hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.1);
}

.coupon-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 5px;
  height: 100%;
}

.coupon-card.fixed-amount::before {
  background: #4a90e2;
}

.coupon-card.fix-to-amount::before {
  background: #e53935;
}

.coupon-card.percentage::before {
  background: #ff9800;
}

.coupon-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.coupon-header h3 {
  font-size: 1.2rem;
  margin: 0;
  color: #333;
  flex: 1;
}

.coupon-badge {
  font-size: 0.8rem;
  padding: 3px 8px;
  border-radius: 12px;
  background: #fff2e8;
  color: #ff9800;
}

.coupon-badge.sold-out {
  background: #f9e9e9;
  color: #e53935;
}

.coupon-value {
  font-size: 2rem;
  font-weight: bold;
  margin: 15px 0;
}

.coupon-card.fixed-amount .coupon-value {
  color: #4a90e2;
}

.coupon-card.fix-to-amount .coupon-value {
  color: #e53935;
}

.coupon-card.percentage .coupon-value {
  color: #ff9800;
}

.coupon-conditions {
  margin-bottom: 20px;
}

.condition-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 8px;
}

.condition-item i {
  color: #999;
}

.claim-btn {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
}

.coupon-card.fixed-amount .claim-btn {
  background: #4a90e2;
  color: white;
}

.coupon-card.fix-to-amount .claim-btn {
  background: #e53935;
  color: white;
}

.coupon-card.percentage .claim-btn {
  background: #ff9800;
  color: white;
}

.claim-btn.claimed {
  background: #9e9e9e !important;
  cursor: not-allowed;
}

.claim-btn.disabled {
  background: #ddd !important;
  color: #999 !important;
  cursor: not-allowed;
}

.coupon-card.disabled {
  opacity: 0.7;
}

/* 成功弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.success-modal {
  width: 90%;
  max-width: 350px;
  background: white;
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 5px 20px rgba(0,0,0,0.2);
}

.success-icon {
  font-size: 4rem;
  color: #4CAF50;
  margin-bottom: 20px;
}

.success-modal h2 {
  font-size: 1.5rem;
  color: #333;
  margin-bottom: 10px;
}

.success-modal p {
  color: #666;
  margin-bottom: 25px;
}

.primary-btn, .secondary-btn {
  padding: 12px 20px;
  border-radius: 8px;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
  margin: 0 5px;
}

.primary-btn {
  background: #4a90e2;
  color: white;
}

.primary-btn:hover {
  background: #357abd;
}

.secondary-btn {
  background: #f1f1f1;
  color: #666;
}

.secondary-btn:hover {
  background: #e1e1e1;
}
</style>
