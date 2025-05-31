# 运维工单管理系统 (OPS Work Order System)

## 项目概述

运维工单管理系统是一个基于Spring Boot的企业级运维管理平台，提供工单创建、多级审批、任务分配、进度跟踪等功能。系统实现了灵活的工作流引擎，支持自定义审批流程和自动化任务流转。

## 主要功能特性

### 1. 用户与权限管理
- **角色划分**：管理员(ADMIN)、经理(MANAGER)、操作员(OPERATOR)、普通用户(USER)
- **部门管理**：支持多层级部门结构（总部、部门、小组）
- **认证授权**：基于Spring Security + JWT的安全认证

### 2. 工单管理
- **工单创建**：支持多种工单类型（运维工单、紧急变更、例行维护等）
- **优先级管理**：高(HIGH)、中(MEDIUM)、低(LOW)优先级
- **状态跟踪**：待处理、进行中、已完成、已拒绝、已超时等状态
- **超时监控**：自动检测超时工单并更新状态

### 3. 多层级审批工作流 ✨
- **可配置流程**：通过工作流模板定义审批流程
- **并行/串行审批**：支持多人并行审批和顺序审批
- **自动流转**：完成当前步骤后自动流转到下一步
- **超时自动处理**：支持配置超时自动通过
- **灵活分配**：基于角色和部门的任务自动分配

### 4. 操作记录与审计
- **审批记录**：记录所有审批操作和意见
- **操作日志**：追踪工单的所有状态变更
- **时间记录**：记录每个步骤的开始和完成时间

## 技术架构

### 后端技术栈
- **框架**：Spring Boot 3.4.5
- **安全**：Spring Security + JWT
- **持久层**：Spring Data JPA + Hibernate
- **数据库**：MySQL 8.0
- **构建工具**：Maven
- **Java版本**：Java 21

### 主要依赖
- Spring Boot DevTools（热部署）
- MySQL Connector
- Jackson（JSON处理）
- Spring Boot Actuator（监控）

## 快速开始

### 环境要求
- JDK 21+
- Maven 3.6+
- MySQL 8.0+

### 安装步骤

1. **克隆项目**
```bash
git clone [repository-url]
cd ops
```

2. **创建数据库**
```sql
CREATE DATABASE ops_work_order_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **导入数据库结构**
```bash
mysql -u root -p ops_work_order_system < ops_work_order_system.sql
```

4. **配置数据库连接**
编辑 `ops-work-order-system/src/main/resources/application.properties`：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ops_work_order_system
spring.datasource.username=your_username
spring.datasource.password=your_password
```

5. **启动应用**
```bash
# Windows
startup.bat

# 或直接运行
cd ops-work-order-system
mvn spring-boot:run
```

应用将在 http://localhost:8080 启动

### 测试账号
| 用户名 | 密码 | 角色 | 部门 |
|--------|------|------|------|
| admin | admin123 | 管理员 | 技术部 |
| manager | manager123 | 经理 | 运维部 |
| operator | operator123 | 操作员 | 运维部 |

## API文档

### 认证相关
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 用户登出

### 工单管理
- `GET /api/workorders` - 获取工单列表
- `POST /api/workorders` - 创建工单
- `GET /api/workorders/{id}` - 获取工单详情
- `PUT /api/workorders/{id}/status` - 更新工单状态

### 工作流模板
- `GET /api/workflow-templates` - 获取所有模板
- `POST /api/workflow-templates` - 创建模板
- `GET /api/workflow-templates/by-type/{type}` - 根据类型获取模板

### 工作流步骤
- `GET /api/workflow-steps/pending/user/{userId}` - 获取用户待办
- `GET /api/workflow-steps/pending/department/{deptId}` - 获取部门待办
- `POST /api/workflow-steps/{stepId}/approve` - 审批通过
- `POST /api/workflow-steps/{stepId}/reject` - 审批拒绝

### 用户管理
- `GET /api/users` - 获取用户列表
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户信息

## 工作流配置示例

### 标准运维工单流程
```
1. 部门初审 (DEPARTMENT_REVIEW) - 经理角色 - 24小时时限
2. 经理审批 (MANAGER_APPROVAL) - 经理角色 - 48小时时限  
3. 执行操作 (EXECUTION) - 操作员角色 - 72小时时限
4. 完成确认 (COMPLETION) - 用户角色 - 24小时时限
```

### 并行审批流程示例
```
1. 技术部审核 + 运维部审核 (并行) - 24小时时限
2. 主管审批 - 48小时时限
3. 执行操作 - 72小时时限
```

## 数据库设计

### 核心表结构
- **users** - 用户表
- **departments** - 部门表
- **work_orders** - 工单表
- **workflow_templates** - 工作流模板表
- **workflow_template_steps** - 模板步骤表
- **workflow_steps** - 工作流实例步骤表
- **approval_records** - 审批记录表
- **operation_records** - 操作记录表

## 定时任务

系统包含以下定时任务：
- **工单超时检查**：每30分钟检查一次超时工单
- **工作流步骤超时处理**：每小时检查超时的审批步骤

## 开发指南

### 项目结构
```
ops-work-order-system/
├── src/main/java/com/example/opsworkordersystem/
│   ├── controller/     # REST控制器
│   ├── service/        # 业务逻辑层
│   ├── repository/     # 数据访问层
│   ├── entity/         # 实体类
│   ├── dto/           # 数据传输对象
│   ├── config/        # 配置类
│   ├── security/      # 安全相关
│   └── scheduler/     # 定时任务
├── src/main/resources/
│   └── application.properties  # 应用配置
└── pom.xml            # Maven配置
```

### 扩展工作流

1. **添加新的工单类型**：
   - 在数据库中插入新的workflow_template记录
   - 定义对应的workflow_template_steps

2. **自定义审批逻辑**：
   - 扩展WorkflowService中的处理逻辑
   - 实现特定的业务规则

## 注意事项

1. **数据库初始化**：首次运行时会自动创建基础部门和用户数据
2. **工作流配置**：创建工单前需要先配置对应类型的工作流模板
3. **权限控制**：确保用户具有相应的角色和部门权限
4. **超时处理**：合理设置步骤的时限，避免流程阻塞

## 后续优化计划

- [ ] 添加工单统计分析功能
- [ ] 实现工作流可视化设计器
- [ ] 集成消息通知（邮件、短信、钉钉等）
- [ ] 添加工单模板功能
- [ ] 实现更复杂的审批条件（如金额限制、时间限制等）
- [ ] 添加移动端支持

## 许可证

[添加许可证信息]

## 联系方式

[添加联系方式]

---
最后更新：2024-05-31
