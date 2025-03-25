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
import apiClient from "@/apiClient";

export default {
  data() {
    return {
      albums: [],
      userInfo: {},
      albumInfo: {},
    };
  },
  methods: {
    async fetchUserInfo() {
      try {
        const response = await apiClient
          .get("/user/userInfo")
          .then((response) => {
            this.userInfo = response.data.data;
          })
          .catch((error) => {
            console.error(error);
          });
        this.userID = response.data.data.userId;
        console.log("用户信息获取成功，ID:", this.user);
      } catch (error) {
        console.error("用户信息获取失败:", error);
        this.$router.push("/home/dicover");
      }
    },
    async fetchAlbums() {
      if (!this.userID) {
        console.error("用户ID获取失败，无法获取相册列表！");
        alert("获取相册信息失败，请重试！");
        return;
      }
      try {
        const response = await apiClient.get(`/albums/user/${this.userID}`, {
          params: { userId: this.userID },
        }); /* 获取用户自己的相册信息 */
        this.albumsInfo = response.data;
        console.log("相册数据获取成功:", this.albumInfo.data.code);
      } catch (error) {
        console.error("获取相册数据失败:", error);
        alert("加载相册失败，请稍后再试！");
      }
    },

    async initializePage() {
      await this.fetchUserInfo();
      await this.fetchAlbums();
    },
    goToAlbum(albumId) {
      this.$router.push(`/albums/${albumId}`);
    },
  },
  mounted() {
    this.initializePage();
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
  position: absolute;
  top: 0;
  left: 0;
  border-radius: 0;
  overflow: hidden;
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
  background-color: #f1ebeb;
  background-image: url("/src/assets/images/xiangce.svg");
  background-repeat: no-repeat;
  background-position: center;
  background-size: 40%;
  border-radius: 0;
}

/* 右侧文本信息 */
.album-info {
  margin-left: 100px;
}

.album-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.album-count {
  font-size: 14px;
  color: #82888f;
}
</style>
