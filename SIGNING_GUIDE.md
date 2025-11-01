# APK 签名配置指南

解决 "app-release-unsigned.apk" 无法安装的问题。

## 🔐 为什么需要签名？

Android 要求所有 APK 必须经过数字签名才能安装。未签名的 APK 会显示"安装失败"或"解析包时出现问题"。

## 🚀 快速解决方案

### 方法 1：配置 GitHub Actions 自动签名（推荐）

#### 步骤 1：生成签名文件

在项目根目录打开 PowerShell，执行：

```powershell
keytool -genkey -v -keystore meeting-minutes.jks -keyalg RSA -keysize 2048 -validity 10000 -alias meeting-minutes
```

**按提示输入信息**：

```
密钥库口令: 输入密码（例如：123456）并记住它！
再次输入新口令: 再次输入相同密码
您的名字与姓氏是什么? 输入你的名字（例如：Zhang San）
您的组织单位名称是什么? 输入：个人
您的组织名称是什么? 输入：个人
您所在的城市或区域名称是什么? 输入城市（例如：Beijing）
您所在的省/市/自治区名称是什么? 输入省份（例如：Beijing）
该单位的双字母国家/地区代码是什么? 输入：CN
正确吗? 输入：是 或 y
密钥口令: 直接回车（使用与密钥库相同的密码）
```

**完成后会生成 `meeting-minutes.jks` 文件。**

#### 步骤 2：转换为 Base64

```powershell
# 在 PowerShell 中执行
[Convert]::ToBase64String([IO.File]::ReadAllBytes("meeting-minutes.jks")) | Out-File keystore-base64.txt

# 查看生成的文件
notepad keystore-base64.txt
```

#### 步骤 3：配置 GitHub Secrets

1. **访问 GitHub Secrets 页面**：
   - https://github.com/gwj7401/meeting-minutes/settings/secrets/actions

2. **添加 4 个 Secrets**：

点击 "New repository secret"，依次添加：

**Secret 1: KEYSTORE_FILE**
- Name: `KEYSTORE_FILE`
- Value: 打开 `keystore-base64.txt`，复制全部内容粘贴

**Secret 2: KEYSTORE_PASSWORD**
- Name: `KEYSTORE_PASSWORD`
- Value: 你在步骤 1 设置的密钥库密码（例如：123456）

**Secret 3: KEY_ALIAS**
- Name: `KEY_ALIAS`
- Value: `meeting-minutes`

**Secret 4: KEY_PASSWORD**
- Name: `KEY_PASSWORD`
- Value: 你在步骤 1 设置的密钥密码（通常与密钥库密码相同）

#### 步骤 4：重新触发构建

配置完成后，有两种方式触发构建：

**方式 A：推送新的提交**
```bash
# 随便修改一个文件，然后提交
git add .
git commit -m "trigger build"
git push origin master
```

**方式 B：重新运行失败的工作流**
1. 访问：https://github.com/gwj7401/meeting-minutes/actions
2. 点击最新的失败构建
3. 点击右上角 "Re-run all jobs"

#### 步骤 5：下载已签名的 APK

构建成功后：
1. 在 Actions 页面点击成功的构建
2. 在 "Artifacts" 区域下载 `app-release`
3. 解压 ZIP 文件
4. 得到 **已签名** 的 APK
5. 安装到手机 ✅

---

### 方法 2：本地签名（临时方案）

如果你急需测试，可以在本地对未签名的 APK 进行签名。

#### 步骤 1：生成签名文件（如果还没有）

```powershell
keytool -genkey -v -keystore meeting-minutes.jks -keyalg RSA -keysize 2048 -validity 10000 -alias meeting-minutes
```

#### 步骤 2：签名 APK

```powershell
# 使用 jarsigner 签名
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore meeting-minutes.jks app-release-unsigned.apk meeting-minutes

# 输入密钥库密码
```

#### 步骤 3：验证签名

```powershell
jarsigner -verify -verbose -certs app-release-unsigned.apk
```

看到 "jar verified" 表示签名成功。

#### 步骤 4：安装

现在可以安装 `app-release-unsigned.apk` 了（虽然名字还是 unsigned，但已经签名了）。

---

### 方法 3：使用 Android Studio 签名

1. 用 Android Studio 打开项目
2. 菜单：Build → Generate Signed Bundle / APK
3. 选择 APK
4. 选择或创建 Key Store
5. 填写密码和别名
6. 选择 release
7. 点击 Finish

---

## 📋 检查清单

配置 GitHub Actions 签名前，确保：

- [ ] 已生成 `meeting-minutes.jks` 文件
- [ ] 已转换为 Base64 并保存到 `keystore-base64.txt`
- [ ] 已在 GitHub 添加 4 个 Secrets
- [ ] 记住了密钥库密码和密钥密码
- [ ] 已推送代码或重新运行构建

## ❓ 常见问题

### Q1: 找不到 keytool 命令？

**A**: keytool 是 JDK 自带的工具。

**解决方案**：
1. 确保已安装 JDK
2. 将 JDK 的 bin 目录添加到 PATH
3. 或使用完整路径：
   ```powershell
   "C:\Program Files\Java\jdk-17\bin\keytool.exe" -genkey ...
   ```

### Q2: GitHub Actions 构建还是生成未签名的 APK？

**A**: 检查以下几点：

1. **Secrets 是否正确配置**：
   - 访问：https://github.com/gwj7401/meeting-minutes/settings/secrets/actions
   - 确认 4 个 Secrets 都已添加

2. **Base64 内容是否完整**：
   - `KEYSTORE_FILE` 的内容应该很长（几千个字符）
   - 确保复制时没有遗漏

3. **密码是否正确**：
   - `KEYSTORE_PASSWORD` 和 `KEY_PASSWORD` 必须正确

4. **查看构建日志**：
   - 在 Actions 页面查看详细日志
   - 搜索 "signing" 相关信息

### Q3: 签名后 APK 还是无法安装？

**A**: 可能的原因：

1. **手机设置问题**：
   - 允许安装未知来源应用
   - 设置 → 安全 → 未知来源

2. **APK 损坏**：
   - 重新下载 APK
   - 检查文件大小是否正常

3. **版本冲突**：
   - 如果之前安装过，先卸载旧版本
   - 或使用不同的 applicationId

### Q4: 忘记了密钥库密码怎么办？

**A**: 密钥库密码无法找回，只能重新生成：

1. 删除旧的 `meeting-minutes.jks`
2. 重新执行步骤 1 生成新的签名文件
3. 重新配置 GitHub Secrets

**注意**：新签名的 APK 无法覆盖安装旧签名的 APK，需要先卸载。

### Q5: 如何查看 APK 是否已签名？

**方法 1：使用 jarsigner**
```powershell
jarsigner -verify -verbose app-release.apk
```

**方法 2：使用 apksigner（Android SDK 工具）**
```powershell
apksigner verify --verbose app-release.apk
```

**方法 3：查看文件名**
- 未签名：`app-release-unsigned.apk`
- 已签名：`app-release.apk` 或 `MeetingMinutes-v1.0.0.apk`

## 🎯 推荐流程

1. **首次配置**：
   - 生成签名文件
   - 配置 GitHub Secrets
   - 推送代码触发构建

2. **日常开发**：
   - 修改代码
   - 推送到 GitHub
   - GitHub Actions 自动构建并签名
   - 下载已签名的 APK

3. **发布版本**：
   ```bash
   git tag -a v1.0.0 -m "版本 1.0.0"
   git push origin v1.0.0
   ```
   - GitHub Actions 自动创建 Release
   - 自动上传已签名的 APK

## 🔒 安全提示

1. **保护签名文件**：
   - ⚠️ 不要将 `.jks` 文件提交到 Git
   - ⚠️ 不要分享密钥库密码
   - ✅ 将 `.jks` 添加到 `.gitignore`

2. **备份签名文件**：
   - ✅ 将 `meeting-minutes.jks` 备份到安全的地方
   - ✅ 记录密码（使用密码管理器）
   - ⚠️ 丢失签名文件将无法更新应用

3. **GitHub Secrets**：
   - ✅ Secrets 是加密存储的
   - ✅ 只有仓库所有者可以查看
   - ✅ 不会出现在日志中

## 📞 需要帮助？

如果遇到问题：

1. 查看 GitHub Actions 构建日志
2. 检查 Secrets 配置
3. 提交 Issue：https://github.com/gwj7401/meeting-minutes/issues

---

**配置完成后，每次构建都会自动生成已签名的 APK！** 🎉

