<template>
  // ...existing code...
</template>

<script>
  // ...existing code...
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.coupon-code-container {
  width: 100%;
  max-width: 500px;
  margin: 20px auto;
}

.success-header {
  text-align: center;
  margin-bottom: 30px;
}

.success-header i {
  font-size: 4rem;
  color: #4CAF50;
  margin-bottom: 15px;
}

.success-header h1 {
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 10px;
}

.success-header p {
  color: #666;
  font-size: 1.1rem;
}

.code-section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  margin-bottom: 20px;
}

.code-number {
  display: flex;
  justify-content: center;
  margin-bottom: 25px;
  flex-wrap: wrap;
}

.code-group {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  background: #f8f9fa;
  padding: 10px 15px;
  border-radius: 8px;
  margin: 0 5px 10px;
  letter-spacing: 2px;
}

.qr-code {
  margin-bottom: 20px;
}

.qr-code img {
  width: 200px;
  height: 200px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.code-tips {
  color: #666;
  font-size: 0.9rem;
}

.order-info-section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  margin-bottom: 20px;
}

.order-info-section h2 {
  font-size: 1.3rem;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.order-info-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-row {
  display: flex;
  justify-content: space-between;
}

.info-label {
  color: #666;
}

.info-value {
  color: #333;
  font-weight: 500;
}

.info-value.price {
  color: #e53935;
  font-weight: bold;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.primary-btn, .secondary-btn {
  flex: 1;
  padding: 15px;
  border-radius: 8px;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
}

.primary-btn {
  background: #4a90e2;
  color: white;
}

.primary-btn:hover {
  background: #357abd;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(74, 144, 226, 0.3);
}

.secondary-btn {
  background: #f8f9fa;
  border: 1px solid #ddd;
  color: #666;
}

.secondary-btn:hover {
  background: #eee;
  transform: translateY(-2px);
}
</style>