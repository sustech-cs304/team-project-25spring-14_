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
  
</script>