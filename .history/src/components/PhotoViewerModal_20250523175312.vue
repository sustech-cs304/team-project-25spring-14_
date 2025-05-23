<template>
  <el-dialog
    v-model="visible"
    fullscreen
    :show-close="false"
    class="photo-viewer-dialog"
  >
    <div class="photo-viewer-container">
      <!-- 图片信息区 -->
      <div class="photo-info">
        <div>
          <h3>主题：{{ photo.fileName || "暂无主题" }}</h3>
          <p>标签：{{ photo.tag || "暂无标签" }}</p>
          <p>地点：{{ photo.location || "暂无地点" }}</p>
          <p>拍摄于：{{ photo.capturedAt || "暂无时间" }}</p>
          <p v-if="photo.isFavorite">已收藏</p>
        </div>
        <el-button type="success" @click="editInfoVisible = true"
          >修改信息</el-button
        >
      </div>

      <el-dialog v-model="editInfoVisible" title="修改照片信息">
        <el-form :model="editablePhoto" label-width="80px">
          <el-form-item label="主题">
            <el-input v-model="editablePhoto.fileName"></el-input>
          </el-form-item>
          <el-form-item label="标签">
            <el-input v-model="editablePhoto.tag"></el-input>
          </el-form-item>
          <el-form-item label="地点">
            <el-input v-model="editablePhoto.location"></el-input>
          </el-form-item>
          <el-form-item label="收藏">
            <el-switch v-model="editablePhoto.isFavorite"></el-switch>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="editInfoVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdits">保存</el-button>
        </template>
      </el-dialog>

      <!-- 图片显示区 -->
      <div class="photo-display">
        <img
          v-if="!isVideo(photo.fileUrl)"
          :src="photo.fileUrl"
          alt="full"
          class="photo-full"
        />
        <video v-else controls :src="photo.fileUrl" class="photo-full" />
      </div>

      <!-- 操作区 -->
      <div class="photo-actions">
        <el-button 
        v-if
        type="primary" @click="$emit('edit', photo)">编辑</el-button>
        <el-button type="warning" @click="$emit('report', photo)"
          >举报</el-button
        >
        <el-popconfirm
          title="确认删除该图片？"
          confirm-button-text="删除"
          cancel-button-text="取消"
          @confirm="$emit('delete', photo)"
        >
          <template #reference>
            <el-button type="danger">删除</el-button>
          </template>
        </el-popconfirm>
        <el-button @click="close">关闭</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: "PhotoViewerModal",
  props: {
    photo: {
      type: Object,
      required: true,
    },
    modelValue: {
      type: Boolean,
      required: true,
    },
  },
  emits: ["update:modelValue", "edit", "delete", "report", "editInfo"],
  data() {
    return {
      editInfoVisible: false,
      editablePhoto: { ...this.photo },
    };
  },
  watch: {
    photo: {
      handler(newVal) {
        this.editablePhoto = { ...newVal };
      },
      immediate: true,
      deep: true,
    },
  },
  computed: {
    visible: {
      get() {
        return this.modelValue;
      },
      set(val) {
        this.$emit("update:modelValue", val);
      },
    },
  },
  methods: {
    formatDate(dateStr) {
      if (!dateStr) return "未知时间";
      const date = new Date(dateStr);
      return date.toLocaleString();
    },
    close() {
      this.visible = false;
    },
    submitEdits() {
      this.$emit("editInfo", { ...this.editablePhoto });
      this.editInfoVisible = false;
    },
    isVideo(url) {
      return /\.(mp4|webm|ogg|mov|avi|mkv|mpeg|wmv)$/i.test(url);
    },
  },
};
</script>

<style scoped>
.photo-viewer-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.photo-info {
  padding: 16px;
  border-bottom: 1px solid #ddd;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.photo-display {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f9f9f9;
}

.photo-full {
  max-width: 100%;
  max-height: 80vh;
  object-fit: contain;
}

.photo-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 12px;
  border-top: 1px solid #ddd;
  background-color: #fff;
}
</style>
