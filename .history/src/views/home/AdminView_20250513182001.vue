<template>
  <div class="main-container">
    <SideBar />
    <div class="container">
      <div class="nav-buttons">
        <button
          class="nav-button"
          :class="{ active: currentTab === 'users' }"
          @click="selectTab('users')"
        >
          用户管理
        </button>
        <button
          class="nav-button"
          :class="{ active: currentTab === 'reports' }"
          @click="selectTab('reports')"
        >
          举报信息
        </button>
        <button
          class="nav-button"
          :class="{ active: currentTab === 'statistics' }"
          @click="selectTab('statistics')"
        >
          详细情况
        </button>
      </div>
      <div class="content-area">
        <div v-if="currentTab === 'users'">
          <div class="user-search">
            <input
              type="number"
              v-model.number="searchQuery"
              placeholder="请输入用户ID"
            />
          </div>
          <div class="user-list">
            <div
              class="user-item"
              v-for="user in filteredUsers"
              :key="user.userId"
            >
              用户ID：{{ user.userId }}，用户名：{{ user.username }}，用户属性：
              {{ user.roleName }}
              <el-button
                type="primary"
                size="small"
                @click="showUserDetail(user)"
                style="float: right"
              >
                详细信息
              </el-button>
            </div>
            <p v-if="filteredUsers.length === 0">无对应用户</p>
          </div>
        </div>
        <div v-if="currentTab === 'reports'">
          <div class="report-tab-buttons">
            <button
              class="report-tab-button"
              :class="{ active: currentReportTab === 'unhandled' }"
              @click="selectReportTab('unhandled')"
            >
              未处理
            </button>
            <button
              class="report-tab-button"
              :class="{ active: currentReportTab === 'banned' }"
              @click="selectReportTab('banned')"
            >
              用户已封禁
            </button>
            <button
              class="report-tab-button"
              :class="{ active: currentReportTab === 'rectified' }"
              @click="selectReportTab('rectified')"
            >
              用户已整改
            </button>
          </div>
          <!-- 举报信息内容 -->
          <div class="report-list">
            <div
              class="report-item"
              v-for="report in filteredReports"
              :key="report.reportId"
            >
              举报ID：{{ report.reportId }}，被举报人ID：{{
                report.reporteeId
              }}，类型：{{ report.resourceType }}
              <el-button
                type="primary"
                size="small"
                @click="showReportDetail(report)"
                style="float: right"
              >
                详细信息
              </el-button>
            </div>
            <p v-if="filteredReports.length === 0">暂无对应举报信息</p>
          </div>
        </div>
        <div v-if="currentTab === 'statistics'">
          <!-- 详细情况内容 -->
          <p>这是详细情况内容</p>
        </div>
      </div>
    </div>
  </div>
  <el-dialog v-model="dialogVisible" title="用户详情" width="400px">
    <p>用户ID：{{ selectedUser.userId }}</p>
    <p>用户名：{{ selectedUser.username }}</p>
    <p>用户属性：{{ selectedUser.rolename }}</p>
    <p>邮箱：{{ selectedUser.email || "无" }}</p>
    <p>状态：{{ selectedUser.status }}</p>
    <p>已用存储：{{ selectedUser.storageUsed }} MB</p>
    <p>创建时间：{{ selectedUser.createdAt }}</p>
    <p>最后登录时间：{{ selectedUser.lastLogin || "无" }}</p>
  </el-dialog>
  <el-dialog v-model="reportDialogVisible" title="举报详情" width="700px">
    <p>举报人ID：{{ selectedReport.reporterId }}</p>
    <p>被举报人ID：{{ selectedReport.reporteeId }}</p>
    <p>资源类型：{{ selectedReport.resourceType }}</p>
    <p><strong>理由：</strong>{{ selectedReport.reason }}</p>
    <p>状态：{{ selectedReport.status }}</p>
    
    <p>审核人：{{ selectedReport.reviewedBy || "未审核" }}</p>
    <p>创建时间：{{ selectedReport.createdAt }}</p>
    <template #footer>
      <el-button @click="viewReportedResource">查看被举报资源</el-button>
      <el-button type="danger" @click="banReportedUser">封禁该用户</el-button>
      <el-button @click="ignoreReport">忽略</el-button>
    </template>
  </el-dialog>
  <PhotoViewerModal
    v-model="photoViewerVisible"
    :photo="selectedPhoto"
    @edit="editPhoto"
    @delete="deletePhoto"
  />
</template>

<script>
import SideBar from "@/components/SideBar.vue";
import apiClient from "@/apiClient";
import PhotoViewerModal from "@/components/PhotoViewerModal.vue";
import router from "@/router";

export default {
  name: "AdminView",
  components: {
    SideBar,
    PhotoViewerModal,
  },
  data() {
    return {
      users: [],
      reports: [],
      currentTab: "users",
      searchQuery: "",
      dialogVisible: false,
      selectedUser: {},
      currentReportTab: "unhandled",
      reportDialogVisible: false,
      selectedReport: {},
      photoViewerVisible: false,
      selectedPhoto: {},
    };
  },
  computed: {
    filteredUsers() {
      if (!this.searchQuery) return this.users;
      return this.users.filter((user) => user.userId === this.searchQuery);
    },
    filteredReports() {
      return this.reports.filter((report) => {
        if (this.currentReportTab === "unhandled") {
          return report.status === "pending";
        } else if (this.currentReportTab === "banned") {
          return report.status === "reviewed" && report.isCorrected === false;
        } else if (this.currentReportTab === "rectified") {
          return report.status === "reviewed" && report.isCorrected === true;
        }
        return false;
      });
    },
  },
  methods: {
    selectTab(tab) {
      this.currentTab = tab;
    },
    selectReportTab(tab) {
      this.currentReportTab = tab;
    },
    async fetchUsers() {
      try {
        const response = await apiClient.get("/user/all");
        this.users = response.data.data;
        console.log("获取用户成功:", this.users);
      } catch (error) {
        console.error("获取用户失败:", error);
      }
    },
    async showUserDetail(user) {
      try {
        const response = await apiClient.get(`/user/${user.userId}`);
        this.selectedUser = response.data.data;
        this.dialogVisible = true;
      } catch (error) {
        console.error("获取用户详情失败:", error);
      }
    },
    async fetchReports() {
      try {
        const response = await apiClient.get("/reports/admin");
        this.reports = response.data.data;
        console.log("获取举报信息成功:", this.reports);
      } catch (error) {
        console.error("获取举报信息失败:", error);
      }
    },
    showReportDetail(report) {
      this.selectedReport = report;
      this.reportDialogVisible = true;
    },
    viewReportedResource() {
      const type = this.selectedReport.resourceType;
      const id = this.selectedReport.resourceId;
      if (type === "ALBUM") {
        this.viewAlbum(id);
      } else if (type === "PHOTO") {
        this.viewPhoto(id);
      } else {
        console.warn("未知资源类型：", type);
      }
    },
    viewAlbum(id) {
      console.log("查看相册", id);
      this.$router.push({
        path: `/albums/${id}`,
        query: { isSelf: false },
      });
    },
    async viewPhoto(id) {
      console.log("查看照片", id);
      this.photoViewerVisible = true;
      try {
        const response = await apiClient.get(`/photos/${id}`);
        this.selectedPhoto = response.data.data.photo;
      } catch (error) {
        console.error("获取照片失败:", error);
      }
    },
    async deletePhoto(selectedPhoto) {
      try {
        await apiClient.delete(`/photos/${selectedPhoto.photoId}`);
        this.viewerVisible = false;
        this.$message.success("照片已删除");
      } catch (error) {
        console.error("删除失败：", error);
        this.$message.error("删除照片失败");
      }
    },
    async banReportedUser() {
      const reportId = this.selectedReport.reportId;
      const reporteeId = this.selectedReport.reporteeId;
      try {
        await apiClient.put(
          `/reports/admin/${reportId}?status=reviewed&reporteeId=${reporteeId}`
        );
        this.$message.success("用户已封禁");
        this.reportDialogVisible = false;
        this.fetchReports();
      } catch (error) {
        console.error("封禁用户失败：", error);
        this.$message.error("封禁用户失败");
      }
    },
    ignoreReport() {
      console.log("忽略举报", this.selectedReport.reportId);
      this.reportDialogVisible = false;
    },
  },
  mounted() {
    this.fetchUsers();
    this.fetchReports();
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
  overflow: hidden;
}
.nav-buttons {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}
.nav-button {
  flex: 1;
  padding: 10px;
  border-radius: 8px;
  border: none;
  background-color: #ddd;
  cursor: pointer;
  font-weight: bold;
}
.nav-button:hover {
  background-color: #ccc;
}
.nav-button.active {
  background-color: #aaa;
  color: white;
}
.content-area {
  flex: 1;
  overflow-y: auto;
  background-color: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-search {
  margin-bottom: 12px;
}

.user-search input {
  width: 100%;
  padding: 8px 12px;
  font-size: 14px;
  border-radius: 6px;
  border: 1px solid #ccc;
}

.user-list {
  max-height: calc(100vh - 200px);
  min-height: 100px;
  overflow-y: auto;
  background-color: #fafafa;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px;
}

.user-item {
  padding: 8px;
  border-bottom: 1px solid #eee;
}

.user-item:last-child {
  border-bottom: none;
}

.report-tab-buttons {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.report-tab-button {
  flex: 1;
  padding: 8px;
  border-radius: 6px;
  border: none;
  background-color: #e0e0e0;
  font-weight: bold;
  cursor: pointer;
}

.report-tab-button.active {
  background-color: #999;
  color: white;
}

.report-list {
  background-color: #fdfdfd;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 12px;
  max-height: calc(100vh - 280px);
  overflow-y: auto;
}

.report-item {
  padding: 8px;
  border-bottom: 1px solid #eee;
}

.report-item:last-child {
  border-bottom: none;
}
/* 对话框内容段落增加下边距，提升可读性 */
.el-dialog__body p {
  margin-bottom: 10px;
  font-size: 16px;
}
</style>
