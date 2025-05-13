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
        <h3>标签：{{ photo.tag || "暂无标签" }}</h3>
        <p>地点：{{ photo.location || "暂无地点" }}</p>
        <p>拍摄于：{{ photo.tag || "暂无标签    " }}</p>
        <p>拍摄时间：{{ formatDate(photo.capturedAt) }}</p>
      </div>

      <!-- 图片显示区 -->
      <div class="photo-display">
        <img :src="photo.fileUrl" alt="full" class="photo-full" />
      </div>

      <!-- 操作区 -->
      <div class="photo-actions">
        <el-button type="primary" @click="$emit('edit', photo)">编辑</el-button>
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
  emits: ["update:modelValue", "edit", "delete", "report"],
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
