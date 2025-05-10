<template>
  <div
    class="comment-container"
    :style="{ top: position.y + 'px', left: position.x + 'px' }"
  >
    <div class="header" @mousedown="startDrag">
      评论区
      <span class="close-btn" @click="$emit('close')">×</span>
    </div>
    <!-- 评论输入框 -->
    <div class="comment-input-box">
      <div class="input-wrapper">
        <textarea
          v-model="newComment"
          placeholder="写下你的评论..."
          @keyup.enter="submitComment"
        ></textarea>
        <button class="submit-btn" @click="submitComment">发布</button>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list">
      <div
        v-for="comment in comments"
        :key="comment.commentId"
        class="comment-item"
      >
        <!-- 主评论 -->
        <div class="comment-main">
          <!-- <img class="avatar" :src="comment.author.avatar" alt="avatar" /> -->
          <div class="content-wrapper">
            <div class="user-info">
              <span class="username">{{ comment.userId }}</span>
              <span class="time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <div class="content">{{ comment.content }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from "@/apiClient";

export default {
  props: {
    postId: {
      type: [String, Number],
      required: true,
    },
    currentUserId: {
      type: [String, Number],
      required: true,
    },
  },

  data() {
    return {
      // 拖动相关
      position: {
        x: window.innerWidth - 420,
        y: 60,
      },
      dragging: false,
      startX: 0,
      startY: 0,
      startLeft: 0,
      startTop: 0,

      // 评论数据
      newComment: "",
      newReply: "",
      activeReplyId: null,
      comments: [],
      pollInterval: null,
    };
  },

  mounted() {
    this.loadComments();
    this.startPolling();
  },

  beforeUnmount() {
    clearInterval(this.pollInterval);
  },

  methods: {
    // 拖动相关方法
    startDrag(e) {
      this.dragging = true;
      this.startX = e.clientX;
      this.startY = e.clientY;
      this.startLeft = this.position.x;
      this.startTop = this.position.y;

      document.addEventListener("mousemove", this.onDrag);
      document.addEventListener("mouseup", this.stopDrag);
    },

    onDrag(e) {
      if (!this.dragging) return;
      const deltaX = e.clientX - this.startX;
      const deltaY = e.clientY - this.startY;
      this.position.x = this.startLeft + deltaX;
      this.position.y = this.startTop + deltaY;
    },

    stopDrag() {
      this.dragging = false;
      document.removeEventListener("mousemove", this.onDrag);
      document.removeEventListener("mouseup", this.stopDrag);
    },

    // 数据加载
    startPolling() {
      this.pollInterval = setInterval(async () => {
        await this.loadComments();
      }, 5000); // 每5秒刷新评论
    },

    async loadComments() {
      try {
        const response = await apiClient.get(
          `/community/comments/post/${this.postId}`
        );
        this.comments = response.data
          .map((comment) => ({
            ...comment,
            createTime: new Date(comment.create_time),
          }))
          .reverse();
      } catch (error) {
        console.error("加载评论失败", error);
      }
    },

    // 评论操作
    async submitComment() {
      if (!this.newComment.trim()) return;

      try {
        await apiClient.post(
          `/community/comments?postId=${this.postId}&userId=${
            this.currentUserId
          }&content=${encodeURIComponent(this.newComment.trim())}`,
          {}
        );
        this.newComment = "";
        await this.loadComments();
        this.scrollToBottom();
      } catch (error) {
        console.error("提交评论失败", error);
      }
    },

    // 其他方法
    formatTime(date) {
      return date.toLocaleString();
    },

    scrollToBottom() {
      const container = this.$refs.commentList;
      container.scrollTop = container.scrollHeight;
    },
  },
};
</script>

<style scoped>
.comment-container {
  position: fixed;
  width: 400px;
  height: 70vh;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  display: flex;
  flex-direction: column;
  z-index: 1000;
  overflow: hidden;
  transition: box-shadow 0.3s ease;
}

.comment-container:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.header {
  position: relative;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  cursor: move;
  user-select: none;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #2d3436;
}

.close-btn {
  cursor: pointer;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  color: #adb5bd;
  font-size: 24px;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: #f1f3f5;
  color: #495057;
}

.comment-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 20px;
}

.comment-input-box,
.reply-input-box {
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  margin-right: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

textarea {
  width: 100%;
  min-height: 80px;
  padding: 12px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  resize: none;
  font-family: inherit;
  font-size: 14px;
  transition: all 0.2s ease;
}

textarea:focus {
  border-color: #4dabf7;
  box-shadow: 0 0 0 3px rgba(77, 171, 247, 0.1);
  outline: none;
}

.submit-btn {
  background: #4dabf7;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.submit-btn:hover {
  background: #339af0;
  transform: translateY(-1px);
}

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #f1f3f5;
}

.comment-item:last-child {
  border-bottom: none;
}

.user-info {
  margin-bottom: 4px;
}

.username {
  color: #2d3436;
  font-weight: 600;
}

.time {
  color: #adb5bd;
  font-size: 12px;
}

.content {
  color: #495057;
  line-height: 1.5;
  margin: 8px 0;
}

.actions button {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 13px;
  transition: all 0.2s ease;
}

.like-btn:hover {
  color: #ff6b6b;
  background: #fff5f5;
}

.reply-btn:hover {
  color: #4dabf7;
  background: #e7f5ff;
}

.sub-comments {
  margin-left: 48px;
  border-left: 2px solid #f1f3f5;
  padding-left: 16px;
  margin-top: 12px;
}

.sub-comment {
  padding: 8px 0;
}

.reply-to {
  color: #4dabf7;
  font-weight: 500;
}
</style>
