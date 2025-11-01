# 会议纪要 App

![Android CI](https://github.com/gwj7401/meeting-minutes/workflows/Android%20CI/badge.svg)
![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)

一款功能强大的会议录音转文字应用，支持实时语音识别、多方言识别、AI 智能总结和多格式文档导出。

---

## 🎯 快速开始

**👉 如果你是第一次使用，请先阅读 [START_HERE.md](START_HERE.md)**

### 仓库地址

- **GitHub**（推荐）：https://github.com/gwj7401/meeting-minutes.git
- **Gitee**（备选）：https://gitee.com/gwj7401/meeting-minutes.git

### 自动构建

✨ **GitHub Actions 已配置完成！** 推送代码即可自动构建 APK。

```bash
git push origin master
# 访问 https://github.com/gwj7401/meeting-minutes/actions 查看构建状态
```

详细说明：[GITHUB_DEPLOY_GUIDE.md](GITHUB_DEPLOY_GUIDE.md)

---

## 功能特性

### 📝 核心功能

- **实时录音转文字**：边录音边转换为文字，实时查看识别结果
- **多方言支持**：支持普通话、粤语、四川话、上海话等 12 种方言
- **音频导入**：支持导入本地音频文件并转换为文字
- **AI 智能总结**：自动生成会议摘要，提取关键信息
- **多格式导出**：支持导出为 TXT、Word、PDF、Markdown、HTML 等格式
- **版本自动管理**：基于 Git 的自动版本号管理和更新检测

### 🎯 技术亮点

- **Kotlin + Coroutines**：现代化的 Android 开发
- **Room 数据库**：本地数据持久化
- **Material Design**：优雅的用户界面
- **多语音识别引擎**：支持阿里云、腾讯云、讯飞等
- **多 AI 模型**：支持 OpenAI、通义千问、文心一言等
- **Gitee CI/CD**：自动化构建和部署

## 支持的方言

- 普通话（Mandarin）
- 粤语（Cantonese）
- 四川话（Sichuanese）
- 上海话（Shanghainese）
- 闽南语（Hokkien）
- 客家话（Hakka）
- 东北话（Northeastern）
- 天津话（Tianjin）
- 武汉话（Wuhan）
- 西安话（Xian）
- 郑州话（Zhengzhou）
- 南京话（Nanjing）

## 导出格式

- **TXT**：纯文本格式
- **Markdown**：支持 Markdown 语法
- **Word**：.docx 格式，兼容 Microsoft Word
- **PDF**：便于分享和打印
- **HTML**：网页格式，可在浏览器中查看

## 快速开始

### 环境要求

- Android Studio Arctic Fox 或更高版本
- JDK 11 或更高版本
- Android SDK 24 或更高版本
- Gradle 7.0 或更高版本

### 本地构建

1. 克隆仓库

```bash
# GitHub（推荐）
git clone https://github.com/gwj7401/meeting-minutes.git
cd meeting-minutes

# 或 Gitee
git clone https://gitee.com/gwj7401/meeting-minutes.git
cd meeting-minutes
```

2. 配置 API 密钥

在 `local.properties` 中添加：

```properties
# 阿里云语音识别
ali.speech.appkey=your_app_key
ali.speech.token=your_token

# AI 模型 API
openai.api.key=your_api_key
qianwen.api.key=your_api_key
```

3. 构建项目

```bash
./gradlew assembleDebug
```

### 自动化部署

#### 方式一：GitHub Actions（推荐）⭐

**完全自动化，零配置！**

```bash
# 1. 推送代码到 GitHub
git remote add origin https://github.com/gwj7401/meeting-minutes.git
git push -u origin master

# 2. GitHub Actions 自动构建
# 访问：https://github.com/gwj7401/meeting-minutes/actions

# 3. 下载 APK
# 在 Actions 页面的 Artifacts 区域下载
```

**发布正式版本**：

```bash
# 创建标签
git tag -a v1.0.0 -m "版本 1.0.0"
git push origin v1.0.0

# GitHub Actions 自动：
# ✅ 构建 Release APK
# ✅ 创建 GitHub Release
# ✅ 上传 APK 文件
# ✅ 生成下载链接
```

详细说明：[GITHUB_DEPLOY_GUIDE.md](GITHUB_DEPLOY_GUIDE.md)

#### 方式二：本地构建 + 手动发布

```bash
# 1. 本地构建
./gradlew assembleDebug  # Linux/Mac
.\gradlew.bat assembleDebug  # Windows

# 2. APK 位置
# app/build/outputs/apk/debug/app-debug.apk

# 3. 手动创建 Release 并上传
```

#### 方式三：Gitee Go CI/CD（可选）

如果使用 Gitee，项目已配置 `.gitee-ci.yml`：

- **develop 分支**：自动构建 Debug APK
- **master 分支**：自动构建 Release APK
- **tags**：构建并发布正式版本

详细说明：[GITEE_DEPLOY_GUIDE.md](GITEE_DEPLOY_GUIDE.md)

### 配置签名（Release 构建）

在 Gitee 仓库的 CI/CD 设置中添加环境变量：

- `KEYSTORE_FILE`：Keystore 文件的 Base64 编码
- `KEYSTORE_PASSWORD`：Keystore 密码
- `KEY_ALIAS`：密钥别名
- `KEY_PASSWORD`：密钥密码

生成 Base64 编码：

```bash
base64 -w 0 your-keystore.jks > keystore.txt
```

## 版本管理

### 自动版本号

- **versionCode**：基于 Git 提交次数自动生成
- **versionName**：基于 Git 标签自动获取

### 创建新版本

```bash
# 创建标签
git tag -a v1.0.0 -m "版本 1.0.0 发布说明"

# 推送标签
git push origin v1.0.0
```

### 版本更新检测

应用会自动检查更新，需要配置更新服务器 API：

```kotlin
// 在 MainActivity 中配置
val updateUrl = "https://your-server.com/api/version/check"
```

API 返回格式：

```json
{
  "versionName": "1.0.1",
  "versionCode": 101,
  "downloadUrl": "https://example.com/app-v1.0.1.apk",
  "updateLog": "1. 新增功能\n2. 修复 Bug",
  "forceUpdate": false,
  "fileSize": 15728640,
  "md5": "abc123..."
}
```

## 项目结构

```
app/
├── src/main/
│   ├── java/com/meetingminutes/app/
│   │   ├── ai/              # AI 总结功能
│   │   ├── data/            # 数据层
│   │   │   ├── dao/         # 数据访问对象
│   │   │   ├── database/    # 数据库
│   │   │   └── model/       # 数据模型
│   │   ├── export/          # 文档导出
│   │   ├── recorder/        # 录音功能
│   │   ├── speech/          # 语音识别
│   │   ├── ui/              # 用户界面
│   │   ├── update/          # 版本更新
│   │   └── MeetingApp.kt    # 应用入口
│   └── res/                 # 资源文件
├── build.gradle             # 模块构建配置
└── proguard-rules.pro       # 混淆规则
```

## 依赖库

- **AndroidX**：核心库和 UI 组件
- **Kotlin Coroutines**：异步编程
- **Room**：数据库
- **OkHttp & Retrofit**：网络请求
- **Apache POI**：Word 文档生成
- **iText**：PDF 文档生成
- **PermissionX**：权限请求

## 配置说明

### 语音识别配置

支持多种语音识别引擎，需要在设置中配置相应的 API 密钥：

1. **阿里云**：https://nls.console.aliyun.com/
2. **腾讯云**：https://console.cloud.tencent.com/asr
3. **讯飞**：https://www.xfyun.cn/

### AI 总结配置

支持多种 AI 模型，需要配置 API 密钥：

1. **OpenAI**：https://platform.openai.com/
2. **通义千问**：https://dashscope.aliyun.com/
3. **文心一言**：https://cloud.baidu.com/product/wenxinworkshop

## 常见问题

### 1. 录音没有声音？

检查是否授予了录音权限，在设置中开启麦克风权限。

### 2. 语音识别不准确？

- 确保网络连接正常
- 选择正确的方言类型
- 在安静的环境中录音

### 3. 导出 PDF 中文显示异常？

需要在 `assets` 目录下放置中文字体文件（如 `simhei.ttf`）。

### 4. 构建失败？

- 检查 Gradle 版本是否匹配
- 清理项目：`./gradlew clean`
- 检查网络连接（国内建议使用阿里云镜像）

## 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/your-feature`
3. 提交更改：`git commit -am 'Add some feature'`
4. 推送分支：`git push origin feature/your-feature`
5. 提交 Pull Request

## 开源协议

本项目采用 MIT 协议开源，详见 [LICENSE](LICENSE) 文件。

## 联系方式

- 问题反馈：[Issues](https://gitee.com/your-username/meeting-minutes/issues)
- 邮箱：your-email@example.com

## 更新日志

### v1.0.0 (2024-01-01)

- 🎉 首次发布
- ✨ 实时录音转文字功能
- ✨ 支持 12 种方言识别
- ✨ AI 智能总结
- ✨ 多格式文档导出
- ✨ 自动版本管理

---

**Made with ❤️ by Your Name**

