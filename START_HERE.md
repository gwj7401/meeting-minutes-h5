# 🚀 从这里开始

你好！欢迎使用会议纪要 App。这个文档将帮助你快速开始。

## 📌 你的仓库信息

- **GitHub 仓库**：https://github.com/gwj7401/meeting-minutes.git ⭐ 推荐
- **Gitee 仓库**：https://gitee.com/gwj7401/meeting-minutes.git
- **仓库名称**：meeting-minutes
- **用户名**：gwj7401

## 🎯 强烈推荐使用 GitHub ⭐

**为什么选择 GitHub？**
- ✅ **完全免费**：公开仓库无限制使用 GitHub Actions
- ✅ **自动构建**：推送代码自动构建 APK（无需本地环境）
- ✅ **自动发布**：创建标签自动发布 Release
- ✅ **产物管理**：自动保存和下载 APK
- ✅ **零配置**：GitHub Actions 已配置完成
- ✅ **可视化**：清晰的构建日志和状态

**🎉 GitHub Actions 已配置完成！推送代码即可自动构建。**

详细对比：[PLATFORM_COMPARISON.md](PLATFORM_COMPARISON.md)

## ⚡ 3 步快速开始

### 第 1 步：推送代码到 GitHub

```bash
# 在项目目录下执行
cd /path/to/会议纪要

# 初始化 Git
git init

# 添加所有文件
git add .

# 提交
git commit -m "Initial commit: 会议纪要 App"

# 关联你的 GitHub 仓库（推荐）
git remote add origin https://github.com/gwj7401/meeting-minutes.git

# 推送到 master 分支
git push -u origin master

# 🎉 推送后 GitHub Actions 会自动开始构建！
# 访问：https://github.com/gwj7401/meeting-minutes/actions
```

**或者推送到 Gitee**（如果你更喜欢 Gitee）：

```bash
# 关联 Gitee 仓库
git remote add gitee https://gitee.com/gwj7401/meeting-minutes.git
git push -u gitee master
```

### 第 2 步：获取 APK

#### 方式 A：从 GitHub Actions 下载（推荐，自动构建）

推送代码后，GitHub Actions 会自动构建：

1. 访问：https://github.com/gwj7401/meeting-minutes/actions
2. 点击最新的工作流运行
3. 等待构建完成（约 5-10 分钟）
4. 在"Artifacts"区域下载 APK

**优势**：
- ✅ 完全自动化，无需本地环境
- ✅ 云端构建，速度快
- ✅ 自动保存构建产物

#### 方式 B：本地构建（备选）

如果你想本地构建：

**Windows**：
```powershell
.\gradlew.bat assembleDebug
```

**Linux/Mac**：
```bash
chmod +x gradlew
./gradlew assembleDebug
```

APK 位置：`app/build/outputs/apk/debug/app-debug.apk`

### 第 3 步：安装测试

**就这么简单！** 现在你可以：
- 将 APK 安装到手机测试
- 或创建 Release 分享给其他人

## 📱 安装到手机

1. 将 `app-debug.apk` 复制到手机
2. 在手机上打开文件管理器
3. 找到 APK 文件并点击
4. 允许"安装未知来源应用"
5. 点击"安装"

## 🎯 关于"构建 APK"按钮

你提到在 Gitee 页面上看不到"构建 APK"按钮，这是正常的。原因可能是：

1. **该功能需要特定权限**：可能需要企业版或特定会员等级
2. **功能调整**：Gitee 可能调整了该功能的可用性
3. **仓库设置**：需要特定的仓库配置

**不用担心！** 使用本地构建的方式更加：
- ✅ 可靠：完全掌控构建过程
- ✅ 快速：不依赖在线服务
- ✅ 灵活：可以随时构建
- ✅ 免费：无需付费服务

## 🔄 后续开发流程

### 日常开发

```bash
# 1. 修改代码
# ... 编辑文件 ...

# 2. 提交更改
git add .
git commit -m "feat: 添加新功能"

# 3. 推送到 Gitee
git push origin master

# 4. 重新构建
./gradlew assembleDebug
```

### 发布新版本

```bash
# 1. 创建版本标签
git tag -a v1.0.0 -m "版本 1.0.0"

# 2. 推送标签
git push origin v1.0.0

# 3. 构建 Release 版本
./gradlew assembleRelease

# 4. 在 Gitee 创建 Release
# 访问：https://gitee.com/gwj7401/meeting-minutes/releases/new
# 上传：app/build/outputs/apk/release/app-release.apk
```

## 📚 重要文档

按优先级阅读：

1. **START_HERE.md**（本文件）- 快速开始 ⭐⭐⭐⭐⭐
2. **GITEE_DEPLOY_GUIDE.md** - Gitee 部署详细指南 ⭐⭐⭐⭐⭐
3. **QUICKSTART.md** - 5 分钟快速上手 ⭐⭐⭐⭐
4. **README.md** - 项目完整说明 ⭐⭐⭐⭐
5. **USAGE_GUIDE.md** - 应用使用指南 ⭐⭐⭐
6. **DEPLOYMENT.md** - 高级部署选项 ⭐⭐⭐

## 🛠️ 环境准备

### 必需工具

1. **Git**
   - Windows: https://git-scm.com/download/win
   - Linux: `sudo apt install git`
   - Mac: `brew install git`

2. **JDK 11**（构建 Android 项目需要）
   - 下载：https://adoptium.net/
   - 安装后设置 JAVA_HOME 环境变量

### 可选工具

3. **Android Studio**（如果要开发）
   - 下载：https://developer.android.com/studio
   - 用于编辑代码和调试

## ❓ 常见问题

### Q: 构建时提示找不到 SDK？

**A**: 有两种解决方案：

**方案 1**（推荐）：安装 Android Studio
1. 下载并安装 Android Studio
2. 用 Android Studio 打开项目
3. 让它自动下载和配置 SDK
4. 然后就可以用命令行构建了

**方案 2**：手动配置
1. 下载 Android SDK
2. 创建 `local.properties` 文件
3. 添加：`sdk.dir=/path/to/Android/sdk`

### Q: gradlew 没有执行权限？

**A**: Linux/Mac 系统需要添加权限：

```bash
chmod +x gradlew
```

### Q: 构建很慢？

**A**: 
- 首次构建需要下载依赖，会比较慢（10-30 分钟）
- 项目已配置阿里云镜像加速
- 后续构建会快很多（1-5 分钟）
- 耐心等待即可

### Q: 如何构建 Release 版本？

**A**: 需要先配置签名，详见 [GITEE_DEPLOY_GUIDE.md](GITEE_DEPLOY_GUIDE.md)

## 🎯 下一步做什么？

### 立即可做

1. ✅ 推送代码到 Gitee
2. ✅ 本地构建 APK
3. ✅ 安装到手机测试

### 配置 API（可选）

如果要使用完整功能，需要配置 API：

1. **语音识别 API**
   - 阿里云：https://nls.console.aliyun.com/
   - 腾讯云：https://console.cloud.tencent.com/asr
   - 讯飞：https://www.xfyun.cn/

2. **AI 模型 API**
   - OpenAI：https://platform.openai.com/
   - 通义千问：https://dashscope.aliyun.com/
   - 文心一言：https://cloud.baidu.com/product/wenxinworkshop

3. **配置方法**
   - 复制 `local.properties.example` 为 `local.properties`
   - 填入你的 API Key
   - 重新构建

### 自定义应用

1. **修改应用名称**
   - 编辑 `app/src/main/res/values/strings.xml`
   - 修改 `app_name`

2. **修改应用图标**
   - 替换 `app/src/main/res/mipmap-*/` 中的图标文件

3. **修改主题颜色**
   - 编辑 `app/src/main/res/values/colors.xml`

## 📞 需要帮助？

### 查看文档
- 所有文档都在项目根目录
- 按照上面的优先级阅读

### 提交问题
- 访问：https://gitee.com/gwj7401/meeting-minutes/issues
- 点击"新建 Issue"
- 描述你的问题

### Gitee 帮助
- 官方文档：https://gitee.com/help

## 🎉 总结

**你现在需要做的就是：**

```bash
# 1. 推送代码
git init
git add .
git commit -m "Initial commit"
git remote add origin https://gitee.com/gwj7401/meeting-minutes.git
git push -u origin master

# 2. 构建 APK
./gradlew assembleDebug  # 或 .\gradlew.bat assembleDebug (Windows)

# 3. 完成！
# APK 位置：app/build/outputs/apk/debug/app-debug.apk
```

**就这么简单！不需要复杂的在线构建配置。** 🚀

---

**有问题随时查看文档或提交 Issue！** 💪

