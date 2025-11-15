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
import { speechRecognition } from '@/services/speechRecognition'
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
const showDebug = ref(false)
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
  if (!speechRecognition.isSupported()) {
    addDebugLog('浏览器不支持语音识别')
    networkStatus.value = 'google-blocked'
    return
  }

  // 3. 尝试测试语音识别服务连接
  try {
    addDebugLog('测试语音识别服务连接...')

    // 创建一个临时的语音识别实例进行测试
    const testRecognition = new (window.SpeechRecognition || window.webkitSpeechRecognition)()
    testRecognition.lang = 'zh-CN'
    testRecognition.continuous = false
    testRecognition.interimResults = false

    const testPromise = new Promise<boolean>((resolve) => {
      let resolved = false

      testRecognition.onstart = () => {
        addDebugLog('语音识别服务连接成功')
        if (!resolved) {
          resolved = true
          testRecognition.stop()
          resolve(true)
        }
      }

      testRecognition.onerror = (event: any) => {
        addDebugLog(`语音识别服务测试失败: ${event.error}`)
        if (!resolved) {
          resolved = true
          resolve(event.error !== 'network')
        }
      }

      // 5秒超时
      setTimeout(() => {
        if (!resolved) {
          resolved = true
          addDebugLog('语音识别服务测试超时')
          testRecognition.stop()
          resolve(false)
        }
      }, 5000)

      try {
        testRecognition.start()
      } catch (error) {
        addDebugLog(`启动测试识别失败: ${error}`)
        if (!resolved) {
          resolved = true
          resolve(false)
        }
      }
    })

    const isAvailable = await testPromise

    if (isAvailable) {
      networkStatus.value = 'online'
      addDebugLog('语音识别服务可用')
    } else {
      networkStatus.value = 'google-blocked'
      addDebugLog('语音识别服务不可用（可能被阻止）')
    }
  } catch (error) {
    addDebugLog(`网络检测异常: ${error}`)
    networkStatus.value = 'google-blocked'
  }
}

const showDialectPicker = ref(false)

let durationTimer: number | null = null

// 设置语音识别回调（只设置一次）
onMounted(async () => {
  addDebugLog('页面加载完成，设置语音识别回调')
  addDebugLog(`浏览器支持语音识别: ${speechRecognition.isSupported()}`)
  addDebugLog(`User Agent: ${navigator.userAgent}`)

  speechRecognition.onResult((result) => {
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

  speechRecognition.onError((error) => {
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
  speechRecognition.onSoundStart(() => {
    addDebugLog('检测到声音开始')
    isSpeaking.value = true
  })

  // 监听声音结束
  speechRecognition.onSoundEnd(() => {
    addDebugLog('检测到声音结束')
    isSpeaking.value = false
  })

  // 检测网络状态
  await checkNetworkAndSpeechService()

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

    if (!speechRecognition.isSupported()) {
      showToast('浏览器不支持语音识别，请使用 Chrome 或 Edge')
      return
    }

    // 检查网络状态并给出提示
    if (networkStatus.value === 'offline') {
      const confirmed = await showConfirmDialog({
        title: '网络离线',
        message: '当前网络离线，无法使用实时转写功能。\n\n录音功能正常，您可以继续录音，稍后手动输入文字。\n\n是否继续？',
      }).catch(() => false)

      if (!confirmed) return
    } else if (networkStatus.value === 'google-blocked') {
      const confirmed = await showConfirmDialog({
        title: '语音识别服务不可用',
        message: '无法连接到语音识别服务（可能需要特殊网络环境）。\n\n录音功能正常，您可以继续录音，稍后手动输入文字。\n\n是否继续？',
      }).catch(() => false)

      if (!confirmed) return
    }

    // 重置文本
    recognizedText.value = ''
    interimText.value = ''

    // 创建会议记录
    await meetingStore.createMeeting(selectedDialect.value)
    console.log('会议记录已创建')

    // 启动录音
    await audioRecorder.start()
    console.log('录音已启动')

    // 只在网络正常时启动语音识别
    if (networkStatus.value === 'online') {
      const lang = getDialectLang(selectedDialect.value)
      console.log('启动语音识别，语言:', lang)
      try {
        speechRecognition.start(lang)
      } catch (error) {
        console.warn('启动语音识别失败，但录音继续:', error)
        showToast('语音识别启动失败，但录音正常')
      }
    } else {
      console.log('网络状态不佳，跳过语音识别')
    }

    // 更新状态
    isRecording.value = true
    duration.value = 0

    // 启动计时器
    durationTimer = window.setInterval(() => {
      duration.value++
    }, 1000)

    const message = networkStatus.value === 'online'
      ? '开始录音，请说话...'
      : '开始录音（无实时转写），请说话...'
    showToast(message)
  } catch (error: any) {
    console.error('启动录音失败:', error)
    showToast(error.message || '启动录音失败')
    isRecording.value = false
  }
}

async function stopRecording() {
  try {
    console.log('停止录音...')

    // 先更新状态，防止重复点击
    isRecording.value = false

    // 停止语音识别
    speechRecognition.stop()
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
      
      speechRecognition.stop()
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
    speechRecognition.stop()
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
</style>

