import type { SpeechResult } from '@/types'

export class SpeechRecognitionService {
  private recognition: any
  private isRecognizing = false
  private resultCallback: ((result: SpeechResult) => void) | null = null
  private errorCallback: ((error: string) => void) | null = null
  private startCallback: (() => void) | null = null
  private soundStartCallback: (() => void) | null = null
  private soundEndCallback: (() => void) | null = null
  private networkErrorCount = 0
  private maxNetworkErrors = 3
  private lastNetworkErrorTime = 0

  constructor() {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
    
    if (!SpeechRecognition) {
      throw new Error('浏览器不支持语音识别，请使用 Chrome 或 Edge 浏览器')
    }

    this.recognition = new SpeechRecognition()
    this.setupRecognition()
  }

  private setupRecognition() {
    // 启用连续识别，不会因为停顿而停止
    this.recognition.continuous = true

    // 启用临时结果，可以实时看到识别过程
    this.recognition.interimResults = true

    // 每个识别结果返回的候选数量
    this.recognition.maxAlternatives = 1

    console.log('语音识别配置:', {
      continuous: this.recognition.continuous,
      interimResults: this.recognition.interimResults,
      maxAlternatives: this.recognition.maxAlternatives
    })

    this.recognition.onstart = () => {
      console.log('语音识别已启动')
      if (this.startCallback) {
        this.startCallback()
      }
    }

    this.recognition.onsoundstart = () => {
      console.log('检测到声音开始')
      if (this.soundStartCallback) {
        this.soundStartCallback()
      }
    }

    this.recognition.onsoundend = () => {
      console.log('检测到声音结束')
      if (this.soundEndCallback) {
        this.soundEndCallback()
      }
    }

    this.recognition.onspeechstart = () => {
      console.log('检测到语音开始')
    }

    this.recognition.onspeechend = () => {
      console.log('检测到语音结束')
    }

    this.recognition.onresult = (event: any) => {
      console.log('收到语音识别结果事件，结果数量:', event.results.length, 'resultIndex:', event.resultIndex)

      // 收集所有已确认的最终结果
      let allFinalTranscript = ''
      for (let i = 0; i < event.results.length; i++) {
        if (event.results[i].isFinal) {
          allFinalTranscript += event.results[i][0].transcript
        }
      }

      // 收集当前的临时结果（只从 resultIndex 开始）
      let currentInterimTranscript = ''
      for (let i = event.resultIndex; i < event.results.length; i++) {
        const result = event.results[i]
        const transcript = result[0].transcript

        if (!result.isFinal) {
          currentInterimTranscript += transcript
          console.log('临时识别文本:', transcript)
        } else {
          console.log('最终识别文本:', transcript)
        }
      }

      // 发送结果给回调
      if (this.resultCallback) {
        // 如果有新的最终结果
        const hasNewFinal = Array.from(event.results).slice(event.resultIndex).some((r: any) => r.isFinal)

        if (hasNewFinal) {
          // 发送新的最终结果
          const newFinalText = Array.from(event.results)
            .slice(event.resultIndex)
            .filter((r: any) => r.isFinal)
            .map((r: any) => r[0].transcript)
            .join('')

          if (newFinalText) {
            console.log('发送最终结果:', newFinalText)
            this.resultCallback({
              text: newFinalText,
              isFinal: true,
              timestamp: Date.now()
            })
          }
        }

        // 发送临时结果（如果有）
        if (currentInterimTranscript) {
          console.log('发送临时结果:', currentInterimTranscript)
          this.resultCallback({
            text: currentInterimTranscript,
            isFinal: false,
            timestamp: Date.now()
          })
        }
      }
    }

    this.recognition.onerror = (event: any) => {
      console.error('语音识别错误:', event.error, event)

      let errorMessage = '语音识别出错'
      let shouldNotify = true

      switch (event.error) {
        case 'no-speech':
          // 没有检测到语音，这是正常的，不需要通知用户
          // 识别会自动重启
          errorMessage = '未检测到语音'
          shouldNotify = false
          console.log('未检测到语音，识别将继续...')
          break
        case 'audio-capture':
          errorMessage = '无法访问麦克风，请检查设备'
          this.isRecognizing = false
          break
        case 'not-allowed':
          errorMessage = '麦克风权限被拒绝，请允许访问麦克风'
          this.isRecognizing = false
          break
        case 'network':
          // 网络错误处理 - 统计错误次数
          const now = Date.now()
          // 如果距离上次网络错误超过30秒，重置计数器
          if (now - this.lastNetworkErrorTime > 30000) {
            this.networkErrorCount = 0
          }
          this.networkErrorCount++
          this.lastNetworkErrorTime = now

          console.warn(`网络错误 (${this.networkErrorCount}/${this.maxNetworkErrors})`)

          if (this.networkErrorCount >= this.maxNetworkErrors) {
            errorMessage = '网络连接失败，无法使用语音识别服务。\n\n可能原因：\n1. 网络未连接\n2. 无法访问Google服务\n3. VPN未正常工作\n\n录音功能正常，您可以继续录音。'
            this.isRecognizing = false
            shouldNotify = true
          } else {
            errorMessage = `网络连接不稳定 (${this.networkErrorCount}次)，正在重试...`
            shouldNotify = true
            // 网络错误时不停止识别，让它自动重试
          }
          break
        case 'aborted':
          errorMessage = '语音识别被中止'
          shouldNotify = false
          break
        case 'service-not-allowed':
          errorMessage = '语音识别服务不可用，可能需要特殊网络环境访问Google服务'
          this.isRecognizing = false
          break
        case 'bad-grammar':
          errorMessage = '语音识别配置错误'
          this.isRecognizing = false
          break
        default:
          errorMessage = `语音识别错误: ${event.error}`
          console.error('未知错误类型:', event.error)
          break
      }

      if (this.errorCallback && shouldNotify) {
        this.errorCallback(errorMessage)
      }
    }

    this.recognition.onend = () => {
      console.log('语音识别结束，isRecognizing:', this.isRecognizing)
      if (this.isRecognizing) {
        console.log('自动重启语音识别...')
        // 添加短暂延迟，避免立即重启导致的问题
        setTimeout(() => {
          if (this.isRecognizing) {
            try {
              this.recognition.start()
              console.log('语音识别已重启')
            } catch (error: any) {
              console.error('重启识别失败:', error)
              // 如果是因为已经在运行而失败，忽略错误
              if (error.message && error.message.includes('already started')) {
                console.log('识别已在运行中，忽略重启')
              }
            }
          }
        }, 100) // 100ms 延迟
      }
    }
  }

  start(lang: string = 'zh-CN') {
    if (this.isRecognizing) {
      console.log('语音识别已在运行中')
      return
    }

    console.log('启动语音识别，语言:', lang)
    this.recognition.lang = lang
    this.isRecognizing = true

    // 重置网络错误计数器
    this.networkErrorCount = 0
    this.lastNetworkErrorTime = 0

    try {
      this.recognition.start()
      console.log('语音识别 start() 调用成功')
    } catch (error) {
      console.error('启动识别失败:', error)
      this.isRecognizing = false
      throw error
    }
  }

  stop() {
    console.log('停止语音识别')
    this.isRecognizing = false

    // 重置网络错误计数器
    this.networkErrorCount = 0
    this.lastNetworkErrorTime = 0

    try {
      this.recognition.stop()
      console.log('语音识别 stop() 调用成功')
    } catch (error) {
      console.error('停止识别失败:', error)
    }
  }

  onResult(callback: (result: SpeechResult) => void) {
    this.resultCallback = callback
  }

  onError(callback: (error: string) => void) {
    this.errorCallback = callback
  }

  onStart(callback: () => void) {
    this.startCallback = callback
  }

  onSoundStart(callback: () => void) {
    this.soundStartCallback = callback
  }

  onSoundEnd(callback: () => void) {
    this.soundEndCallback = callback
  }

  isSupported(): boolean {
    return !!(window.SpeechRecognition || window.webkitSpeechRecognition)
  }
}

export const speechRecognition = new SpeechRecognitionService()

