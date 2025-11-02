<template>
  <van-overlay :show="showWarning" z-index="9999">
    <div class="security-warning">
      <div class="warning-content">
        <van-icon name="warning-o" size="60" color="#ff6b6b" />
        <h2>⚠️ 不安全的连接</h2>
        <p class="warning-text">
          当前使用的是局域网 IP 地址访问，浏览器不允许使用麦克风和语音识别功能。
        </p>
        
        <div class="solution">
          <h3>✅ 解决方案</h3>
          <div class="solution-item">
            <strong>方案 1：使用 localhost（推荐）</strong>
            <p>在地址栏中访问：</p>
            <div class="code-block" @click="copyToClipboard(localhostUrl)">
              {{ localhostUrl }}
              <van-icon name="copy" />
            </div>
          </div>
          
          <div class="solution-item">
            <strong>方案 2：构建 Android APK</strong>
            <p>如果要在手机上使用，建议直接安装 APK</p>
          </div>
        </div>

        <div class="buttons">
          <van-button type="primary" block @click="redirectToLocalhost">
            跳转到 localhost
          </van-button>
          <van-button plain block @click="showWarning = false" style="margin-top: 10px">
            我知道了，继续访问
          </van-button>
        </div>
      </div>
    </div>
  </van-overlay>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { showToast } from 'vant'

const showWarning = ref(false)
const localhostUrl = ref('')

onMounted(() => {
  // 检查是否使用了不安全的连接
  const isLocalhost = window.location.hostname === 'localhost' || 
                      window.location.hostname === '127.0.0.1' ||
                      window.location.hostname === '[::1]'
  
  const isHttps = window.location.protocol === 'https:'
  
  // 如果不是 localhost 且不是 HTTPS，显示警告
  if (!isLocalhost && !isHttps) {
    showWarning.value = true
    localhostUrl.value = `http://localhost:${window.location.port}${window.location.pathname}`
  }
})

function redirectToLocalhost() {
  window.location.href = localhostUrl.value
}

function copyToClipboard(text: string) {
  navigator.clipboard.writeText(text).then(() => {
    showToast('已复制到剪贴板')
  }).catch(() => {
    showToast('复制失败，请手动复制')
  })
}
</script>

<style scoped>
.security-warning {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 20px;
}

.warning-content {
  background: white;
  border-radius: 16px;
  padding: 30px;
  max-width: 500px;
  text-align: center;
}

.warning-content h2 {
  margin: 20px 0 10px;
  color: #ff6b6b;
  font-size: 24px;
}

.warning-text {
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
}

.solution {
  text-align: left;
  margin: 20px 0;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 8px;
}

.solution h3 {
  margin: 0 0 15px;
  color: #333;
  font-size: 18px;
}

.solution-item {
  margin-bottom: 15px;
}

.solution-item:last-child {
  margin-bottom: 0;
}

.solution-item strong {
  display: block;
  margin-bottom: 5px;
  color: #7c3aed;
}

.solution-item p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.code-block {
  background: #2d3748;
  color: #68d391;
  padding: 12px;
  border-radius: 6px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
  margin-top: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: background 0.2s;
}

.code-block:hover {
  background: #1a202c;
}

.buttons {
  margin-top: 20px;
}
</style>

