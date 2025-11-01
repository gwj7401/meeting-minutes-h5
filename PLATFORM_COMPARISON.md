# GitHub vs Gitee 平台对比

帮助你选择最适合的代码托管平台。

## 📊 功能对比

| 功能 | GitHub | Gitee |
|------|--------|-------|
| **CI/CD** | GitHub Actions（免费） | Gitee Go（可能收费） |
| **自动构建** | ✅ 完全自动化 | ⚠️ 需要配置或手动 |
| **构建速度** | ⭐⭐⭐⭐⭐ 快 | ⭐⭐⭐ 中等 |
| **免费额度** | 公开仓库无限制 | 有限制 |
| **Actions 市场** | ✅ 丰富的插件 | ❌ 较少 |
| **Release 管理** | ✅ 自动化 | ⚠️ 手动 |
| **产物管理** | ✅ 自动保存 | ⚠️ 需配置 |
| **访问速度** | ⭐⭐⭐⭐ 全球快 | ⭐⭐⭐⭐⭐ 国内快 |
| **社区活跃度** | ⭐⭐⭐⭐⭐ 非常活跃 | ⭐⭐⭐ 活跃 |
| **国际化** | ✅ 全球最大 | ⚠️ 主要国内 |

## 🎯 推荐选择

### 推荐使用 GitHub（本项目已配置）

**理由**：

1. **GitHub Actions 完全免费**
   - 公开仓库无限制使用
   - 每月 2000 分钟免费额度
   - 无需额外付费

2. **完全自动化**
   - 推送代码自动构建
   - 创建标签自动发布
   - 自动上传 APK
   - 自动生成下载链接

3. **功能强大**
   - 丰富的 Actions 市场
   - 可视化构建日志
   - 产物自动管理
   - 支持多种触发条件

4. **全球访问**
   - 更好的国际访问速度
   - 更大的用户群体
   - 更活跃的社区

### 使用 Gitee 的场景

**适合**：

1. **国内访问优先**
   - 如果主要用户在国内
   - 需要更快的国内访问速度

2. **企业内部使用**
   - 企业版功能
   - 私有部署需求

3. **备份仓库**
   - 作为 GitHub 的镜像
   - 双平台托管

## 🚀 本项目的配置

### GitHub（已完整配置）✅

**配置文件**：
- `.github/workflows/android-build.yml` - 主构建流程
- `.github/workflows/android-release.yml` - 手动发布流程

**功能**：
- ✅ 推送代码自动构建
- ✅ 创建标签自动发布
- ✅ 自动上传 APK
- ✅ 自动创建 Release
- ✅ 产物自动管理
- ✅ 构建状态徽章

**使用方式**：
```bash
# 推送代码
git push origin master
# 访问 https://github.com/gwj7401/meeting-minutes/actions

# 发布版本
git tag -a v1.0.0 -m "版本 1.0.0"
git push origin v1.0.0
# 访问 https://github.com/gwj7401/meeting-minutes/releases
```

### Gitee（已配置，可选）

**配置文件**：
- `.gitee-ci.yml` - Gitee Go 配置

**功能**：
- ⚠️ 需要开通 Gitee Go 服务
- ⚠️ 可能需要付费
- ✅ 支持自动构建
- ✅ 支持产物管理

**使用方式**：
```bash
# 推送代码
git push gitee master
# 需要在 Gitee 页面查看构建状态
```

## 💰 成本对比

### GitHub

**免费版**：
- ✅ 公开仓库：完全免费
- ✅ GitHub Actions：每月 2000 分钟
- ✅ 存储空间：500MB
- ✅ 产物保留：90 天

**付费版**（Pro，$4/月）：
- 每月 3000 分钟
- 2GB 存储空间
- 私有仓库高级功能

**对于本项目**：免费版完全够用！

### Gitee

**免费版**：
- ✅ 基础功能免费
- ⚠️ Gitee Go 可能需要付费
- ⚠️ 构建次数有限制

**企业版**：
- 需要联系销售
- 价格较高

**对于本项目**：如果使用 Gitee Go，可能需要付费。

## 📈 性能对比

### 构建速度

**GitHub Actions**：
- 首次构建：10-15 分钟
- 后续构建：5-8 分钟（缓存）
- 并发构建：支持

**Gitee Go**：
- 首次构建：15-20 分钟
- 后续构建：8-12 分钟
- 并发构建：有限制

### 访问速度

**GitHub**：
- 国内访问：⭐⭐⭐⭐ 良好
- 国际访问：⭐⭐⭐⭐⭐ 优秀
- CDN 加速：全球

**Gitee**：
- 国内访问：⭐⭐⭐⭐⭐ 优秀
- 国际访问：⭐⭐⭐ 一般
- CDN 加速：国内

## 🎯 使用建议

### 推荐方案：GitHub 主仓库

```bash
# 1. 使用 GitHub 作为主仓库
git remote add origin https://github.com/gwj7401/meeting-minutes.git

# 2. 推送代码
git push origin master

# 3. 享受自动化构建和发布
```

**优势**：
- 完全免费
- 自动化程度高
- 功能强大
- 社区活跃

### 可选方案：双平台托管

如果你想同时使用两个平台：

```bash
# 添加两个远程仓库
git remote add github https://github.com/gwj7401/meeting-minutes.git
git remote add gitee https://gitee.com/gwj7401/meeting-minutes.git

# 推送到两个平台
git push github master
git push gitee master

# 或者一次推送到所有远程
git remote add all https://github.com/gwj7401/meeting-minutes.git
git remote set-url --add --push all https://github.com/gwj7401/meeting-minutes.git
git remote set-url --add --push all https://gitee.com/gwj7401/meeting-minutes.git
git push all master
```

**优势**：
- 双重备份
- 国内外都能快速访问
- GitHub 用于自动化构建
- Gitee 用于国内分发

## 📝 总结

### 对于本项目

**强烈推荐使用 GitHub**：

1. ✅ **已完整配置** GitHub Actions
2. ✅ **完全免费** 无需额外费用
3. ✅ **自动化程度高** 推送即构建
4. ✅ **功能强大** Release 自动管理
5. ✅ **社区活跃** 更多资源和支持

### 快速开始

```bash
# 推送到 GitHub（推荐）
git remote add origin https://github.com/gwj7401/meeting-minutes.git
git push -u origin master

# 查看自动构建
# https://github.com/gwj7401/meeting-minutes/actions

# 发布版本
git tag -a v1.0.0 -m "版本 1.0.0"
git push origin v1.0.0

# 查看 Release
# https://github.com/gwj7401/meeting-minutes/releases
```

### 文档链接

- **GitHub 快速开始**：[GITHUB_QUICKSTART.md](GITHUB_QUICKSTART.md)
- **GitHub 详细指南**：[GITHUB_DEPLOY_GUIDE.md](GITHUB_DEPLOY_GUIDE.md)
- **Gitee 部署指南**：[GITEE_DEPLOY_GUIDE.md](GITEE_DEPLOY_GUIDE.md)
- **通用快速开始**：[START_HERE.md](START_HERE.md)

---

**推荐：使用 GitHub，享受完全自动化的构建和发布！** 🚀

