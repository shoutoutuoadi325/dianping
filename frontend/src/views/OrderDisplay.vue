<template>
    <div class="order-display">
      <div class="page-header">
        <button class="back-btn" @click="goBack">
          <i class="fas fa-arrow-left"></i> 返回
        </button>
        <h1>我的订单</h1>
      </div>
      <div class="orders-container">
        <div v-if="loading" class="loading">
          <i class="fas fa-spinner fa-spin"></i> 加载中...
        </div>
        <div v-else-if="orders.length === 0" class="empty-state">
          暂无订单
        </div>
        <div v-else>
          <div v-for="order in orders" :key="order.id" class="order-card">
            <h3>订单号: {{ order.id }}</h3>
            <p>{{ order.details }}</p>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'OrderDisplay',
    data() {
      return {
        orders: [],
        loading: true
      }
    },
    mounted() {
      // 模拟加载订单数据
      setTimeout(() => {
        // 示例订单数据，可替换为实际接口调用
        this.orders = [
          { id: '001', details: '订单详情示例1' },
          { id: '002', details: '订单详情示例2' }
        ];
        this.loading = false;
      }, 1000);
    },
    methods: {
      goBack() {
        this.$router.push('/home');
      }
    }
  }
  </script>
  
  <style scoped>
  .order-display {
    min-height: 100vh;
    background: linear-gradient(135deg, #f0f4f8, #d9e2ec);
    padding: 20px;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    color: #333;
  }
  
  .page-header {
    display: flex;
    align-items: center;
    margin-bottom: 30px;
  }
  
  .back-btn {
    background: none;
    border: none;
    font-size: 1rem;
    cursor: pointer;
    margin-right: 15px;
    color: #4a90e2;
    transition: color 0.3s;
  }
  
  .back-btn:hover {
    color: #357abd;
  }
  
  .page-header h1 {
    font-size: 1.8rem;
    margin: 0;
    flex-grow: 1;
    text-align: center;
    color: #2c3e50;
  }
  
  .orders-container {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .order-card {
    background: white;
    border: 1px solid #e1e8ed;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 4px 10px rgba(0,0,0,0.05);
    transition: transform 0.3s, box-shadow 0.3s;
  }
  
  .order-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 15px rgba(0,0,0,0.1);
  }
  
  .loading,
  .empty-state {
    text-align: center;
    padding: 50px;
    font-size: 1.2rem;
    color: #666;
  }
  
  .empty-state {
    background: #fff;
    border: 1px dashed #ccc;
    border-radius: 8px;
  }
  </style>