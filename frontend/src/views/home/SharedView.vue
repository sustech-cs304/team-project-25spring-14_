<template>
  <div class="main-container">
    <SideBar />
    <div class="container">
      <nav class="social-nav">
        <div class="nav-left">
          <h1>共享空间</h1>
        </div>
        <div class="nav-center">
          <input type="text" placeholder="搜索内容/用户" class="search-input" />
        </div>

        <div class="nav-right">
          <button class="refresh-btn" @click="initializePage()">刷新</button>
          <button class="upload-btn" @click="showPost = true">
            + 发布内容
          </button>
        </div>
      </nav>

      <!-- 内容区域 -->
      <main class="content-wrapper">
        <PostModal
          v-if="showPost"
          @confirm="Postit"
          @close="showPost = false"
        />
        <!-- 帖子列表 -->
        <div class="post-grid">
          <div v-for="(post, index) in posts" :key="index" class="post-card">
            <div class="post-header">
              <img :src="post.userAvatar" class="author-avatar" />
              <span class="author-name">{{ post.username }}</span>
              <button class="follow-btn">关注</button>
            </div>

            <div class="post-media">
              <img
                :src="post.photoUrl"
                alt="帖子图片"
                class="post-image"
                @click="openPostDetail(post)"
              />
            </div>

            <div class="post-actions">
              <button
                class="action-btn"
                @click="toggleLike(post)"
                :class="{ liked: post.isLiked }"
              >
                <i class="album-icons icon-zantong"></i> {{ post.likeCount }}
              </button>
              <button class="action-btn">
                <i class="album-icons icon-pinglun"></i> {{ post.commentCount }}
              </button>
              <button
                class="action-btn"
                @click="toggleCollect(post)"
                :class="{ collected: post.isCollected }"
              >
                <i class="album-icons icon-shoucang"></i> {{ post.collects }}
              </button>
            </div>

            <div class="post-content">
              <p class="post-text">{{ post.caption }}</p>
              <div class="post-tags">
                <span v-for="(tag, i) in post.tags" :key="i" class="tag">
                  #{{ tag }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import SideBar from "@/components/SideBar.vue";
import apiClient from "@/apiClient";
import PostModal from "@/components/PostModal.vue";
export default {
  components: {
    SideBar,
    PostModal,
  },
  data() {
    return {
      showPost: false,
      posts: [],
      userInfo: {},
      count: 0,
    };
  },
  mounted() {
    this.initializePage();
  },
  methods: {
    async fetchUserInfo() {
      try {
        await apiClient
          .get("/user/userInfo")
          .then((response) => {
            this.userInfo = response.data.data;
          })
          .catch((error) => {
            console.error(error);
          });
        console.log("用户信息获取成功，ID:", this.userInfo.userId);
        localStorage.setItem("userId", this.userInfo.userId);
      } catch (error) {
        console.error("用户信息获取失败:", error);
        this.$router.push("/home/dicover");
      }
    },
    async fetchPosts() {
      try {
        await apiClient
          .get("/posts/public")
          .then((response) => {
            this.count = response.data.data.count;
            this.posts = response.data.data.posts;
          })
          .catch((error) => {
            console.error(error);
          });
      } catch (error) {
        console.error("获取帖子失败:", error);
        alert("获取帖子失败");
      }
    },
    async initializePage() {
      await this.fetchUserInfo();
      await this.fetchPosts();
    },

    toggleLike(post) {
      post.isLiked = !post.isLiked;
      post.likes += post.isLiked ? 1 : -1;
    },
    toggleCollect(post) {
      post.isCollected = !post.isCollected;
      post.collects += post.isCollected ? 1 : -1;
    },
    openPostDetail(post) {
      // 打开帖子详情逻辑
    },
    async Postit({ photo, caption, privacy }) {
      this.showPost = false;
      if (!photo) return;
      try {
        const formData = new FormData();
        formData.append("photo", photo);
        if (caption) {
          formData.append("caption", caption);
        }
        if (privacy) {
          formData.append("privacy", privacy);
        }
        const response = await apiClient.post(`posts/upload`, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });
        if (response.data.code == 0) {
          this.initializePage();
        } else {
          alert("发帖失败");
        }
      } catch (error) {
        alert("发帖失败");
        console.error(error);
      }
    },
  },
};
</script>

<style scoped>
.social-container {
  max-width: 1440px;
  margin: 0 auto;
}
.main-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
  width: 100vw;
}
.container {
  flex: 1;
  margin: 10px;
  padding: 20px;
  background-color: #f3f4f6;
  min-height: 300px;
  box-shadow: inset 0 4px 6px rgba(0, 0, 0, 0.1),
    inset 0 -4px 6px rgba(255, 255, 255, 0.7);
  border-radius: 32px;
  display: flex;
  flex-direction: column;
  overflow-y: auto; /* 允许右侧内容区域垂直滚动 */
}

/* 顶部导航样式 */
.social-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
  border-radius: 15px;
}

.search-input {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  border: 1px solid #ddd;
  width: 300px;
}

.refresh-btn {
  background: #91fd03;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 5px;
  border: none;
  cursor: pointer;
  margin-right: 20px;
}

.upload-btn {
  background: #ff4757;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 5px;
  border: none;
  cursor: pointer;
}

.user-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

/* 内容布局 */
.content-wrapper {
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
  padding: 2rem;
}

.post-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(320px, 1fr));
  gap: 2rem;
}

.post-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(to right, #ffffff 80%, #f8f8f8);
  border-radius: 12px 12px 0 0;
  position: relative;
  transition: background 0.3s ease;
}

.author-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  margin-top: auto;
  margin-right: 8px;
  align-self: center;
}

.author-name {
  font-weight: 600;
  font-size: 15px;
  color: #262626;
  position: relative;
  cursor: pointer;
}

.follow-btn {
  margin-left: auto;
  padding: 6px 12px;
  font-size: 14px;
  color: #fff;
  background-color: #007bff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.follow-btn:hover {
  background-color: #0056b3;
}

/* 帖子卡片样式 */
.post-card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.post-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
  cursor: pointer;
}

.post-actions {
  padding: 0.5rem 1rem;
  display: flex;
  gap: 1rem;
}

.action-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.1rem;
}

.liked {
  color: #ff4757;
}

.collected {
  color: #ffd700;
}

.post-content {
  padding: 1rem;
}

.post-tags {
  margin-top: 0.5rem;
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag {
  color: #3498db;
  font-size: 0.9rem;
}

/* 侧边栏样式 */
.sidebar {
  background: #f8f8f8;
  padding: 1rem;
  border-radius: 10px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }

  .sidebar {
    display: none;
  }
}

@media (max-width: 768px) {
  .social-nav {
    padding: 1rem;
  }

  .search-input {
    width: 200px;
  }

  .post-image {
    height: 300px;
  }
}
</style>
