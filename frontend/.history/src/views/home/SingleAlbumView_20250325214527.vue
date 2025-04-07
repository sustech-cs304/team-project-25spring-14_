<template>
    <div class="container">
      <!-- 标题部分（相册封面 + 右侧信息） -->
      <div class="album-header">
        <div class="album-cover">
          <img
            v-if="album.coverPhotoUrl"
            :src="album.coverPhotoUrl"
            alt="Album Cover"
            class="cover-image"
          />
          <div v-else class="default-cover">
            <i class="icon-placeholder"></i>
          </div>
        </div>
        <div class="album-info">
          <h1 class="album-title">{{ album.title }}</h1>
          <p class="album-description">{{ album.description || "暂无描述" }}</p>
          <p class="album-count">{{ album.photoCount }} 张照片</p>
          <el-button type="primary" size="small" @click="editAlbum">编辑相册</el-button>
        </div>
      </div>
  
      <!-- 照片网格 -->
      <div class="photo-grid">
        <div
          v-for="photo in album.photos"
          :key="photo.photoId"
          class="photo-card"
        >
          <img :src="photo.photoUrl" class="photo-image" alt="Photo" />
        </div>
      </div>
    </div>
  </template>

  <script>
  import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";

export default {
  setup() {
    const route = useRoute();
    const album = ref({
      albumId: route.params.albumId, // 从路由参数获取相册ID
      title: "旅游记忆",
      description: "这里记录了我的旅行美好瞬间。",
      coverPhotoUrl: "https://via.placeholder.com/400x250",
      photoCount: 8,
      photos: [
        { photoId: 1, photoUrl: "https://via.placeholder.com/150" },
        { photoId: 2, photoUrl: "https://via.placeholder.com/150" },
        { photoId: 3, photoUrl: "https://via.placeholder.com/150" },
        { photoId: 4, photoUrl: "https://via.placeholder.com/150" },
        { photoId: 5, photoUrl: "https://via.placeholder.com/150" },
        { photoId: 6, photoUrl: "https://via.placeholder.com/150" },
        { photoId: 7, photoUrl: "https://via.placeholder.com/150" },
        { photoId: 8, photoUrl: "https://via.placeholder.com/150" },
      ],
    });

    onMounted(() => {
      console.log(`当前相册ID: ${album.value.albumId}`);
    });

    const editAlbum = () => {
      console.log("编辑相册");
    };

    return {
      album,
      editAlbum,
    };
  },
};
</script>
<style>
.container {
  padding: 24px;
  background-color: #f3f4f6;
  min-height: calc(100vh);
  border-radius: 32px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 相册标题部分 */
.album-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
}

/* 相册封面 */
.album-cover {
  width: 200px;
  height: 150px;
  border-radius: 12px;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-cover {
  width: 100%;
  height: 100%;
  background-color: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #888;
}

/* 相册信息 */
.album-info {
  flex: 1;
}

.album-title {
  font-size: 24px;
  font-weight: bold;
}

.album-description {
  font-size: 14px;
  color: #666;
  margin: 8px 0;
}

.album-count {
  font-size: 14px;
  color: #82888f;
}

/* 照片网格 */
.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
  padding: 16px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  flex: 1;
}

/* 照片卡片 */
.photo-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
  cursor: pointer;
}

.photo-card:hover {
  transform: scale(1.05);
}

.photo-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}</style>
