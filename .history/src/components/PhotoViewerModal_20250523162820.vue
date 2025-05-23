<template>
  <el-dialog
    v-model="visible"
    fullscreen
    :show-close="false"
    class="photo-viewer-dialog"
  >
    <div class="photo-viewer-container">
      <!-- 图片信息区 -->
      <div
        class="photo-info"
        style="
          display: flex;
          align-items: center;
          justify-content: space-between;
        "
      >
        <div>
          <h3>主题：{{ photo.fileName || "暂无主题" }}</h3>
          <p>标签：{{ photo.tag || "暂无标签" }}</p>
          <p>地点：{{ photo.location || "暂无地点" }}</p>
          <p>拍摄于：{{ photo.capturedAt || "暂无时间" }}</p>
        </div>
        <el-button type="success" @click="editDialogVisible = true"
          >信息编辑</el-button
        >
      </div>

      <!-- 编辑弹窗 -->
      <el-dialog v-model="editDialogVisible" title="编辑照片信息">
        <el-form :model="editForm" label-width="90px">
          <el-form-item label="主题">
            <el-input v-model="editForm.fileName"></el-input>
          </el-form-item>
          <el-form-item label="标签">
            <el-input v-model="editForm.tag"></el-input>
          </el-form-item>
          <el-form-item label="地点">
            <el-input v-model="editForm.location"></el-input>
          </el-form-item>
          <el-form-item label="收藏">
            <el-switch v-model="editForm.isFavorite"></el-switch>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit">保存</el-button>
        </template>
      </el-dialog>

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
  data() {
    return {
      editDialogVisible: false,
      editForm: {
        fileName: "",
        tag: "",
        location: "",
        isFavorite: false,
      },
    };
  },
  watch: {
    photo: {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.editForm = {
            fileName: newVal.fileName || "",
            tag: newVal.tag || "",
            location: newVal.location || "",
            isFavorite: !!newVal.isFavorite,
          };
        }
      },
    },
  },
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
    submitEdit() {
      // 注意这里photo.id写死16，建议用photo.id更通用
      const photoId = this.photo.id || 16;
      t
        .put(`http://localhost:8080/photos/${photoId}`, this.editForm)
        .then(() => {
          this.$message.success("更新成功");
          this.editDialogVisible = false;
          // 通知父组件刷新
          this.$emit("edit", { ...this.photo, ...this.editForm });
        })
        .catch(() => {
          this.$message.error("更新失败");
        });
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
