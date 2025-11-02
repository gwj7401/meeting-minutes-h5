# 🔒 配置 HTTPS 以支持局域网访问

## 问题

使用局域网 IP（如 `http://192.168.2.248:5174`）访问时，`navigator.mediaDevices` 不可用，因为它需要安全上下文（HTTPS 或 localhost）。

## 解决方案

### 方案 1：使用 localhost（推荐）✅

直接访问：
```
http://localhost:5174
```

这是最简单的方案，无需任何配置。

---

### 方案 2：使用 mkcert 配置本地 HTTPS

如果需要在局域网其他设备上测试，需要配置 HTTPS。

#### 步骤 1: 安装 mkcert

**Windows (PowerShell 管理员):**

```powershell
# 使用 Chocolatey
choco install mkcert

# 或使用 Scoop
scoop bucket add extras
scoop install mkcert
```

如果没有 Chocolatey 或 Scoop，可以手动下载：
1. 访问 https://github.com/FiloSottile/mkcert/releases
2. 下载 `mkcert-v1.4.4-windows-amd64.exe`
3. 重命名为 `mkcert.exe`
4. 放到 `C:\Windows\System32\` 目录

#### 步骤 2: 安装本地 CA

```powershell
mkcert -install
```

这会在系统中安装一个本地证书颁发机构。

#### 步骤 3: 生成证书

在项目根目录运行：

```powershell
# 替换 192.168.2.248 为你的实际 IP
mkcert localhost 127.0.0.1 192.168.2.248 ::1
```

这会生成两个文件：
- `localhost+3.pem` (证书)
- `localhost+3-key.pem` (私钥)

#### 步骤 4: 修改 vite.config.ts

```bash
npm install --save-dev @vitejs/plugin-basic-ssl
```

然后修改 `vite.config.ts`:

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { VantResolver } from 'unplugin-vue-components/resolvers'
import fs from 'fs'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [VantResolver()],
      imports: ['vue', 'vue-router', 'pinia'],
      dts: 'src/auto-imports.d.ts',
    }),
    Components({
      resolvers: [VantResolver()],
      dts: 'src/components.d.ts',
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: '0.0.0.0',
    port: 5173,
    https: {
      key: fs.readFileSync('./localhost+3-key.pem'),
      cert: fs.readFileSync('./localhost+3.pem'),
    }
  }
})
```

#### 步骤 5: 重启开发服务器

```bash
npm run dev
```

现在可以通过 HTTPS 访问：
```
https://localhost:5173
https://192.168.2.248:5173
```

#### 步骤 6: 在手机上安装证书（可选）

如果要在手机上测试：

1. 找到 mkcert 的根证书：
   ```powershell
   mkcert -CAROOT
   ```

2. 将 `rootCA.pem` 文件传输到手机

3. 在手机上安装证书：
   - Android: 设置 → 安全 → 加密与凭据 → 从存储设备安装
   - iOS: 设置 → 通用 → VPN与设备管理 → 安装描述文件

---

### 方案 3：直接构建 Android APK（最推荐）✅

如果主要是在手机上使用，直接构建 APK 是最好的选择：

```bash
# 初始化 Android 项目
npm run android:init

# 同步代码
npm run android:sync

# 构建 APK
npm run android:build
```

APK 位置: `android/app/build/outputs/apk/debug/app-debug.apk`

安装到手机后，所有功能都可以正常使用，无需担心 HTTPS 问题。

---

## 快速解决

**现在立即解决问题：**

1. 在浏览器地址栏中，将 `http://192.168.2.248:5174` 改为：
   ```
   http://localhost:5174
   ```

2. 刷新页面

3. 重新测试录音功能

---

## 为什么 localhost 可以使用 HTTP？

浏览器将以下地址视为"安全上下文"：
- ✅ `https://` 任何域名
- ✅ `http://localhost`
- ✅ `http://127.0.0.1`
- ✅ `http://[::1]` (IPv6 localhost)
- ❌ `http://192.168.x.x` (局域网 IP)
- ❌ `http://其他域名`

所以在本地开发时，使用 `localhost` 是最简单的方案。

---

## 总结

| 方案 | 难度 | 适用场景 |
|------|------|----------|
| 使用 localhost | ⭐ 简单 | 本地开发 |
| 配置 HTTPS | ⭐⭐⭐ 中等 | 局域网测试 |
| 构建 APK | ⭐⭐ 简单 | 手机使用 |

**推荐：** 本地开发使用 localhost，手机测试直接构建 APK。

