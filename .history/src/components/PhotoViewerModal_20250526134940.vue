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
        <template v-if="!showFontStyleForm">
          <div style="text-align: center; padding: 20px">
            <el-button type="primary" @click="enableSubtitleEdit"
              >添加字幕</el-button
            >
          </div>
          <div
            v-if="subtitleEntries.length > 0"
            style="margin-top: 10px; padding: 0 20px"
          >
            <div
              v-for="entry in subtitleEntries"
              :key="entry.id"
              style="margin-bottom: 16px"
            >
              <span style="margin-right: 10px">#{{ entry.id }}</span>
              <el-input
                v-model="entry.text"
                placeholder="请输入字幕内容"
                style="width: 300px; margin-bottom: 8px"
              />
              <el-time-picker
                v-model="entry.timeRange"
                is-range
                format="HH:mm:ss"
                value-format="HH:mm:ss"
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                style="width: 300px"
                :disabled-hours="getDisabledTimeMethods().disabledHours"
                :disabled-minutes="getDisabledTimeMethods().disabledMinutes"
                :disabled-seconds="getDisabledTimeMethods().disabledSeconds"
                @change="
                  (val) => {
                    if (val && checkTimeOverlap(val, entry.id)) {
                      entry.timeRange = [];
                      $message.warning('该时间段与现有字幕重叠');
                    }
                  }
                "
              />
            </div>
          </div>
        </template>
        <template v-else>
          <el-form label-width="80px" style="padding: 0 20px">
            <el-form-item label="字体名称">
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
            <el-form-item label="字体大小">
              <el-input-number v-model="videoEditOptions.font_size" :min="1" />
            </el-form-item>
            <el-form-item label="字体颜色">
              <el-select
                v-model="videoEditOptions.font_color"
                placeholder="请选择颜色"
              >
                <el-option label="白色" value="white" />
                <el-option label="黑色" value="black" />
                <el-option label="红色" value="red" />
                <el-option label="黄色" value="yellow" />
                <el-option label="蓝色" value="blue" />
              </el-select>
            </el-form-item>
          </el-form>
        </template>
        <template #footer>
          <el-button @click="videoEditDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="
              showFontStyleForm ? finalizeSubtitleEdit() : submitVideoEdit()
            "
          >
            {{ showFontStyleForm ? "完成" : "下一步" }}
          </el-button>
        </template>
      </el-dialog>

      <el-dialog
        v-model="showVideo"
        title="字幕视频预览"
        width="60%"
        :before-close="() => (showVideo = false)"
        ref="videoDialog"
      >
        <video
          v-if="VideoByte"
          controls
          autoplay
          muted
          style="width: 100%; height: auto"
          :src="`data:video/mp4;base64,${VideoByte}`"
        ></video>
        <template #footer>
          <el-button @click="showVideo = false">关闭</el-button>
          <el-button type="primary" @click="downloadVideo">保存</el-button>
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
        <video
          v-else
          controls
          :src="photo.fileUrl"
          ref="videoPlayer"
          @loadedmetadata="handleLoadedMetadata"
          class="photo-full"
        />
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
          v-if="isSelf"
        >
          编辑
        </el-button>
        <el-button
          type="warning"
          @click="$emit('report', photo)"
          v-if="!isSelf"
        >
          举报
        </el-button>
        <el-popconfirm
          title="确认删除该图片？"
          confirm-button-text="删除"
          cancel-button-text="取消"
          @confirm="$emit('delete', photo)"
          v-if="isSelf"
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
import apiClient from "@/apiClient";
import qs from "qs";
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
      showSubtitleForm: false,
      videoEditOptions: {
        font_name: "Arial",
        font_size: 24,
        font_color: "#FFFFFF",
      },
      subtitleEntries: [],
      videoDuration: 0,
      finalSubtitleObject: "",
      showFontStyleForm: false,
      encodedVideoDialogVisible: false,
      VideoByte: "",
      showVideo: false,
      isSelf: loca
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
      this.showSubtitleForm = false;
      this.videoEditDialogVisible = true;
      this.showFontStyleForm = false;
    },
    enableSubtitleEdit() {
      // 先验证当前已有时间段是否完整
      for (const entry of this.subtitleEntries) {
        if (entry.timeRange.length !== 2) {
          this.$message.warning(`请先完善第${entry.id}条字幕的时间段`);
          return;
        }
      }
      const newId = this.subtitleEntries.length + 1;
      this.subtitleEntries.push({
        id: newId,
        text: "",
        timeRange: [],
      });
    },
    submitVideoEdit() {
      const result = {};
      for (const entry of this.subtitleEntries) {
        if (entry.timeRange.length === 2 && entry.text.trim()) {
          const key = `${entry.timeRange[0]}-${entry.timeRange[1]}`;
          result[key] = entry.text.trim();
        }
      }
      this.finalSubtitleObject = JSON.stringify(result);
      this.showFontStyleForm = true;

      // 控制台打印所有内容
      console.log("字幕内容:", this.finalSubtitleObject);
      console.log("样式设置:", this.videoEditOptions);
    },
    async finalizeSubtitleEdit() {
      try {
        const payload = {
          tag: this.finalSubtitleObject,
          font_name: this.videoEditOptions.font_name,
          font_size: this.videoEditOptions.font_size,
          font_color: this.videoEditOptions.font_color,
          photoId: this.photo.photoId,
        };
        console.log("提交的字幕数据:", qs.stringify(payload));
        const res = await apiClient.post(
          "/video/add_caption",
          qs.stringify(payload),
          {
            headers: {
              "Content-Type": "application/x-www-form-urlencoded",
            },
          }
        );
        this.showVideo = true;
        this.$nextTick(() => {
          const dialogEl = this.$refs.videoDialog?.$el;
          if (dialogEl && dialogEl.scrollIntoView) {
            dialogEl.scrollIntoView({ behavior: "smooth" });
          }
        });
        this.VideoByte = res.data.data;
        this.$message.success("字幕已提交并处理成功");
        this.videoEditDialogVisible = false;
        this.subtitleEntries = [];
      } catch (err) {
        console.error("字幕提交失败", err);
        this.$message.error("提交失败，请稍后重试");
      }
    },
    downloadVideo() {
      if (!this.VideoByte) return;
      const byteCharacters = atob(this.VideoByte);
      const byteNumbers = new Array(byteCharacters.length);
      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
      const byteArray = new Uint8Array(byteNumbers);
      const blob = new Blob([byteArray], { type: "video/mp4" });
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = "subtitle_video.mp4";
      a.click();
      URL.revokeObjectURL(url);
      this.showVideo = false;
    },
    handleLoadedMetadata() {
      const video = this.$refs.videoPlayer;
      if (video) {
        this.videoDuration = video.duration;
        console.log("视频时长（秒）:", this.videoDuration);
      }
    },
    getDisabledTimeMethods() {
      const maxSeconds = Math.floor(this.videoDuration);
      const maxH = Math.floor(maxSeconds / 3600);
      const maxM = Math.floor((maxSeconds % 3600) / 60);
      const maxS = maxSeconds % 60;

      return {
        disabledHours: () =>
          Array.from({ length: 24 }, (_, i) => (i > maxH ? i : null)).filter(
            (i) => i !== null
          ),
        disabledMinutes: (hour) => {
          if (hour < maxH) return [];
          return Array.from({ length: 60 }, (_, i) =>
            i > maxM ? i : null
          ).filter((i) => i !== null);
        },
        disabledSeconds: (hour, minute) => {
          if (hour < maxH || minute < maxM) return [];
          return Array.from({ length: 60 }, (_, i) =>
            i > maxS ? i : null
          ).filter((i) => i !== null);
        },
      };
    },
    checkTimeOverlap(newRange, currentId) {
      const [newStart, newEnd] = newRange.map((t) =>
        this.timeStringToSeconds(t)
      );
      for (const entry of this.subtitleEntries) {
        if (entry.id === currentId || entry.timeRange.length !== 2) continue;
        const [existStart, existEnd] = entry.timeRange.map((t) =>
          this.timeStringToSeconds(t)
        );
        const isOverlap = newStart < existEnd && existStart < newEnd;
        if (isOverlap) return true;
      }
      return false;
    },
    timeStringToSeconds(time) {
      const [h, m, s] = time.split(":").map(Number);
      return h * 3600 + m * 60 + s;
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
