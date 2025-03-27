<template>
  <div class="main-container">
    <SideBar />
    <div class="content-wrapper">
      <div class="album-header">
        <div class="album-info">
          <h1 class="album-title">{{ album.title }}</h1>
          <p class="album-description">{{ album.description }}</p>
          <p class="photo-count">{{ album.photoCount }} 张照片</p>
        </div>
        <div class="album-privacy">
          <h1></h1>
        </div>
        <div clas></div>
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

export default {
  components: {
    SideBar,
  },
  data() {
    return {
      album: {},
      userId: localStorage.getItem("userId"),
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
    } catch (error) {
      console.error("获取相册详情失败：", error);
      alert("加载相册详情失败，请稍后重试！");
    }
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
}

.album-title {
  font-size: 24px;
  font-weight: bold;
}

.album-description {
  color: #6b7280;
  margin-top: 4px;
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
</style>
