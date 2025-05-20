<template>
<div class="page-container">
  <div class="business-detail">
    <div class="cover-image-container">
      <div class="cover-overlay"></div>
      <img :src="coverImage" :alt="business.merchantName">
      <h1 class="business-title">{{ business.merchantName }}</h1>
    </div>

    <div class="content-wrapper">
      <div class="info-section">
        <div class="rating-box">
          <span class="stars">{{ renderStars(business.rating) }}</span>
          <span class="rating-value">{{ business.rating }}分</span>
        </div>
        
        <div class="info-grid">
          <div class="info-item">
            <i class="fas fa-yen-sign"></i>
            <div class="info-text">
              <label>人均消费</label>
              <span>￥{{ business.avgPrice }}</span>
            </div>
          </div>
          
          <div class="info-item">
            <i class="fas fa-map-marker-alt"></i>
            <div class="info-text">
              <label>地址</label>
              <span>{{ business.address }}</span>
            </div>
          </div>
          
          <div class="info-item">
            <i class="fas fa-clock"></i>
            <div class="info-text">
              <label>营业时间</label>
              <span>{{ business.businessHours }}</span>
            </div>
          </div>
          
          <div class="info-item">
            <i class="fas fa-phone"></i>
            <div class="info-text">
              <label>联系电话</label>
              <span>{{ business.telephone }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 团购套餐列表 -->
      <div class="packages-section">
        <h3><i class="fas fa-tag"></i> 团购套餐</h3>
        <div v-if="packages.length === 0" class="no-packages">
          暂无可用团购套餐
        </div>
        <div v-else class="packages-list">
          <div 
            v-for="pkg in packages" 
            :key="pkg.id" 
            class="package-item"
            @click="goToPackageDetail(pkg.id)"
          >
            <div class="package-image">
              <img :src="pkg.imageUrl" :alt="pkg.title">
            </div>
            <div class="package-info">
              <h4>{{ pkg.title }}</h4>
              <div class="package-price">
                <span class="current-price">￥{{ pkg.price }}</span>
                <span class="original-price" v-if="pkg.originalPrice">￥{{ pkg.originalPrice }}</span>
              </div>
              <div class="package-sales">已售 {{ pkg.sales }} 份</div>
            </div>
          </div>
        </div>
      </div>

      <div class="description-section">
        <h3><i class="fas fa-store"></i> 商家特色</h3>
        <p>{{ business.description }}</p>
      </div>

      <div class="gallery-section">
        <h3><i class="fas fa-images"></i> 店铺实景</h3>
        <div class="gallery">
          <div v-for="(photo, index) in processedPhotos" 
               :key="index" 
               class="gallery-item"
               @click="showFullImage(photo)">
            <img :src="photo" alt="商家图片">
          </div>
        </div>
      </div>

      <!-- 添加评论区 -->
      <div class="reviews-section">
        <div class="reviews-header">
          <h3><i class="fas fa-comment"></i> 用户评论</h3>
          <el-button type="primary" size="small" @click="showReviewDialog">
            <i class="fas fa-pen"></i> 我要评论
          </el-button>
        </div>

        <div v-if="reviews.length === 0" class="no-reviews">
          暂无评论，快来发表第一条评论吧！
        </div>
        
        <div v-else class="reviews-list">
          <!-- 显示所有评论 -->
          <div v-for="review in filterTopLevelReviews()" :key="review.id" class="review-item">            
            <div class="review-content-wrapper">
              <div class="review-avatar">
                <div class="avatar-circle" :style="{ backgroundColor: getUserColor(review.userID) }">
                  {{ getUserInitial(review.userID) }}
                </div>
              </div>
              <div class="review-content">
                <div class="review-header">
                  <span class="review-username">{{ getUserName(review.userID) }}</span>
                  <span class="review-rating">{{ renderStars(review.rating) }}</span>
                  <span class="review-date">{{ formatDate(review.createTime) }}</span>
                </div>
                <div class="review-text">{{ review.comment }}</div>
                
                <div class="review-actions">
                  <span class="reply-button" @click="showReplyDialog(review)">
                    <i class="fas fa-reply"></i> 回复
                  </span>
                  
                  <!-- 如果有回复，显示查看回复按钮 -->
                  <span v-if="hasReplies(review.id)" class="view-replies" @click="toggleReplies(review.id)">
                    <i :class="['fas', isRepliesVisible(review.id) ? 'fa-chevron-up' : 'fa-chevron-down']"></i>
                    {{ isRepliesVisible(review.id) ? '收起回复' : `查看回复(${countReplies(review.id)})` }}
                  </span>
                </div>
                
                <!-- 显示评论的回复 -->
                <review-replies 
                  v-if="hasReplies(review.id) && isRepliesVisible(review.id)"
                  :review-id="review.id"
                  :all-reviews="reviews"
                  :users="users"
                  :replies-visible="repliesVisible"
                  @reply="showReplyDialog"
                  @toggle-replies="toggleReplies"
                ></review-replies>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 新增评论对话框 -->
  <el-dialog
    v-model="reviewDialogVisible"
    :title="isReply ? '回复评论' : '发表评论'"
    width="500px"
  >
    <el-form :model="newReview" label-width="80px">
      <el-form-item v-if="!isReply" label="评分">
        <el-rate
          v-model="newReview.rating"
          :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
          :texts="['很差', '较差', '一般', '不错', '很好']"
          show-text
        ></el-rate>
      </el-form-item>
      <el-form-item label="评论">
        <el-input
          type="textarea"
          v-model="newReview.comment"
          :rows="4"
          placeholder="请输入您的评论..."
        ></el-input>
        <div v-if="isReply" class="reply-to-info">
          回复给: {{ getUserName(replyToReview.userID) }}
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交</el-button>
      </span>
    </template>
  </el-dialog>
</div>
</template>

<script>
import axios from 'axios';
import ReviewReplies from '@/components/ReviewReplies.vue';

export default {
  components: {
    ReviewReplies
  },
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
      },
      packages: [],
      // 评论相关数据
      reviews: [],
      users: {},
      reviewDialogVisible: false,
      isReply: false,
      replyToReview: null,
      newReview: {
        rating: 5,
        comment: '',
        parentID: null
      },
      // 记录哪些评论的回复是展开的
      repliesVisible: []
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
    },
    // 筛选顶级评论（没有父评论的评论）
    topLevelReviews() {
      console.log('筛选顶级评论，全部评论:', this.reviews);
      const topReviews = this.reviews.filter(review => {
        // 注意：如果parentID是null或undefined，表示是顶级评论
        const isTopLevel = !review.parentID;
        console.log(`评论ID: ${review.id}, parentID: ${review.parentID}, 是否顶级: ${isTopLevel}`);
        return isTopLevel;
      });
      console.log('顶级评论:', topReviews);
      return topReviews;
    }
  },
  async mounted() {
    try {
      const id = this.$route.params.id;
      const response = await axios.get(`/api/businesses/${id}`);
      this.business = response.data;
      
      // 获取该商户的套餐列表
      this.fetchPackages(id);
      
      // 获取该商户的评论列表
      this.fetchReviews(id);
    } catch (error) {
      console.error('加载商家详情失败:', error);
    }
  },
  methods: {
    renderStars(rating) {
      const fullStars = Math.floor(rating);
      const halfStar = rating % 1 >= 0.5 ? 1 : 0;
      return '★'.repeat(fullStars) + (halfStar ? '☆' : '');
    },
    showFullImage(photo) {
      // Logic to show full image in a modal or new view
      console.log('Show full image:', photo);
    },
    async fetchPackages(businessId) {
      try {
        const response = await axios.get(`/api/packages/business/${businessId}`);
        this.packages = response.data;
        this.packages.forEach(pkg => {
          if (pkg.imageUrl && !pkg.imageUrl.startsWith('http')) {
            pkg.imageUrl = `http://localhost:8080${pkg.imageUrl}`;
          }
        });

      } catch (error) {
        console.error('获取套餐列表失败:', error);
        this.packages = [];
      }
    },
    goToPackageDetail(packageId) {
      this.$router.push(`/package/${packageId}`);
    },
    
    // 评论相关方法
    async fetchReviews(merchantId) {
      try {
        console.log(`开始获取商家评论，商家ID: ${merchantId}`);
        const response = await axios.get(`/api/reviews/merchant/${merchantId}`);
        console.log('评论获取结果:', response.data);
        this.reviews = response.data;
        
        // 获取所有评论中出现的用户ID
        const userIds = [...new Set(this.reviews.map(review => review.userID))];
        console.log('评论中的用户IDs:', userIds);
        
        // 获取所有用户信息
        for (const userId of userIds) {
          await this.fetchUserInfo(userId);
        }
      } catch (error) {
        console.error('获取评论列表失败:', error);
        this.reviews = [];
      }
    },
    
    async fetchUserInfo(userId) {
      if (this.users[userId]) return;
      
      try {
        // 由于后端没有提供通过ID获取用户信息的API，我们使用一个间接方法
        // 首先尝试从localStorage中获取当前用户信息
        const currentUserInfo = JSON.parse(localStorage.getItem('userInfo'));
        
        // 如果是当前登录用户，直接使用本地存储的用户信息
        if (currentUserInfo && currentUserInfo.id === userId) {
          console.log(`使用本地缓存的用户信息 (ID: ${userId}):`, currentUserInfo);
          this.users[userId] = {
            id: currentUserInfo.id,
            username: currentUserInfo.username
          };
          return;
        }
        
        // 对于其他用户，我们创建一个默认的用户信息对象
        console.log(`创建默认用户信息 (ID: ${userId})`);
        this.users[userId] = { 
          id: userId,
          username: `用户${userId}`
        };
      } catch (error) {
        console.error(`获取/创建用户信息失败 (ID: ${userId}):`, error);
        this.users[userId] = { 
          id: userId,
          username: `用户${userId}`
        };
      }
    },
    
    getUserName(userId) {
      return this.users[userId]?.username || '未知用户';
    },
    
    formatDate(dateString) {
      if (!dateString) return '';
      
      const date = new Date(dateString);
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    },
    
    getChildReviews(parentId) {
      console.log(`获取父评论ID ${parentId} 的子评论`, this.reviews);
      // 转换为数字类型进行比较，防止字符串比较问题
      const childReviews = this.reviews.filter(review => {
        // 将两者都转换为相同类型后比较
        const reviewParentId = review.parentID;
        const isChild = reviewParentId !== null && reviewParentId !== undefined && 
                       (reviewParentId === parentId || 
                        Number(reviewParentId) === Number(parentId));
        console.log(`评论ID: ${review.id}, parentID: ${review.parentID}, 是否子评论: ${isChild}`);
        return isChild;
      });
      console.log(`父评论ID ${parentId} 的子评论:`, childReviews);
      return childReviews;
    },
    
    showReviewDialog() {
      // 检查用户是否登录
      const userInfo = JSON.parse(localStorage.getItem('userInfo'));
      if (!userInfo) {
        this.$message.error('请先登录后再评论');
        this.$router.push('/auth');
        return;
      }
      
      this.isReply = false;
      this.replyToReview = null;
      this.newReview = {
        rating: 5,
        comment: '',
        parentID: null
      };
      this.reviewDialogVisible = true;
    },
    
    showReplyDialog(review) {
      // 检查用户是否登录
      const userInfo = JSON.parse(localStorage.getItem('userInfo'));
      if (!userInfo) {
        this.$message.error('请先登录后再评论');
        this.$router.push('/auth');
        return;
      }
      
      this.isReply = true;
      this.replyToReview = review;
      
      // 将评论ID转换为数字类型
      const reviewId = review.id ? Number(review.id) : null;
      
      this.newReview = {
        rating: 0, // 回复评论不需要评分
        comment: '',
        parentID: reviewId
      };
      this.reviewDialogVisible = true;
      
      console.log('准备回复评论:', {
        评论ID: reviewId,
        评论内容: review.comment,
        回复者: review.userID
      });
    },
    
    async submitReview() {
      // 检查评论内容是否为空
      if (!this.newReview.comment.trim()) {
        this.$message.error('评论内容不能为空');
        return;
      }
      
      // 如果是直接评论商家，需要评分
      if (!this.isReply && (!this.newReview.rating || this.newReview.rating < 1)) {
        this.$message.error('请为商家评分');
        return;
      }
      
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo'));
        
        // 确保parentID是数字类型
        let parentID = this.newReview.parentID;
        if (parentID) {
          parentID = Number(parentID);
        }
        
        const reviewData = {
          userID: userInfo.id,
          merchantID: this.business.id,
          rating: this.isReply ? 0 : this.newReview.rating,
          comment: this.newReview.comment,
          parentID: parentID,
          createTime: new Date().toISOString()
        };
        
        console.log('准备提交的评论数据:', reviewData);
        const response = await axios.post('/api/reviews/add', reviewData);
        console.log('评论提交响应:', response);
        
        this.$message.success('评论提交成功');
        this.reviewDialogVisible = false;
        
        // 重新获取评论列表
        this.fetchReviews(this.business.id);
      } catch (error) {
        console.error('提交评论失败:', error);
        this.$message.error('评论提交失败，请稍后重试');
      }
    },
    hasReplies(reviewId) {
      return this.reviews.some(review => review.parentID === reviewId);
    },
    getDirectReplies(reviewId) {
      return this.reviews.filter(review => review.parentID === reviewId);
    },
    isRepliesVisible(reviewId) {
      // 对每个reviewId使用唯一的标识符，以便于区分不同层级的同一ID
      return this.repliesVisible.includes(reviewId);
    },
    toggleReplies(reviewId) {
      console.log(`切换评论ID ${reviewId} 的回复显示状态`);
      if (this.isRepliesVisible(reviewId)) {
        // 收起当前评论的回复时，也需要收起其所有子回复
        this.repliesVisible = this.repliesVisible.filter(id => id !== reviewId);
        
        // 递归查找并收起所有子回复
        const childReplies = this.getDirectReplies(reviewId);
        childReplies.forEach(reply => {
          if (this.isRepliesVisible(reply.id)) {
            this.toggleReplies(reply.id); // 递归收起子回复
          }
        });
        
        console.log(`收起评论ID ${reviewId} 的回复，当前可见回复:`, this.repliesVisible);
      } else {
        this.repliesVisible = [...this.repliesVisible, reviewId];
        console.log(`展开评论ID ${reviewId} 的回复，当前可见回复:`, this.repliesVisible);
      }
    },
    countReplies(reviewId) {
      return this.getDirectReplies(reviewId).length;
    },
    getReviewById(reviewId) {
      return this.reviews.find(review => review.id === reviewId);
    },
    filterTopLevelReviews() {
      return this.reviews.filter(review => !review.parentID);
    },
    getUserInitial(userId) {
      const user = this.users[userId];
      if (user && user.username) {
        return user.username.charAt(0).toUpperCase();
      }
      return 'U';
    },
    
    // 根据用户ID生成一个固定的颜色
    getUserColor(userId) {
      // 预定义的颜色数组，柔和但有区分度的颜色
      const colors = [
        '#4A90E2', '#50C878', '#F5A623', '#E74C3C', '#9B59B6', 
        '#3498DB', '#2ECC71', '#F1C40F', '#E67E22', '#8E44AD'
      ];
      
      // 使用userId作为索引选择颜色
      const colorIndex = (userId % colors.length);
      return colors[colorIndex];
    }
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.business-detail {
  max-width: 1200px;
  margin: 0 auto;
}

.cover-image-container {
  position: relative;
  height: 400px;
  overflow: hidden;
  border-radius: 0 0 30px 30px;
}

.cover-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 50%;
  background: linear-gradient(to bottom, transparent, rgba(0,0,0,0.7));
}

.cover-image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.business-title {
  position: absolute;
  bottom: 30px;
  left: 40px;
  color: white;
  font-size: 2.5rem;  /* 增大标题文字 */
  font-weight: 600;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
}

.content-wrapper {
  padding: 30px;
  margin-top: -50px;
  position: relative;
}

.info-section {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  font-size: 1.1rem;  /* 增大信息文字 */
}

.rating-box {
  text-align: center;
  margin-bottom: 20px;
  font-size: 1.8rem;  /* 增大评分文字 */
}

.stars {
  color: #ffd700;
  font-size: 24px;
  margin-right: 10px;
}

.rating-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 18px;  /* 增加内边距 */
  background: #f8f9fa;
  border-radius: 10px;
}

.info-item i {
  font-size: 1.4rem;  /* 增大图标 */
  margin-right: 15px;
  color: #4a90e2;
}

.info-text {
  display: flex;
  flex-direction: column;
}

.info-text label {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.info-text span {
  font-size: 16px;
  color: #333;
}

.description-section, .gallery-section, .packages-section, .reviews-section {
  background: white;
  border-radius: 15px;
  padding: 25px;
  margin-top: 20px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.description-section h3, .gallery-section h3, .packages-section h3, .reviews-section h3 {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #333;
  margin-bottom: 15px;
  font-size: 1.6rem;  /* 增大描述标题 */
}

.description-section p {
  line-height: 1.8;  /* 增加行高 */
  font-size: 1.1rem;  /* 增大描述文字 */
  color: #666;
}

.gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
  margin-top: 20px;
}

.gallery-item {
  aspect-ratio: 1;
  overflow: hidden;
  border-radius: 10px;
  cursor: pointer;
  transition: transform 0.3s;
}

.gallery-item:hover {
  transform: scale(1.05);
}

.gallery-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 套餐样式 */
.packages-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.package-item {
  display: flex;
  background: #f8f9fa;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
}

.package-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.1);
}

.package-image {
  width: 120px;
  height: 120px;
  overflow: hidden;
}

.package-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.package-info {
  flex: 1;
  padding: 15px;
  position: relative;
}

.package-info h4 {
  margin: 0 0 10px 0;
  font-size: 1.1rem;
  color: #333;
}

.package-price {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.current-price {
  color: #e53935;
  font-size: 1.2rem;
  font-weight: bold;
}

.original-price {
  color: #999;
  font-size: 0.9rem;
  text-decoration: line-through;
}

.package-sales {
  color: #666;
  font-size: 0.8rem;
}

.buy-btn {
  position: absolute;
  bottom: 15px;
  right: 15px;
  padding: 6px 12px;
  background: #e53935;
  color: white;
  border: none;
  border-radius: 15px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.buy-btn:hover {
  background: #c62828;
}

.no-packages {
  padding: 30px;
  text-align: center;
  color: #999;
  font-size: 1.1rem;
}

/* 评论区样式 */
.reviews-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.no-reviews {
  padding: 30px;
  text-align: center;
  color: #999;
  font-size: 1.1rem;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 10px;
}

/* 回复评论的样式 */
.reply-review {
  margin-left: 40px;
  background: rgba(248, 249, 250, 0.5);
  border-left: 3px solid #4a90e2;
}

.review-content-wrapper {
  display: flex;
}

.review-avatar {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  margin-right: 15px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1.2rem;
  font-weight: bold;
}

.review-content {
  flex: 1;
}

.review-header {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 8px;
}

.review-username {
  font-weight: bold;
  color: #333;
}

.review-rating {
  color: #ffd700;
}

.review-date {
  color: #999;
  font-size: 0.8rem;
}

.review-text {
  line-height: 1.5;
  margin-bottom: 10px;
}

.review-actions {
  display: flex;
  gap: 15px;
  margin-top: 10px;
}

.reply-button {
  cursor: pointer;
  color: #4a90e2;
  font-size: 0.9rem;
}

.reply-button:hover {
  text-decoration: underline;
}

.reply-to-info {
  margin-top: 8px;
  color: #4a90e2;
  font-size: 0.9rem;
}

.reply-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  color: #666;
  font-size: 0.9rem;
}

.reply-icon {
  color: #4a90e2;
  font-size: 0.8rem;
}

.view-replies {
  cursor: pointer;
  color: #4a90e2;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 5px;
}

.view-replies:hover {
  text-decoration: underline;
}

.replies-container {
  margin-top: 15px;
  margin-left: 20px;
  padding-left: 15px;
  border-left: 2px solid #e0e0e0;
}

.reply-item {
  display: flex;
  margin-bottom: 15px;
  padding: 12px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.nested-replies {
  margin-top: 12px;
  margin-left: 15px;
  padding-left: 15px;
  border-left: 2px solid #e0e0e0;
}

.reply-item:last-child {
  margin-bottom: 0;
}
</style>