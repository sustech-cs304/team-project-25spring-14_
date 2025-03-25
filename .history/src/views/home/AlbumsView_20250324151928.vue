<template>
  <div class="albums-container">
    <h2>📚 我的相册</h2>
    <div class="albums-grid">
      <div 
        v-for="album in albums" 
        :key="album.id" 
        class="album-card"
        @click="goToAlbum(album.id)"
      >
        <img :src="album.cover" alt="Album Cover" />
        <div class="album-info">
          <h3>{{ album.name }}</h3>
          <p>{{ album.photoCount }} 张照片</p>
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
        { id: 1, name: "旅行", cover: "/images/album1.jpg", photoCount: 30 },
        { id: 2, name: "家庭", cover: "/images/album2.jpg", photoCount: 50 },
        { id: 3, name: "朋友", cover: "/images/album3.jpg", photoCount: 20 },
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
.albums-container {
  padding: 20px;
}
.albums-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}
.album-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: 0.3s;
}
.album-card:hover {
  transform: scale(1.05);
}
.album-card img {
  width: 100%;
  height: 150px;
  object-fit: cover;
}
.album-info {
  padding: 10px;
  text-align: center;
}
</style>
