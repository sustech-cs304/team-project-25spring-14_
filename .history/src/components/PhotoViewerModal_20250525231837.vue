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
          <p>拍摄于：{{ formatDate(photo.capturedAt) || "暂无时间" }}</p>
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

      <el-dialog
        v-model="videoEditDialogVisible"
        title="视频编辑设置"
        width="400px"
      >
        <el-form :model="videoEditOptions" label-width="80px">
          <el-form-item label="字体">
            <el-select
              v-model="videoEditOptions.font_name"
              placeholder="请选择字体"
            >
              <el-option label="Arial" value="Arial" />
              <el-option label="Helvetica" value="Helvetica" />
              <el-option label="Times New Roman" value="Times New Roman" />
              <el-option label="Courier New" value="Courier New" />
              <el-option label="Verdana" value="Verdana" />
            </el-select>
          </el-form-item>
          <el-form-item label="字号">
            <el-input
              v-model="videoEditOptions.font_size"
              type="number"
            ></el-input>
          </el-form-item>
          <el-form-item label="字体颜色">
            <el-color-picker v-model="videoEditOptions.font_color" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="videoEditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitVideoEdit">确认</el-button>
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
          type="primary"
          @click="
            isVideo(photo.fileUrl)
              ? openVideoEditDialog()
              : $emit('edit', photo)
          "
        >
          编辑
        </el-button>
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
      videoEditDialogVisible: false,
      videoEditOptions: {
        font_name: "Arial",
        font_size: 24,
        font_color: "#FFFFFF",
      },
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
    formatDate(dateTime) {
      // dateTime 可能为 null/undefined，先做保护
      if (!dateTime) return "暂无时间";
      // 按 “T” 分隔，取前半部分
      return dateTime.split("T")[0];
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
    openVideoEditDialog() {
      this.videoEditDialogVisible = true;
    },
    submitVideoEdit() {
      // 可扩展
      this.videoEditDialogVisible = false;
      this.$message.success("已确认视频编辑配置");
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
