<template>
  <div class="post-modal-mask" v-show="visible">
    <div class="post-modal-container">
      <!-- 头部 -->
      <div class="modal-header">
        <h3>发布新内容</h3>
        <button class="close-btn" @click="close">×</button>
      </div>

      <!-- 内容区域 -->
      <div class="modal-content">
        <!-- 文本输入 -->
        <textarea
          v-model="caption"
          placeholder="分享你的想法..."
          class="post-textarea"
        ></textarea>

        <!-- 图片上传 -->
        <div class="upload-section">
          <input
            type="file"
            accept="image/*"
            multiple
            @change="handleFileUpload"
            ref="fileInput"
            class="file-input"
          />
          <div class="upload-area" @click="triggerFileInput">
            <span class="icon">+</span>
            <p>添加图片</p>
          </div>
          <!-- 图片预览 -->
          <div class="preview-container" v-if="images.length > 0">
            <div
              v-for="(img, index) in images"
              :key="index"
              class="preview-item"
            >
              <img :src="img.preview" class="preview-image" />
              <button class="remove-btn" @click="removeImage(index)">×</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部操作 -->
      <div class="modal-footer">
        <button class="post-btn" @click="confirm" :disabled="isProcessing">
          {{ isProcessing ? "正在处理图片..." : "发布" }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      visible: true,
      caption: "",
      photos: null,
      images: [],
      location: "",
      privacy: "PUBLIC",
      showLocation: false,
      showPrivacy: false,
      isProcessing: false,
      // 新增待处理队列
      pendingFiles: [],
    };
  },

  methods: {
    close() {
      this.resetForm();
      this.$emit("close");
    },
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    /**
     * AI-generated-content
     * tool: DeepSeek
     * version: latest
     * usage: I asked the ai to help me optimize the upload method
     */
    async handleFileUpload(e) {
      this.isProcessing = true;
      this.pendingFiles = [...e.target.files]; // 保存原始文件引用

      try {
        // 使用新数组替换保证响应式更新
        const newImages = await Promise.all(
          Array.from(e.target.files).map(
            (file) =>
              new Promise((resolve) => {
                const reader = new FileReader();
                reader.onload = (e) =>
                  resolve({
                    file, // 保留原始文件对象
                    preview: e.target.result,
                  });
                reader.readAsDataURL(file);
              })
          )
        );

        // 使用数组替换方式更新
        this.images = newImages.filter(Boolean);
      } catch (error) {
        console.error("文件处理失败:", error);
      } finally {
        this.isProcessing = false;
      }
    },

    // 修改删除逻辑
    removeImage(index) {
      this.images.splice(index, 1);
    },

    // 修改确认逻辑
    confirm() {
      // 双重验证机制
      if (this.isProcessing) {
        alert("图片仍在处理中，请稍候");
        return;
      }

      const validFiles = this.pendingFiles.filter((file) =>
        file.type.startsWith("image/")
      );

      if (validFiles.length === 0) {
        alert("请选择至少一张有效图片");
        return;
      }

      this.$emit("confirm", {
        photos: validFiles.map((f) => f),
        caption: this.caption,
        privacy: this.privacy,
      });
    },

    resetForm() {
      this.caption = "";
      this.images = [];
      this.location = "";
    },
  },
};
</script>

<style scoped>
/** 
     * AI-generated-content 
     * tool: DeepSeek 
     * version: latest
     * usage: I let the ai help me beautify the look of the interface
     */
.preview-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 8px;
  margin-top: 12px;
}

.preview-item {
  position: relative;
  aspect-ratio: 1/1;
  height: 120px;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #eee;
}

.post-modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.post-modal-container {
  background: white;
  width: 90%;
  max-width: 600px;
  border-radius: 12px;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.close-btn {
  font-size: 24px;
  background: none;
  border: none;
  cursor: pointer;
}

.modal-content {
  padding: 16px;
}

.post-textarea {
  width: 100%;
  height: 120px;
  border: none;
  resize: none;
  font-size: 16px;
  margin-bottom: 16px;
}

.upload-section {
  margin-bottom: 16px;
}

.file-input {
  display: none;
}

.upload-area {
  border: 2px dashed #ddd;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  border-radius: 8px;
  transition: border-color 0.3s;
}

.upload-area:hover {
  border-color: #409eff;
}

.remove-btn {
  position: absolute;
  top: -8px;
  right: -8px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  cursor: pointer;
}

.additional-options {
  border-top: 1px solid #eee;
  padding-top: 12px;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 8px;
  cursor: pointer;
  color: #666;
}

.option-item:hover {
  background: #f5f5f5;
}

.modal-footer {
  padding: 16px;
  text-align: right;
}

.post-btn {
  background: #409eff;
  color: white;
  border: none;
  padding: 8px 24px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
}

.post-btn:hover {
  background: #66b1ff;
}
</style>
