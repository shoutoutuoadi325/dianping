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
        <p>您可以选择以下优惠券中的一张或多张 (根据活动规则)</p>
      </div>

      <div v-if="loading" class="loading-container">
        <i class="fas fa-spinner fa-spin"></i>
        <span>加载中...</span>
      </div>

      <div v-else-if="coupons.length === 0 && !error" class="empty-state">
        <div class="empty-icon">
          <i class="fas fa-gift"></i> <!-- 更换图标 -->
        </div>
        <h2>暂无新人专享活动</h2>
        <p>敬请期待更多优惠活动</p>
        <button class="explore-btn" @click="$router.push('/nearby-food')">去首页逛逛</button>
      </div>

       <div v-else-if="error" class="empty-state">
         <div class="empty-icon">
           <i class="fas fa-exclamation-circle"></i>
         </div>
         <h2>加载失败</h2>
         <p>{{ error }}</p>
         <button class="explore-btn" @click="fetchNewUserCoupons">重试</button>
       </div>


      <div v-else class="coupons-grid">
        <div
          v-for="coupon in coupons"
          :key="coupon.id"
          class="coupon-card"
          :class="[getCouponClass(coupon), { 'claimed': coupon.claimed, 'unavailable': !coupon.available || coupon.remainingQuantity === 0 }]"
        >
          <!-- 左侧颜色条 -->
          <div class="color-bar"></div>

          <div class="coupon-content">
            <div class="coupon-header">
              <h3>{{ coupon.title }}</h3>
              <div class="coupon-badge remaining" v-if="coupon.available && coupon.remainingQuantity > 0 && coupon.remainingQuantity <= 20">
                仅剩{{ coupon.remainingQuantity }}张
              </div>
              <div class="coupon-badge sold-out" v-if="!coupon.claimed && (!coupon.available || coupon.remainingQuantity === 0)">
                已抢光
              </div>
               <div class="coupon-badge claimed-badge" v-if="coupon.claimed">
                 <i class="fas fa-check"></i> 已领取
               </div>
            </div>

            <div class="coupon-value-section">
              <div class="coupon-value">{{ formatCouponValueDisplay(coupon) }}</div>
              <div class="coupon-condition" v-if="coupon.minAmount > 0">满{{ coupon.minAmount }}元可用</div>
              <div class="coupon-condition" v-else>无门槛</div>
            </div>

            <div class="coupon-details">
              <div class="detail-item" v-if="getCouponScope(coupon)">
                <i class="fas fa-store"></i> {{ getCouponScope(coupon) }}
              </div>
              <div class="detail-item">
                <i class="fas fa-clock"></i> {{ formatValidity(coupon) }}
              </div>
               <div class="detail-item" v-if="coupon.maxDiscount > 0 && (coupon.type === 'PERCENTAGE' || coupon.type === 'FIX_TO_AMOUNT')">
                 <i class="fas fa-info-circle"></i> 最多优惠￥{{ coupon.maxDiscount }}
               </div>
            </div>
          </div>

          <button
            class="claim-btn"
            :disabled="coupon.claimed || !coupon.available || coupon.remainingQuantity === 0 || claimingId === coupon.id"
            @click.stop="claimCoupon(coupon)"
          >
            <span v-if="claimingId === coupon.id"><i class="fas fa-spinner fa-spin"></i> 领取中</span>
            <span v-else>{{ getCouponButtonText(coupon) }}</span>
          </button>
        </div>
      </div>

      <!-- 领取成功弹窗 -->
      <div class="modal-overlay" v-if="showSuccessModal" @click="closeSuccessModal">
        <div class="success-modal" @click.stop>
          <div class="success-icon">
            <i class="fas fa-check-circle"></i>
          </div>
          <h2>领取成功!</h2>
          <p v-if="claimedCouponInfo"> {{ claimedCouponInfo.title }} 已放入您的卡包</p>
          <button class="primary-btn" @click="viewMyCoupons">查看我的卡包</button>
          <button class="secondary-btn" @click="closeSuccessModal">继续领取</button>
        </div>
      </div>

       <!-- 领取失败提示 -->
       <div v-if="claimError" class="claim-error-toast">
         <i class="fas fa-exclamation-triangle"></i> {{ claimError }}
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
      claimedCouponInfo: null, // 存储刚领取的券信息用于弹窗显示
      claimingId: null, // 正在领取的优惠券ID，用于按钮loading状态
      error: null, // 加载错误信息
      claimError: null, // 领取错误信息
      claimErrorTimeout: null, // 领取错误提示定时器
    }
  },
  mounted() {
    const storedUserInfo = localStorage.getItem('userInfo');
    if (!storedUserInfo) {
      this.$router.push('/login');
      return;
    }
    this.userInfo = JSON.parse(storedUserInfo);
    this.fetchNewUserCoupons();
  },
  beforeDestroy() {
    // 清除可能存在的定时器
    if (this.claimErrorTimeout) {
      clearTimeout(this.claimErrorTimeout);
    }
  },
  methods: {
    async fetchNewUserCoupons() {
      this.loading = true;
      this.error = null; // 重置错误状态
      try {
        const response = await axios.get('/api/coupons/new-user', {
          headers: {
            'UserId': this.userInfo.id
          }
        });
        // 对返回的数据进行处理，确保字段完整性
        this.coupons = response.data.map(c => ({
          ...c,
          claimed: c.claimed || false, // 确保有 claimed 字段
          available: c.available !== undefined ? c.available : c.remainingQuantity > 0, // 确保有 available 字段
          remainingQuantity: c.remainingQuantity || 0 // 确保有 remainingQuantity 字段
        }));
      } catch (error) {
        console.error('获取新人优惠券失败:', error);
        this.error = error.response?.data?.message || '加载优惠券失败，请稍后重试';
        this.coupons = [];
      } finally {
        this.loading = false;
      }
    },
    // 格式化优惠券面值显示
    formatCouponValueDisplay(coupon) {
      switch (coupon.type) {
        case 'FIXED_AMOUNT':
          return `￥${coupon.value}`;
        case 'FIX_TO_AMOUNT':
          // 可以显示 "减至X元" 或 "最高抵扣Y元"
          return `最高抵￥${coupon.maxDiscount || coupon.value}`; // 优先显示maxDiscount
        case 'PERCENTAGE':
          return `${coupon.value}折`;
        default:
          return '';
      }
    },
    formatValidity(coupon) {
       if (coupon.endDate) {
         return `有效期至 ${new Date(coupon.endDate).toLocaleDateString()}`;
       } else if (coupon.validDays) {
         return `领取后${coupon.validDays}天内有效`;
       } else {
         return '长期有效';
       }
    },
    // 获取优惠券适用范围描述
    getCouponScope(coupon) {
      if (coupon.applicableBusiness) {
        return `限“${coupon.applicableBusiness}”可用`;
      }
      if (coupon.applicableCategory) {
        return `限“${coupon.applicableCategory}”品类`;
      }
      return '全场通用'; // 如果都没有限制
    },
    getCouponClass(coupon) {
      switch (coupon.type) {
        case 'FIXED_AMOUNT': return 'fixed-amount';
        case 'FIX_TO_AMOUNT': return 'fix-to-amount';
        case 'PERCENTAGE': return 'percentage';
        default: return '';
      }
    },
    getCouponButtonText(coupon) {
      if (coupon.claimed) {
        return '已领取';
      } else if (!coupon.available || coupon.remainingQuantity === 0) {
        return '已抢光';
      } else {
        return '立即领取';
      }
    },
    async claimCoupon(coupon) {
      // 防止重复点击或领取无效券
      if (coupon.claimed || !coupon.available || coupon.remainingQuantity === 0 || this.claimingId) {
        return;
      }

      this.claimingId = coupon.id; // 设置领取中状态
      this.claimError = null; // 清除之前的错误提示
      if (this.claimErrorTimeout) clearTimeout(this.claimErrorTimeout);

      try {
        const response = await axios.post(`/api/coupons/${coupon.id}/claim`, null, {
          headers: {
            'UserId': this.userInfo.id
          }
        });

        if (response.data.success) {
          // 更新前端券的状态
          const index = this.coupons.findIndex(c => c.id === coupon.id);
          if (index !== -1) {
            this.$set(this.coupons, index, {
              ...this.coupons[index],
              claimed: true,
              remainingQuantity: this.coupons[index].remainingQuantity - 1, // 数量减一
              // 如果后端返回了更新后的券信息，可以用 response.data.coupon 更新
            });
          }

          // 显示成功弹窗
          this.claimedCouponInfo = this.coupons[index]; // 保存信息用于弹窗
          this.showSuccessModal = true;
        } else {
          // 显示后端返回的错误信息
          this.showClaimError(response.data.message || '领取失败，请稍后重试');
        }
      } catch (error) {
        console.error('领取优惠券失败:', error);
        const errorMsg = error.response?.data?.message || '领取失败，网络错误或服务器异常';
        this.showClaimError(errorMsg);
      } finally {
        this.claimingId = null; // 清除领取中状态
      }
    },
    // 显示领取错误提示
    showClaimError(message) {
        this.claimError = message;
        // 3秒后自动隐藏
        this.claimErrorTimeout = setTimeout(() => {
            this.claimError = null;
        }, 3000);
    },
    closeSuccessModal() {
      this.showSuccessModal = false;
      this.claimedCouponInfo = null;
    },
    viewMyCoupons() {
      this.closeSuccessModal();
      this.$router.push('/my-coupons');
    },
    goBack() {
      this.$router.push('/my'); // 修改跳转路径为 /my
    }
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
  box-sizing: border-box;
}

.new-user-coupons {
  max-width: 1000px;
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

.banner {
  text-align: center;
  padding: 25px; /* 调整内边距 */
  background: linear-gradient(135deg, #ff7e5f, #feb47b); /* 暖色调渐变 */
  color: white;
  border-radius: 12px;
  margin: 0 10px 30px 10px; /* 左右留白 */
  box-shadow: 0 5px 15px rgba(254, 180, 123, 0.4);
}

.banner h2 {
  font-size: 1.6rem; /* 调整字体 */
  margin-bottom: 8px;
}

.banner p {
  font-size: 1rem; /* 调整字体 */
  opacity: 0.9;
}

.loading-container, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px; /* 增加上下边距 */
  text-align: center;
  color: #666;
}

.loading-container i {
  font-size: 2.5rem; /* 增大图标 */
  margin-bottom: 20px;
  color: #4a90e2;
}
.loading-container span {
  font-size: 1rem;
}


.empty-icon {
  font-size: 4rem;
  color: #ddd;
  margin-bottom: 25px;
}
.empty-state .empty-icon i {
  color: #bdc3c7; /* 图标颜色 */
}


.empty-state h2 {
  color: #333;
  font-size: 1.2rem; /* 调整字体 */
  margin-bottom: 10px;
}

.empty-state p {
  color: #999;
  font-size: 0.9rem; /* 调整字体 */
  margin-bottom: 25px;
}

.explore-btn {
  padding: 10px 25px; /* 调整按钮大小 */
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

.coupons-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); /* 调整最小宽度 */
  gap: 20px;
  padding: 0 10px; /* 左右留白 */
}

.coupon-card {
  background: white;
  border-radius: 8px; /* 调整圆角 */
  box-shadow: 0 3px 10px rgba(0,0,0,0.06); /* 调整阴影 */
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  display: flex; /* 使用 flex 布局 */
  flex-direction: column; /* 垂直排列 */
  border: 1px solid #eee;
}

.coupon-card:not(.unavailable):not(.claimed):hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 15px rgba(0,0,0,0.08);
}

/* 左侧颜色条 */
.color-bar {
  width: 6px; /* 调整宽度 */
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
}
.coupon-card.fixed-amount .color-bar { background: #4a90e2; }
.coupon-card.fix-to-amount .color-bar { background: #e53935; }
.coupon-card.percentage .color-bar { background: #ff9800; }

.coupon-content {
  padding: 15px 15px 15px 25px; /* 左侧留出颜色条空间 */
  flex: 1; /* 占据剩余空间 */
  display: flex;
  flex-direction: column;
}


.coupon-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px; /* 调整间距 */
  min-height: 20px; /* 保证高度 */
}

.coupon-header h3 {
  font-size: 1.1rem; /* 调整字体 */
  margin: 0;
  color: #333;
  flex: 1;
  line-height: 1.3;
  margin-right: 10px; /* 与徽章间距 */
}

.coupon-badge {
  font-size: 0.75rem; /* 调整字体 */
  padding: 3px 8px;
  border-radius: 10px;
  flex-shrink: 0; /* 防止被压缩 */
  font-weight: 500;
}
.coupon-badge.remaining {
  background: #fff2e8;
  color: #ff9800;
}
.coupon-badge.sold-out {
  background: #f1f1f1; /* 灰色背景 */
  color: #999;
}
.coupon-badge.claimed-badge {
  background: #e8f5e9; /* 浅绿色背景 */
  color: #4CAF50;
}
.coupon-badge.claimed-badge i {
  margin-right: 3px;
}


.coupon-value-section {
  display: flex;
  align-items: baseline; /* 基线对齐 */
  margin-bottom: 15px; /* 调整间距 */
}

.coupon-value {
  font-size: 1.8rem; /* 调整字体 */
  font-weight: bold;
  line-height: 1;
  margin-right: 8px; /* 与条件间距 */
}
.coupon-card.fixed-amount .coupon-value { color: #4a90e2; }
.coupon-card.fix-to-amount .coupon-value { color: #e53935; }
.coupon-card.percentage .coupon-value { color: #ff9800; }

.coupon-condition {
  font-size: 0.85rem; /* 调整字体 */
  color: #666;
}

.coupon-details {
  margin-top: auto; /* 将详情推到底部 */
  padding-top: 10px;
  border-top: 1px dashed #eee; /* 分隔线 */
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.8rem; /* 调整字体 */
  color: #888; /* 调整颜色 */
  margin-bottom: 5px;
}
.detail-item:last-child {
  margin-bottom: 0;
}
.detail-item i {
  width: 12px; /* 固定图标宽度 */
  text-align: center;
  color: #aaa;
}


.claim-btn {
  width: calc(100% - 30px); /* 调整宽度以适应内边距 */
  margin: 0 15px 15px 15px; /* 底部和左右边距 */
  padding: 10px; /* 调整按钮大小 */
  border: none;
  border-radius: 20px; /* 圆角 */
  font-size: 0.95rem; /* 调整字体 */
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  color: white;
}
.coupon-card.fixed-amount .claim-btn:not(:disabled) { background: #4a90e2; }
.coupon-card.fix-to-amount .claim-btn:not(:disabled) { background: #e53935; }
.coupon-card.percentage .claim-btn:not(:disabled) { background: #ff9800; }

.claim-btn:disabled {
  background: #ddd !important;
  color: #999 !important;
  cursor: not-allowed;
}
.claim-btn span i {
  margin-right: 5px;
}


/* 不可用/已领取状态 */
.coupon-card.unavailable, .coupon-card.claimed {
  opacity: 0.7; /* 降低透明度 */
}
.coupon-card.unavailable:hover, .coupon-card.claimed:hover {
  transform: none;
  box-shadow: 0 3px 10px rgba(0,0,0,0.06);
}


/* 成功弹窗 */
.modal-overlay {
  /* ... (样式与 OrderConfirmation 类似) ... */
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.success-modal {
  width: 90%;
  max-width: 320px; /* 调整宽度 */
  background: white;
  border-radius: 12px;
  padding: 30px 25px; /* 调整内边距 */
  text-align: center;
  box-shadow: 0 5px 20px rgba(0,0,0,0.15);
  animation: zoom-in 0.3s ease-out; /* 添加动画 */
}
@keyframes zoom-in {
  from { transform: scale(0.8); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}


.success-icon {
  font-size: 3.5rem; /* 调整大小 */
  color: #4CAF50;
  margin-bottom: 15px; /* 调整间距 */
}

.success-modal h2 {
  font-size: 1.3rem; /* 调整字体 */
  color: #333;
  margin-bottom: 8px;
}

.success-modal p {
  color: #666;
  font-size: 0.9rem; /* 调整字体 */
  margin-bottom: 25px;
}

.primary-btn, .secondary-btn {
  padding: 10px 18px; /* 调整按钮大小 */
  border-radius: 20px; /* 圆角 */
  border: none;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.3s;
  margin: 5px; /* 按钮间距 */
  min-width: 120px; /* 最小宽度 */
}

.primary-btn {
  background: #4a90e2;
  color: white;
}
.primary-btn:hover { background: #357abd; }

.secondary-btn {
  background: #f1f1f1;
  color: #666;
}
.secondary-btn:hover { background: #e1e1e1; }

/* 领取失败提示 */
.claim-error-toast {
  position: fixed;
  bottom: 80px; /* 调整位置 */
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(229, 57, 53, 0.9); /* 红色背景 */
  color: white;
  padding: 10px 20px;
  border-radius: 20px;
  font-size: 0.9rem;
  z-index: 1100;
  box-shadow: 0 2px 10px rgba(0,0,0,0.2);
  animation: fade-in-out 3s ease-in-out;
}
.claim-error-toast i {
  margin-right: 8px;
}

@keyframes fade-in-out {
  0%, 100% { opacity: 0; transform: translateX(-50%) translateY(10px); }
  10%, 90% { opacity: 1; transform: translateX(-50%) translateY(0); }
}

</style>
