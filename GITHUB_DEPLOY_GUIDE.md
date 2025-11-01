# GitHub 部署指南

完整的 GitHub Actions 自动化构建和发布指南。

## 🎯 仓库信息

- **仓库地址**：https://github.com/gwj7401/meeting-minutes.git
- **用户名**：gwj7401
- **仓库名**：meeting-minutes

## ✨ GitHub Actions 优势

相比 Gitee，GitHub Actions 提供：

- ✅ **完全免费**：公开仓库无限制使用
- ✅ **功能强大**：丰富的 Actions 市场
- ✅ **自动化**：推送代码自动构建
- ✅ **可视化**：清晰的构建日志和状态
- ✅ **产物管理**：自动保存和下载 APK
- ✅ **自动发布**：创建 Release 并上传 APK

## 🚀 快速开始

### 第 1 步：推送代码到 GitHub

```bash
# 进入项目目录
cd /path/to/会议纪要

# 初始化 Git
git init

# 添加所有文件
git add .

# 提交
git commit -m "Initial commit: 会议纪要 App"

# 关联 GitHub 仓库
git remote add origin https://github.com/gwj7401/meeting-minutes.git

# 推送到 master 分支
git push -u origin master
```

### 第 2 步：自动构建触发

推送代码后，GitHub Actions 会自动：

1. **检测到推送**
2. **启动构建流程**
3. **编译 APK**
4. **上传构建产物**

### 第 3 步：查看构建状态

1. 访问：https://github.com/gwj7401/meeting-minutes/actions
2. 查看正在运行的工作流
3. 点击查看详细日志

### 第 4 步：下载 APK

构建完成后：

1. 进入 Actions 页面
2. 点击对应的工作流运行
3. 在"Artifacts"区域下载 APK

## 📋 工作流说明

项目包含两个 GitHub Actions 工作流：

### 1. Android CI (android-build.yml)

**触发条件**：
- 推送到 `master` 或 `develop` 分支
- 创建 Pull Request
- 创建以 `v` 开头的标签（如 `v1.0.0`）

**功能**：
- ✅ 自动构建 Debug/Release APK
- ✅ 运行单元测试
- ✅ 上传构建产物
- ✅ 自动创建 GitHub Release（标签触发时）

**分支策略**：
- `develop` 分支 → 构建 Debug APK（保留 7 天）
- `master` 分支 → 构建 Release APK（保留 30 天）
- `v*` 标签 → 构建 Release APK 并创建 Release

### 2. Release Build (android-release.yml)

**触发条件**：
- 手动触发（在 Actions 页面）

**功能**：
- ✅ 手动指定版本号
- ✅ 自动创建 Git 标签
- ✅ 构建 Release APK
- ✅ 创建 GitHub Release
- ✅ 上传 APK 和映射文件

## 🎯 使用场景

### 场景 1：日常开发（自动构建 Debug）

```bash
# 在 develop 分支开发
git checkout -b develop
git add .
git commit -m "feat: 添加新功能"
git push origin develop

# GitHub Actions 自动构建 Debug APK
# 在 Actions 页面下载
```

### 场景 2：发布到 Master（自动构建 Release）

```bash
# 合并到 master
git checkout master
git merge develop
git push origin master

# GitHub Actions 自动构建 Release APK
# 在 Actions 页面下载
```

### 场景 3：发布正式版本（自动创建 Release）

```bash
# 创建版本标签
git tag -a v1.0.0 -m "版本 1.0.0

新功能：
- 实时录音转文字
- 支持 12 种方言
- AI 智能总结
- 多格式导出
"

# 推送标签
git push origin v1.0.0

# GitHub Actions 自动：
# 1. 构建 Release APK
# 2. 创建 GitHub Release
# 3. 上传 APK 文件
# 4. 生成下载链接
```

### 场景 4：手动发布（可视化操作）

1. 访问：https://github.com/gwj7401/meeting-minutes/actions
2. 点击左侧"Release Build"
3. 点击"Run workflow"
4. 输入版本号（如 `1.0.0`）
5. 点击"Run workflow"开始构建
6. 等待完成，自动创建 Release

## 📦 下载 APK

### 方式 1：从 Actions 下载（开发版本）

1. 访问：https://github.com/gwj7401/meeting-minutes/actions
2. 点击最新的成功构建
3. 滚动到底部"Artifacts"区域
4. 下载 `app-debug` 或 `app-release`

### 方式 2：从 Releases 下载（正式版本）

1. 访问：https://github.com/gwj7401/meeting-minutes/releases
2. 查看最新的 Release
3. 在"Assets"区域下载 APK
4. 这是推荐给用户的下载方式

## 🔐 配置签名（Release 构建）

### 方式 1：使用 GitHub Secrets（推荐）

1. **生成签名文件**

```bash
keytool -genkey -v -keystore meeting-minutes.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias meeting-minutes
```

2. **转换为 Base64**

```bash
# Linux/Mac
base64 -w 0 meeting-minutes.jks > keystore.txt

# Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("meeting-minutes.jks")) > keystore.txt
```

3. **配置 GitHub Secrets**

访问：https://github.com/gwj7401/meeting-minutes/settings/secrets/actions

添加以下 Secrets：

| 名称 | 值 |
|------|-----|
| `KEYSTORE_FILE` | keystore.txt 的内容（Base64） |
| `KEYSTORE_PASSWORD` | Keystore 密码 |
| `KEY_ALIAS` | meeting-minutes |
| `KEY_PASSWORD` | 密钥密码 |

4. **更新工作流**

在 `.github/workflows/android-build.yml` 中添加签名步骤（已预留位置）。

### 方式 2：本地签名

如果不想在 GitHub 配置签名，可以：

1. 让 GitHub Actions 构建未签名的 APK
2. 下载后在本地签名
3. 手动上传到 Release

## 📊 查看构建状态

### 在 README 中显示徽章

在 `README.md` 顶部添加：

```markdown
![Android CI](https://github.com/gwj7401/meeting-minutes/workflows/Android%20CI/badge.svg)
```

效果：显示构建状态（通过/失败）

### 查看详细日志

1. 访问 Actions 页面
2. 点击具体的工作流运行
3. 点击"build"或"test"查看详细日志
4. 可以下载日志文件

## 🔄 版本管理

### 自动版本号

项目使用 Git 自动管理版本：

- **versionCode**：`git rev-list --count HEAD`（提交次数）
- **versionName**：Git 标签（如 `v1.0.0`）

### 版本号规则

遵循语义化版本：

```
主版本号.次版本号.修订号

例如：1.0.0, 1.2.3, 2.0.0
```

- **主版本号**：不兼容的 API 修改
- **次版本号**：向下兼容的功能性新增
- **修订号**：向下兼容的问题修正

### 创建新版本

```bash
# 1. 完成开发
git add .
git commit -m "feat: 完成新功能"

# 2. 创建标签
git tag -a v1.0.0 -m "版本 1.0.0"

# 3. 推送
git push origin master
git push origin v1.0.0

# 4. GitHub Actions 自动构建并发布
```

## 🎯 完整发布流程

### 标准发布流程

```bash
# 1. 确保在 master 分支
git checkout master
git pull origin master

# 2. 合并开发分支
git merge develop

# 3. 运行测试（可选）
./gradlew test

# 4. 提交更改
git add .
git commit -m "release: v1.0.0"

# 5. 创建标签
git tag -a v1.0.0 -m "版本 1.0.0

新功能：
- 功能 1
- 功能 2

Bug 修复：
- 修复 1
- 修复 2
"

# 6. 推送
git push origin master
git push origin v1.0.0

# 7. 等待 GitHub Actions 完成
# 访问：https://github.com/gwj7401/meeting-minutes/actions

# 8. 检查 Release
# 访问：https://github.com/gwj7401/meeting-minutes/releases

# 9. 分享下载链接
# https://github.com/gwj7401/meeting-minutes/releases/latest
```

## 📱 分享 APK

### 直接下载链接

最新版本：
```
https://github.com/gwj7401/meeting-minutes/releases/latest
```

特定版本：
```
https://github.com/gwj7401/meeting-minutes/releases/tag/v1.0.0
```

### 二维码分享

可以使用工具生成下载链接的二维码，方便手机扫码下载。

## ❓ 常见问题

### Q1: Actions 构建失败？

**查看日志**：
1. 进入 Actions 页面
2. 点击失败的运行
3. 查看错误信息

**常见原因**：
- Gradle 配置错误
- 依赖下载失败
- 代码编译错误

### Q2: 如何取消正在运行的构建？

1. 进入 Actions 页面
2. 点击正在运行的工作流
3. 点击右上角"Cancel workflow"

### Q3: Actions 配额限制？

**公开仓库**：
- ✅ 无限制免费使用
- ✅ 每月 2000 分钟（足够使用）

**私有仓库**：
- 每月 2000 分钟免费
- 超出后需要付费

### Q4: 如何禁用 Actions？

1. 仓库设置 → Actions → General
2. 选择"Disable actions"
3. 保存

## 🎉 总结

### GitHub Actions 的优势

1. **完全自动化**：推送代码即构建
2. **零配置成本**：无需自己搭建服务器
3. **可视化管理**：清晰的界面和日志
4. **免费使用**：公开仓库完全免费
5. **产物管理**：自动保存和下载
6. **自动发布**：一键创建 Release

### 推荐工作流

```
开发 → develop 分支 → 自动构建 Debug APK
  ↓
测试通过 → 合并到 master → 自动构建 Release APK
  ↓
准备发布 → 创建标签 → 自动创建 Release
  ↓
用户下载 → GitHub Releases 页面
```

---

**GitHub Actions 已配置完成，推送代码即可自动构建！** 🚀

