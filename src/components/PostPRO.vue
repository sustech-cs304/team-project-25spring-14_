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

        <!-- 图片选择 -->
        <div class="upload-section">
          <div class="upload-area" @click="showPhotoSelector = true">
            <span class="icon">+</span>
            <p>选择图片</p>
          </div>
          <!-- 图片预览 -->
          <div class="preview-container" v-if="selectedPhotos.length > 0">
            <div
              v-for="(photo, index) in selectedPhotos"
              :key="photo.photoId"
              class="preview-item"
            >
              <img :src="photo.thumbnailUrl" class="preview-image" />
              <button class="remove-btn" @click="removePhoto(index)">×</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部操作 -->
      <div class="modal-footer">
        <button class="post-btn" @click="confirm">发布</button>
      </div>
    </div>

    <!-- 照片选择模态框 -->
    <PhotoSelectorModal
      v-if="showPhotoSelector"
      :selected="selectedPhotos"
      @close="showPhotoSelector = false"
      @confirm="handlePhotoSelect"
    />
  </div>
</template>

<script>
import PhotoSelectorModal from "@/components/PhotoSelectorModal.vue";

export default {
  components: {
    PhotoSelectorModal,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      caption: "",
      selectedPhotos: [],
      location: "",
      privacy: "PRIVATE",
      showLocation: false,
      showPhotoSelector: false,
    };
  },
  methods: {
    close() {
      this.resetForm();
      this.$emit("close");
    },
    handlePhotoSelect(selectedPhotos) {
      this.selectedPhotos = selectedPhotos;
    },
    removePhoto(index) {
      this.selectedPhotos.splice(index, 1);
    },
    confirm() {
      if (!this.selectedPhotos.length) {
        alert("请至少选择一张图片");
        return;
      }

      const payload = {
        photoIds: this.selectedPhotos.map((p) => p.photoId),
        caption: this.caption,
        privacy: this.privacy,
      };

      if (this.location) {
        payload.location = this.location;
      }

      this.$emit("confirm", payload);
      this.resetForm();
    },
    resetForm() {
      this.caption = "";
      this.selectedPhotos = [];
      this.location = "";
      this.privacy = "PRIVATE";
    },
  },
};
</script>

<style scoped>
/* 保持原有样式不变 */
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
