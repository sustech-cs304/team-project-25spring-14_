<template>
  <div class="main-container">
    <SideBar />
    <div class="content-wrapper">
      <div class="album-header">
        <div class="album-cover-wrapper">
          <img
            v-if="album.coverPhotoUrl"
            :src="album.coverPhotoUrl"
            alt="Album Cover"
            class="album-cover"
          />
          <div v-else class="album-cover default" @click="updateAlbumCover">
            <span class="add-cover-text">添加封面</span>
          </div>
        </div>
        <div class="album-info-wrapper">
          <div class="album-title-row">
            <div v-if="!editingTitle" @click="editingTitle = true">
              <h1 class="album-title">{{ album.title }}</h1>
            </div>
            <div
              v-else
              class="album-title editable"
              contenteditable="true"
              @blur="cancelOrUpdateTitle"
              @keydown.enter.prevent="updateAlbumTitle"
              @focus="selectAllContent('titleEditor')"
              ref="titleEditor"
            >
              {{ editableTitle }}
            </div>
            <div class="privacy-select" @click="privacySelectVisible = true">
              <img
                :src="getPrivacyIcon(editablePrivacy)"
                class="privacy-icon"
                alt="privacy"
              />
              <el-select
                v-if="privacySelectVisible"
                v-model="editablePrivacy"
                size="small"
                @change="updateAlbumPrivacy"
                :placeholder="''"
                :teleported="false"
                :suffix-icon="null"
                class="privacy-dropdown"
                @blur="privacySelectVisible = false"
                @visible-change="
                  (visible) => !visible && (privacySelectVisible = false)
                "
              >
                <el-option
                  v-for="item in privacyOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                  <img :src="item.icon" class="privacy-icon" />
                  <span style="margin-left: 6px">{{ item.label }}</span>
                </el-option>
              </el-select>
            </div>
          </div>
          <div v-if="!editingDescription" @click="editingDescription = true">
            <p class="album-description quoted">{{ album.description }}</p>
          </div>
          <div
            v-else
            class="album-description editable"
            contenteditable="true"
            @blur="cancelOrUpdateDescription"
            @keydown.enter.prevent="updateAlbumDescription"
            @focus="selectAllContent('descriptionEditor')"
            ref="descriptionEditor"
          >
            {{ editableDescription }}
          </div>
          <p class="photo-count">{{ album.photoCount }} 张照片</p>
        </div>
        <div class="album-actions">
          <el-button type="danger" @click="deleteConfirmVisible = true" plain
            >删除相册</el-button
          >
        </div>
      </div>
      <div class="photo-grid">
        <div
          class="photo-card"
          v-for="photo in album.photos"
          :key="photo.photoId"
        >
          <img :src="photo.thumbnailUrl" alt="photo" class="photo-image" />
        </div>
        <div class="photo-card add-photo-card" @click="triggerAddPhoto">
          <span class="add-icon">+</span>
          <input
            type="file"
            ref="photoInput"
            style="display: none"
            @change="handlePhotoChange"
          />
        </div>
      </div>
      <el-dialog
        v-model="deleteConfirmVisible"
        title="确认删除"
        width="300px"
        center
      >
        <span>你确定要删除该相册吗？此操作不可恢复。</span>
        <template #footer>
          <el-button @click="deleteConfirmVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDeleteAlbum">删除</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import SideBar from "@/components/SideBar.vue";
import apiClient from "@/apiClient";
import { ElButton, ElSelect, ElOption } from "element-plus";

export default {
  components: {
    SideBar,
    ElButton,
    ElSelect,
    ElOption,
  },
  data() {
    return {
      album: {
        photos: [],
      },
      userId: localStorage.getItem("userId"),
      editingTitle: false,
      editingDescription: false,
      editableTitle: "",
      editableDescription: "",
      editablePrivacy: "",
      privacySelectVisible: false,
      privacyOptions: [
        {
          label: "公开",
          value: "PUBLIC",
          icon: require("@/assets/images/PUBLIC.svg"),
        },
        {
          label: "私密",
          value: "PRIVATE",
          icon: require("@/assets/images/PRIVATE.svg"),
        },
        {
          label: "共享",
          value: "SHARED",
          icon: require("@/assets/images/SHARED.svg"),
        },
      ],
      deleteConfirmVisible: false,
    };
  },
  async created() {
    const albumId = this.$route.params.albumId;
    try {
      const response = await apiClient.get(`/albums/${albumId}`, {
        params: {
          userId: this.userId,
        },
      });
      this.album = response.data.data.album;
      this.editableTitle = this.album.title;
      this.editableDescription = this.album.description;
      this.editablePrivacy = this.album.privacy;

      const photoRes = await apiClient.get(`/photos/album/${albumId}`, {
        params: {
          userId: this.userId,
        },
      });
      this.album.photos = photoRes.data.data.photos;
    } catch (error) {
      console.error("获取相册详情失败：", error);
      alert("加载相册详情失败，请稍后重试！");
      this.$router.push("/albums");
    }
  },
  methods: {
    triggerAddPhoto() {
      this.$refs.photoInput.click();
    },
    async handlePhotoChange(event) {
      const file = event.target.files[0];
      if (!file) return;

      try {
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
        this.$message.success("照片上传成功");
        this.w
      } catch (error) {
        console.error("上传失败：", error);
        this.$message.error("上传照片失败");
      }
    },
    async confirmDeleteAlbum() {
      try {
        await apiClient.delete(`/albums/${this.album.albumId}`, {
          params: {
            userId: localStorage.getItem("userId"),
          },
        });
        this.$message.success("相册已删除");
        this.$router.push("/albums");
      } catch (error) {
        console.error("删除相册失败：", error);
        this.$message.error("删除相册失败");
      } finally {
        this.deleteConfirmVisible = false;
      }
    },
    async updateAlbum(fields) {
      try {
        const formData = new FormData();
        for (const key in fields) {
          formData.append(key, fields[key]);
        }
        await apiClient.put(`/albums/${this.album.albumId}`, formData, {
          params: {
            userId: localStorage.getItem("userId"),
          },
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });
        this.album = { ...this.album, ...fields };
        this.$message.success("相册信息已更新");
      } catch (err) {
        this.$message.error("更新相册失败");
      }
    },
    async updateAlbumCover(photoId) {
      try {
        await this.updateAlbum({ coverPhotoId: photoId });
        const photo = this.album.photos.find((p) => p.photoId === photoId);
        if (photo) {
          this.album.coverPhotoUrl = photo.thumbnailUrl;
        }
        this.$message.success("相册封面已更新");
      } catch (error) {
        console.error("更新相册封面失败：", error);
        this.$message.error("更新相册封面失败");
      }
    },
    async updateAlbumTitle() {
      this.editingTitle = false;
      if (this.editableTitle !== this.album.title) {
        await this.updateAlbum({ title: this.editableTitle });
      }
    },
    async updateAlbumDescription() {
      this.editingDescription = false;
      if (this.editableDescription !== this.album.description) {
        await this.updateAlbum({ description: this.editableDescription });
      }
    },
    async updateAlbumPrivacy() {
      if (this.editablePrivacy !== this.album.privacy) {
        await this.updateAlbum({ privacy: this.editablePrivacy });
      }
    },
    async cancelOrUpdateTitle(event) {
      const newText = event.target.innerText.trim();
      this.editingTitle = false;
      if (newText !== this.album.title) {
        this.editableTitle = newText;
        await this.updateAlbumTitle();
      }
    },
    async cancelOrUpdateDescription(event) {
      const newText = event.target.innerText.trim();
      this.editingDescription = false;
      if (newText !== this.album.description) {
        this.editableDescription = newText;
        await this.updateAlbumDescription();
      }
    },
    selectAllContent(refName) {
      this.$nextTick(() => {
        const el = this.$refs[refName];
        if (el && window.getSelection && document.createRange) {
          const range = document.createRange();
          range.selectNodeContents(el);
          const selection = window.getSelection();
          selection.removeAllRanges();
          selection.addRange(range);
        }
      });
    },
    getPrivacyIcon(privacy) {
      const item = this.privacyOptions.find((opt) => opt.value === privacy);
      return item ? item.icon : "";
    },
  },
};
</script>

<style scoped>
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
</style>
