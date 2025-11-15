<template>
  <div class="record-view">
    <van-nav-bar
      title="录音"
      left-text="返回"
      left-arrow
      @click-left="handleBack"
      fixed
    />

    <div class="content">
      <div class="recording-area">
        <!-- 网络状态提示 -->
        <div v-if="!isRecording" class="network-status" :class="networkStatusClass">
          <van-icon :name="networkStatusIcon" />
          <span>{{ networkStatusText }}</span>
        </div>

        <div class="wave-container" :class="{ active: isRecording }">
          <div class="wave"></div>
          <div class="wave"></div>
          <div class="wave"></div>
        </div>

        <div class="duration">{{ formatDuration(duration) }}</div>

        <div v-if="!isRecording" class="dialect-selector">
          <van-field
            v-model="selectedDialectName"
            label="识别语言"
            placeholder="选择语言"
            readonly
            is-link
            @click="showDialectPicker = true"
          />
        </div>
      </div>

      <div class="text-area">
        <div v-if="isRecording" class="recognition-status">
          <van-icon name="volume-o" class="listening-icon" :class="{ active: isSpeaking || interimText }" />
          <span class="status-text">{{ interimText ? '正在识别...' : (isSpeaking ? '正在监听...' : '等待说话...') }}</span>
        </div>
        <div class="text-content">
          <p v-if="recognizedText || interimText">
            {{ recognizedText }}
            <span class="interim">{{ interimText }}</span>
          </p>
          <van-empty v-else description="开始录音后，文字将实时显示在这里" />
        </div>
      </div>

      <div class="controls">
        <van-button
          v-if="!isRecording"
          type="primary"
          size="large"
          round
          @click="startRecording"
        >
          开始录音
        </van-button>
        <van-button
          v-else
          type="danger"
          size="large"
          round
          @click="stopRecording"
        >
          停止录音
        </van-button>
      </div>

      <!-- 调试日志面板 -->
      <div v-if="debugLogs.length > 0" class="debug-panel">
        <div class="debug-header" @click="showDebugLogs = !showDebugLogs">
          <span>调试日志 ({{ debugLogs.length }})</span>
          <van-icon :name="showDebugLogs ? 'arrow-up' : 'arrow-down'" />
        </div>
        <div v-show="showDebugLogs" class="debug-content">
          <div v-for="(log, index) in debugLogs" :key="index" class="debug-log">
            {{ log }}
          </div>
        </div>
      </div>
    </div>

    <van-popup v-model:show="showDialectPicker" position="bottom">
      <van-picker
        :columns="dialectColumns"
        @confirm="onDialectConfirm"
        @cancel="showDialectPicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMeetingStore } from '@/stores/meeting'
import { unifiedSpeechRecognition } from '@/services/speechRecognitionUnified'
import { audioRecorder } from '@/services/audioRecorder'
import { DIALECTS, getDialectLang, getDialectName } from '@/utils/dialects'
import { showToast, showConfirmDialog } from 'vant'

const router = useRouter()
const meetingStore = useMeetingStore()

const isRecording = ref(false)
const duration = ref(0)
const recognizedText = ref('')
const interimText = ref('')
const selectedDialect = ref('mandarin')
const debugLogs = ref<string[]>([])
const showDebugLogs = ref(false)
const networkStatus = ref<'checking' | 'online' | 'offline' | 'google-blocked'>('checking')
const isSpeaking = ref(false) // 是否正在说话

// 添加调试日志
function addDebugLog(message: string) {
  const timestamp = new Date().toLocaleTimeString()
  debugLogs.value.unshift(`[${timestamp}] ${message}`)
  if (debugLogs.value.length > 20) {
    debugLogs.value.pop()
  }
  console.log(message)
}

// 检测网络状态和语音识别服务可用性
async function checkNetworkAndSpeechService() {
  addDebugLog('开始检测网络状态...')
  networkStatus.value = 'checking'

  // 1. 检查基本网络连接
  if (!navigator.onLine) {
    addDebugLog('设备离线')
    networkStatus.value = 'offline'
    return
  }

  // 2. 检查是否支持语音识别
  if (!unifiedSpeechRecognition.isSupported()) {
    addDebugLog('当前平台不支持语音识别')
    networkStatus.value = 'google-blocked'
    return
  }

  // 3. 检查语音识别服务可用性
  try {
    addDebugLog('检查语音识别服务可用性...')
    const result = await unifiedSpeechRecognition.checkAvailability()
    
    if (result.available) {
      networkStatus.value = 'online'
      addDebugLog('语音识别服务可用')
    } else {
      networkStatus.value = 'google-blocked'
      addDebugLog(`语音识别服务不可用: ${result.reason}`)
    }
  } catch (error) {
    addDebugLog(`检测异常: ${error}`)
    networkStatus.value = 'google-blocked'
  }
}

const showDialectPicker = ref(false)

let durationTimer: number | null = null

// 设置语音识别回调（只设置一次）
onMounted(async () => {
  console.log('页面加载完成，设置语音识别回调')
  addDebugLog('页面加载完成，设置语音识别回调')
  addDebugLog('>>> APP版本: v1.0.3 - 2024-11-15-18:40')
  addDebugLog(`语音识别服务类型: ${unifiedSpeechRecognition.getServiceType()}`)
  addDebugLog(`语音识别支持: ${unifiedSpeechRecognition.isSupported()}`)
  addDebugLog(`User Agent: ${navigator.userAgent}`)

  unifiedSpeechRecognition.onResult((result) => {
    addDebugLog(`收到识别结果: "${result.text}" (最终: ${result.isFinal})`)

    if (result.isFinal) {
      // 最终结果：追加到已识别文本
      const newText = result.text.trim()
      if (newText) {
        recognizedText.value += newText + ' '
        addDebugLog(`已添加最终文本，当前总长度: ${recognizedText.value.length}`)
      }
      interimText.value = ''
    } else {
      // 临时结果：显示在临时区域
      interimText.value = result.text
    }
  })

  unifiedSpeechRecognition.onError((error) => {
    addDebugLog(`识别错误: ${error}`)

    // 如果是网络错误，更新网络状态
    if (error.includes('网络错误')) {
      networkStatus.value = 'google-blocked'
      showToast({
        message: '语音识别服务连接失败\n录音功能正常，但无法实时转文字\n您可以继续录音，稍后手动输入文字',
        duration: 5000,
        wordBreak: 'break-word'
      })
    } else {
      showToast(error)
    }
  })

  // 监听声音开始
  unifiedSpeechRecognition.onSoundStart(() => {
    addDebugLog('检测到声音开始')
    isSpeaking.value = true
  })

  // 监听声音结束
  unifiedSpeechRecognition.onSoundEnd(() => {
    addDebugLog('检测到声音结束')
    isSpeaking.value = false
  })

  // 检测网络状态
  try {
    await checkNetworkAndSpeechService()
  } catch (error) {
    addDebugLog(`网络检测失败: ${error}`)
    // 原生平台默认设置为可用
    if (unifiedSpeechRecognition.getServiceType() === 'native') {
      networkStatus.value = 'online'
      addDebugLog('原生平台，默认设置为可用')
    }
  }

  // 监听网络状态变化
  window.addEventListener('online', checkNetworkAndSpeechService)
  window.addEventListener('offline', () => {
    networkStatus.value = 'offline'
    addDebugLog('网络已断开')
  })
})

const selectedDialectName = computed(() => getDialectName(selectedDialect.value))
const dialectColumns = computed(() => DIALECTS.map(d => ({ text: d.name, value: d.code })))

// 网络状态相关计算属性
const networkStatusText = computed(() => {
  switch (networkStatus.value) {
    case 'checking':
      return '正在检测网络...'
    case 'online':
      return '网络正常，支持实时转写'
    case 'offline':
      return '网络离线，仅支持录音'
    case 'google-blocked':
      return '无法访问语音识别服务，仅支持录音'
    default:
      return ''
  }
})

const networkStatusIcon = computed(() => {
  switch (networkStatus.value) {
    case 'checking':
      return 'clock-o'
    case 'online':
      return 'success'
    case 'offline':
      return 'warning-o'
    case 'google-blocked':
      return 'info-o'
    default:
      return 'info-o'
  }
})

const networkStatusClass = computed(() => {
  switch (networkStatus.value) {
    case 'online':
      return 'status-success'
    case 'offline':
      return 'status-warning'
    case 'google-blocked':
      return 'status-info'
    default:
      return ''
  }
})

function formatDuration(seconds: number): string {
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

function onDialectConfirm({ selectedOptions }: any) {
  selectedDialect.value = selectedOptions[0].value
  showDialectPicker.value = false
}

async function startRecording() {
  try {
    console.log('开始录音...')
    addDebugLog('用户点击开始录音')

    if (!unifiedSpeechRecognition.isSupported()) {
      showToast('浏览器不支持语音识别，请使用 Chrome 或 Edge')
      return
    }

    // 重置文本
    recognizedText.value = ''
    interimText.value = ''

    // 创建会议记录
    await meetingStore.createMeeting(selectedDialect.value)
    console.log('会议记录已创建')
    addDebugLog('会议记录已创建')

    // 先启动录音（这会触发麦克风权限请求）
    addDebugLog('正在请求麦克风权限...')
    try {
      await audioRecorder.start()
      console.log('录音已启动')
      addDebugLog('麦克风权限已授予，录音已启动')
    } catch (error: any) {
      addDebugLog(`麦克风权限被拒绝: ${error.message}`)
      throw error // 重新抛出错误，让外层 catch 处理
    }

    // 录音启动成功后，更新状态并启动计时器
    isRecording.value = true
    duration.value = 0
    durationTimer = window.setInterval(() => {
      duration.value++
    }, 1000)
    addDebugLog('计时器已启动')

    // 尝试启动语音识别
    const lang = getDialectLang(selectedDialect.value)
    console.log('准备启动语音识别，语言:', lang)
    addDebugLog(`准备启动语音识别，语言: ${lang}`)
    addDebugLog(`当前网络状态: ${networkStatus.value}`)
    
    // 原生平台强制尝试启动语音识别
    if (unifiedSpeechRecognition.getServiceType() === 'native') {
      addDebugLog('原生平台，强制启动语音识别')
      try {
        await unifiedSpeechRecognition.start(lang)
        addDebugLog('语音识别启动成功')
        showToast('开始录音，请说话...')
      } catch (error: any) {
        console.error('启动语音识别失败:', error)
        addDebugLog(`语音识别启动失败: ${error.message || error}`)
        showToast({
          message: `语音识别启动失败\n${error.message || '未知错误'}\n录音正常`,
          duration: 5000,
          wordBreak: 'break-word'
        })
      }
    } else {
      // Web 平台根据网络状态决定
      if (networkStatus.value === 'offline') {
        addDebugLog('网络离线，仅录音模式')
        showToast({
          message: '网络离线\n仅录音模式，无实时转写',
          duration: 3000
        })
      } else if (networkStatus.value === 'google-blocked') {
        addDebugLog('语音识别服务不可用，仅录音模式')
        showToast({
          message: '语音识别服务不可用\n仅录音模式，无实时转写',
          duration: 3000
        })
      } else {
        addDebugLog('Web 平台，启动语音识别')
        try {
          unifiedSpeechRecognition.start(lang)
          showToast('开始录音，请说话...')
        } catch (error) {
          console.warn('启动语音识别失败，但录音继续:', error)
          addDebugLog(`语音识别启动失败: ${error}`)
          showToast({
            message: '语音识别启动失败\n录音正常，无实时转写',
            duration: 3000
          })
        }
      }
    }
  } catch (error: any) {
    console.error('启动录音失败:', error)
    addDebugLog(`启动录音失败: ${error.message}`)
    
    // 清理状态
    isRecording.value = false
    if (durationTimer) {
      clearInterval(durationTimer)
      durationTimer = null
    }
    
    // 显示详细错误信息
    const errorMsg = error.message || '启动录音失败'
    showToast({
      message: errorMsg,
      duration: 5000,
      wordBreak: 'break-word'
    })
  }
}

async function stopRecording() {
  try {
    console.log('停止录音...')

    // 先更新状态，防止重复点击
    isRecording.value = false

    // 停止语音识别
    unifiedSpeechRecognition.stop()
    console.log('语音识别已停止')

    // 停止录音
    const { blob, duration: recordDuration } = await audioRecorder.stop()
    console.log('录音已停止，时长:', recordDuration)

    // 停止计时器
    if (durationTimer) {
      clearInterval(durationTimer)
      durationTimer = null
    }

    // 保存会议记录
    const meeting = meetingStore.currentMeeting
    if (meeting) {
      meeting.content = recognizedText.value.trim()
      meeting.duration = recordDuration
      await meetingStore.saveMeeting(meeting, blob)
      console.log('会议记录已保存')
    }

    showToast('录音已保存')

    // 延迟跳转，让用户看到提示
    setTimeout(() => {
      router.push('/')
    }, 1000)
  } catch (error: any) {
    console.error('停止录音失败:', error)
    showToast(error.message || '停止录音失败')
    isRecording.value = false
  }
}

async function handleBack() {
  if (isRecording.value) {
    try {
      await showConfirmDialog({
        title: '确认退出',
        message: '正在录音中，退出将丢失当前录音，确定要退出吗？',
      })
      
      unifiedSpeechRecognition.stop()
      await audioRecorder.stop()
      
      if (durationTimer) {
        clearInterval(durationTimer)
      }
      
      router.back()
    } catch {
      // 用户取消
    }
  } else {
    router.back()
  }
}

onUnmounted(() => {
  if (durationTimer) {
    clearInterval(durationTimer)
  }
  if (isRecording.value) {
    unifiedSpeechRecognition.stop()
  }
})
</script>

<style scoped>
.record-view {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.content {
  padding-top: 46px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.recording-area {
  flex: 0 0 auto;
  padding: 40px 20px;
  text-align: center;
  color: white;
}

.network-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 8px 16px;
  margin: 0 auto 20px;
  max-width: 300px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  font-size: 14px;
  color: white;
}

.network-status.status-success {
  background: rgba(76, 175, 80, 0.3);
}

.network-status.status-warning {
  background: rgba(255, 152, 0, 0.3);
}

.network-status.status-info {
  background: rgba(33, 150, 243, 0.3);
}

.wave-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  height: 80px;
  margin-bottom: 20px;
}

.wave {
  width: 4px;
  height: 20px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 2px;
  transition: all 0.3s ease;
}

.wave-container.active .wave {
  animation: wave 1s ease-in-out infinite;
}

.wave-container.active .wave:nth-child(2) {
  animation-delay: 0.2s;
}

.wave-container.active .wave:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes wave {
  0%, 100% {
    height: 20px;
  }
  50% {
    height: 60px;
  }
}

.duration {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 20px;
}

.dialect-selector {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  overflow: hidden;
}

.text-area {
  flex: 1;
  background: white;
  border-radius: 20px 20px 0 0;
  padding: 20px;
  overflow-y: auto;
}

.recognition-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  margin-bottom: 16px;
  background: #f7f8fa;
  border-radius: 12px;
  font-size: 14px;
  color: #969799;
}

.listening-icon {
  font-size: 20px;
  color: #969799;
  transition: all 0.3s ease;
}

.listening-icon.active {
  color: #1989fa;
  animation: pulse 1s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.8;
  }
}

.status-text {
  font-weight: 500;
}

.text-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
}

.interim {
  color: #999;
}

.controls {
  padding: 20px;
  background: white;
}

.debug-panel {
  background: white;
  border-top: 1px solid #eee;
  max-height: 200px;
  overflow: hidden;
}

.debug-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #f7f8fa;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #646566;
}

.debug-content {
  max-height: 150px;
  overflow-y: auto;
  padding: 10px 20px;
}

.debug-log {
  font-size: 12px;
  font-family: monospace;
  color: #666;
  padding: 4px 0;
  border-bottom: 1px solid #f0f0f0;
  word-break: break-all;
}
</style>

