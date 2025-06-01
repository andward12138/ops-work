# 运维工单系统 (OPS Work Order System)

## 📋 项目简介

运维工单系统是一个基于Spring Boot开发的企业级运维管理平台，旨在简化和自动化IT运维流程，提高工作效率和服务质量。系统支持工单的全生命周期管理，包括创建、分配、处理、转派、审批和统计分析。

## ✨ 核心功能

### 🎫 工单管理
- **工单创建**：支持多种工单类型创建，包含详细描述、优先级设置
- **工单分配**：自动或手动分配工单给相应负责人
- **状态管理**：完整的工单状态流转（待处理→已批准→处理中→已完成）
- **优先级管理**：高/中/低优先级分类，支持紧急工单处理
- **截止时间**：自动计算或手动设置工单截止时间
- **搜索过滤**：支持按状态、优先级、关键词等多维度筛选

### 🔄 转派管理 ⭐ **最新功能**
- **部门转派**：跨部门工单转派，支持专业化分工
- **用户转派**：精确转派给特定技术专家
- **协助请求**：请求其他人员协助处理，保持原负责人
- **转派历史**：完整的转派记录追踪和审计
- **权限控制**：基于角色的转派权限管理
- **实时通知**：转派状态变更实时通知相关人员

### 👥 用户权限管理
- **角色管理**：管理员、经理、操作员等多级权限
- **部门管理**：组织架构管理，支持跨部门协作
- **用户管理**：完整的用户生命周期管理
- **权限控制**：细粒度的功能权限控制

### 📊 统计报表
- **工单统计**：按状态、部门、时间等维度统计分析
- **转派统计**：转派效率和成功率分析
- **性能分析**：响应时间、解决时间等KPI指标
- **数据导出**：支持Excel格式数据导出

### 🔧 工作流管理
- **流程定义**：自定义工单处理流程
- **状态流转**：可配置的状态转换规则
- **自动化**：基于条件的自动化处理规则

## 🏗️ 技术架构

### 后端技术栈
- **Spring Boot 3.x** - 主框架
- **Spring Security** - 安全认证
- **Spring Data JPA** - 数据访问层
- **MySQL 8.0** - 关系型数据库
- **Flyway** - 数据库版本管理
- **Maven** - 项目构建管理

### 前端技术栈
- **Thymeleaf** - 模板引擎
- **Bootstrap 5** - UI框架
- **Bootstrap Icons** - 图标库
- **jQuery** - JavaScript库
- **AJAX** - 异步数据加载

### 架构特点
- **MVC架构**：清晰的分层架构设计
- **RESTful API**：标准的API设计规范
- **响应式设计**：支持多设备访问
- **模块化设计**：高内聚低耦合的模块结构

## ⚡ 性能优化

### 数据库优化
- **索引优化**：15+个针对性复合索引，覆盖所有常用查询场景
- **查询优化**：使用JOIN FETCH避免N+1查询问题
- **分页查询**：大数据量场景下的分页处理
- **批量操作**：减少数据库交互次数

### 应用层优化
- **异步处理**：使用CompletableFuture并行处理
- **懒加载**：按需加载数据，减少内存占用
- **缓存策略**：静态数据缓存，提高响应速度
- **连接池**：数据库连接池优化

### 前端优化
- **AJAX懒加载**：动态加载用户和部门数据
- **前端缓存**：减少重复API调用
- **响应式UI**：优化用户体验
- **资源压缩**：CSS/JS资源优化

## 📊 数据库设计

### 核心表结构
```sql
-- 工单表
work_orders
├── id (主键)
├── title (标题)
├── description (描述)
├── status (状态)
├── priority (优先级)
├── created_by_id (创建人)
├── assigned_to_id (分配给)
├── created_at (创建时间)
├── deadline (截止时间)
└── updated_at (更新时间)

-- 转派记录表 ⭐ 新增
transfer_records
├── id (主键)
├── work_order_id (工单ID)
├── transfer_type (转派类型)
├── from_user_id (源用户)
├── to_user_id (目标用户)
├── from_department_id (源部门)
├── to_department_id (目标部门)
├── requested_by_id (申请人)
├── accepted_by_id (接受人)
├── status (转派状态)
├── transfer_reason (转派原因)
├── is_assistance (是否协助)
├── requested_at (申请时间)
├── accepted_at (接受时间)
└── completed_at (完成时间)

-- 用户表
users
├── id (主键)
├── username (用户名)
├── password (密码)
├── email (邮箱)
├── role (角色)
├── department_id (部门ID)
└── created_at (创建时间)

-- 部门表
departments
├── id (主键)
├── name (部门名称)
├── description (描述)
└── created_at (创建时间)
```

### 索引优化策略
```sql
-- 转派记录核心索引
idx_transfer_records_status_to_user_created_at  -- 用户待处理查询
idx_transfer_records_status_to_dept_created_at  -- 部门待处理查询
idx_transfer_records_work_order_created_at      -- 工单历史查询
idx_transfer_records_requested_by_created_at    -- 用户发起查询

-- 工单表核心索引
idx_work_orders_status              -- 状态查询
idx_work_orders_assigned_to         -- 分配人查询
idx_work_orders_created_at          -- 时间排序
```

## 🚀 快速开始

### 环境要求
- Java 17+
- MySQL 8.0+
- Maven 3.8+

### 安装步骤

1. **克隆项目**
```bash
git clone [项目地址]
cd ops-work-order-system
```

2. **配置数据库**
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE ops_work_order_system;
```

3. **修改配置文件**
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ops_work_order_system
    username: your_username
    password: your_password
```

4. **运行应用**
```bash
mvn clean compile
mvn spring-boot:run
```

5. **访问系统**
- 地址：http://localhost:8080
- 默认账号：admin/admin

## 👤 默认用户

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin | ADMIN | 系统管理员 |
| manager | password | MANAGER | 部门经理 |
| operator | password | OPERATOR | 操作员 |

## 📱 功能截图

### 工单管理
- 工单列表：支持多维度筛选和搜索
- 工单详情：完整的工单信息展示
- 工单创建：直观的工单创建流程

### 转派管理 ⭐
- 转派概览：统计数据和待处理列表
- 转派创建：分步骤的转派创建流程
- 转派历史：完整的转派记录追踪

### 统计报表
- 数据大屏：实时运维数据展示
- 详细报表：多维度数据分析

## 🔧 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ops_work_order_system?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
```

### 安全配置
```yaml
# 密码加密策略
security:
  password:
    encoder: bcrypt
    strength: 12

# 会话配置  
server:
  servlet:
    session:
      timeout: 30m
```

## 📈 性能监控

### 关键指标
- **查询响应时间**：平均 < 100ms
- **工单处理效率**：转派成功率 > 95%
- **系统可用性**：99.9%+
- **并发处理能力**：支持100+并发用户

### 监控建议
- 定期检查数据库索引使用情况
- 监控转派处理时间和成功率
- 关注系统资源使用情况

## 🚧 开发计划

### 近期计划
- [ ] 工单模板功能
- [ ] 移动端适配
- [ ] 邮件通知集成
- [ ] API文档完善

### 长期规划
- [ ] 微服务架构升级
- [ ] 容器化部署支持
- [ ] 多租户支持
- [ ] 高级工作流引擎

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交代码变更
4. 推送到分支
5. 创建 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- 项目地址：[GitHub Repository]
- 问题反馈：[GitHub Issues]
- 邮箱：[your-email@example.com]

---

⭐ **如果这个项目对您有帮助，请给我们一个星标！** 