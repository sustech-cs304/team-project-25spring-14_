<template>
  <div class="main-container">
    <el-dialog
      v-model="showVideo"
      title="回忆视频"
      width="60%"
      :before-close="() => (showVideo = false)"
      ref="videoDialog"
    >
      <video
        v-if="VideoByte"
        controls
        autoplay
        style="width: 100%; height: auto"
        :src="`data:video/mp4;base64,${VideoByte}`"
      ></video>
      <template #footer>
        <el-button @click="showVideo = false">关闭</el-button>
        <el-button type="primary" @click="downloadVideo">保存</el-button>
      </template>
    </el-dialog>
    <SideBar />
    <div class="content-wrapper">
      <div class="album-header">
        <div class="album-info-wrapper">
          <h1 class="album-title">我的照片</h1>
          <p class="photo-count">共{{ filterPhotos.length }}张照片</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="multiSelectMode = !multiSelectMode">
            {{ multiSelectMode ? "取消多选" : "多选" }}
          </el-button>
          <el-button type="primary" @click="filterDialogVisible = true"
            >筛选</el-button
          >
          <el-button
            v-if="multiSelectMode"
            size="small"
            type="success"
            @click="showSelectedDialog = true"
            >确定</el-button
          >
          <el-button
            v-if="multiSelectMode"
            size="small"
            type="info"
            @click="selectAllPhotos"
            >全选</el-button
          >
        </div>
      </div>
      <div v-if="filterDialogVisible" class="filter-form">
        <el-form :model="filterCriteria" label-width="80px" inline>
          <el-form-item label="开始日期">
            <el-date-picker
              v-model="filterCriteria.startDate"
              type="date"
              placeholder="开始日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item label="结束日期">
            <el-date-picker
              v-model="filterCriteria.endDate"
              type="date"
              placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item label="标签">
            <el-input v-model="filterCriteria.tag" placeholder="标签" />
          </el-form-item>
          <el-form-item label="地点">
            <el-input v-model="filterCriteria.location" placeholder="地点" />
          </el-form-item>
          <el-form-item label="收藏">
            <el-switch
              v-model="filterCriteria.isFavorite"
              :active-value="true"
              :inactive-value="null"
            />
          </el-form-item>
          <el-form-item>
            <el-button size="small" @click="cancerFilter">取消</el-button>
            <el-button size="small" type="primary" @click="applyFilter"
              >应用筛选</el-button
            >
          </el-form-item>
        </el-form>
      </div>
      <div class="photo-grid">
        <div
          class="photo-card"
          v-for="photo in filterPhotos"
          :key="photo.photoId"
          @click="
            multiSelectMode
              ? togglePhotoSelection(photo)
              : openPhotoViewer(photo)
          "
          :class="{
            selected: multiSelectMode && selectedPhotos.includes(photo),
          }"
        >
          <template v-if="multiSelectMode">
            <el-checkbox
              :model-value="selectedPhotos.includes(photo)"
              @change="togglePhotoSelection(photo)"
              @click.stop
              style="position: absolute; top: 8px; left: 8px; z-index: 2"
            />
          </template>
          <img
            v-if="!isVideo(photo.fileUrl)"
            :src="photo.thumbnailUrl"
            alt="photo"
            class="photo-image"
          />
          <video v-else :src="photo.fileUrl" controls class="photo-image" />
        </div>
      </div>
    </div>
  </div>
  <PhotoViewerModal
    v-model="viewerVisible"
    :photo="selectedPhoto"
    @edit="editPhoto"
    @delete="deletePhoto"
    @report="handlePhotoReport"
    @editInfo="handlePhotoInfoUpdate"
  />
  <ImageEditorModal
    v-model="editorVisible"
    :file-url="editingPhotoUrl"
    @save="handlePhotoSave"
  />
  <el-dialog
    v-model="showSelectedDialog"
    title="已选照片"
    width="60%"
    @close="showSelectedDialog = false"
  >
    <div class="selected-photos-grid">
      <div
        class="photo-card"
        v-for="photo in selectedPhotos"
        :key="photo.photoId"
        style="cursor: default"
      >
        <img
          v-if="!isVideo(photo.fileUrl)"
          :src="photo.thumbnailUrl"
          alt="selected"
          class="photo-image"
        />
        <video v-else :src="photo.fileUrl" controls class="photo-image" />
      </div>
    </div>
    <template #footer>
      <el-button @click="showSelectedDialog = false">关闭</el-button>
      <el-button type="primary" @click="generateMemory">生成回忆</el-button>
      <el-dialog
        v-model="memoryDialogVisible"
        title="生成回忆设置"
        width="400px"
      >
        <el-form label-width="100px">
          <el-form-item label="转场效果">
            <el-select
              v-model="memoryOptions.transition"
              placeholder="选择转场"
            >
              <el-option label="淡入淡出" value="fade" />
              <el-option label="缩放" value="zoom" />
              <el-option label="滑动" value="slide" />
            </el-select>
          </el-form-item>

          <el-form-item label="帧率">
            <el-select v-model="memoryOptions.fps">
              <el-option label="25" :value="25" />
            </el-select>
          </el-form-item>

          <el-form-item label="音频文件">
            <input type="file" @change="handleAudioChange" accept="audio/*" />
          </el-form-item>
        </el-form>

        <template #footer>
          <el-button @click="memoryDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitMemoryRequest"
            >生成</el-button
          >
        </template>
      </el-dialog>
    </template>
  </el-dialog>
  <el-dialog v-model="photoReportDialogVisible" title="举报照片" width="400px">
    <el-input
      type="textarea"
      v-model="photoReportReason"
      placeholder="请输入举报理由"
      rows="4"
    />
    <template #footer>
      <el-button @click="photoReportDialogVisible = false">取消</el-button>
      <el-button type="danger" @click="submitPhotoReport">提交举报</el-button>
    </template>
  </el-dialog>
  <el-dialog v-model="reportDialogVisible" title="举报相册" width="400px">
    <el-input
      type="textarea"
      v-model="reportReason"
      placeholder="请输入举报理由"
      rows="4"
    />
    <template #footer>
      <el-button @click="reportDialogVisible = false">取消</el-button>
      <el-button type="danger" @click="submitAlbumReport">提交举报</el-button>
    </template>
  </el-dialog>
</template>

<script>
import SideBar from "@/components/SideBar.vue";
import apiClient from "@/apiClient";
import { ElButton } from "element-plus";
import PhotoViewerModal from "@/components/PhotoViewerModal.vue";
import ImageEditorModal from "@/components/ImageEditorModal.vue";

export default {
  components: {
    SideBar,
    ElButton,
    PhotoViewerModal,
    ImageEditorModal,
  },
  data() {
    return {
      isSelf: this.$route.query.isSelf === "true",
      photos: [],
      count: 0,
      userId: localStorage.getItem("userId"),
      editorVisible: false,
      editingPhoto: null,
      editingPhotoUrl: "",
      deleteConfirmVisible: false,
      viewerVisible: false,
      selectedPhoto: null,
      reportDialogVisible: false,
      reportReason: "",
      photoReportDialogVisible: false,
      photoReportReason: "",
      // 筛选对话框可见性
      filterDialogVisible: false,
      // 筛选条件模型
      filterCriteria: {
        startDate: "",
        endDate: "",
        tag: "",
        location: "",
        isFavorite: null,
      },
      // 多选模式开关
      multiSelectMode: false,
      // 存放选中的照片
      selectedPhotos: [],
      // 控制已选照片对话框
      showSelectedDialog: false,
      memoryDialogVisible: false,
      memoryOptions: {
        transition: "fade",
        fps: 25,
        audioFile: null,
      },
      VideoByte: null,
      showVideo: false,
    };
  },
  async created() {
    try {
      const photoRes = await apiClient.get(`/photos/my`);
      this.photos = photoRes.data.data.photos;
      this.count = photoRes.data.data.count;
    } catch (error) {
      console.error("获取照片详情失败：", error);
      alert("加载照片详情失败，请稍后重试！");
      this.$router.push("/albums");
    }
  },
  methods: {
    // ...其他方法...

    async uploadVideo() {
      if (!this.VideoByte) {
        this.$message.error("没有可下载的视频");
        return;
      }
      try {
        // 将base64转为Blob
        const byteString = atob(this.VideoByte);
        const ab = new ArrayBuffer(byteString.length);
        const ia = new Uint8Array(ab);
        for (let i = 0; i < byteString.length; i++) {
          ia[i] = byteString.charCodeAt(i);
        }
        const blob = new Blob([ab], { type: "video/mp4" });

        // 创建下载链接并自动下载
        const url = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = "memory.mp4";
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);

        this.$message.success("视频已保存到本地");
      } catch (error) {
        console.error("视频保存失败：", error);
        this.$message.error("视频保存失败");
      }
    },
  },
  computed: {
    filterPhotos() {
      const { startDate, endDate, tag, location, isFavorite } =
        this.filterCriteria;
      return this.photos.filter((photo) => {
        // 取照片的日期部分（去掉T之后的时间）
        const date = photo.capturedAt ? photo.capturedAt.split("T")[0] : "";
        const photoTag = photo.tag === null ? "" : photo.tag;
        const photoLocation = photo.location === null ? "" : photo.location;
        if (startDate && date < startDate) return false;
        if (endDate && date > endDate) return false;
        if (
          tag &&
          !(photoTag || "").toLowerCase().includes(tag.trim().toLowerCase())
        )
          return false;
        if (
          location &&
          !(photoLocation || "")
            .toLowerCase()
            .includes(location.trim().toLowerCase())
        )
          return false;
        if (isFavorite !== null && photo.isFavorite !== isFavorite)
          return false;
        return true;
      });
    },
  },
  watch: {
    multiSelectMode(newVal) {
      if (!newVal) {
        this.selectedPhotos = [];
      }
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
.main-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
  width: 100vw;
}

.content-wrapper {
  flex: 1;
  margin: 10px;
  padding: 20px;
  overflow-y: auto;
  background-color: #f3f4f6;
  border-radius: 32px;
  box-shadow: inset 0 4px 6px rgba(0, 0, 0, 0.1),
    inset 0 -4px 6px rgba(255, 255, 255, 0.7);
}

.album-header {
  margin-bottom: 24px;
  border-bottom: 1px solid #d1d5db;
  padding-bottom: 12px;
  margin-top: 20px;
  padding-left: 20px;
  padding-right: 20px;
  display: flex;
  align-items: flex-start;
}

.album-cover-wrapper {
  margin-right: 20px;
}

.album-cover {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 12px;
  background-color: #e5e7eb;
}

.album-cover.default {
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px dashed #d1d5db;
  background-color: #e5e7eb;
  color: #9ca3af;
  font-size: 14px;
  transition: transform 0.3s;
  cursor: pointer;
  position: relative;
}

.album-cover.default:hover {
  transform: scale(1.1);
}

.add-cover-text {
  position: absolute;
  opacity: 0;
  transition: opacity 0.3s;
  color: black;
  font-weight: bold;
}

.album-cover.default:hover .add-cover-text {
  opacity: 1;
}
.album-cover.default::before {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background-image: url("@/assets/images/albumlogo.svg");
  background-repeat: no-repeat;
  background-position: center;
  background-size: 40%;
  opacity: 0.3;
  pointer-events: none;
  transition: opacity 0.3s;
}

.album-cover.default:hover::before {
  opacity: 0.1;
}

.album-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.album-info-wrapper {
  flex: 1;
}

.album-title {
  font-size: 24px;
  font-weight: bold;
}

.album-description {
  color: #6b7280;
  margin-top: 4px;
}

.album-description.quoted::before,
.album-description.quoted::after {
  content: '"';
  color: #6b7280;
  user-select: none;
  pointer-events: none;
  font-weight: normal;
}

.photo-count {
  font-size: 14px;
  color: #9ca3af;
  margin-top: 4px;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 10px;
  margin-top: 20px;
}

.photo-card {
  background: #f3f4f6;
  overflow: hidden;
  aspect-ratio: 1 / 1;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s;
}

.photo-card:hover {
  transform: scale(1.05);
  transition: transform 0.3s;
}

.photo-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  display: block;
}

.privacy-select {
  display: flex;
  align-items: center;
}

.privacy-select .el-select {
  width: 32px;
}

.privacy-select .el-select .el-input__inner {
  padding: 0;
  text-indent: -9999px;
}

.privacy-icon {
  width: 24px;
  height: 24px;
  vertical-align: middle;
}

.add-cover-text {
  text-align: center;
  white-space: nowrap;
}

.album-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  justify-content: center;
}

.album-actions .el-button {
  width: 120px;
}

.editable-input {
  max-width: 300px;
  margin-top: 4px;
}

.editable[contenteditable="true"] {
  outline: none;
  border-bottom: 1px dashed transparent;
}

.editable[contenteditable="true"]:focus {
  border-bottom: 1px dashed #9ca3af;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.5); /* 蓝色外圈 */
  border-radius: 4px;
  transition: box-shadow 0.3s ease;
}

.privacy-dropdown {
  position: absolute;
  z-index: 10;
}

.add-photo-card {
  border: 2px dashed #d1d5db;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.add-photo-card:hover {
  transform: scale(1.05);
}

.add-icon {
  font-size: 36px;
  color: #9ca3af;
}

.header-actions {
  margin-left: auto;
  padding: 10px;
}
.album-header {
  display: flex;
  align-items: center;
}

.filter-form {
  padding: 10px 0;
}
/* Highlight selected photo cards in multi-select mode */
.photo-card.selected {
  border: 2px solid #409eff; /* Blue border for selection */
}

.selected-photos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 10px;
  max-height: 60vh;
  overflow-y: auto;
}
</style>
