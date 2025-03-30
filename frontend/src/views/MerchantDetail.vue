<template>
  <div class="container">
    <!-- 返回按钮 -->
    <button class="back-btn" @click="$router.go(-1)">
      <i class="fas fa-arrow-left"></i>
      返回列表
    </button>

    <!-- 商家详情内容 -->
    <div v-if="merchant" class="merchant-detail">
      <h1>{{ merchant.merchantName }}</h1>
      <div class="detail-photo">
        <img :src="merchant.photoUrl">
      </div>

      <div class="detail-info">
        <div class="info-item">
          <i class="fas fa-star"></i>
          <span>评分：{{ merchant.score.toFixed(1) }}</span>
        </div>
        <div class="info-item">
          <i class="fas fa-wallet"></i>
          <span>人均：￥{{ merchant.averageConsumption.toFixed(0) }}</span>
        </div>
        <div class="info-item">
          <i class="fas fa-map-marker-alt"></i>
          <span>地址：{{ merchant.location }}</span>
        </div>
        <div class="info-item">
          <i class="fas fa-phone"></i>
          <span>电话：{{ merchant.telephoneNumber }}</span>
        </div>
        <div class="info-item">
          <i class="fas fa-clock"></i>
          <span>营业时间：{{ formatTime(merchant.startTime) }} - {{ formatTime(merchant.endTime) }}</span>
        </div>
        <div class="tags">
          <span v-for="tag in merchant.tags.split(',')" :key="tag" class="tag">
            {{ tag }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const merchant = ref(null)

const formatTime = (time) => {
  return new Date(time).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

onMounted(async () => {
  try {
    const response = await axios.get(`/api/merchants/${route.params.id}`)
    merchant.value = response.data
  } catch (error) {
    console.error('获取详情失败:', error)
  }
})
</script>