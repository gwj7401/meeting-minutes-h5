import { SpeechRecognition } from '@capacitor-community/speech-recognition'
import { Capacitor } from '@capacitor/core'
import type { SpeechResult } from '@/types'

export class NativeSpeechRecognitionService {
  private isRecognizing = false
  private resultCallback: ((result: SpeechResult) => void) | null = null
  private errorCallback: ((error: string) => void) | null = null
  private startCallback: (() => void) | null = null
  private isNative = false
  private listenersRegistered = false

  constructor() {
    this.isNative = Capacitor.isNativePlatform()
    console.log('语音识别服务初始化，原生平台:', this.isNative)
    
    // 在原生平台上立即注册事件监听器
    if (this.isNative) {
      this.registerListeners()
    }
  }

  private registerListeners() {
    if (this.listenersRegistered) {
      return
    }

    console.log('注册语音识别事件监听器')

    // 监听识别结果
    SpeechRecognition.addListener('partialResults', (data: { matches: string[] }) => {
      console.log('收到识别结果:', data)
      if (this.resultCallback && data.matches && data.matches.length > 0) {
        const text = data.matches[0]
        
        // 发送临时结果
        this.resultCallback({
          text: text,
          isFinal: false,
          timestamp: Date.now()
        })
        
        // 延迟发送最终结果
        setTimeout(() => {
          if (this.resultCallback) {
            this.resultCallback({
              text: text,
              isFinal: true,
              timestamp: Date.now()
            })
          }
        }, 500)
      }
    })

    // 监听识别状态
    SpeechRecognition.addListener('listeningState', (state: { status: 'started' | 'stopped' }) => {
      console.log('识别状态变化:', state)
      
      if (state.status === 'stopped' && this.isRecognizing) {
        // 自动重启识别（实现连续识别）
        console.log('检测到识别停止，准备自动重启...')
        setTimeout(() => {
          if (this.isRecognizing) {
            console.log('自动重启语音识别')
            this.start('zh-CN').catch(error => {
              console.error('重启识别失败:', error)
              if (this.errorCallback) {
                this.errorCallback('语音识别重启失败: ' + error.message)
              }
            })
          }
        }, 100)
      }
    })

    this.listenersRegistered = true
    console.log('事件监听器注册完成')
  }

  async checkPermissions(): Promise<boolean> {
    if (!this.isNative) {
      return true // Web 平台不需要检查权限
    }

    try {
      const { speechRecognition } = await SpeechRecognition.checkPermissions()
      console.log('语音识别权限状态:', speechRecognition)
      
      if (speechRecognition === 'granted') {
        return true
      }

      // 请求权限
      const result = await SpeechRecognition.requestPermissions()
      console.log('权限请求结果:', result)
      return result.speechRecognition === 'granted'
    } catch (error) {
      console.error('检查权限失败:', error)
      return false
    }
  }

  async isAvailable(): Promise<boolean> {
    if (!this.isNative) {
      return false // Web 平台使用原来的 Web Speech API
    }

    try {
      const result = await SpeechRecognition.available()
      console.log('语音识别服务可用性:', result)
      return result.available
    } catch (error) {
      console.error('检查服务可用性失败:', error)
      return false
    }
  }

  async start(lang: string = 'zh-CN') {
    if (this.isRecognizing) {
      console.log('语音识别已在运行中')
      return
    }

    if (!this.isNative) {
      throw new Error('原生语音识别仅在移动设备上可用')
    }

    console.log('启动原生语音识别，语言:', lang)

    // 检查权限
    const hasPermission = await this.checkPermissions()
    if (!hasPermission) {
      throw new Error('语音识别权限被拒绝')
    }

    // 检查服务可用性
    const available = await this.isAvailable()
    if (!available) {
      throw new Error('语音识别服务不可用')
    }

    this.isRecognizing = true

    try {
      // 启动语音识别
      await SpeechRecognition.start({
        language: lang,
        maxResults: 1,
        prompt: '请说话...',
        partialResults: true,
        popup: false
      })

      console.log('原生语音识别已启动')

      if (this.startCallback) {
        this.startCallback()
      }

    } catch (error: any) {
      console.error('启动原生识别失败:', error)
      this.isRecognizing = false
      throw error
    }
  }

  async stop() {
    console.log('停止原生语音识别')
    this.isRecognizing = false

    if (!this.isNative) {
      return
    }

    try {
      await SpeechRecognition.stop()
      // 移除所有监听器
      await SpeechRecognition.removeAllListeners()
      console.log('原生语音识别已停止')
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

  isSupported(): boolean {
    return this.isNative
  }
}

export const nativeSpeechRecognition = new NativeSpeechRecognitionService()
