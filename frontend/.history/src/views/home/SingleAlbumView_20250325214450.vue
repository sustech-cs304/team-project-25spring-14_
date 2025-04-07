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