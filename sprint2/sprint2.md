# Sprint 2 成果报告

## 1. 项目指标 (Metrics)

我们建立了全面的项目绩效指标体系，监控开发进度和质量：

| line of code               | 3391    |
| -------------------------- | ------- |
| **number of source file**  | **38**  |
| **Cyclomatic complexity**  | **297** |
| **Number of dependencies** | **68**  |



## 2. 项目文档 (Documentation)

参考wikipage

[Home · sustech-cs304/team-project-25spring-14_ Wiki](https://github.com/sustech-cs304/team-project-25spring-14_/wiki)

## 3. 测试策略与实现 (Tests)

我们采用Apifox进行自动化API测试，构建了高效的"端到端"验证体系：

### 测试方法与工具

- **Apifox Test**：集成化的API设计、文档和测试平台
- **基于场景的测试**：模拟真实用户操作流程的端到端测试
- **自动化测试流水线**：与Jenkins集成，实现持续测试

### 测试覆盖范围

我们的测试全面覆盖了系统核心功能模块：

- **用户管理**：注册流程、登录认证、权限验证、个人信息管理
- **相册管理**：创建、查询、修改、删除相册及权限控制
- **照片管理**：多格式上传、缩略图生成、元数据处理、标签管理
- **社交功能**：内容发布、点赞、评论、关注关系处理

### 测试成效与优势

- **高效验证**：每次迭代后快速执行全套API测试，确保功能完整性
- **问题早期发现**：在集成阶段及时发现并修复接口兼容性问题
- **质量保障**：最终测试报告显示所有测试样例100%通过
- **文档一致性**：确保API实现与文档描述保持同步

虽然我们的方法专注于功能完整性验证，但对复杂边界条件的覆盖有待增强。未来计划引入更多单元测试和模糊测试，提升对边界情况的测试覆盖。

本地：
[team-project-25spring-14_/sprint2/apifox.png at master · sustech-cs304/team-project-25spring-14_](https://github.com/sustech-cs304/team-project-25spring-14_/blob/master/sprint2/apifox.png)
[team-project-25spring-14_/sprint2/apiox.png at master · sustech-cs304/team-project-25spring-14_](https://github.com/sustech-cs304/team-project-25spring-14_/blob/master/sprint2/apiox.png)

生成报告：
[team-project-25spring-14_/sprint2/html-test1.png at master · sustech-cs304/team-project-25spring-14_](https://github.com/sustech-cs304/team-project-25spring-14_/blob/master/sprint2/html-test1.png)

jenkins：
[team-project-25spring-14_/sprint2/jenkins-apifox.png at master · sustech-cs304/team-project-25spring-14_](https://github.com/sustech-cs304/team-project-25spring-14_/blob/master/sprint2/jenkins-apifox.png)

运行后也生成和上边一样的报告

## 4. 构建与部署 (Build & Deployment)

### 构建流程与技术栈

#### 后端构建

- **构建工具**：Maven 3
- 核心任务
  - 代码编译与字节码生成
  - 依赖解析与资源管理
  - 单元测试执行（可选）
  - JAR包构建与签名
- **构建配置**：基于`pom.xml`的声明式配置，确保构建过程可重现

#### 前端构建

- **构建工具**：Node.js与npm/webpack
- 核心任务
  - 依赖安装与版本控制
  - ESLint代码规范检查
  - SCSS/LESS预处理
  - 资源优化与压缩（JS/CSS/图像）
  - 静态资源打包
- **构建配置**：`package.json`与`webpack.config.js`定义构建流程

### 部署架构与自动化

#### 容器化技术栈

我们采用现代微服务部署架构，实现了完整的容器化解决方案：

- **Docker**：应用程序容器化
- **Docker Compose**：多容器编排与服务定义
- **PostgreSQL**：数据持久化存储
- **Nginx**：静态资源服务与API反向代理
- **Jenkins**：CI/CD自动化流水线

#### 容器配置与部署流程

- **前端容器**：基于`nginx:stable-alpine`的轻量级容器，优化静态资源交付
- **后端容器**：基于`eclipse-temurin:21-jdk`的JVM容器，运行Spring Boot应用
- **数据库容器**：PostgreSQL容器，配置数据持久化与自动初始化脚本
- **网络隔离**：通过Docker网络实现容器间安全通信

[team-project-25spring-14_/sprint2/docker_image.png at master · sustech-cs304/team-project-25spring-14_](https://github.com/sustech-cs304/team-project-25spring-14_/blob/master/sprint2/docker_image.png)

[team-project-25spring-14_/sprint2/docker_running_iamge.png at master · sustech-cs304/team-project-25spring-14_](https://github.com/sustech-cs304/team-project-25spring-14_/blob/master/sprint2/docker_running_iamge.png)



以上三个部分最后都通过jenkinsfile在jinkins中构建，jenkinsfile地址如下链接：
[team-project-25spring-14_/Jenkinsfile at master · sustech-cs304/team-project-25spring-14_](https://github.com/sustech-cs304/team-project-25spring-14_/blob/master/Jenkinsfile)

#### CI/CD自动化流水线

Jenkins流水线实现了端到端的自动化构建与部署：

1. **代码获取**：从Git仓库拉取最新代码
2. **并行构建**：前后端代码并行构建，优化构建时间
3. **容器构建**：创建应用Docker镜像并推送至Docker Hub
4. **服务编排**：使用Docker Compose部署完整应用栈
5. **数据初始化**：自动执行数据库初始化与迁移
6. **部署验证**：自动检查服务健康状态
7. **成果归档**：构建产物与部署配置存档

通过这套完整的CI/CD流程，我们实现了一键式部署，显著提升了交付效率和系统可靠性。每次代码提交后，应用可在30分钟内完成从构建到部署的全流程，确保产品快速迭代与持续交付。

[team-project-25spring-14_/sprint2/pipeline2.png at master · sustech-cs304/team-project-25spring-14_](https://github.com/sustech-cs304/team-project-25spring-14_/blob/master/sprint2/pipeline2.png)

[team-project-25spring-14_/sprint2/jenkins_generate_docker_container.png at master · sustech-cs304/team-project-25spring-14_](https://github.com/sustech-cs304/team-project-25spring-14_/blob/master/sprint2/jenkins_generate_docker_container.png)
