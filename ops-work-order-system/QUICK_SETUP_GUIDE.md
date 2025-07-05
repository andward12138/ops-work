# 🚀 运维工单系统快速部署指南

## 📋 适用场景
- 在新机器上快速部署项目
- 本地开发环境搭建
- 使用Cursor进行开发调试

## 🎯 部署流程

### 1️⃣ 准备数据库环境

#### 方法一：使用Navicat图形化操作（推荐）
1. 打开Navicat，连接到你的MySQL服务器
2. 执行 `database/01_create_database_and_tables.sql` 
   - 可以直接打开SQL文件执行
   - 或者复制粘贴到查询窗口执行
3. **可选：** 如果需要测试数据，执行 `database/02_insert_test_data.sql`

#### 方法二：命令行操作
```bash
# 连接到MySQL
mysql -u root -p

# 执行SQL文件
mysql -u root -p < database/01_create_database_and_tables.sql

# 可选：插入测试数据
mysql -u root -p < database/02_insert_test_data.sql
```

#### 执行完成后的验证
```sql
-- 验证数据库创建成功
SHOW DATABASES;

-- 查看表结构
USE ops_work_order_system;
SHOW TABLES;

-- 查看默认管理员账号
SELECT username, role, email FROM users WHERE role = 'ADMIN';
```

### 2️⃣ 从GitHub获取项目

```bash
# 克隆项目
git clone your-github-repo-url
cd ops-work-order-system

# 查看项目结构
ls -la
```

### 3️⃣ 配置Java环境

#### 检查Java版本
```bash
# 检查Java版本（需要Java 21）
java -version

# 如果没有Java 21，请先安装
```

#### Windows系统安装Java 21（如果需要）
1. 访问 [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) 或 [OpenJDK](https://openjdk.org/)
2. 下载Java 21 JDK
3. 安装后配置环境变量

#### macOS系统安装Java 21
```bash
# 使用Homebrew安装
brew install openjdk@21

# 配置环境变量
echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

#### Linux系统安装Java 21
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-21-jdk

# CentOS/RHEL
sudo yum install java-21-openjdk java-21-openjdk-devel
```

### 4️⃣ 配置项目

#### 步骤1：复制配置文件
```bash
# 复制开发环境配置文件
cp application-dev.properties src/main/resources/application-dev.properties
```

#### 步骤2：修改数据库连接信息
打开 `src/main/resources/application-dev.properties` 文件，修改以下内容：

```properties
# 修改数据库连接信息
spring.datasource.url=jdbc:mysql://localhost:3306/ops_work_order_system?useSSL=false&serverTimezone=Asia/Shanghai&createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false&maxReconnects=10

# 修改用户名
spring.datasource.username=你的MySQL用户名

# 修改密码
spring.datasource.password=你的MySQL密码
```

#### 步骤3：测试数据库连接
```bash
# 使用Maven Wrapper编译项目（同时会测试数据库连接）
./mvnw clean compile

# Windows系统使用：
mvnw.cmd clean compile
```

### 5️⃣ 启动项目

#### 方法一：在Cursor中启动（推荐开发调试）

1. 在Cursor中打开项目文件夹
2. 打开终端（Terminal）
3. 运行以下命令：
```bash
# 启动开发环境
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Windows系统：
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

#### 方法二：IDE中启动
1. 在Cursor中打开 `src/main/java/com/example/opsworkordersystem/OpsWorkOrderSystemApplication.java`
2. 配置运行参数：
   - Program arguments: `--spring.profiles.active=dev`
   - VM options: `-Xms512m -Xmx1g`
3. 点击运行

#### 方法三：命令行后台运行
```bash
# 编译打包
./mvnw clean package -DskipTests

# 后台运行
nohup java -jar target/ops-work-order-system-*.jar --spring.profiles.active=dev > application.log 2>&1 &

# 查看日志
tail -f application.log
```

### 6️⃣ 验证部署

#### 访问系统
- 打开浏览器访问：http://localhost:8080
- 默认管理员账号：`admin`
- 默认密码：`admin123`

#### 如果有测试数据，还可以使用：
- 技术部经理：`tech_manager` / `123456`
- 运维部经理：`ops_manager` / `123456`
- 系统管理员：`system_admin` / `123456`

### 7️⃣ 开发调试

#### 在Cursor中调试
1. 在代码中设置断点
2. 使用Debug模式启动应用
3. 访问对应的页面触发断点

#### 实时日志查看
应用启动后，终端会显示实时日志：
```
- SQL查询日志
- 业务逻辑日志
- 错误信息
- 性能统计
```

#### 热重载（开发时修改代码自动重启）
```bash
# 添加spring-boot-devtools依赖后，修改代码会自动重启
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### 8️⃣ 项目开发后续操作

#### 提交代码到GitHub
```bash
# 添加修改的文件
git add .

# 提交代码
git commit -m "描述你的修改"

# 推送到远程仓库
git push origin main
```

#### 版本管理
```bash
# 创建功能分支
git checkout -b feature/new-feature

# 合并分支
git checkout main
git merge feature/new-feature

# 删除功能分支
git branch -d feature/new-feature
```

## 🔧 常见问题解决

### 数据库连接问题
```
错误：Communications link failure
解决：
1. 检查MySQL服务是否启动
2. 确认数据库用户名密码正确
3. 检查防火墙设置
4. 确认MySQL端口（默认3306）未被占用
```

### 端口占用问题
```
错误：Port 8080 was already in use
解决：
1. 修改application-dev.properties中的server.port=8081
2. 或者关闭占用8080端口的其他应用
```

### Maven依赖下载慢
```
解决：配置国内镜像
在~/.m2/settings.xml中添加：
<mirror>
    <id>aliyun</id>
    <name>aliyun maven</name>
    <url>https://maven.aliyun.com/repository/public</url>
    <mirrorOf>central</mirrorOf>
</mirror>
```

### 编码问题
```
如果遇到中文乱码：
1. 确保所有文件都是UTF-8编码
2. 在Cursor中设置：Settings > Editor > File Encodings > UTF-8
3. 重启应用
```

## 📝 系统账号信息

### 默认账号
- **超级管理员**：`admin` / `admin123`

### 测试账号（如果执行了测试数据脚本）
- **技术部经理**：`tech_manager` / `123456`
- **运维部经理**：`ops_manager` / `123456`
- **产品经理**：`product_manager` / `123456`
- **网络管理员**：`network_admin` / `123456`
- **系统管理员**：`system_admin` / `123456`
- **数据库管理员**：`db_admin` / `123456`
- **测试用户**：`test_user` / `123456`

## 🎉 完成！

现在你的运维工单系统已经成功部署并运行！

- **系统地址**：http://localhost:8080
- **开发调试**：在Cursor中直接修改代码并调试
- **版本管理**：使用git进行版本控制
- **数据库管理**：使用Navicat进行数据库操作

如果遇到任何问题，请检查日志文件或联系技术支持。 