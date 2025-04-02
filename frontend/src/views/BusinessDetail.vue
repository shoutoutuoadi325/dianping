<template>
  <div class="business-detail">
    <!-- 封面图显示单张 -->
    <div class="cover-image">
      <img :src="coverImage" :alt="business.merchantName">
    </div>

    <h1>{{ business.merchantName }}</h1>

    <div class="info-section">
      <div class="rating">
        <span class="stars">{{ renderStars(business.rating) }}</span>
        {{ business.rating }}分
      </div>
      <div class="price">人均: ￥{{ business.avgPrice }}</div>
      <div class="address">
        <i class="fas fa-map-marker-alt"></i> {{ business.address }}
      </div>
      <div class="hours">
        <i class="fas fa-clock"></i> 营业时间: {{ business.businessHours }}
      </div>
      <div class="phone">
        <i class="fas fa-phone"></i> 电话: {{ business.telephone }}
      </div>
    </div>

    <div class="description">
      <h3>商家特色</h3>
      <p>{{ business.description }}</p>
    </div>

    <!-- 详情图遍历photoUrls -->
    <div class="gallery">
      <img
          v-for="(photo, index) in processedPhotos"
          :key="index"
          :src="photo"
          class="gallery-img"
          alt="商家图片"
      >
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      business: {
        merchantName: '',
        rating: 0,
        avgPrice: 0,
        address: '',
        businessHours: '',
        telephone: '',
        description: '',
        coverUrl: '',
        photoUrls: [],
      }
    }
  },
  computed: {
    // 处理封面图路径
    coverImage() {
      return `http://localhost:8080${this.business.coverUrl}`
    },
    // 处理详情图路径
    processedPhotos() {
      if (!this.business.photoUrls || this.business.photoUrls.length === 0) return [];

      const rawUrls = Array.isArray(this.business.photoUrls)
          ? this.business.photoUrls
          : this.business.photoUrls.split(',');

      return rawUrls.map(url => {
        const trimmedUrl = url.trim();
        return trimmedUrl.startsWith('http')
            ? trimmedUrl
            : `http://localhost:8080${trimmedUrl}`;
      });
    }
  },
  async mounted() {
    try {
      const id = this.$route.params.id;
      const response = await axios.get(`/api/businesses/${id}`);
      this.business = response.data;
    } catch (error) {
      console.error('加载商家详情失败:', error);
    }
  },
  methods: {
    renderStars(rating) {
      const fullStars = Math.floor(rating);
      const halfStar = rating % 1 >= 0.5 ? 1 : 0;
      return '★'.repeat(fullStars) + (halfStar ? '☆' : '');
    }
  }
}
</script>

<style scoped>
.business-detail {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
}

.cover-image img {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 10px;
  margin-bottom: 20px;
}

.info-section > div {
  margin: 12px 0;
  font-size: 16px;
  color: #666;
}

.stars {
  color: #ffb800;
  font-size: 20px;
}

.gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 15px;
  margin-top: 30px;
}

.gallery-img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.description {
  margin: 30px 0;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}
</style>