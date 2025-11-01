# 快速开始指南

5 分钟快速上手会议纪要 App 开发和部署。

## 📋 前置要求

- Git
- Android Studio（本地开发）
- Gitee 账号（在线构建）

## 🚀 快速部署到 Gitee

### 方法一：使用官方 Android 在线构建（推荐新手）

```bash
# 1. 克隆或下载项目
git clone https://gitee.com/your-username/meeting-minutes.git
cd meeting-minutes

# 2. 推送到你的 Gitee 仓库
git remote set-url origin https://gitee.com/your-username/your-repo.git
git push -u origin master

# 3. 在 Gitee 网页操作
# - 进入仓库 → 管理 → 基本信息
# - 设置"语言"为 Android
# - 回到首页，点击"构建 APK"
# - 选择分支和构建类型，开始构建
# - 构建完成后下载 APK
```

### 方法二：使用 Gitee Go CI/CD（推荐进阶）

```bash
# 1. 推送代码（同上）

# 2. 开通 Gitee Go
# - 进入仓库 → 服务 → Gitee Go
# - 开通服务

# 3. 自动构建
# - 推送到 develop 分支 → 自动构建 Debug APK
# - 推送到 master 分支 → 自动构建 Release APK
# - 创建 tag → 自动构建并发布
```

## 💻 本地开发

### 1. 环境准备

```bash
# 安装 Android Studio
# 下载地址：https://developer.android.com/studio

# 安装 JDK 11
# 下载地址：https://adoptium.net/
```

### 2. 导入项目

```bash
# 克隆项目
git clone https://gitee.com/your-username/meeting-minutes.git

# 用 Android Studio 打开项目
# File → Open → 选择项目目录
```

### 3. 配置 API 密钥

创建 `local.properties` 文件（如果不存在）：

```properties
# Android SDK 路径（Android Studio 会自动配置）
sdk.dir=/path/to/Android/sdk

# 语音识别 API（可选，用于测试）
ali.speech.appkey=your_app_key
ali.speech.token=your_token

# AI 模型 API（可选，用于测试）
openai.api.key=your_api_key
qianwen.api.key=your_api_key
```

### 4. 运行项目

```bash
# 方式一：使用 Android Studio
# 点击工具栏的 Run 按钮（绿色三角形）

# 方式二：使用命令行
./gradlew installDebug

# 方式三：构建 APK
./gradlew assembleDebug
# APK 位置：app/build/outputs/apk/debug/app-debug.apk
```

## 📦 构建 Release 版本

### 1. 生成签名文件

```bash
keytool -genkey -v -keystore meeting-minutes.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias meeting-minutes
```

### 2. 配置签名（本地）

在 `app/build.gradle` 中添加：

```gradle
android {
    signingConfigs {
        release {
            storeFile file('../meeting-minutes.jks')
            storePassword 'your_password'
            keyAlias 'meeting-minutes'
            keyPassword 'your_password'
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            // ...
        }
    }
}
```

### 3. 构建

```bash
./gradlew assembleRelease
# APK 位置：app/build/outputs/apk/release/app-release.apk
```

## 🏷️ 版本发布

### 创建新版本

```bash
# 1. 确保所有更改已提交
git add .
git commit -m "feat: 完成 v1.0.0 开发"

# 2. 创建标签
git tag -a v1.0.0 -m "版本 1.0.0

新功能：
- 实时录音转文字
- 支持多方言识别
- AI 智能总结
- 多格式导出
"

# 3. 推送标签
git push origin v1.0.0

# 4. Gitee Go 会自动构建并发布
```

### 版本号规则

- **主版本号.次版本号.修订号**（如 1.0.0）
- versionCode 自动基于 Git 提交次数
- versionName 自动基于 Git 标签

## 🔧 常用命令

```bash
# 清理项目
./gradlew clean

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease

# 运行测试
./gradlew test

# 安装到设备
./gradlew installDebug

# 查看所有任务
./gradlew tasks

# 查看依赖
./gradlew dependencies

# 检查代码
./gradlew lint
```

## 📱 功能测试

### 1. 录音功能

1. 打开应用
2. 点击右下角录音按钮
3. 授予麦克风权限
4. 选择方言类型
5. 点击"开始录音"
6. 说话测试
7. 点击"停止录音"
8. 输入标题并保存

### 2. 导入音频

1. 点击导入按钮
2. 选择音频文件
3. 等待识别完成
4. 查看转换结果

### 3. AI 总结

1. 打开已保存的记录
2. 点击"生成摘要"
3. 选择 AI 模型
4. 等待生成完成
5. 查看摘要结果

### 4. 导出文档

1. 在编辑界面
2. 点击"导出文档"
3. 选择导出格式
4. 等待导出完成
5. 分享或保存文件

## 🐛 常见问题

### 构建失败

```bash
# 清理并重新构建
./gradlew clean
./gradlew build --refresh-dependencies
```

### 依赖下载慢

项目已配置阿里云镜像，如仍然很慢：

```bash
# 使用代理
./gradlew build -Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=7890
```

### 权限问题

```bash
# Linux/Mac 给 gradlew 添加执行权限
chmod +x gradlew
```

### 模拟器无法录音

使用真机测试录音功能。

## 📚 下一步

- 阅读 [README.md](README.md) 了解详细功能
- 阅读 [DEPLOYMENT.md](DEPLOYMENT.md) 了解部署细节
- 查看代码注释了解实现原理
- 自定义 UI 和功能

## 🎯 核心文件说明

```
meeting-minutes/
├── app/
│   ├── src/main/
│   │   ├── java/com/meetingminutes/app/
│   │   │   ├── ui/              # UI 界面
│   │   │   │   ├── MainActivity.kt      # 主界面
│   │   │   │   ├── RecordActivity.kt    # 录音界面
│   │   │   │   └── EditorActivity.kt    # 编辑界面
│   │   │   ├── recorder/        # 录音功能
│   │   │   ├── speech/          # 语音识别
│   │   │   ├── ai/              # AI 总结
│   │   │   ├── export/          # 文档导出
│   │   │   ├── update/          # 版本管理
│   │   │   └── data/            # 数据层
│   │   └── res/                 # 资源文件
│   └── build.gradle             # 应用配置
├── build.gradle                 # 项目配置
├── .gitee-ci.yml               # CI/CD 配置
└── README.md                    # 项目说明
```

## 💡 开发提示

### 1. 语音识别

需要申请对应平台的 API：
- 阿里云：https://nls.console.aliyun.com/
- 腾讯云：https://console.cloud.tencent.com/asr
- 讯飞：https://www.xfyun.cn/

### 2. AI 总结

需要申请 AI 模型 API：
- OpenAI：https://platform.openai.com/
- 通义千问：https://dashscope.aliyun.com/
- 文心一言：https://cloud.baidu.com/product/wenxinworkshop

### 3. 测试建议

- 使用本地总结功能测试基本流程
- 配置一个 API 测试完整功能
- 在真机上测试录音功能

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

```bash
# Fork 项目
# 创建功能分支
git checkout -b feature/your-feature

# 提交更改
git commit -am 'Add some feature'

# 推送分支
git push origin feature/your-feature

# 创建 Pull Request
```

## 📞 获取帮助

- 查看 [Issues](https://gitee.com/your-username/meeting-minutes/issues)
- 阅读 [Gitee 文档](https://gitee.com/help)
- 联系开发者

---

**开始你的开发之旅吧！** 🚀

