<div align="center">
  <img src="public/logo.svg" alt="会议纪要" width="150" />

  # 会议纪要

  **免费实时语音转文字应用**

  [![GitHub](https://img.shields.io/badge/GitHub-gwj7401-blue?logo=github)](https://github.com/gwj7401/meeting-minutes-h5)
  [![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
  [![Vue](https://img.shields.io/badge/Vue-3.x-brightgreen.svg)](https://vuejs.org/)
  [![TypeScript](https://img.shields.io/badge/TypeScript-5.x-blue.svg)](https://www.typescriptlang.org/)

  一个基于 Vue 3 + TypeScript 开发的移动端会议纪要应用，支持实时语音转文字功能
</div>

---

## ✨ 特性

- 🎤 **实时语音识别** - 边录音边转文字，基于 Web Speech API
- 🆓 **完全免费** - 使用浏览器原生 API，无需第三方付费服务
- 🗣️ **多方言支持** - 支持普通话、粤语、四川话、东北话、英语、日语等
- 💾 **本地存储** - 使用 IndexedDB 存储会议记录和音频文件，保护隐私
- 📤 **一键导出** - 支持导出为 TXT、Markdown、HTML 格式
- 🎨 **精美界面** - 现代化 UI 设计，流畅的动画效果
- 📱 **响应式设计** - 完美适配手机、平板、电脑
- 🔊 **录音回放** - 支持录音并回放，方便复查
- 🌈 **PWA 支持** - 可安装到主屏幕，像原生应用一样使用
- 🚀 **自动构建** - GitHub Actions 自动打包 Android APK

## 🛠️ 技术栈

- **前端框架**: Vue 3 + TypeScript
- **UI 组件**: Vant 4
- **状态管理**: Pinia
- **路由**: Vue Router
- **构建工具**: Vite
- **移动端打包**: Capacitor
- **本地存储**: LocalForage (IndexedDB)
- **语音识别**: Web Speech API (免费)

## 📦 快速开始

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

访问: `https://localhost:5173`

**注意**: Web Speech API 需要 HTTPS 环境。

### 构建生产版本

```bash
npm run build
```

## 📱 打包 Android APK

### 方法一：GitHub Actions 自动打包（推荐）

1. 将代码推送到 GitHub
2. GitHub Actions 会自动构建 APK
3. 在 Actions 页面下载构建好的 APK

### 方法二：本地打包

```bash
# 1. 构建 Web 应用
npm run build

# 2. 初始化 Android 项目（首次）
npm run android:init

# 3. 同步代码到 Android
npm run android:sync

# 4. 构建 APK
npm run android:build
```

APK 位置: `android/app/build/outputs/apk/release/app-release-unsigned.apk`

## 🎯 功能说明

### 实时语音识别

- 使用浏览器原生 Web Speech API
- 完全免费，无需 API Key
- 支持多语言识别
- 边录音边显示文字

### 会议管理

- 创建、编辑、删除会议记录
- 查看会议详情
- 复制会议内容
- 本地持久化存储

### 录音功能

- 高质量音频录制
- 自动保存到本地
- 支持音频播放

## 🌐 浏览器兼容性

- ✅ Chrome/Edge (推荐)
- ✅ Safari (iOS/macOS)
- ⚠️ Firefox (部分支持)

**注意**: Web Speech API 需要 HTTPS 环境。

## 📚 文档

- [快速开始指南](./QUICK_START.md)
- [功能特性详解](./FEATURES.md)
- [项目总结](./PROJECT_SUMMARY.md)
- [开始使用](./开始使用.md)

## 📄 许可证

MIT License

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

### 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

## 👨‍💻 作者

**多线程吉他**

- GitHub: [@gwj7401](https://github.com/gwj7401)
- 项目地址: [meeting-minutes-h5](https://github.com/gwj7401/meeting-minutes-h5)

---

## 🙏 致谢

- [Vue.js](https://vuejs.org/) - 渐进式 JavaScript 框架
- [Vant](https://vant-ui.github.io/) - 轻量、可靠的移动端组件库
- [Vite](https://vitejs.dev/) - 下一代前端构建工具
- [Dexie.js](https://dexie.org/) - IndexedDB 封装库
- [Web Speech API](https://developer.mozilla.org/en-US/docs/Web/API/Web_Speech_API) - 浏览器语音识别 API

---

## ⭐ Star History

如果这个项目对你有帮助，请给个 Star ⭐️

---

<div align="center">
  Made with ❤️ by 多线程吉他

  © 2025 会议纪要. All rights reserved.
</div>

