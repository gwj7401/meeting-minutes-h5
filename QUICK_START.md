# 🚀 快速开始指南

## 📋 前置要求

- Node.js 18+ 
- npm 或 pnpm
- JDK 17（仅打包 Android 时需要）

## 🎯 本地开发

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

**重要**: Web Speech API 需要 HTTPS 环境，Vite 已配置自动启用 HTTPS。

访问: `https://localhost:5173`

浏览器会提示证书不安全，点击"继续访问"即可。

### 3. 测试功能

1. 点击"开始录音"按钮
2. 浏览器会请求麦克风权限，点击"允许"
3. 开始说话，文字会实时显示
4. 点击"停止录音"保存会议记录

## 📱 打包 Android APK

### 方法一：使用 GitHub Actions（推荐）

1. **创建 GitHub 仓库**

```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/你的用户名/meeting-minutes-h5.git
git push -u origin main
```

2. **自动构建**

推送代码后，GitHub Actions 会自动：
- 安装依赖
- 构建 Web 应用
- 初始化 Android 项目
- 使用 JDK 17 编译 APK
- 上传 APK 到 Artifacts
- 创建 Release（main 分支）

3. **下载 APK**

- 进入 GitHub 仓库的 Actions 页面
- 点击最新的工作流运行
- 在 Artifacts 中下载 `app-release`

### 方法二：本地打包

1. **安装 Android SDK**

下载并安装 Android Studio，或单独安装 Android SDK。

2. **设置环境变量**

```bash
# Windows (PowerShell)
$env:ANDROID_HOME = "C:\Users\你的用户名\AppData\Local\Android\Sdk"
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"

# macOS/Linux
export ANDROID_HOME=$HOME/Library/Android/sdk
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
```

3. **构建 APK**

```bash
# 构建 Web 应用
npm run build

# 初始化 Android 项目（首次）
npx cap add android

# 同步代码
npx cap sync android

# 构建 APK
cd android
./gradlew assembleRelease

# Windows 使用
.\gradlew.bat assembleRelease
```

4. **查找 APK**

APK 位置: `android/app/build/outputs/apk/release/app-release-unsigned.apk`

## 🔧 常见问题

### Q1: 语音识别不工作？

**A**: 检查以下几点：
1. 使用 Chrome 或 Edge 浏览器
2. 确保使用 HTTPS 访问
3. 允许麦克风权限
4. 检查麦克风是否正常工作

### Q2: 开发服务器 HTTPS 证书警告？

**A**: 这是正常的，因为使用的是自签名证书。点击"高级" -> "继续访问"即可。

### Q3: GitHub Actions 构建失败？

**A**: 检查以下几点：
1. 确保仓库是 Public（私有仓库需要付费）
2. 检查 `.github/workflows/build-android.yml` 文件是否存在
3. 查看 Actions 日志，找到具体错误信息

### Q4: APK 安装失败？

**A**: 
1. 确保手机允许"未知来源"安装
2. 如果提示"解析包时出现问题"，可能是 APK 损坏，重新下载
3. 如果提示"应用未安装"，可能是签名问题

## 🎉 完成！

现在你已经成功创建了一个会议纪要应用！

如有问题，请查看 [README.md](./README.md) 或提交 Issue。

