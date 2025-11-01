# 项目文件清单

## 📁 项目结构

```
会议纪要/
├── 📄 项目配置文件
│   ├── build.gradle                    # 项目级 Gradle 配置
│   ├── settings.gradle                 # Gradle 设置
│   ├── gradle.properties               # Gradle 属性
│   ├── gradlew                         # Gradle Wrapper (Linux/Mac)
│   ├── gradlew.bat                     # Gradle Wrapper (Windows)
│   └── gradle/
│       └── wrapper/
│           └── gradle-wrapper.properties
│
├── 📱 应用模块
│   └── app/
│       ├── build.gradle                # 应用级 Gradle 配置
│       ├── proguard-rules.pro          # ProGuard 混淆规则
│       │
│       └── src/main/
│           ├── AndroidManifest.xml     # 应用清单文件
│           │
│           ├── 📂 java/com/meetingminutes/app/
│           │   ├── MeetingApp.kt       # Application 类
│           │   │
│           │   ├── 🎨 ui/              # UI 层
│           │   │   ├── MainActivity.kt
│           │   │   ├── RecordActivity.kt
│           │   │   ├── EditorActivity.kt
│           │   │   ├── SettingsActivity.kt
│           │   │   └── MeetingRecordAdapter.kt
│           │   │
│           │   ├── 💾 data/            # 数据层
│           │   │   ├── model/
│           │   │   │   └── MeetingRecord.kt
│           │   │   ├── dao/
│           │   │   │   └── MeetingRecordDao.kt
│           │   │   └── database/
│           │   │       └── AppDatabase.kt
│           │   │
│           │   ├── 🎙️ recorder/        # 录音模块
│           │   │   └── AudioRecorder.kt
│           │   │
│           │   ├── 🗣️ speech/          # 语音识别模块
│           │   │   ├── SpeechRecognizer.kt
│           │   │   └── AliSpeechRecognizer.kt
│           │   │
│           │   ├── 🤖 ai/              # AI 总结模块
│           │   │   └── AISummarizer.kt
│           │   │
│           │   ├── 📤 export/          # 文档导出模块
│           │   │   └── DocumentExporter.kt
│           │   │
│           │   └── 🔄 update/          # 版本管理模块
│           │       └── VersionManager.kt
│           │
│           └── 📂 res/                 # 资源文件
│               ├── layout/             # 布局文件
│               │   ├── activity_main.xml
│               │   ├── activity_record.xml
│               │   ├── activity_editor.xml
│               │   ├── activity_settings.xml
│               │   ├── item_meeting_record.xml
│               │   └── dialog_input_title.xml
│               │
│               ├── values/             # 值资源
│               │   ├── strings.xml
│               │   ├── colors.xml
│               │   └── themes.xml
│               │
│               ├── menu/               # 菜单
│               │   └── menu_main.xml
│               │
│               ├── xml/                # XML 配置
│               │   ├── file_paths.xml
│               │   ├── backup_rules.xml
│               │   └── data_extraction_rules.xml
│               │
│               └── mipmap-anydpi-v26/  # 应用图标
│                   ├── ic_launcher.xml
│                   └── ic_launcher_round.xml
│
├── 🔧 CI/CD 配置
│   └── .gitee-ci.yml                   # Gitee CI/CD 配置
│
├── 📚 文档
│   ├── README.md                       # 项目说明
│   ├── QUICKSTART.md                   # 快速开始指南
│   ├── DEPLOYMENT.md                   # 部署指南
│   ├── USAGE_GUIDE.md                  # 使用指南
│   ├── PROJECT_SUMMARY.md              # 项目总结
│   └── FILE_LIST.md                    # 本文件
│
├── 🔐 配置示例
│   └── local.properties.example        # 本地配置示例
│
├── 📜 其他
│   ├── .gitignore                      # Git 忽略文件
│   └── LICENSE                         # 开源协议
│
└── 📊 统计
    ├── 总文件数: 50+
    ├── 代码文件: 20+
    ├── 布局文件: 10+
    ├── 配置文件: 10+
    └── 文档文件: 10+
```

## 📋 核心文件说明

### 项目配置

| 文件 | 说明 | 重要性 |
|------|------|--------|
| `build.gradle` | 项目级构建配置，包含仓库和依赖 | ⭐⭐⭐⭐⭐ |
| `app/build.gradle` | 应用级配置，版本管理、依赖库 | ⭐⭐⭐⭐⭐ |
| `gradle.properties` | Gradle 属性配置 | ⭐⭐⭐ |
| `settings.gradle` | 项目设置 | ⭐⭐⭐ |
| `gradlew` / `gradlew.bat` | Gradle Wrapper 脚本 | ⭐⭐⭐⭐⭐ |

### 应用清单

| 文件 | 说明 | 重要性 |
|------|------|--------|
| `AndroidManifest.xml` | 应用配置、权限、组件声明 | ⭐⭐⭐⭐⭐ |

### 核心代码

| 文件 | 功能 | 代码行数 |
|------|------|----------|
| `MeetingApp.kt` | Application 入口 | ~30 |
| `MainActivity.kt` | 主界面 | ~200 |
| `RecordActivity.kt` | 录音界面 | ~250 |
| `EditorActivity.kt` | 编辑界面 | ~250 |
| `AudioRecorder.kt` | 录音功能 | ~200 |
| `AliSpeechRecognizer.kt` | 语音识别 | ~150 |
| `AISummarizer.kt` | AI 总结 | ~200 |
| `DocumentExporter.kt` | 文档导出 | ~200 |
| `VersionManager.kt` | 版本管理 | ~150 |
| `MeetingRecord.kt` | 数据模型 | ~60 |
| `AppDatabase.kt` | 数据库 | ~40 |

### 布局文件

| 文件 | 界面 | 复杂度 |
|------|------|--------|
| `activity_main.xml` | 主界面 | 中 |
| `activity_record.xml` | 录音界面 | 高 |
| `activity_editor.xml` | 编辑界面 | 高 |
| `activity_settings.xml` | 设置界面 | 低 |
| `item_meeting_record.xml` | 列表项 | 中 |

### 资源文件

| 文件 | 内容 | 数量 |
|------|------|------|
| `strings.xml` | 字符串资源 | 50+ |
| `colors.xml` | 颜色定义 | 15+ |
| `themes.xml` | 主题样式 | 5+ |

### CI/CD

| 文件 | 说明 | 重要性 |
|------|------|--------|
| `.gitee-ci.yml` | Gitee CI/CD 配置 | ⭐⭐⭐⭐⭐ |

### 文档

| 文件 | 内容 | 字数 |
|------|------|------|
| `README.md` | 项目介绍、功能说明 | 2000+ |
| `QUICKSTART.md` | 快速开始指南 | 1500+ |
| `DEPLOYMENT.md` | 详细部署说明 | 2500+ |
| `USAGE_GUIDE.md` | 使用指南 | 3000+ |
| `PROJECT_SUMMARY.md` | 项目总结 | 2000+ |

## 🎯 关键文件详解

### 1. build.gradle (app)

**作用**: 应用级构建配置

**关键内容**:
- 版本号自动管理（基于 Git）
- 依赖库配置
- 签名配置
- ProGuard 配置

**重要函数**:
```gradle
getVersionCodeFromGit()  // 从 Git 获取版本号
getVersionNameFromGit()  // 从 Git 获取版本名
getBuildTime()           // 获取构建时间
getGitCommit()           // 获取 Git 提交哈希
```

### 2. .gitee-ci.yml

**作用**: CI/CD 自动化配置

**关键阶段**:
- `build_debug`: 构建 Debug APK
- `build_release`: 构建 Release APK
- `test`: 运行测试
- `deploy_beta`: 部署到测试环境
- `deploy_production`: 发布到生产环境

**触发条件**:
- develop 分支 → Debug 构建
- master 分支 → Release 构建
- tags → 正式发布

### 3. AudioRecorder.kt

**作用**: 音频录制核心类

**关键方法**:
- `startRecording()`: 开始录音
- `pauseRecording()`: 暂停录音
- `resumeRecording()`: 继续录音
- `stopRecording()`: 停止录音
- `writeWavHeader()`: 写入 WAV 文件头

### 4. AISummarizer.kt

**作用**: AI 总结功能

**支持的 AI**:
- OpenAI GPT
- 通义千问
- 文心一言
- 本地总结

**关键方法**:
- `summarizeWithOpenAI()`
- `summarizeWithQianwen()`
- `summarizeWithErnie()`
- `simpleLocalSummary()`

### 5. DocumentExporter.kt

**作用**: 文档导出功能

**支持格式**:
- TXT
- Markdown
- Word (DOCX)
- PDF
- HTML

**关键方法**:
- `exportToTxt()`
- `exportToMarkdown()`
- `exportToWord()`
- `exportToPdf()`
- `exportToHtml()`

## 📊 代码统计

### 按语言

| 语言 | 文件数 | 代码行数 |
|------|--------|----------|
| Kotlin | 15 | ~2500 |
| XML | 15 | ~1000 |
| Gradle | 3 | ~300 |
| Markdown | 6 | ~10000 |

### 按模块

| 模块 | 文件数 | 代码行数 |
|------|--------|----------|
| UI | 5 | ~800 |
| 数据层 | 3 | ~200 |
| 录音 | 1 | ~200 |
| 语音识别 | 2 | ~200 |
| AI 总结 | 1 | ~200 |
| 文档导出 | 1 | ~200 |
| 版本管理 | 1 | ~150 |

## 🔍 文件依赖关系

```
MainActivity
├── MeetingRecordAdapter
├── RecordActivity
│   ├── AudioRecorder
│   └── SpeechRecognizer
├── EditorActivity
│   ├── AISummarizer
│   └── DocumentExporter
└── AppDatabase
    └── MeetingRecordDao
        └── MeetingRecord
```

## 📦 构建产物

### Debug 构建

```
app/build/outputs/apk/debug/
└── app-debug.apk
```

### Release 构建

```
app/build/outputs/apk/release/
├── MeetingMinutes-v1.0.0-1.apk
└── mapping.txt (ProGuard 映射文件)
```

## 🎯 下一步

### 必须完成

- [ ] 添加实际的应用图标
- [ ] 配置真实的 API Key
- [ ] 测试所有功能
- [ ] 修复可能的 Bug

### 建议添加

- [ ] 单元测试
- [ ] UI 测试
- [ ] 性能优化
- [ ] 更多文档

## 📞 文件相关问题

如有文件相关问题，请查看对应的文档或提交 Issue。

---

**文件清单完成！** 📋

