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
          <div v-else class="album-cover default" @click="handleAddCover">
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
          <el-button type="danger" @click="deleteAlbum" plain
            >删除相册</el-button
          >
        </div>
      </div>
      <div class="photo-grid">
        <div
          class="photo-card"
          v-for="photo in album.latestPhotos"
          :key="photo.photoId"
        >
          <img :src="photo.thumbnailUrl" alt="photo" class="photo-image" />
        </div>
      </div>
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
      album: {},
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
    } catch (error) {
      console.error("获取相册详情失败：", error);
      alert("加载相册详情失败，请稍后重试！");
    }
  },
  methods: {
    handleAddCover() {
      alert("点击添加封面，可实现上传逻辑");
    },
    editAlbum() {
      this.$message.info("点击了编辑信息");
    },
    async deleteAlbum() {
      try {
        const respoawait apiClient.delete(`/albums/${this.album.albumId}`, {
          params: {
            userId: localStorage.getItem("userId"),
          },
        });
        this.$router.push("/albums");
      } catch (error) {
        console.error("删除相册失败：", error);
        this.$message.error("删除相册失败");

      }
      this.$message.warning("点击了删除相册");
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
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
  margin-top: 20px;
}

.photo-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s;
}

.photo-card:hover {
  transform: scale(1.02);
}

.photo-thumbnail {
  width: 100%;
  height: 180px;
  object-fit: cover;
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
</style>
