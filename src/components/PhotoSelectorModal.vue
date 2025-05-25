<template>
  <div class="photo-selector-mask">
    <div class="photo-selector-container">
      <div class="modal-header">
        <h3>选择照片（共 {{ count }} 张）</h3>
        <button class="close-btn" @click="close">×</button>
      </div>

      <div v-if="loading" class="loading-state">
        <i class="el-icon-loading"></i>
        正在加载您的照片库...
      </div>

      <template v-else>
        <div class="photo-grid">
          <div
            v-for="photo in photos"
            :key="photo.photoId"
            class="photo-item"
            :class="{ selected: isSelected(photo) }"
            @click="toggleSelect(photo)"
          >
            <img :src="photo.thumbnailUrl" alt="用户照片" />
            <div class="selection-marker">
              <i class="el-icon-check"></i>
            </div>
          </div>
        </div>

        <div class="pagination" v-if="count > photos.length">
          <!-- 可分页加载实现 -->
        </div>
      </template>

      <div class="modal-footer">
        <el-button @click="close">取消</el-button>
        <el-button
          type="primary"
          @click="confirmSelection"
          :disabled="selected.length === 0"
        >
          确认选择 ({{ selected.length }})
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from "@/apiClient";

export default {
  data() {
    return {
      loading: true,
      photos: [],
      selected: [],
      count: 0,
    };
  },
  async created() {
    await this.loadPhotos();
  },
  methods: {
    async loadPhotos() {
      try {
        const photoRes = await apiClient.get("/photos/my");
        if (photoRes.data.code === 0) {
          this.photos = photoRes.data.data.photos;
          this.count = photoRes.data.data.count;
        }
      } catch (error) {
        console.error("获取照片失败:", error);
        this.$message.error("照片加载失败，请检查网络连接");
        this.$emit("close"); // 关闭模态框而不是跳转
      } finally {
        this.loading = false;
      }
    },
    isSelected(photo) {
      return this.selected.some((p) => p.photoId === photo.photoId);
    },
    toggleSelect(photo) {
      const index = this.selected.findIndex((p) => p.photoId === photo.photoId);
      if (index > -1) {
        this.selected.splice(index, 1);
      } else {
        this.selected.push(photo);
      }
    },
    confirmSelection() {
      this.$emit("confirm", this.selected);
      this.close();
    },
    close() {
      this.$emit("close");
    },
  },
};
</script>

<style scoped>
/* 优化后的样式 */
.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
  padding: 12px;
  max-height: 60vh;
  overflow-y: auto;
}

.photo-item {
  position: relative;
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.2s;
  aspect-ratio: 1;
}

.photo-item:hover {
  transform: scale(1.03);
}

.photo-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border: 2px solid transparent;
}

.photo-item.selected img {
  border-color: #409eff;
}

.selection-marker {
  position: absolute;
  top: 5px;
  right: 5px;
  background: #409eff;
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-state {
  padding: 40px;
  text-align: center;
  color: #666;
}

.el-icon-loading {
  font-size: 24px;
  margin-bottom: 8px;
}
</style>
