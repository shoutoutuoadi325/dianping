<template>
  <div class="page-container">
    <div class="order-confirmation">
      <div class="page-header">
        <div class="back-link" @click="goBack">
          <i class="fas fa-arrow-left"></i> 返回套餐详情
        </div>
        <h1>确认订单</h1>
      </div>
      
      <div class="order-section package-info">
        <div class="package-image">
          <img :src="packageData.imageUrl" :alt="packageData.title">
        </div>
        <div class="package-details">
          <h2>{{ packageData.title }}</h2>
          <div class="merchant-name">{{ packageData.businessName }}</div>
          <div class="package-price">￥{{ packageData.price }}</div>
        </div>
      </div>
      
      <div class="order-section coupon-section">
        <div class="section-header">
          <h2>优惠券</h2>
          <span v-if="availableCoupons.length > 0" class="coupon-count">{{ availableCoupons.length }}张可用</span>
        </div>
        
        <div v-if="loading" class="loading-state">
          <i class="fas fa-spinner fa-spin"></i> 加载中...
        </div>
        
        <div v-else-if="availableCoupons.length === 0" class="no-coupon">
          暂无可用优惠券
        </div>
        
        <div v-else class="coupon-selector" @click="showCouponModal = true">
          <div v-if="selectedCoupon" class="selected-coupon">
            <div class="coupon-title">{{ selectedCoupon.title }}</div>
            <div class="coupon-amount" :class="{'discount-amount': selectedCoupon.type === 'PERCENTAGE'}">
              {{ formatCouponValue(selectedCoupon) }}
            </div>
          </div>
          <div v-else class="no-selected-coupon">
            不使用优惠券
          </div>
          <i class="fas fa-chevron-right"></i>
        </div>
      </div>
      
      <div class="order-section price-calculation">
        <div class="price-item">
          <span>套餐原价</span>
          <span>￥{{ packageData.price }}</span>
        </div>
        <div class="price-item discount" v-if="discount > 0">
          <span>优惠金额</span>
          <span>-￥{{ discount.toFixed(2) }}</span>
        </div>
        <div class="price-item total">
          <span>实付金额</span>
          <span>￥{{ finalPrice.toFixed(2) }}</span>
        </div>
      </div>
      
      <div class="submit-order">
        <button class="submit-btn" @click="submitOrder">提交订单</button>
      </div>
    </div>
    
    <!-- 优惠券选择弹窗 -->
    <div class="modal-overlay" v-if="showCouponModal" @click="showCouponModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>选择优惠券</h2>
          <button class="close-btn" @click="showCouponModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="coupon-list">
          <div class="coupon-option no-coupon" @click="selectCoupon(null)">
            <div class="radio-button">
              <div class="radio-inner" :class="{active: !selectedCoupon}"></div>
            </div>
            <div class="coupon-text">不使用优惠券</div>
          </div>
          
          <div 
            v-for="coupon in availableCoupons" 
            :key="coupon.id" 
            class="coupon-option" 
            @click="selectCoupon(coupon)"
          >
            <div class="radio-button">
              <div class="radio-inner" :class="{active: selectedCoupon && selectedCoupon.id === coupon.id}"></div>
            </div>
            
            <div class="coupon-card" :class="getCouponClass(coupon)">
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
              </div>
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
      packageId: null,
      packageData: {
        id: '',
        title: '',
        price: 0,
        businessId: '',
        businessName: '',
        imageUrl: ''
      },
      availableCoupons: [],
      selectedCoupon: null,
      loading: true,
      showCouponModal: false,
      userInfo: null
    }
  },
  computed: {
    discount() {
      if (!this.selectedCoupon) return 0;
      
      // 根据优惠券类型计算折扣
      const coupon = this.selectedCoupon;
      const packagePrice = this.packageData.price;
      
      switch (coupon.type) {
        case 'FIXED_AMOUNT': // 满减券
          if (packagePrice >= coupon.minAmount) {
            return Math.min(coupon.value, packagePrice);
          }
          return 0;
          
        case 'FIX_TO_AMOUNT': // 减到固定金额券
          if (packagePrice >= coupon.minAmount) {
            const discountAmount = packagePrice - coupon.value;
            // 确保折扣不超过套餐价格和最大折扣限制
            return Math.min(
              discountAmount > 0 ? discountAmount : packagePrice, 
              coupon.maxDiscount > 0 ? coupon.maxDiscount : packagePrice
            );
          }
          return 0;
          
        case 'PERCENTAGE': // 折扣券
          if (packagePrice >= coupon.minAmount) {
            const discountAmount = packagePrice * (1 - coupon.value / 10);
            // 确保折扣不超过最大折扣限制
            return coupon.maxDiscount > 0 
              ? Math.min(discountAmount, coupon.maxDiscount) 
              : discountAmount;
          }
          return 0;
          
        default:
          return 0;
      }
    },
    finalPrice() {
      // 确保最终价格不小于0
      return Math.max(this.packageData.price - this.discount, 0);
    }
  },
  async mounted() {
    this.packageId = this.$route.params.packageId;
    this.userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    
    await Promise.all([
      this.fetchPackageDetails(),
      this.fetchAvailableCoupons()
    ]);
    
    // 默认选择减免金额最高的优惠券
    this.selectBestCoupon();
  },
  methods: {
    async fetchPackageDetails() {
      try {
        const response = await axios.get(`/api/packages/${this.packageId}`);
        this.packageData = response.data;
        
        // 如果图片没有完整URL，添加前缀
        if (this.packageData.imageUrl && !this.packageData.imageUrl.startsWith('http')) {
          this.packageData.imageUrl = `http://localhost:8080${this.packageData.imageUrl}`;
        }
      } catch (error) {
        console.error('获取套餐详情失败:', error);
      }
    },
    async fetchAvailableCoupons() {
      try {
        // 获取当前用户可用于此套餐的优惠券
        const response = await axios.get('/api/coupons/available', {
          params: {
            businessId: this.packageData.businessId,
            amount: this.packageData.price,
            category: this.packageData.category
          },
          headers: {
            'UserId': this.userInfo.id
          }
        });
        
        this.availableCoupons = response.data;
        this.loading = false;
      } catch (error) {
        console.error('获取可用优惠券失败:', error);
        this.availableCoupons = [];
        this.loading = false;
      }
    },
    // 自动选择最优惠的券
    selectBestCoupon() {
      if (this.availableCoupons.length === 0) return;
      
      // 计算每张券的实际优惠金额
      const couponsWithDiscount = this.availableCoupons.map(coupon => {
        let discountAmount = 0;
        const packagePrice = this.packageData.price;
        
        switch (coupon.type) {
          case 'FIXED_AMOUNT': // 满减券
            if (packagePrice >= coupon.minAmount) {
              discountAmount = Math.min(coupon.value, packagePrice);
            }
            break;
            
          case 'FIX_TO_AMOUNT': // 减到固定金额券
            if (packagePrice >= coupon.minAmount) {
              discountAmount = Math.min(
                packagePrice - coupon.value, 
                coupon.maxDiscount > 0 ? coupon.maxDiscount : packagePrice
              );
            }
            break;
            
          case 'PERCENTAGE': // 折扣券
            if (packagePrice >= coupon.minAmount) {
              discountAmount = Math.min(
                packagePrice * (1 - coupon.value / 10),
                coupon.maxDiscount > 0 ? coupon.maxDiscount : packagePrice
              );
            }
            break;
        }
        
        return { coupon, discountAmount };
      });
      
      // 找出折扣最大的优惠券
      const bestCoupon = couponsWithDiscount.reduce((best, current) => {
        return current.discountAmount > best.discountAmount ? current : best;
      }, { discountAmount: 0 }).coupon;
      
      // 只有当有实际折扣效果时才选择
      if (bestCoupon && this.calculateDiscount(bestCoupon) > 0) {
        this.selectedCoupon = bestCoupon;
      }
    },
    calculateDiscount(coupon) {
      const packagePrice = this.packageData.price;
      
      switch (coupon.type) {
        case 'FIXED_AMOUNT': // 满减券
          if (packagePrice >= coupon.minAmount) {
            return Math.min(coupon.value, packagePrice);
          }
          return 0;
          
        case 'FIX_TO_AMOUNT': // 减到固定金额券
          if (packagePrice >= coupon.minAmount) {
            const discountAmount = packagePrice - coupon.value;
            return Math.min(
              discountAmount > 0 ? discountAmount : packagePrice, 
              coupon.maxDiscount > 0 ? coupon.maxDiscount : packagePrice
            );
          }
          return 0;
          
        case 'PERCENTAGE': // 折扣券
          if (packagePrice >= coupon.minAmount) {
            const discountAmount = packagePrice * (1 - coupon.value / 10);
            return coupon.maxDiscount > 0 
              ? Math.min(discountAmount, coupon.maxDiscount) 
              : discountAmount;
          }
          return 0;
          
        default:
          return 0;
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
    selectCoupon(coupon) {
      this.selectedCoupon = coupon;
      this.showCouponModal = false;
    },
    goBack() {
      this.$router.go(-1);
    },
    async submitOrder() {
      try {
        const orderData = {
          packageId: this.packageId,
          packagePrice: this.packageData.price,
          finalPrice: this.finalPrice,
          couponId: this.selectedCoupon ? this.selectedCoupon.id : null,
          discount: this.discount,
          businessId: this.packageData.businessId
        };
        
        const response = await axios.post('/api/orders', orderData, {
          headers: {
            'UserId': this.userInfo.id
          }
        });
        
        // 跳转到券码页面
        this.$router.push(`/coupon-code/${response.data.orderId}`);
      } catch (error) {
        console.error('提交订单失败:', error);
        alert('订单提交失败，请重试');
      }
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

.order-confirmation {
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

.order-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.package-info {
  display: flex;
  gap: 20px;
}

.package-image {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
}

.package-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.package-details {
  flex: 1;
}

.package-details h2 {
  font-size: 1.3rem;
  margin: 0 0 10px 0;
  color: #333;
}

.merchant-name {
  color: #666;
  margin-bottom: 15px;
}

.package-price {
  font-size: 1.4rem;
  font-weight: bold;
  color: #e53935;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h2 {
  font-size: 1.2rem;
  margin: 0;
  color: #333;
}

.coupon-count {
  color: #e53935;
  font-weight: 500;
}

.coupon-selector {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
}

.selected-coupon {
  display: flex;
  flex-direction: column;
}

.coupon-title {
  font-weight: 500;
  color: #333;
}

.coupon-amount {
  color: #e53935;
  font-weight: bold;
  margin-top: 5px;
}

.discount-amount {
  color: #ff9800;
}

.no-selected-coupon {
  color: #999;
}

.no-coupon, .loading-state {
  color: #999;
  text-align: center;
  padding: 15px;
}

.loading-state i {
  margin-right: 5px;
}

.price-calculation {
  padding-bottom: 10px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  color: #666;
}

.price-item.discount {
  color: #e53935;
}

.price-item.total {
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
  padding-top: 15px;
  border-top: 1px solid #eee;
  margin-bottom: 0;
}

.submit-order {
  margin-top: 30px;
}

.submit-btn {
  width: 100%;
  padding: 15px;
  background: #e53935;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1.2rem;
  cursor: pointer;
  transition: all 0.3s;
}

.submit-btn:hover {
  background: #c62828;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(229, 57, 53, 0.3);
}

/* 优惠券选择弹窗 */
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

.modal-content {
  width: 95%;
  max-width: 500px;
  max-height: 90vh;
  background: white;
  border-radius: 12px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.2);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h2 {
  font-size: 1.3rem;
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  color: #999;
  cursor: pointer;
}

.coupon-list {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.coupon-option {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
  cursor: pointer;
}

.radio-button {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid #ddd;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
}

.radio-inner {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: transparent;
}

.radio-inner.active {
  background: #4a90e2;
}

.coupon-text {
  font-size: 1rem;
  color: #666;
}

.coupon-card {
  flex: 1;
  display: flex;
  border-radius: 8px;
  overflow: hidden;
  height: 100px;
  position: relative;
}

.coupon-card::before {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0;
  left: 75px; /* 左边宽度 */
  width: 10px;
  background: white;
  background-image: radial-gradient(circle at 5px 10px, transparent 5px, white 5px);
  background-size: 10px 20px;
  background-repeat: repeat-y;
  z-index: 1;
}

.coupon-left {
  width: 80px;
  background: #4a90e2;
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 10px;
}

.coupon-right {
  flex: 1;
  background: #f8f9fa;
  padding: 15px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.coupon-value {
  font-size: 1.3rem;
  font-weight: bold;
  margin-bottom: 5px;
}

.coupon-condition {
  font-size: 0.8rem;
  text-align: center;
}

.coupon-title {
  font-size: 1.1rem;
  font-weight: 500;
  margin-bottom: 5px;
  color: #333;
}

.coupon-desc {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 10px;
}

.coupon-validity {
  font-size: 0.8rem;
  color: #999;
}

/* 不同类型优惠券的样式 */
.fixed-amount .coupon-left {
  background: #4a90e2;
}

.fix-to-amount .coupon-left {
  background: #e53935;
}

.percentage .coupon-left {
  background: #ff9800;
}
</style>
