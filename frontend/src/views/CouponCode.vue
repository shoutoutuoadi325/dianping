<template>
  <div class="page-container">
    <div class="coupon-code">
      <div class="back-link" @click="goBack">
        <i class="fas fa-arrow-left"></i> 返回我的订单
      </div>

      <h1>券码详情</h1>

      <div class="code-section">
        <h2>券码</h2>
        <p class="code">{{ couponCode }}</p>
      </div>

      <div class="qr-section">
        <h2>二维码</h2>
        <div class="qr-code">
          <qrcode-vue :value="couponCode" :size="200" />
        </div>
      </div>

      <div class="order-details">
        <h2>订单详情</h2>
        <p><strong>套餐：</strong>{{ orderDetails.packageTitle }}</p>
        <p><strong>商家：</strong>{{ orderDetails.businessName }}</p>
        <p><strong>下单时间：</strong>{{ formatDate(orderDetails.createTime) }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import QrcodeVue from 'qrcode.vue';
import axios from 'axios';

export default {
  components: { QrcodeVue },
  data() {
    return {
      orderId: null,
      couponCode: '',
      orderDetails: {
        packageTitle: '',
        businessName: '',
        createTime: ''
      }
    };
  },
  async mounted() {
    this.orderId = this.$route.params.id;
    await this.fetchOrderDetails();
  },
  methods: {
    async fetchOrderDetails() {
      try {
        const response = await axios.get(`/api/orders/${this.orderId}`);
        this.orderDetails = response.data;
        this.couponCode = this.orderDetails.couponCode;
      } catch (error) {
        console.error('获取订单详情失败:', error);
      }
    },
    formatDate(dateString) {
      const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
      return new Date(dateString).toLocaleDateString(undefined, options);
    },
    goBack() {
      this.$router.push('/my-orders');
    }
  }
};
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.coupon-code {
  max-width: 600px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.back-link {
  display: inline-flex;
  align-items: center;
  color: #4a90e2;
  margin-bottom: 20px;
  cursor: pointer;
  font-size: 1rem;
}

.back-link i {
  margin-right: 8px;
}

h1 {
  font-size: 1.8rem;
  margin-bottom: 20px;
  color: #333;
}

.code-section, .qr-section, .order-details {
  margin-bottom: 20px;
}

.code {
  font-size: 1.5rem;
  font-weight: bold;
  color: #e53935;
  text-align: center;
  margin: 10px 0;
}

.qr-code {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}

.order-details p {
  font-size: 1rem;
  color: #666;
  margin: 5px 0;
}

.order-details strong {
  color: #333;
}
</style>