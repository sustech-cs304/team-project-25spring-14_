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
            <div class="preview-item">
              <img :src="images[0].preview" class="preview-image" />
              <button class="remove-btn" @click="removeImage">×</button>
            </div>
          </div>
        </div>

        <!-- 附加选项 -->
        <div class="additional-options">
          <div class="option-item" @click="showLocation = true">
            <i class="icon-location"></i>
            <span>{{ location || "添加位置" }}</span>
          </div>
          <div>
            <select v-model="privacy" class="select">
              <option value="PRIVATE">私密</option>
              <option value="PUBLIC">公开</option>
              <option value="SHARED">好友可见</option>
            </select>
          </div>
        </div>
      </div>

      <!-- 底部操作 -->
      <div class="modal-footer">
        <button class="post-btn" @click="confirm">发布</button>
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
      photo: null,
      images: [],
      location: "",
      privacy: "PRIVATE",
      showLocation: false,
      showPrivacy: false,
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
    handleFileUpload(e) {
      const file = e.target.files[0]; // 只取第一个文件
      if (!file) return;

      const reader = new FileReader();
      reader.onload = (e) => {
        this.images = [
          {
            // 直接替换数组，保持只有一张
            file,
            preview: e.target.result,
          },
        ];
        this.photo = file;
      };
      reader.readAsDataURL(file);
    },
    removeImage() {
      this.images = [];
      this.photo = null;
    },
    confirm() {
      if (!this.photo) {
        alert("请选择一张图片");
        return;
      }
      this.$emit("confirm", {
        photo: this.photo,
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
/* 原有样式保持不变，只修改预览部分 */
.preview-container {
  margin-top: 12px;
}

.preview-item {
  position: relative;
  max-width: 100%;
  height: 200px;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 4px;
  border: 1px solid #eee;
}

/* 其他样式保持不变 */
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
