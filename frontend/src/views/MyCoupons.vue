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
          <!-- 可以跳转到领券中心或新人领券页 -->
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
            <div class="coupon-value">{{ formatCouponValueDisplay(coupon) }}</div>
            <div class="coupon-condition" v-if="coupon.minAmount > 0">
              满{{ coupon.minAmount }}元可用
            </div>
            <div class="coupon-condition" v-else>
              无门槛
            </div>
          </div>

          <div class="coupon-right">
            <div class="coupon-info">
              <div class="coupon-title">{{ coupon.title }}</div>
              <div class="coupon-desc">{{ getCouponDescription(coupon) }}</div>
              <div class="coupon-validity">{{ formatValidity(coupon) }}</div>
            </div>

            <!-- 状态角标 -->
            <div class="coupon-status-badge" :class="coupon.status">
               <img v-if="coupon.status === 'used'" src="@/assets/used-stamp.png" alt="已使用">
               <img v-if="coupon.status === 'expired'" src="@/assets/expired-stamp.png" alt="已过期">
            </div>

             <!-- 未使用状态下，可以添加“去使用”按钮 -->
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
// 假设你有名为 used-stamp.png 和 used-stamp.png 的图片在 assets 文件夹
// import usedStamp from '@/assets/used-stamp.png';
// import expiredStamp from '@/assets/expired-stamp.png';

export default {
  data() {
    return {
      loading: true,
      allCoupons: [], // 存储从后端获取的所有券
      userInfo: null,
      currentTab: 'unused', // 默认显示未使用券
      tabs: [
        { label: '未使用', value: 'unused' },
        { label: '已使用', value: 'used' },
        { label: '已过期', value: 'expired' }
      ],
      // usedStamp, // 引入图片以便模板使用
      // expiredStamp
    }
  },
  computed: {
    // 根据当前 tab 计算要显示的券列表，并添加状态
    processedCoupons() {
      const now = new Date();
      return this.allCoupons.map(coupon => {
        let status = 'unused'; // 默认未使用
        if (coupon.used) {
          status = 'used';
        } else if (this.isCouponExpired(coupon, now)) {
          status = 'expired';
        }
        return { ...coupon, status }; // 添加 status 字段
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
        const response = await axios.get('/api/coupons/user', {
          headers: {
            'UserId': this.userInfo.id
          }
        });
        this.allCoupons = response.data;
      } catch (error) {
        console.error('获取优惠券列表失败:', error);
        this.allCoupons = [];
        // 可以添加错误提示
      } finally {
        this.loading = false;
      }
    },
    // 根据当前 tab 过滤优惠券
    getFilteredCoupons() {
      return this.processedCoupons.filter(coupon => coupon.status === this.currentTab);
    },
    // 计算每个 tab 的数量
    getTabCount(tabValue) {
      return this.processedCoupons.filter(coupon => coupon.status === tabValue).length;
    },
    // 判断优惠券是否过期
    isCouponExpired(coupon, now = new Date()) {
      if (coupon.endDate) {
        return new Date(coupon.endDate) < now;
      }
      // 如果有领取时间和有效天数
      if (coupon.validDays && coupon.claimTime) {
        const expiryDate = new Date(coupon.claimTime);
        expiryDate.setDate(expiryDate.getDate() + coupon.validDays);
        // 设置到当天结束时间比较
        expiryDate.setHours(23, 59, 59, 999);
        return expiryDate < now;
      }
      // 如果只有有效天数但无领取时间（异常情况），或无结束日期和有效天数，视为未过期或长期
      return false;
    },
    // 格式化优惠券面值显示
    formatCouponValueDisplay(coupon) {
      switch (coupon.type) {
        case 'FIXED_AMOUNT':
          return `￥${coupon.value}`;
        case 'FIX_TO_AMOUNT':
          // 卡包里显示类型可能比显示具体优惠金额更好
          return `减至￥${coupon.value}`;
        case 'PERCENTAGE':
          return `${coupon.value}折`;
        default:
          return '';
      }
    },
    getCouponDescription(coupon) {
      let description = [];
      // 适用范围
      if (coupon.applicableBusiness) {
        description.push(`限商家: ${coupon.applicableBusiness}`);
      } else if (coupon.applicableCategory) {
        description.push(`限品类: ${coupon.applicableCategory}`);
      } else {
         description.push('全场通用');
      }
      // 最大抵扣
      if (coupon.maxDiscount > 0 && (coupon.type === 'PERCENTAGE' || coupon.type === 'FIX_TO_AMOUNT')) {
        description.push(`最多优惠￥${coupon.maxDiscount}`);
      }
      return description.join(' | ');
    },
    formatValidity(coupon) {
      if (coupon.status === 'used' && coupon.useTime) {
         return `使用于: ${new Date(coupon.useTime).toLocaleDateString()}`;
      }
      if (coupon.status === 'expired' && coupon.endDate) {
         return `过期于: ${new Date(coupon.endDate).toLocaleDateString()}`;
      }
      if (coupon.endDate) {
        const endDate = new Date(coupon.endDate);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        const expiryDay = new Date(endDate);
        expiryDay.setHours(0, 0, 0, 0);

        const diffTime = expiryDay - today;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        if (diffDays < 0) {
           return `已过期`; // 理论上会被 status 覆盖，但作为兜底
        } else if (diffDays === 0) {
           return `今天 ${endDate.getHours()}:${endDate.getMinutes().toString().padStart(2, '0')} 到期`;
        } else if (diffDays <= 3) { // 3天内到期提示
           return `${diffDays}天后到期`;
        } else {
           return `有效期至 ${endDate.toLocaleDateString()}`;
        }
      } else if (coupon.validDays && coupon.claimTime) {
         const expiryDate = new Date(coupon.claimTime);
         expiryDate.setDate(expiryDate.getDate() + coupon.validDays);
         expiryDate.setHours(23, 59, 59, 999);
         return `有效期至 ${expiryDate.toLocaleDateString()}`;
      } else if (coupon.validDays) {
         return `领取后${coupon.validDays}天内有效`;
      } else {
        return '长期有效';
      }
    },
    getCouponClass(coupon) {
      switch (coupon.type) {
        case 'FIXED_AMOUNT': return 'fixed-amount';
        case 'FIX_TO_AMOUNT': return 'fix-to-amount';
        case 'PERCENTAGE': return 'percentage';
        default: return '';
      }
    },
    // 跳转到可以使用该券的页面（例如商家列表或商品列表）
    goToUse(coupon) {
      // 这里需要根据优惠券的适用范围决定跳转逻辑
      if (coupon.applicableBusiness) {
        // 跳转到指定商家页面 (需要商家ID)
        // this.$router.push(`/merchant/${coupon.businessId}`);
        alert(`跳转到商家 "${coupon.applicableBusiness}" 页面 (功能待实现)`);
      } else if (coupon.applicableCategory) {
        // 跳转到品类搜索结果页
        // this.$router.push(`/search?category=${coupon.applicableCategory}`);
         alert(`跳转到品类 "${coupon.applicableCategory}" 搜索页 (功能待实现)`);
      } else {
        // 跳转到首页或通用列表页
        this.$router.push('/my');
      }
    },
    goBack() {
      // 返回个人中心页
      this.$router.push('/my');
    }
  }
}
</script>

<style scoped>
/* ... existing styles ... */
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
  margin: 0 10px 20px 10px; /* 左右留白 */
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.tab-btn {
  flex: 1;
  padding: 14px 10px; /* 调整内边距 */
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  color: #666;
  position: relative;
  transition: all 0.3s;
  font-weight: 500; /* 加粗 */
}

.tab-btn.active {
  color: #4a90e2;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 25%; /* 调整下划线长度 */
  right: 25%;
  height: 3px;
  background: #4a90e2;
  border-radius: 3px;
}

.tab-count {
  display: inline-block;
  background: #e0e0e0; /* 灰色背景 */
  color: #666; /* 深灰色文字 */
  font-size: 0.75rem; /* 调整字体 */
  min-width: 18px; /* 最小宽度 */
  height: 18px;
  line-height: 18px;
  text-align: center;
  border-radius: 9px; /* 圆角 */
  margin-left: 6px; /* 调整间距 */
  padding: 0 5px; /* 左右内边距 */
  font-weight: normal; /* 计数器不加粗 */
}
.tab-btn.active .tab-count {
  background: #4a90e2; /* 激活状态下用主题色 */
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
  color: #bdc3c7; /* 图标颜色 */
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
  padding: 0 10px; /* 左右留白 */
}

.coupon-card {
  display: flex;
  border-radius: 8px;
  overflow: hidden;
  min-height: 110px; /* 调整高度 */
  position: relative;
  box-shadow: 0 3px 10px rgba(0,0,0,0.06);
  background: white;
  transition: opacity 0.3s;
}

/* 移除伪元素虚线 */
/* .coupon-card::before { ... } */

.coupon-left {
  width: 90px; /* 调整宽度 */
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 15px 5px; /* 调整内边距 */
  text-align: center;
  position: relative; /* 为了虚线 */
  flex-shrink: 0;
}
/* 右侧虚线 */
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
  padding: 12px 15px; /* 调整内边距 */
  display: flex;
  justify-content: space-between; /* 主要内容和按钮/状态分开 */
  align-items: center; /* 垂直居中对齐 */
  position: relative; /* 为了状态角标定位 */
  min-width: 0; /* 防止内容撑开 */
}

.coupon-info {
  display: flex;
  flex-direction: column;
  justify-content: center; /* 垂直居中 */
  flex: 1; /* 占据主要空间 */
  margin-right: 10px; /* 与按钮/状态间距 */
  min-width: 0;
}


.coupon-value {
  font-size: 1.4rem; /* 调整字体 */
  font-weight: bold;
  margin-bottom: 4px; /* 调整间距 */
  line-height: 1.2;
}

.coupon-condition {
  font-size: 0.8rem; /* 调整字体 */
  opacity: 0.9;
}

.coupon-title {
  font-size: 1.05rem; /* 调整字体 */
  font-weight: 500;
  margin-bottom: 6px; /* 调整间距 */
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.coupon-desc {
  font-size: 0.85rem; /* 调整字体 */
  color: #666;
  margin-bottom: 8px; /* 调整间距 */
  line-height: 1.3;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.coupon-validity {
  font-size: 0.8rem; /* 调整字体 */
  color: #999;
}

/* 状态角标 */
.coupon-status-badge {
  position: absolute;
  top: 0;
  right: 0;
  width: 60px; /* 调整大小 */
  height: 60px;
  overflow: hidden;
  opacity: 0.8; /* 半透明 */
}
.coupon-status-badge img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}


/* 去使用按钮 */
.use-btn {
  background: none;
  border: 1px solid #4a90e2;
  color: #4a90e2;
  padding: 6px 12px; /* 调整大小 */
  border-radius: 15px; /* 圆角 */
  font-size: 0.85rem; /* 调整字体 */
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap; /* 防止换行 */
  flex-shrink: 0; /* 防止被压缩 */
}
.use-btn:hover {
  background: #eaf4ff;
}
.use-btn i {
  margin-left: 4px;
  font-size: 0.7rem;
}


/* 不同优惠券类型的样式 */
.fixed-amount .coupon-left { background: #4a90e2; }
.fix-to-amount .coupon-left { background: #e53935; }
.percentage .coupon-left { background: #ff9800; }

/* 已使用和已过期样式 */
.coupon-card.used,
.coupon-card.expired {
  opacity: 0.6; /* 整体变淡 */
}

.coupon-card.used .coupon-left,
.coupon-card.expired .coupon-left {
  background: #bdc3c7; /* 灰色背景 */
}
.coupon-card.used .coupon-left::after,
.coupon-card.expired .coupon-left::after {
   background: linear-gradient(to bottom, #f5f7fa 5px, transparent 5px, transparent 10px); /* 虚线用背景色 */
   background-size: 100% 15px;
}


/* 移除覆盖层，使用状态角标代替 */
/* .coupon-card.used::after, .coupon-card.expired::after { ... } */

</style>
