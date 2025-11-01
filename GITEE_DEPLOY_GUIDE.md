# Gitee 快速部署指南

由于你的仓库 https://gitee.com/gwj7401/meeting-minutes.git 已创建，这里提供最简单的部署方案。

## 🎯 推荐方案：本地构建 + Gitee 托管

由于 Gitee 官方的"构建 APK"按钮可能需要特定权限，我们采用**本地构建 + Gitee 发布**的方式。

## 📋 方案一：本地构建（最简单）

### 步骤 1：推送代码到 Gitee

```bash
# 进入项目目录
cd /path/to/会议纪要

# 初始化 Git（如果还没有）
git init

# 添加所有文件
git add .

# 提交
git commit -m "Initial commit: 会议纪要 App v1.0.0"

# 关联远程仓库
git remote add origin https://gitee.com/gwj7401/meeting-minutes.git

# 推送到 master 分支
git push -u origin master
```

### 步骤 2：本地构建 APK

#### Windows 系统

```powershell
# 在项目根目录执行
.\gradlew.bat assembleDebug

# 构建完成后，APK 位置：
# app\build\outputs\apk\debug\app-debug.apk
```

#### Linux/Mac 系统

```bash
# 给 gradlew 添加执行权限
chmod +x gradlew

# 构建 Debug APK
./gradlew assembleDebug

# 构建完成后，APK 位置：
# app/build/outputs/apk/debug/app-debug.apk
```

### 步骤 3：在 Gitee 发布

1. **创建 Release**
   - 访问 https://gitee.com/gwj7401/meeting-minutes/releases
   - 点击"创建发行版"
   - 填写信息：
     - 标签名称：`v1.0.0`
     - 发行版标题：`会议纪要 v1.0.0`
     - 描述：功能说明和更新日志

2. **上传 APK**
   - 在"附件"区域上传构建好的 APK
   - 点击"发布"

3. **分享下载链接**
   - 发布后会生成下载链接
   - 可以分享给其他人下载

## 📋 方案二：使用 GitHub Actions（如果需要自动化）

如果你需要自动化构建，可以考虑：

1. 同时推送到 GitHub
2. 使用 GitHub Actions 自动构建
3. 将 APK 同步回 Gitee

但这比较复杂，建议先使用方案一。

## 📋 方案三：使用 Gitee Go（专业方案）

### 前提条件

- 需要开通 Gitee Go 服务（可能需要付费）

### 步骤

1. **开通 Gitee Go**
   ```
   访问：https://gitee.com/gwj7401/meeting-minutes
   点击：服务 → Gitee Go
   开通服务
   ```

2. **配置已完成**
   - 项目已包含 `.gitee-ci.yml` 配置文件
   - 推送代码后会自动触发构建

3. **查看构建**
   ```
   仓库首页 → 服务 → Gitee Go → 流水线
   ```

4. **下载产物**
   - 构建完成后在"产物"标签页下载 APK

## 🚀 快速开始（推荐新手）

### 最简单的方式：

```bash
# 1. 推送代码
git init
git add .
git commit -m "Initial commit"
git remote add origin https://gitee.com/gwj7401/meeting-minutes.git
git push -u origin master

# 2. 本地构建（Windows）
.\gradlew.bat assembleDebug

# 或者（Linux/Mac）
chmod +x gradlew
./gradlew assembleDebug

# 3. 找到 APK
# 位置：app/build/outputs/apk/debug/app-debug.apk

# 4. 安装到手机测试
# 将 APK 复制到手机安装即可
```

## 📱 构建 Release 版本

### 步骤 1：生成签名文件

```bash
# 在项目根目录执行
keytool -genkey -v -keystore meeting-minutes.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias meeting-minutes

# 按提示输入：
# - Keystore 密码（记住这个密码！）
# - 密钥密码（可以和 Keystore 密码相同）
# - 姓名、组织等信息
```

### 步骤 2：配置签名

在 `app/build.gradle` 中添加（在 `android {` 块内）：

```gradle
android {
    signingConfigs {
        release {
            storeFile file('../meeting-minutes.jks')
            storePassword 'your_password'  // 替换为你的密码
            keyAlias 'meeting-minutes'
            keyPassword 'your_password'    // 替换为你的密码
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

### 步骤 3：构建 Release APK

```bash
# Windows
.\gradlew.bat assembleRelease

# Linux/Mac
./gradlew assembleRelease

# APK 位置：
# app/build/outputs/apk/release/app-release.apk
```

## 🔄 版本管理

### 创建新版本

```bash
# 1. 完成开发并提交
git add .
git commit -m "feat: 完成 v1.0.0 开发"

# 2. 创建标签
git tag -a v1.0.0 -m "版本 1.0.0

新功能：
- 实时录音转文字
- 支持 12 种方言
- AI 智能总结
- 多格式导出
"

# 3. 推送代码和标签
git push origin master
git push origin v1.0.0

# 4. 本地构建
./gradlew assembleRelease

# 5. 在 Gitee 创建 Release 并上传 APK
```

### 版本号说明

项目使用自动版本管理：
- **versionCode**：基于 Git 提交次数（自动）
- **versionName**：基于 Git 标签（如 v1.0.0）

## 📝 发布检查清单

发布前请确认：

- [ ] 代码已提交到 Gitee
- [ ] 已创建 Git 标签（如 v1.0.0）
- [ ] 已配置签名文件（Release 版本）
- [ ] 已测试所有核心功能
- [ ] 已编写更新日志
- [ ] APK 已构建成功
- [ ] 已在真机上测试安装

## 🎯 完整发布流程示例

```bash
# 1. 确保代码最新
git pull origin master

# 2. 修改版本信息（如果需要）
# 编辑 app/build.gradle

# 3. 提交所有更改
git add .
git commit -m "release: v1.0.0"

# 4. 创建标签
git tag -a v1.0.0 -m "版本 1.0.0 发布"

# 5. 推送
git push origin master
git push origin v1.0.0

# 6. 构建 Release APK
./gradlew clean
./gradlew assembleRelease

# 7. 在 Gitee 创建 Release
# 访问：https://gitee.com/gwj7401/meeting-minutes/releases/new
# 上传：app/build/outputs/apk/release/app-release.apk

# 8. 发布并分享下载链接
```

## ❓ 常见问题

### Q1: gradlew 没有执行权限？

```bash
# Linux/Mac
chmod +x gradlew
git add gradlew
git commit -m "fix: add execute permission"
git push
```

### Q2: 构建失败，提示找不到 SDK？

**解决方案**：
1. 安装 Android Studio
2. 打开项目，让 Android Studio 自动配置 SDK
3. 或手动创建 `local.properties`：
   ```properties
   sdk.dir=/path/to/Android/sdk
   ```

### Q3: 构建很慢？

**解决方案**：
- 项目已配置阿里云镜像
- 首次构建需要下载依赖，耐心等待
- 后续构建会快很多

### Q4: 如何在手机上安装 APK？

**步骤**：
1. 将 APK 复制到手机
2. 在手机上打开文件管理器
3. 找到 APK 文件并点击
4. 允许"安装未知来源应用"
5. 点击"安装"

## 📞 需要帮助？

- **查看文档**：README.md, QUICKSTART.md
- **提交 Issue**：https://gitee.com/gwj7401/meeting-minutes/issues
- **Gitee 帮助**：https://gitee.com/help

## 🎉 总结

**最简单的方式**：
1. 推送代码到 Gitee ✅
2. 本地运行 `./gradlew assembleDebug` ✅
3. 获得 APK 文件 ✅
4. 安装到手机测试 ✅

**不需要复杂的 CI/CD 配置，就能快速构建和发布！**

---

**祝你构建顺利！** 🚀

