<template>
  <div class="container">
    <!-- 标题部分 -->
    <div class="header">
      <h1 class="title">相簿</h1>
      <p class="subtitle">{{ albums.length }} 个相簿</p>
    </div>

    <!-- 相册网格 -->
    <div class="album-grid">
      <div
        v-for="album in albums"
        :key="album.id"
        class="album-card"
        @click="goToAlbum(album.id)"
      >
        <div class="album-cover">
          <img
            v-if="album.cover"
            :src="album.cover"
            alt="Album Cover"
            class="rounded-xl"
          />
          <div v-else class="default-cover">
            <i class="icon-placeholder"></i>
          </div>
        </div>
        <div class="album-info">
          <h3 class="album-title">{{ album.name }}</h3>
          <p class="album-count">{{ album.photoCount }} 张照片</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      albums: [
        { id: 1, name: "QQ", cover: "/images/album1.jpg", photoCount: 42 },
        { id: 2, name: "我的头像", cover: "", photoCount: 0 },
        { id: 3, name: "LightGameAsset", cover: "", photoCount: 0 },
        { id: 4, name: "微博", cover: "", photoCount: 0 },
        {
          id: 5,
          name: "哔哩哔哩",
          cover: "/images/album2.jpg",
          photoCount: 28,
        },
        { id: 6, name: "抖音", cover: "/images/album3.jpg", photoCount: 7 },
        { id: 7, name: "小红书", cover: "/images/album4.jpg", photoCount: 49 },
        { id: 8, name: "微信", cover: "/images/album5.jpg", photoCount: 58 },
      ],
    };
  },
  methods: {
    goToAlbum(albumId) {
      this.$router.push(`/albums/${albumId}`);
    },
  },
};
</script>

<style scoped>
.container {
  padding: 24px;
  background-color: #f3f4f6;
  min-height: calc(100vh);
  box-shadow: inset 0 4px 6px rgba(0, 0, 0, 0.1),
    inset 0 -4px 6px rgba(255, 255, 255, 0.7);
  border-radius: 32px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
/* 标题部分 */
.header {
  margin-bottom: 24px;
  border-bottom: 1px solid #d1d5db;
  padding-bottom: 12px;
}

.title {
  font-size: 24px;
  font-weight: bold;
  font-family: "黑体", sans-serif;
}

.subtitle {
  color: #82888f;
}

/* 相册网格 */
.album-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}
/* 相册卡片 */
.album-card {
  background: white;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  border-radius: 20px;
  height: 100px;
  position: relative;
  overflow: hidden;
}

.album-card:hover {
  transform: scale(1.02);
}

/* 封面图 */
.album-cover {
  width: 100px;
  height: 100px;

}

.album-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-cover {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #888;
}

/* 右侧文本信息 */
.album-info {
  margin-left: 12px;
}

.album-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.album-count {
  font-size: 14px;
  color: #666;
}
</style>
