### 初步需求分析（Preliminary Requirement Analysis）

#### **1. 功能需求（Functional Requirements）**

- **智能分类**
  支持基于时间、地点、事件、AI自动标签的多维度分类。

- **高级搜索**
  支持自然语言搜索、人脸识别、物体检测、语义化查询。

- **照片编辑**
  提供基础编辑工具（裁剪/旋转/调色），生成视频等内容

- **分享与社交互动**
  在应用内部创建分享链接，用户可将链接发送给应用内好友，并且可以评论照片

- **隐私与安全管理**
设置管理员管理员管理系统（内容合规性检查）、用户端有隐私权限（加密相册）

#### **2. 非功能需求（Non-Functional Requirements）**

- **安全性**
  加密登陆，用户相册可私有化。（可加密储存？）

#### **3. 数据需求（Data Requirements）**

- **照片数据**
  存储时间、地点（GPS坐标）、标签、格式。（文件大小？）用户储存
- **用户数据**
  账号信息、权限角色（普通用户/管理员）、操作日志。
- **AI模型数据**
  yolo预训练模型

#### **4. 技术需求（Technical Requirements）**

- **后端**：Java + Spring Boot + MyBatis-Plus（RESTful API）
- **数据库**：PostgreSQL（主库）
- **AI模块**：Python（OpenCV人脸识别）
- **前端**：Vue.js（Web端）
- **协作工具**：GitHub Projects（任务管理）、Git （版本控制）
