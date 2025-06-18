# 运维工单系统 - Android App 实施指南

## 🎯 项目概述

本指南将帮助您将现有的运维工单网站系统改造成兼容可通信的Android App项目。我们采用了**WebView封装方案**，这是最快速且成本最低的实现方式。

## 📱 方案特点

### ✅ 优势
- **快速开发**：1-2周即可完成
- **代码复用**：现有网站功能直接可用
- **维护简单**：只需维护一套后端代码
- **功能完整**：支持推送通知、文件上传、数据同步

### 🔧 技术栈
- **后端**：Spring Boot 3.x + MySQL 8.0
- **Android**：Kotlin + WebView + FCM推送
- **通信**：REST API + WebSocket（可选）

## 🚀 部署步骤

### 第一步：后端配置更新

#### 1.1 更新Maven依赖
您的`pom.xml`已经添加了推送通知相关依赖：

```xml
<!-- Firebase Admin SDK for FCM push notifications -->
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>9.2.0</version>
</dependency>

<!-- HTTP client for push notifications -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

#### 1.2 数据库迁移
运行新的数据库迁移脚本：

```bash
# 启动应用，Flyway会自动执行V7__Create_device_tokens_table.sql
mvn spring-boot:run
```

数据库将创建新的`device_tokens`表用于存储移动设备令牌。

#### 1.3 配置文件更新
在`application.properties`中添加移动端支持配置：

```properties
# 移动端支持配置
app.mobile.enabled=true
app.mobile.base-url=http://your-server-ip:8080
app.push.enabled=true
app.push.max-tokens-per-user=5
app.push.token-expiry-days=30

# CORS配置（允许移动端访问）
spring.web.cors.allowed-origins=*
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
```

### 第二步：Android项目配置

#### 2.1 创建Android Studio项目
```bash
# 在您的工作目录中创建Android项目
mkdir android-app
cd android-app

# 使用Android Studio创建新项目，或使用提供的项目结构
```

#### 2.2 修改服务器URL
在`MainActivity.kt`中更新您的服务器地址：

```kotlin
companion object {
    // 修改为您的实际服务器地址
    private const val BASE_URL = "http://192.168.1.100:8080/"  // 替换为您的服务器IP
    private const val LOGIN_URL = "${BASE_URL}login"
}
```

#### 2.3 网络安全配置
在`res/xml/network_security_config.xml`中添加网络配置：

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">your-server-domain.com</domain>
        <domain includeSubdomains="true">192.168.1.100</domain> <!-- 您的服务器IP -->
    </domain-config>
</network-security-config>
```

### 第三步：Firebase推送配置（可选）

#### 3.1 创建Firebase项目
1. 访问 [Firebase Console](https://console.firebase.google.com/)
2. 创建新项目：`ops-work-order-system`
3. 添加Android应用，包名：`com.example.opsworkorder`

#### 3.2 下载配置文件
1. 下载`google-services.json`
2. 放置到`android-app/app/`目录下

#### 3.3 配置服务账号密钥
1. 在Firebase控制台生成服务账号密钥
2. 下载JSON文件，放置到后端`src/main/resources/`目录
3. 在`application.properties`中配置：

```properties
# Firebase配置
app.firebase.service-account-key=classpath:firebase-service-account.json
app.firebase.database-url=https://your-project.firebaseio.com
```

### 第四步：测试部署

#### 4.1 启动后端服务
```bash
cd ops-work-order-system
mvn clean compile
mvn spring-boot:run
```

验证服务启动：http://localhost:8080

#### 4.2 构建Android应用
```bash
cd android-app
./gradlew assembleDebug
```

#### 4.3 安装测试
```bash
# 连接Android设备或启动模拟器
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 📊 功能验证清单

### ✅ 基础功能测试
- [ ] 应用启动并显示网站首页
- [ ] 用户登录功能正常
- [ ] 工单列表显示正确
- [ ] 下拉刷新工作正常
- [ ] 页面导航功能正常

### ✅ 移动端优化测试
- [ ] 页面在手机上显示正确
- [ ] 按钮大小适合手指操作
- [ ] 文字大小清晰可读
- [ ] 表格数据移动端友好

### ✅ 推送通知测试
- [ ] 设备令牌注册成功
- [ ] 能接收测试推送通知
- [ ] 工单状态变更推送正常
- [ ] 点击通知跳转到对应页面

### ✅ 网络功能测试
- [ ] 在WiFi环境下正常工作
- [ ] 在移动网络下正常工作
- [ ] 网络中断后能正确提示
- [ ] 网络恢复后能自动重连

## 🔧 高级配置

### 网站移动端优化
在您的网站HTML模板中添加移动端优化标签：

```html
<!-- 在<head>中添加 -->
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-capable" content="yes">

<!-- 检测移动端访问的JavaScript -->
<script>
function isMobileApp() {
    return navigator.userAgent.includes('OpsWorkOrderApp');
}

if (isMobileApp()) {
    // 隐藏桌面版特有元素
    document.body.classList.add('mobile-app');
    
    // 与Android原生代码通信
    if (typeof AndroidInterface !== 'undefined') {
        AndroidInterface.showToast('欢迎使用移动App！');
    }
}
</script>

<!-- 移动端优化CSS -->
<style>
.mobile-app .d-none-mobile {
    display: none !important;
}

.mobile-app .btn {
    min-height: 44px !important;
    padding: 8px 12px !important;
}

.mobile-app .form-control {
    min-height: 44px !important;
    font-size: 16px !important;
}
</style>
```

### 推送通知集成到工单流程
在您的WorkOrderService中添加推送调用：

```java
@Service
public class WorkOrderService {
    
    @Autowired
    private PushNotificationService pushNotificationService;
    
    public WorkOrder assignWorkOrder(Integer workOrderId, Integer assignedToId) {
        WorkOrder workOrder = // ... 分配逻辑
        
        // 发送推送通知
        pushNotificationService.sendWorkOrderNotification(
            assignedToId, workOrder, "assignment"
        );
        
        return workOrder;
    }
    
    public WorkOrder updateWorkOrderStatus(Integer workOrderId, Status newStatus) {
        WorkOrder workOrder = // ... 状态更新逻辑
        
        // 发送状态更新通知
        if (workOrder.getAssignedTo() != null) {
            pushNotificationService.sendWorkOrderNotification(
                workOrder.getAssignedTo().getId(), workOrder, "status_change"
            );
        }
        
        return workOrder;
    }
}
```

## 🚀 进阶功能实现

### 离线缓存支持
在WebView中启用缓存：

```kotlin
// 在MainActivity.kt的setupWebView()方法中添加
webView.settings.apply {
    // 启用缓存
    cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
    setAppCacheEnabled(true)
    setAppCachePath(filesDir.path + "/webcache")
    setAppCacheMaxSize(50 * 1024 * 1024) // 50MB
}
```

### 文件下载支持
```kotlin
webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
    val request = DownloadManager.Request(Uri.parse(url))
    request.setMimeType(mimetype)
    request.addRequestHeader("User-Agent", userAgent)
    request.setDescription("正在下载...")
    request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype))
    request.allowScanningByMediaScanner()
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, 
        URLUtil.guessFileName(url, contentDisposition, mimetype))
    
    val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
    dm.enqueue(request)
    
    Toast.makeText(this, "开始下载...", Toast.LENGTH_LONG).show()
}
```

## 🔒 安全配置

### 网络安全
```kotlin
// 在MainActivity.kt中添加SSL证书验证
private fun setupSSL() {
    // 如果使用自签名证书，可以添加证书验证逻辑
    // 生产环境建议使用正式的SSL证书
}
```

### 用户认证保持
```kotlin
// 在WebView中保持登录状态
webView.settings.apply {
    domStorageEnabled = true
    databaseEnabled = true
    allowContentAccess = true
}

// 清除缓存的方法（用于退出登录）
fun clearWebViewData() {
    webView.clearCache(true)
    webView.clearHistory()
    CookieManager.getInstance().removeAllCookies(null)
    CookieManager.getInstance().flush()
}
```

## 📈 性能优化

### WebView性能调优
```kotlin
webView.settings.apply {
    // 启用硬件加速
    setLayerType(View.LAYER_TYPE_HARDWARE, null)
    
    // 预加载设置
    setRenderPriority(WebSettings.RenderPriority.HIGH)
    
    // 禁用不必要的功能
    allowFileAccessFromFileURLs = false
    allowUniversalAccessFromFileURLs = false
    setGeolocationEnabled(false)
}
```

### 内存优化
```kotlin
override fun onDestroy() {
    webView.stopLoading()
    webView.onPause()
    webView.removeAllViews()
    webView.destroyDrawingCache()
    webView.destroy()
    super.onDestroy()
}

override fun onLowMemory() {
    super.onLowMemory()
    webView.freeMemory()
}
```

## 🚀 发布准备

### 应用签名
```bash
# 生成发布密钥
keytool -genkey -v -keystore ops-work-order.keystore -name ops_work_order -keyalg RSA -keysize 2048 -validity 10000

# 构建发布版本
./gradlew assembleRelease
```

### 应用优化
在`build.gradle`中添加：

```gradle
android {
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

## 📞 故障排除

### 常见问题解决

#### 1. 网络连接问题
- 检查`AndroidManifest.xml`中的网络权限
- 验证`usesCleartextTraffic="true"`设置
- 确认服务器地址可访问

#### 2. 推送通知不工作
- 检查Firebase配置是否正确
- 验证`google-services.json`文件位置
- 确认设备有Google Play服务

#### 3. WebView显示问题
- 检查JavaScript是否启用
- 验证CORS配置
- 确认网站在移动浏览器中正常工作

#### 4. 性能问题
- 启用WebView硬件加速
- 优化网站图片大小
- 减少不必要的JavaScript执行

## 🎯 后续规划

### 短期优化（1-2个月）
- [ ] 添加离线工单查看功能
- [ ] 实现文件上传进度显示
- [ ] 添加应用内更新功能
- [ ] 优化启动速度

### 中期升级（3-6个月）
- [ ] 考虑使用React Native重构
- [ ] 添加语音输入功能
- [ ] 实现扫码功能
- [ ] 添加地图定位功能

### 长期发展（6个月+）
- [ ] 开发iOS版本
- [ ] 实现完全离线功能
- [ ] 添加AI智能分析
- [ ] 集成物联网设备

## 📋 总结

通过本指南，您已经成功将运维工单网站系统改造成了兼容可通信的Android App。这个方案具有以下特点：

1. **快速实现**：WebView方案让您在最短时间内拥有移动应用
2. **功能完整**：支持推送通知、文件处理、数据同步等核心功能
3. **易于维护**：单一代码库，降低维护成本
4. **可扩展性**：为后续升级到原生应用或跨平台方案打下基础

现在您可以为运维团队提供便捷的移动办公体验，随时随地处理工单，提高工作效率！

---

如有任何问题，请参考故障排除部分或联系技术支持团队。 