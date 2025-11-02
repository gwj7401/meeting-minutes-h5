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
const showDialectPicker = ref(false)

let durationTimer: number | null = null

// 设置语音识别回调（只设置一次）
onMounted(() => {
  console.log('设置语音识别回调')

  speechRecognition.onResult((result) => {
    console.log('收到识别结果:', result)
    if (result.isFinal) {
      recognizedText.value += result.text + ' '
      interimText.value = ''
    } else {
      interimText.value = result.text
    }
  })

  speechRecognition.onError((error) => {
    console.error('识别错误:', error)

    // 如果是网络错误，给出更友好的提示
    if (error.includes('网络错误')) {
      showToast({
        message: '语音识别服务连接失败\n录音功能正常，但无法实时转文字\n您可以继续录音，稍后手动输入文字',
        duration: 5000,
        wordBreak: 'break-word'
      })
    } else {
      showToast(error)
    }
  })
})

const selectedDialectName = computed(() => getDialectName(selectedDialect.value))
const dialectColumns = computed(() => DIALECTS.map(d => ({ text: d.name, value: d.code })))

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

    // 重置文本
    recognizedText.value = ''
    interimText.value = ''

    // 创建会议记录
    await meetingStore.createMeeting(selectedDialect.value)
    console.log('会议记录已创建')

    // 启动录音
    await audioRecorder.start()
    console.log('录音已启动')

    // 启动语音识别
    const lang = getDialectLang(selectedDialect.value)
    console.log('启动语音识别，语言:', lang)
    speechRecognition.start(lang)

    // 更新状态
    isRecording.value = true
    duration.value = 0

    // 启动计时器
    durationTimer = window.setInterval(() => {
      duration.value++
    }, 1000)

    showToast('开始录音，请说话...')
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

