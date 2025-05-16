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
              <button
                v-if="comment.userId == currentUserId"
                class="delete-btn"
                @click="deleteComment(comment.commentId)"
              >
                <i class="album-icons icon-huishouzhan"></i>
              </button>
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
            // 确保正确解析日期字段（注意字段名可能是 created_at 而不是 createTime）
            createTime: comment.created_at
              ? new Date(comment.created_at)
              : new Date(),
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
    formatTime(date) {
      if (!(date instanceof Date) || isNaN(date.getTime())) {
        return "未知时间";
      }
      const now = new Date();
      const diffInSeconds = Math.floor((now - date) / 1000);

      if (diffInSeconds < 60) {
        return "刚刚";
      } else if (diffInSeconds < 3600) {
        return `${Math.floor(diffInSeconds / 60)}分钟前`;
      } else if (diffInSeconds < 86400) {
        return `${Math.floor(diffInSeconds / 3600)}小时前`;
      } else if (diffInSeconds < 2592000) {
        return `${Math.floor(diffInSeconds / 86400)}天前`;
      } else {
        return date.toLocaleDateString("zh-CN", {
          year: "numeric",
          month: "short",
          day: "numeric",
          hour: "2-digit",
          minute: "2-digit",
        });
      }
    },
    async deleteComment(commentId) {
      if (!confirm("确定要删除这条评论吗？")) return;
      try {
        const response = await apiClient.delete(
          `/community/comments/${commentId}`
        );
        this.loadComments();
      } catch (error) {
        console.error("删除评论失败:", error);
        this.$message.error("删除失败，请重试");
      }
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
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  position: relative; /* 添加定位上下文 */
  padding-right: 40px; /* 为删除按钮预留空间 */
}

.username {
  color: #2d3436;
  font-weight: 600;
  margin-right: 8px; /* 用户名和时间的间距 */
}

.time {
  color: #adb5bd;
  font-size: 12px;
  flex-grow: 1; /* 让时间占据剩余空间 */
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

.delete-btn {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  padding: 2px 6px;
  font-size: 12px;
  color: #4dabf7;
  background-color: transparent;
  border: none;
  cursor: pointer;
  transition: color 0.2s ease;
}

.delete-btn:hover {
  color: #e84118; /* 悬停时加深红色 */
}

.delete-btn i {
  font-size: 16px; /* 图标大小 */
}
</style>
