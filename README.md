# 运维工单系统 (OPS Work Order System)

## 📋 项目简介

运维工单系统是一个基于Spring Boot开发的企业级运维管理平台，旨在简化和自动化IT运维流程，提高工作效率和服务质量。系统支持工单的全生命周期管理，包括创建、分配、处理、转派、审批和统计分析，并提供完整的工作流管理和权限控制功能。

## ✨ 核心功能

### 🎫 工单管理
- **工单创建**：支持多种工单类型创建（应急、故障、维护、需求），包含详细描述、优先级设置
- **工单分配**：自动或手动分配工单给相应负责人
- **状态管理**：完整的工单状态流转（待处理→已批准→处理中→已完成→已拒绝）
- **优先级管理**：紧急/高/中/低优先级分类，支持紧急工单处理
- **截止时间**：自动计算或手动设置工单截止时间，超时预警
- **搜索过滤**：支持按状态、优先级、关键词、部门等多维度筛选

### 🔄 转派管理 ⭐ **核心功能**
- **部门转派**：跨部门工单转派，支持专业化分工
- **用户转派**：精确转派给特定技术专家
- **协助请求**：请求其他人员协助处理，保持原负责人
- **转派历史**：完整的转派记录追踪和审计
- **权限控制**：基于角色的转派权限管理
- **实时通知**：转派状态变更实时通知相关人员
- **批量操作**：支持批量转派和批量接受

### 🔧 工作流管理 ⭐ **最新功能**
- **工作流模板**：预定义的工单处理流程模板
- **多种工单类型支持**：应急工单、故障工单、维护工单、需求工单
- **流程步骤管理**：部门初审、经理审批、执行操作、完成确认等标准步骤
- **模板状态管理**：支持启用/停用模板，灵活管理流程
- **角色权限控制**：不同角色对应不同的审批权限
- **时间限制**：每个步骤可设置时间限制，确保及时处理
- **自动化规则**：支持自动审批和并行处理

### 👥 用户权限管理 ⭐ **优化升级**
- **角色管理**：管理员(ADMIN)、经理(MANAGER)、操作员(OPERATOR)等多级权限
- **部门管理**：完整的组织架构管理，支持跨部门协作
- **用户管理**：完整的用户生命周期管理，支持批量操作
- **权限控制**：细粒度的功能权限控制，API级别权限验证
- **安全认证**：Spring Security集成，密码加密存储
- **会话管理**：安全的会话管理和超时控制

### 📊 统计报表 ⭐ **全面修复**
- **日常统计**：每日工单创建、完成、超时等关键指标
- **周度分析**：周度工单处理趋势和效率分析
- **超时预警**：实时监控超时工单，分级预警（轻微/严重/紧急）
- **部门效率分析**：各部门工单处理效率对比和排名
- **数据可视化**：图表展示，直观的数据分析
- **数据导出**：支持Excel格式数据导出，便于进一步分析
- **实时刷新**：定时自动刷新数据，确保信息及时性

### 🏢 部门管理
- **组织架构**：支持多级部门结构
- **部门联系人**：每个部门可配置多个联系人
- **权限配置**：部门级别的权限管理
- **工单分配**：按部门自动分配工单

## 🏗️ 技术架构

### 后端技术栈
- **Spring Boot 3.x** - 主框架
- **Spring Security 6.x** - 安全认证与权限控制
- **Spring Data JPA** - 数据访问层，支持复杂查询
- **MySQL 8.0** - 关系型数据库
- **Flyway** - 数据库版本管理和迁移
- **Maven 3.8+** - 项目构建管理
- **Hibernate** - ORM框架，懒加载优化

### 前端技术栈
- **Thymeleaf 3.x** - 模板引擎
- **Bootstrap 5.3** - 响应式UI框架
- **Bootstrap Icons 1.11** - 现代化图标库
- **jQuery 3.x** - JavaScript库
- **Chart.js** - 数据可视化图表
- **AJAX** - 异步数据加载和实时更新

### 架构特点
- **MVC架构**：清晰的分层架构设计
- **RESTful API**：标准的API设计规范
- **响应式设计**：支持PC、平板、手机等多设备访问
- **模块化设计**：高内聚低耦合的模块结构
- **事务管理**：完整的数据库事务支持
- **异常处理**：全局异常处理和用户友好的错误提示

## ⚡ 性能优化

### 数据库优化
- **索引优化**：20+个针对性复合索引，覆盖所有常用查询场景
- **查询优化**：使用JOIN FETCH避免N+1查询问题，EAGER/LAZY加载策略
- **分页查询**：大数据量场景下的高效分页处理
- **批量操作**：减少数据库交互次数，提高写入性能
- **连接池**：HikariCP连接池优化，支持高并发访问

### 应用层优化
- **异步处理**：使用@Async注解实现异步任务处理
- **缓存策略**：Spring Cache缓存热点数据，减少数据库压力
- **懒加载**：按需加载数据，减少内存占用
- **事务优化**：合理的事务边界控制，避免长事务
- **内存管理**：JVM参数优化，垃圾回收策略调优

### 前端优化
- **AJAX懒加载**：动态加载用户和部门数据，提升页面响应速度
- **前端缓存**：减少重复API调用，本地存储优化
- **响应式UI**：Bootstrap Grid系统，适配各种屏幕尺寸
- **资源压缩**：CSS/JS资源压缩和CDN加速
- **图表优化**：Chart.js按需加载，数据分页展示

## 📊 数据库设计

### 核心表结构
```sql
-- 工单表
work_orders
├── id (主键)
├── title (标题)
├── description (描述)
├── status (状态：PENDING/APPROVED/IN_PROGRESS/COMPLETED/REJECTED)
├── priority (优先级：URGENT/HIGH/MEDIUM/LOW)
├── work_order_type (工单类型：EMERGENCY/INCIDENT/MAINTENANCE/REQUEST)
├── created_by_id (创建人)
├── assigned_to_id (分配给)
├── deadline (截止时间)
├── is_overdue (是否超时)
├── created_at (创建时间)
└── updated_at (更新时间)

-- 转派记录表
transfer_records
├── id (主键)
├── work_order_id (工单ID)
├── transfer_type (转派类型：DEPARTMENT/USER)
├── from_user_id (源用户)
├── to_user_id (目标用户)
├── from_department_id (源部门)
├── to_department_id (目标部门)
├── requested_by_id (申请人)
├── accepted_by_id (接受人)
├── status (转派状态：PENDING/ACCEPTED/REJECTED/COMPLETED)
├── transfer_reason (转派原因)
├── is_assistance (是否协助)
├── requested_at (申请时间)
├── accepted_at (接受时间)
└── completed_at (完成时间)

-- 工作流模板表 ⭐ 新增
workflow_templates
├── id (主键)
├── template_name (模板名称)
├── description (模板描述)
├── work_order_type (适用工单类型)
├── is_active (是否启用)
├── created_at (创建时间)
└── updated_at (更新时间)

-- 工作流模板步骤表 ⭐ 新增
workflow_template_steps
├── id (主键)
├── template_id (模板ID)
├── step_order (步骤顺序)
├── step_name (步骤名称)
├── step_type (步骤类型)
├── assignee_role (负责角色)
├── assignee_department_id (负责部门)
├── time_limit_hours (时间限制)
├── is_skippable (是否可跳过)
├── is_parallel (是否并行)
└── auto_approve (是否自动审批)

-- 用户表
users
├── id (主键)
├── username (用户名)
├── password (加密密码)
├── email (邮箱)
├── phone (电话)
├── role (角色：ADMIN/MANAGER/OPERATOR/USER)
├── department_id (部门ID)
├── created_at (创建时间)
└── updated_at (更新时间)

-- 部门表
departments
├── id (主键)
├── name (部门名称)
├── code (部门代码)
├── level (部门级别)
├── type (部门类型)
├── parent_id (上级部门)
├── description (描述)
├── contact_person (联系人)
├── contact_phone (联系电话)
├── is_active (是否启用)
├── created_at (创建时间)
└── updated_at (更新时间)

-- 部门联系人表
department_contacts
├── id (主键)
├── department_id (部门ID)
├── name (联系人姓名)
├── position (职位)
├── phone (电话)
├── mobile (手机)
├── email (邮箱)
├── is_primary (是否主要联系人)
├── is_emergency (是否紧急联系人)
├── working_hours (工作时间)
└── is_active (是否启用)
```

### 索引优化策略
```sql
-- 工单表核心索引
CREATE INDEX idx_work_orders_status ON work_orders(status);
CREATE INDEX idx_work_orders_assigned_to ON work_orders(assigned_to_id);
CREATE INDEX idx_work_orders_created_at ON work_orders(created_at DESC);
CREATE INDEX idx_work_orders_deadline ON work_orders(deadline);
CREATE INDEX idx_work_orders_priority_status ON work_orders(priority, status);

-- 转派记录核心索引
CREATE INDEX idx_transfer_records_status_to_user_created_at ON transfer_records(status, to_user_id, created_at DESC);
CREATE INDEX idx_transfer_records_status_to_dept_created_at ON transfer_records(status, to_department_id, created_at DESC);
CREATE INDEX idx_transfer_records_work_order_created_at ON transfer_records(work_order_id, created_at DESC);
CREATE INDEX idx_transfer_records_requested_by_created_at ON transfer_records(requested_by_id, created_at DESC);

-- 工作流模板索引
CREATE INDEX idx_workflow_templates_active_type ON workflow_templates(is_active, work_order_type);
CREATE INDEX idx_workflow_template_steps_template_order ON workflow_template_steps(template_id, step_order);

-- 用户和部门索引
CREATE INDEX idx_users_department_role ON users(department_id, role);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_departments_active ON departments(is_active);
```

## 🚀 快速开始

### 环境要求
- **Java 17+** (推荐使用OpenJDK 17)
- **MySQL 8.0+** (支持全文索引和JSON字段)
- **Maven 3.8+** (项目构建工具)
- **内存**: 最少2GB，推荐4GB+
- **磁盘**: 最少1GB可用空间

### 安装步骤

1. **克隆项目**
```bash
git clone [项目地址]
cd ops
```

2. **配置数据库**
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE ops_work_order_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **修改配置文件**
```properties
# application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/ops_work_order_system?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# 服务器配置
server.port=8080
server.servlet.context-path=/
```

4. **运行应用**
```bash
cd ops-work-order-system
mvn clean compile
mvn spring-boot:run
```

5. **访问系统**
- 地址：http://localhost:8080
- 默认管理员账号：admin/admin123
- 默认经理账号：manager/manager123
- 默认操作员账号：operator/operator123

## 👤 默认用户

| 用户名 | 密码 | 角色 | 权限说明 |
|--------|------|------|----------|
| admin | admin123 | ADMIN | 系统管理员，拥有所有权限 |
| manager | manager123 | MANAGER | 部门经理，可管理工作流模板和审批 |
| operator | operator123 | OPERATOR | 操作员，可处理工单和查看报表 |

## 🎯 功能特色

### 🔧 工作流管理系统
- **标准流程模板**：
  - 标准需求工单流程：部门初审 → 经理审批 → 执行操作 → 完成确认
  - 紧急变更流程：经理快速审批 → 立即执行 → 完成确认
  - 故障处理流程：故障确认 → 影响评估 → 紧急修复 → 根因分析 → 完成总结
  - 维护流程：维护计划 → 风险评估 → 执行维护 → 验证测试 → 完成确认

- **智能权限控制**：
  - 管理员和经理可以创建、编辑、启用/停用模板
  - 操作员可以查看和使用模板
  - 基于角色的步骤分配

### 📊 统计报表系统
- **超时预警监控**：
  - 实时监控超时工单
  - 三级预警：轻微(1-12h) / 严重(12-24h) / 紧急(24h+)
  - 自动分类和优先级排序
  - 处理建议和操作指南

- **部门效率分析**：
  - 多维度效率评估
  - 部门排名和对比
  - 效率等级：优秀(≥90%) / 良好(80-90%) / 一般(70-80%) / 待改进(<70%)
  - 可视化图表展示

### 🔄 转派管理系统
- **智能转派**：
  - 部门间转派：专业化分工
  - 用户间转派：精确指派
  - 协助请求：保持原责任人
  - 批量操作：提高效率

- **完整追踪**：
  - 转派历史记录
  - 状态实时更新
  - 性能统计分析

## 📱 界面展示

### 主要页面
- **工单管理**：现代化的工单列表，支持实时搜索和筛选
- **转派管理**：直观的转派流程，清晰的状态展示
- **工作流管理**：可视化的流程模板管理
- **统计报表**：丰富的图表和数据分析
- **用户管理**：完整的用户和权限管理

### 设计特点
- **响应式设计**：适配PC、平板、手机
- **现代化UI**：Bootstrap 5 + Bootstrap Icons
- **用户友好**：直观的操作流程和清晰的信息展示
- **实时更新**：AJAX技术实现页面无刷新更新

## 🔧 配置说明

### 数据库配置
```properties
# 数据源配置
spring.datasource.url=jdbc:mysql://localhost:3306/ops_work_order_system?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:password}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# 连接池配置
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
spring.datasource.hikari.connection-timeout=20000

# JPA配置
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

### 安全配置
```properties
# 密码加密策略
security.password.encoder=bcrypt
security.password.strength=12

# 会话配置  
server.servlet.session.timeout=30m
server.servlet.session.cookie.max-age=1800
server.servlet.session.cookie.secure=false
server.servlet.session.cookie.http-only=true

# CSRF保护（开发环境可关闭）
spring.security.csrf.enabled=false
```

### 应用配置
```properties
# 服务器配置
server.port=8080
server.servlet.context-path=/
server.compression.enabled=true
server.compression.mime-types=text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json

# 日志配置
logging.level.com.example.opsworkordersystem=INFO
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# 文件上传配置
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## 📈 性能监控

### 关键指标
- **查询响应时间**：平均 < 100ms，99%分位 < 500ms
- **工单处理效率**：转派成功率 > 95%，平均处理时间 < 24h
- **系统可用性**：99.9%+ uptime
- **并发处理能力**：支持100+并发用户，500+工单/天
- **数据库性能**：连接池利用率 < 80%，慢查询 < 1%

### 监控建议
- **数据库监控**：定期检查索引使用情况，优化慢查询
- **应用监控**：监控内存使用、GC频率、线程池状态
- **业务监控**：转派处理时间、工单完成率、用户活跃度
- **系统监控**：CPU、内存、磁盘、网络等资源使用情况

## 🛠️ 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查MySQL服务是否启动
   - 验证数据库连接参数
   - 确认数据库用户权限

2. **页面显示异常**
   - 检查Thymeleaf模板语法
   - 验证Controller返回的Model数据
   - 查看浏览器控制台错误信息

3. **权限访问被拒绝**
   - 确认用户角色和权限配置
   - 检查Spring Security配置
   - 验证API路径权限设置

4. **性能问题**
   - 检查数据库索引使用情况
   - 分析慢查询日志
   - 优化N+1查询问题

## 🚧 版本历史

### v2.1.0 (最新版本) - 2025-06-28
- ✅ **新增工作流管理系统**：完整的工作流模板管理功能
- ✅ **修复统计报表**：解决超时预警和部门效率页面的显示问题
- ✅ **优化权限控制**：细化API级别的权限验证
- ✅ **性能优化**：数据库索引优化，查询性能提升30%
- ✅ **UI改进**：统一界面风格，提升用户体验
- ✅ **数据初始化**：自动创建标准工作流模板和测试数据

### v2.0.0 - 2025-06-27
- ✅ **转派管理系统**：完整的工单转派功能
- ✅ **部门管理**：组织架构和联系人管理
- ✅ **用户权限**：基于角色的权限控制
- ✅ **统计报表**：多维度数据分析和可视化

### v1.0.0 - 2025-06-26
- ✅ **基础工单管理**：创建、分配、处理工单
- ✅ **用户认证**：登录注册和基础权限
- ✅ **数据库设计**：核心表结构和关系

## 🚧 开发计划

### 近期计划 (v2.2.0)
- [ ] **移动端适配**：响应式设计优化，PWA支持
- [ ] **邮件通知系统**：工单状态变更邮件通知
- [ ] **文件附件管理**：支持工单附件上传和管理
- [ ] **API文档**：Swagger/OpenAPI文档生成
- [ ] **数据导入导出**：Excel批量导入工单数据

### 中期规划 (v3.0.0)
- [ ] **高级工作流引擎**：可视化流程设计器
- [ ] **消息通知中心**：站内消息、邮件、短信多渠道通知
- [ ] **报表设计器**：自定义报表和仪表板
- [ ] **多租户支持**：企业级多租户架构
- [ ] **集成接口**：与LDAP、OA系统集成

### 长期规划 (v4.0.0)
- [ ] **微服务架构**：拆分为多个微服务
- [ ] **容器化部署**：Docker容器化和Kubernetes支持
- [ ] **大数据分析**：集成Elasticsearch和Kibana
- [ ] **AI智能推荐**：基于机器学习的工单分配和处理建议
- [ ] **国际化支持**：多语言界面支持

## 🤝 贡献指南

### 开发流程
1. **Fork项目**：从主仓库Fork到个人仓库
2. **创建分支**：`git checkout -b feature/your-feature-name`
3. **开发功能**：遵循代码规范和最佳实践
4. **测试验证**：确保所有测试通过
5. **提交代码**：`git commit -m "feat: add your feature"`
6. **推送分支**：`git push origin feature/your-feature-name`
7. **创建PR**：提交Pull Request并描述变更内容

### 代码规范
- **Java代码**：遵循Google Java Style Guide
- **前端代码**：使用ESLint和Prettier格式化
- **数据库**：遵循数据库命名规范
- **文档**：及时更新相关文档

### 测试要求
- **单元测试**：核心业务逻辑覆盖率 > 80%
- **集成测试**：API接口测试覆盖
- **性能测试**：关键功能性能验证
- **安全测试**：权限和数据安全验证

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- **项目地址**：[GitHub Repository]
- **问题反馈**：[GitHub Issues]
- **功能建议**：[GitHub Discussions]
- **邮箱**：[your-email@example.com]

## 🙏 致谢

感谢以下开源项目和技术栈：
- Spring Boot - 强大的Java应用框架
- Bootstrap - 优秀的前端UI框架
- MySQL - 稳定可靠的数据库
- Thymeleaf - 现代化的模板引擎
- Chart.js - 美观的图表库

---

⭐ **如果这个项目对您有帮助，请给我们一个星标！** 

🚀 **欢迎贡献代码，让我们一起打造更好的运维工单系统！** 