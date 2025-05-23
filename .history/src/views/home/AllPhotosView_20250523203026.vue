<template>
  <div class="main-container">
    <SideBar />
    <div class="content-wrapper">
      <div class="album-header">
        <div class="album-info-wrapper">
          <h class="album-title">我的照片</h>
          <p class="photo-count">共{{ this.count }}张照片</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="filterDialogVisible = true"
            >筛选</el-button
          >
        </div>
      </div>
      <div class="photo-grid">
        <div
          class="photo-card"
          v-for="photo in photos"
          :key="photo.photoId"
          @click="openPhotoViewer(photo)"
        >
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
  <el-dialog
    v-model="filterDialogVisible"
    title="筛选照片"
    width="500px"
    @close="filterDialogVisible = false"
  >
    <el-form :model="filterCriteria" label-width="80px">
      <el-form-item label="开始日期">
        <el-date-picker
          v-model="filterCriteria.startDate"
          type="date"
          placeholder="选择开始日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item label="结束日期">
        <el-date-picker
          v-model="filterCriteria.endDate"
          type="date"
          placeholder="选择结束日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item label="标签">
        <el-input v-model="filterCriteria.tag" placeholder="输入标签" />
      </el-form-item>
      <el-form-item label="地点">
        <el-input v-model="filterCriteria.location" placeholder="输入地点" />
      </el-form-item>
      <el-form-item label="收藏">
        <el-switch
          v-model="filterCriteria.isFavorite"
          :active-value="true"
          :inactive-value="null"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="filterDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="applyFilter">应用筛选</el-button>
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
      filterPhotos: [],
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
    async handlePhotoChange(event) {
      const files = event.target.files;
      if (!files.length) return;

      try {
        for (const file of files) {
          const formData = new FormData();
          formData.append("file", file);
          formData.append("albumId", this.album.albumId);
          formData.append("userId", this.userId);

          const response = await apiClient.post("/photos/upload", formData, {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          });

          const newPhoto = response.data.data.photo;
          this.album.photos.push(newPhoto);
          this.album.photoCount++;
        }

        this.$message.success("所有照片上传成功");
      } catch (error) {
        console.error("上传失败：", error);
        this.$message.error("部分照片上传失败");
      }

      window.location.reload();
    },

    openPhotoViewer(photo) {
      this.selectedPhoto = photo;
      this.viewerVisible = true;
    },
    editPhoto(photo) {
      console.log("photo.fileUrl:", photo.fileUrl);
      console.log("typeof photo.fileUrl:", typeof photo.fileUrl);
      console.log("photo.fileUrl?.type:", photo.fileUrl?.type);
      this.editingPhoto = photo;
      this.editingPhotoUrl = photo.fileUrl;
      this.editorVisible = true;
      console.log(this.editingPhoto);
      this.viewerVisible = false;
    },
    async deletePhoto(photo) {
      try {
        await apiClient.delete(`/photos/${photo.photoId}`);
        this.album.photos = this.album.photos.filter(
          (p) => p.photoId !== photo.photoId
        );
        this.album.photoCount--;
        this.viewerVisible = false;
        this.$message.success("照片已删除");
      } catch (error) {
        console.error("删除失败：", error);
        this.$message.error("删除照片失败");
      }
    },
    handlePhotoReport(photo) {
      this.selectedPhoto = photo;
      this.photoReportDialogVisible = true;
    },
    async submitPhotoReport() {
      try {
        await apiClient.post("/reports/user", {
          resourceId: this.selectedPhoto.photoId,
          reason: this.photoReportReason,
          resourceType: "photo",
          reporteeId: this.selectedPhoto.userId,
        });
        this.$message.success("举报已提交");
        this.photoReportDialogVisible = false;
        this.photoReportReason = "";
      } catch (error) {
        console.error("举报失败：", error);
        this.$message.error("举报失败，请稍后再试");
      }
    },
    async handlePhotoSave(blob) {
      try {
        const formData = new FormData();
        formData.append("file", blob, "edited.png");
        formData.append("albumId", this.albumId); // 当前相册ID
        formData.append("userId", this.userId); // 当前用户ID

        // 调用上传新照片的接口
        await apiClient.post("/photos/upload", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });

        this.$message.success("编辑后的照片已作为新照片上传");
        this.editorVisible = false;
        // 刷新照片列表
        window.location.reload();
      } catch (error) {
        this.$message.error("上传失败");
        console.error(error);
      }
    },
    async handlePhotoInfoUpdate(updatedPhoto) {
      try {
        console.log("更新照片信息：", updatedPhoto);
        const formData = new FormData();
        formData.append("fileName", updatedPhoto.fileName);
        formData.append("tag", updatedPhoto.tag);
        formData.append("location", updatedPhoto.location);
        formData.append("isFavorite", updatedPhoto.isFavorite);

        await apiClient.put(`/photos/${updatedPhoto.photoId}`, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });

        const index = this.album.photos.findIndex(
          (photo) => photo.photoId === updatedPhoto.photoId
        );
        if (index !== -1) {
          this.album.photos[index] = { ...updatedPhoto };
          this.$message.success("照片信息已更新");
        }
      } catch (error) {
        console.error("更新照片信息失败：", error);
        this.$message.error("更新照片信息失败");
      }
    },
    isVideo(url) {
      return /\.(mp4|webm|ogg|mov|avi|mkv|mpeg|wmv)$/i.test(url);
    },
    applyFilter() {
      console.log("筛选条件：", this.filterCriteria);
      // TODO: 根据 filterCriteria 进行过滤
      this.filterDialogVisible = false;
      if (this.filterCriteria.tag) {
        this.photos = this.photos.filter((photo) =>
          photo.tag.includes(this.filterCriteria.tag)
        );
      }
    },
    photoF
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
}
.album-header {
  display: flex;
  align-items: center;
}
</style>
