# 运维工单管理系统 (OPS Work Order System)

## 系统概述

这是一个企业级运维工单管理系统，支持多层级审批工作流、部门管理、权限控制等功能。系统采用Spring Boot + Thymeleaf技术栈，提供完整的Web界面和RESTful API。

## 🚀 核心功能

### 1. 用户管理与权限控制
- **用户管理**: 支持用户的增删改查，角色分配
- **角色系统**: ADMIN(管理员)、MANAGER(经理)、OPERATOR(操作员)、USER(用户)
- **权限认证**: 基于Spring Security的安全认证和授权

### 2. 工单管理系统
- **工单创建**: 支持标题、描述、优先级、截止时间、部门等信息
- **工单分配**: 可指派给特定用户或自动分配
- **状态管理**: 待处理、已批准、处理中、已完成、已拒绝、已逾期
- **优先级管理**: 高、中、低三个级别
- **工单搜索**: 支持按状态、优先级、关键词筛选

### 3. 部门管理系统
- **层级结构**: 支持多级部门层次结构
- **部门类型**: 省级、市级、县级、运维、业务、支撑、技术、行政部门
- **联系人管理**: 每个部门可配置多个联系人，支持主要联系人和紧急联系人
- **权限管理**: 24种细粒度权限控制，支持权限的授予、撤销、续期

### 4. 多层级审批工作流
- **工作流模板**: 可配置的审批流程模板
- **步骤类型**: 部门初审、经理审批、主管审批、执行操作、验证确认、完成确认
- **并行/串行**: 支持并行审批和串行审批
- **时限控制**: 可设置步骤时限，支持超时自动处理
- **智能分配**: 基于角色的自动任务分配
- **审批历史**: 完整的审批记录和操作日志

### 5. 统一用户界面
- **响应式设计**: 基于Bootstrap的现代化界面
- **统一导航**: 所有页面统一的导航栏设计
- **可视化仪表板**: 工单统计、待处理任务概览
- **实时更新**: 支持数据的实时刷新和状态更新

## 📋 系统架构

### 技术栈
- **后端框架**: Spring Boot 3.1.0
- **安全框架**: Spring Security
- **数据持久层**: Spring Data JPA + Hibernate
- **数据库**: H2 Database (可切换为MySQL/PostgreSQL)
- **前端模板**: Thymeleaf + Bootstrap 5.3.0
- **构建工具**: Maven

### 项目结构
```
ops-work-order-system/
├── src/main/java/com/example/opsworkordersystem/
│   ├── config/          # 配置类
│   ├── controller/      # 控制器层
│   ├── entity/          # 实体类
│   ├── repository/      # 数据访问层
│   ├── service/         # 业务逻辑层
│   └── OpsWorkOrderSystemApplication.java
├── src/main/resources/
│   ├── templates/       # Thymeleaf模板
│   ├── static/          # 静态资源
│   └── application.properties
└── pom.xml
```

## 🛠️ 安装与运行

### 环境要求
- Java 17+
- Maven 3.6+
- 浏览器支持HTML5和ES6

### 快速启动
1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd ops-work-order-system
   ```

2. **安装依赖**
   ```bash
   mvn clean install
   ```

3. **启动应用**
   ```bash
   mvn spring-boot:run
   ```

4. **访问系统**
   ```
   http://localhost:8080
   ```

### 默认用户账号
- **管理员**: admin / admin123
- **经理**: manager / manager123  
- **操作员**: operator / operator123
- **普通用户**: user / user123

## 📖 使用指南

### 1. 登录系统
访问 `http://localhost:8080` 使用默认账号登录

### 2. 工单管理
- 在"工单管理"页面创建新工单
- 设置标题、描述、优先级和截止时间
- 指派给相应的处理人员
- 跟踪工单处理进度

### 3. 部门管理
- 在"部门管理"页面添加部门
- 配置部门联系人信息
- 设置部门权限和访问控制

### 4. 工作流配置
- 在"工作流管理"页面创建审批模板
- 配置审批步骤和处理人角色
- 设置并行/串行审批和时限控制

### 5. 用户管理(仅管理员)
- 在"用户管理"页面添加用户
- 分配用户角色和权限
- 管理用户信息

## 🔧 配置说明

### 数据库配置
默认使用H2内存数据库，如需切换到MySQL：

```properties
# application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/ops_work_order
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

### 安全配置
可在 `SecurityConfig.java` 中修改安全策略：
- 密码策略
- 会话管理
- CSRF保护
- 权限控制

## 📊 数据模型

### 核心实体
- **User**: 用户信息和角色
- **WorkOrder**: 工单主体信息
- **Department**: 部门层级结构
- **DepartmentContact**: 部门联系人
- **DepartmentPermission**: 部门权限
- **WorkflowTemplate**: 工作流模板
- **WorkflowStep**: 工作流步骤
- **ApprovalRecord**: 审批记录
- **OperationRecord**: 操作记录

### 权限类型 (24种)
- **工单权限**: 创建、查看、编辑、删除、分配工单
- **审批权限**: 审批、拒绝、重新分配工单
- **部门权限**: 管理部门、查看部门、管理联系人
- **用户权限**: 管理用户、查看用户
- **报表权限**: 查看报表、导出报表
- **系统权限**: 系统管理、配置工作流

## 🧪 测试指南

### 测试环境准备
1. 启动应用后，系统会自动初始化测试数据
2. 使用不同角色账号测试不同功能
3. 验证权限控制和工作流程

### 功能测试清单
- [ ] 用户登录/登出
- [ ] 工单创建和状态更新
- [ ] 部门管理和联系人配置
- [ ] 权限授予和撤销
- [ ] 工作流模板创建
- [ ] 审批流程测试
- [ ] 响应式界面测试

## 🔄 开发计划

### 已完成功能
- ✅ 基础用户认证和权限控制
- ✅ 工单CRUD操作和状态管理
- ✅ 部门层级管理和联系人系统
- ✅ 细粒度权限控制系统
- ✅ 多层级审批工作流
- ✅ 统一前端界面设计
- ✅ RESTful API接口

### 待优化功能
- 🔄 工作流编辑功能
- 🔄 报表和统计功能
- 🔄 邮件通知系统
- 🔄 文件附件支持
- 🔄 API文档生成
- 🔄 单元测试覆盖

## 📞 技术支持

如遇到问题，请按以下步骤排查：

1. **检查日志**: 查看控制台输出和日志文件
2. **数据库状态**: 确认数据库连接和数据初始化
3. **浏览器兼容性**: 使用现代浏览器访问
4. **网络连接**: 确认端口8080未被占用

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

**版本**: v2.0.0  
**最后更新**: 2024年12月  
**开发状态**: 生产就绪 🚀 