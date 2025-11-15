import { SpeechRecognition } from '@capacitor-community/speech-recognition'
import { Capacitor } from '@capacitor/core'
import type { SpeechResult } from '@/types'

// 全局标记，确保监听器只注册一次
let listenersRegistered = false
let resultCallback: ((result: SpeechResult) => void) | null = null
let debugLogCallback: ((msg: string) => void) | null = null

// 立即注册全局监听器
function registerGlobalListeners() {
  if (listenersRegistered) {
    console.log('监听器已注册，跳过')
    if (debugLogCallback) debugLogCallback('监听器已注册，跳过')
    return
  }

  console.log('>>> 开始注册全局语音识别监听器')
  if (debugLogCallback) debugLogCallback('>>> 开始注册全局语音识别监听器')

  try {
    // 监听识别结果
    SpeechRecognition.addListener('partialResults', (data: { matches: string[] }) => {
      console.log('>>> 收到识别结果:', data)
      if (resultCallback && data.matches && data.matches.length > 0) {
        const text = data.matches[0]
        
        // 发送临时结果
        resultCallback({
          text: text,
          isFinal: false,
          timestamp: Date.now()
        })
        
        // 延迟发送最终结果
        setTimeout(() => {
          if (resultCallback) {
            resultCallback({
              text: text,
              isFinal: true,
              timestamp: Date.now()
            })
          }
        }, 500)
      }
    }).then(() => {
      console.log('>>> partialResults 监听器注册成功')
      if (debugLogCallback) debugLogCallback('>>> partialResults 监听器注册成功')
    }).catch(err => {
      console.error('>>> partialResults 监听器注册失败:', err)
      if (debugLogCallback) debugLogCallback('>>> partialResults 监听器注册失败: ' + err)
    })

    // 监听识别状态
    SpeechRecognition.addListener('listeningState', (state: { status: 'started' | 'stopped' }) => {
      console.log('>>> 识别状态变化:', state)
    }).then(() => {
      console.log('>>> listeningState 监听器注册成功')
      if (debugLogCallback) debugLogCallback('>>> listeningState 监听器注册成功')
    }).catch(err => {
      console.error('>>> listeningState 监听器注册失败:', err)
      if (debugLogCallback) debugLogCallback('>>> listeningState 监听器注册失败: ' + err)
    })

    listenersRegistered = true
    console.log('>>> 全局监听器注册完成')
    if (debugLogCallback) debugLogCallback('>>> 全局监听器注册完成')
  } catch (error) {
    console.error('>>> 注册监听器异常:', error)
  }
}

export class NativeSpeechRecognitionService {
  private isRecognizing = false
  private isNative = false

  constructor() {
    this.isNative = Capacitor.isNativePlatform()
    console.log('=== 原生语音识别服务初始化 ===')
    console.log('原生平台:', this.isNative)
    console.log('Platform:', Capacitor.getPlatform())
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

    // 确保监听器已注册
    console.log('确保监听器已注册...')
    if (debugLogCallback) debugLogCallback('确保监听器已注册...')
    registerGlobalListeners()

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
    resultCallback = callback
    console.log('设置结果回调函数')
  }

  onDebugLog(callback: (msg: string) => void) {
    debugLogCallback = callback
    console.log('设置调试日志回调函数')
  }

  onError(_callback: (error: string) => void) {
    // 原生平台暂不使用错误回调
    console.log('设置错误回调函数（原生平台暂不使用）')
  }

  onStart(_callback: () => void) {
    // 原生识别不需要 onStart 回调
    console.log('onStart 回调（原生平台忽略）')
  }

  isSupported(): boolean {
    return this.isNative
  }
}

export const nativeSpeechRecognition = new NativeSpeechRecognitionService()
