<template>
  <div class="replies-container">
    <div v-for="reply in directReplies" :key="reply.id" class="reply-item">
      <div class="review-avatar">
        <div class="avatar-circle" :style="{ backgroundColor: getUserColor(reply.userID) }">
          {{ getUserInitial(reply.userID) }}
        </div>
      </div>
      <div class="review-content">
        <div class="review-header">
          <span class="review-username">{{ getUserName(reply.userID) }}</span>
          <span class="review-date">{{ formatDate(reply.createTime) }}</span>
        </div>
        <div class="review-text">{{ reply.comment }}</div>
        <div class="review-actions">
          <span class="reply-button" @click="$emit('reply', reply)">
            <i class="fas fa-reply"></i> 回复
          </span>
          
          <!-- 递归显示下一级回复 -->
          <span v-if="hasReplies(reply.id)" class="view-replies" @click="$emit('toggle-replies', reply.id)">
            <i :class="['fas', isRepliesVisible(reply.id) ? 'fa-chevron-up' : 'fa-chevron-down']"></i>
            {{ isRepliesVisible(reply.id) ? '收起回复' : `查看回复(${countReplies(reply.id)})` }}
          </span>
        </div>
        
        <!-- 递归组件自身以显示更深层次的回复 -->
        <review-replies 
          v-if="hasReplies(reply.id) && isRepliesVisible(reply.id)"
          :review-id="reply.id"
          :all-reviews="allReviews"
          :users="users"
          :replies-visible="repliesVisible"
          @reply="$emit('reply', $event)"
          @toggle-replies="$emit('toggle-replies', $event)"
        ></review-replies>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ReviewReplies',
  props: {
    reviewId: {
      type: [Number, String],
      required: true
    },
    allReviews: {
      type: Array,
      required: true
    },
    users: {
      type: Object,
      required: true
    },
    repliesVisible: {
      type: Array,
      required: true
    }
  },
  computed: {
    directReplies() {
      return this.allReviews.filter(review => {
        return review.parentID === this.reviewId || 
               Number(review.parentID) === Number(this.reviewId);
      });
    }
  },
  methods: {
    getUserName(userId) {
      return this.users[userId]?.username || '未知用户';
    },
    formatDate(dateString) {
      if (!dateString) return '';
      
      const date = new Date(dateString);
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    },
    hasReplies(reviewId) {
      return this.allReviews.some(review => {
        return review.parentID === reviewId || 
               Number(review.parentID) === Number(reviewId);
      });
    },
    countReplies(reviewId) {
      return this.allReviews.filter(review => {
        return review.parentID === reviewId || 
               Number(review.parentID) === Number(reviewId);
      }).length;
    },
    isRepliesVisible(reviewId) {
      return this.repliesVisible.includes(reviewId);
    },
    getUserInitial(userId) {
      if (this.users[userId]?.username) {
        return this.users[userId].username.charAt(0).toUpperCase();
      }
      return 'U';
    },
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

.reply-item:last-child {
  margin-bottom: 0;
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
  width: 40px;
  height: 40px;
  border-radius: 50%;
  color: #fff;
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
</style> 