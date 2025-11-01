# 常用命令速查表

快速查找你需要的命令。

## 📦 Git 操作

### 首次推送到 Gitee

```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://gitee.com/gwj7401/meeting-minutes.git
git push -u origin master
```

### 日常提交

```bash
git add .
git commit -m "feat: 添加新功能"
git push
```

### 创建版本标签

```bash
git tag -a v1.0.0 -m "版本 1.0.0"
git push origin v1.0.0
```

### 查看提交历史

```bash
git log --oneline
```

### 查看当前版本号

```bash
# 查看提交次数（versionCode）
git rev-list --count HEAD

# 查看最新标签（versionName）
git describe --tags --abbrev=0
```

## 🔨 Gradle 构建

### 构建 Debug APK

```bash
# Linux/Mac
./gradlew assembleDebug

# Windows
.\gradlew.bat assembleDebug
```

### 构建 Release APK

```bash
# Linux/Mac
./gradlew assembleRelease

# Windows
.\gradlew.bat assembleRelease
```

### 清理项目

```bash
./gradlew clean
```

### 清理并重新构建

```bash
./gradlew clean assembleDebug
```

### 安装到连接的设备

```bash
./gradlew installDebug
```

### 运行测试

```bash
./gradlew test
```

### 查看所有任务

```bash
./gradlew tasks
```

### 查看依赖

```bash
./gradlew dependencies
```

### 检查代码

```bash
./gradlew lint
```

## 📱 APK 位置

### Debug APK

```
app/build/outputs/apk/debug/app-debug.apk
```

### Release APK

```
app/build/outputs/apk/release/app-release.apk
```

### ProGuard 映射文件

```
app/build/outputs/mapping/release/mapping.txt
```

## 🔐 签名相关

### 生成签名文件

```bash
keytool -genkey -v -keystore meeting-minutes.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias meeting-minutes
```

### 查看签名信息

```bash
keytool -list -v -keystore meeting-minutes.jks
```

### 验证 APK 签名

```bash
# Linux/Mac
jarsigner -verify -verbose -certs app-release.apk

# 或使用 apksigner（Android SDK 工具）
apksigner verify --verbose app-release.apk
```

## 🔧 项目维护

### 更新 Gradle Wrapper

```bash
./gradlew wrapper --gradle-version=8.0
```

### 刷新依赖

```bash
./gradlew build --refresh-dependencies
```

### 查看项目信息

```bash
./gradlew properties
```

## 📊 版本管理

### 查看当前版本

```bash
# 查看 versionCode（基于提交次数）
git rev-list --count HEAD

# 查看 versionName（基于标签）
git describe --tags --abbrev=0

# 如果没有标签，会显示错误，这时默认为 1.0.0
```

### 创建新版本

```bash
# 1. 提交所有更改
git add .
git commit -m "release: v1.0.0"

# 2. 创建标签
git tag -a v1.0.0 -m "版本 1.0.0

新功能：
- 功能 1
- 功能 2

Bug 修复：
- 修复 1
- 修复 2
"

# 3. 推送
git push origin master
git push origin v1.0.0
```

### 查看所有标签

```bash
git tag -l
```

### 删除标签

```bash
# 删除本地标签
git tag -d v1.0.0

# 删除远程标签
git push origin :refs/tags/v1.0.0
```

## 🐛 故障排除

### gradlew 没有执行权限

```bash
# Linux/Mac
chmod +x gradlew
git add gradlew
git commit -m "fix: add execute permission"
git push
```

### 清理所有构建缓存

```bash
./gradlew clean
rm -rf .gradle
rm -rf build
rm -rf app/build
```

### 重置 Git 仓库

```bash
# 警告：这会删除所有未提交的更改！
git reset --hard HEAD
git clean -fd
```

### 查看 Gradle 版本

```bash
./gradlew --version
```

### 查看 Java 版本

```bash
java -version
```

## 📝 常用 Git 提交信息

### 提交类型

```bash
# 新功能
git commit -m "feat: 添加录音功能"

# Bug 修复
git commit -m "fix: 修复录音崩溃问题"

# 文档更新
git commit -m "docs: 更新 README"

# 代码格式
git commit -m "style: 格式化代码"

# 重构
git commit -m "refactor: 重构语音识别模块"

# 性能优化
git commit -m "perf: 优化录音性能"

# 测试
git commit -m "test: 添加单元测试"

# 构建相关
git commit -m "build: 更新依赖版本"

# CI/CD
git commit -m "ci: 更新 CI 配置"

# 其他
git commit -m "chore: 更新 .gitignore"
```

## 🔍 调试命令

### 查看构建详细信息

```bash
./gradlew assembleDebug --info
```

### 查看构建堆栈跟踪

```bash
./gradlew assembleDebug --stacktrace
```

### 调试模式构建

```bash
./gradlew assembleDebug --debug
```

### 离线模式构建

```bash
./gradlew assembleDebug --offline
```

## 📦 发布流程

### 完整发布流程

```bash
# 1. 确保代码最新
git pull origin master

# 2. 运行测试
./gradlew test

# 3. 清理并构建
./gradlew clean
./gradlew assembleRelease

# 4. 提交代码
git add .
git commit -m "release: v1.0.0"

# 5. 创建标签
git tag -a v1.0.0 -m "版本 1.0.0"

# 6. 推送
git push origin master
git push origin v1.0.0

# 7. 在 Gitee 创建 Release
# 访问：https://gitee.com/gwj7401/meeting-minutes/releases/new
# 上传：app/build/outputs/apk/release/app-release.apk
```

## 🎯 快捷命令组合

### 快速构建并安装

```bash
./gradlew clean assembleDebug installDebug
```

### 构建所有变体

```bash
./gradlew assemble
```

### 运行所有检查

```bash
./gradlew check
```

### 生成测试报告

```bash
./gradlew test
# 报告位置：app/build/reports/tests/testDebugUnitTest/index.html
```

## 📱 ADB 命令（如果连接了设备）

### 安装 APK

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 卸载应用

```bash
adb uninstall com.meetingminutes.app
```

### 查看日志

```bash
adb logcat | grep MeetingMinutes
```

### 清除应用数据

```bash
adb shell pm clear com.meetingminutes.app
```

## 💡 提示

- 所有命令都在项目根目录执行
- Windows 用户将 `./gradlew` 替换为 `.\gradlew.bat`
- 首次构建会下载依赖，需要较长时间
- 使用 `--help` 查看命令帮助，如：`./gradlew --help`

---

**保存此文件以便快速查找命令！** 📋

