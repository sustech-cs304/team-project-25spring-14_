<template>
  <div class="chat-container">
    <div class="header">
      <span class="close-btn" @click="$emit('close')">×</span>
    </div>
    <!-- 聊天内容区域 -->
    <div class="message-list" ref="messageList">
      <div
        v-for="(message, index) in messages"
        :key="index"
        class="message-item"
        :class="{ 'message-right': message.isSelf }"
      >
        <!-- <img
          v-if="!message.isSelf"
          :src="message.avatar"
          class="message-avatar"
          alt="对方头像"
        /> -->
        <div class="message-content">
          <div class="message-bubble">{{ message.content }}</div>
          <!-- <div class="message-time">{{ formatTime(message.createdAt) }}</div> -->
        </div>
      </div>
    </div>

    <!-- 消息输入区域 -->
    <div class="input-area">
      <textarea
        v-model="inputMessage"
        @keyup.enter.exact="sendMessage"
        placeholder="输入消息..."
      ></textarea>
      <button @click="sendMessage">发送</button>
    </div>
  </div>
</template>

<script>
import apiClient from "@/apiClient";
export default {
  props: {
    recipientId: {
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
      inputMessage: "",
      messages: [],
      pollInterval: null,
    };
  },
  mounted() {
    this.loadHistory();
    this.scrollToBottom();
    this.startPolling();
  },
  methods: {
    startPolling() {
      this.pollInterval = setInterval(async () => {
        await this.loadHistory();
        this.scrollToBottom();
      }, 3000); // 每3秒刷新一次
    },
    async sendMessage() {
      if (!this.inputMessage.trim()) return;
      const content = this.inputMessage.trim();
      const response = await apiClient.post(
        `/message/send?senderId=${this.currentUserId}&recipientId=${
          this.recipientId
        }&content=${encodeURIComponent(content)}`,
        {}
      );
      await this.loadHistory();
      this.scrollToBottom();
      this.inputMessage = "";
    },
    async loadHistory() {
      try {
        const response = await apiClient.get(
          `/message/history?userId1=${this.currentUserId}&userId2=${this.recipientId}`
        );
        this.messages = response.data.data.map((message) => ({
          ...message,
          isSelf: message.senderId.toString() === this.currentUserId.toString(),
        }));
      } catch (error) {
        console.error("加载消息失败", error);
      }
    },
    scrollToBottom() {
      const container = this.$refs.messageList;
      container.scrollTop = container.scrollHeight;
    },
  },
};
</script>

<style scoped>
.chat-container {
  position: fixed;
  right: 20px;
  bottom: 20px;
  width: 400px;
  height: 600px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  z-index: 1000;
}

.header {
  position: relative;
  padding: 16px 20px;
  background: #ffffff;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-radius: 8px 8px 0 0;
}

/* 添加关闭按钮样式 */
.close-btn {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  color: #909399;
  font-size: 24px;
  line-height: 1;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.close-btn:hover {
  color: #ffffff;
  background-color: #f56c6c;
  transform: translateY(-50%) scale(1);
}

/* 消息列表高度调整 */
.message-list {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #f8f8f8;
}

.message-item {
  display: flex;
  margin-bottom: 20px;
}

.message-right {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 5px;
  margin: 0 12px;
}

.message-content {
  max-width: 60%;
}

.message-bubble {
  background: white;
  padding: 12px;
  border-radius: 5px;
  position: relative;
  word-break: break-word;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.message-right .message-bubble {
  background: #95ec69;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.input-area {
  border-top: 1px solid #e0e0e0;
  padding: 20px;
  background: white;
  display: flex;
}

textarea {
  flex: 1;
  height: 80px;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  resize: none;
  font-size: 14px;
}

button {
  margin-left: 12px;
  padding: 8px 20px;
  background: #07c160;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: opacity 0.3s;
}

button:hover {
  opacity: 0.9;
}
</style>
