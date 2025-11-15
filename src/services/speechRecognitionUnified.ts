import { Capacitor } from '@capacitor/core'
import { speechRecognition as webSpeechRecognition } from './speechRecognition'
import { nativeSpeechRecognition } from './speechRecognitionNative'
import type { SpeechResult } from '@/types'

/**
 * 统一的语音识别服务
 * 自动选择 Web Speech API 或原生语音识别
 */
class UnifiedSpeechRecognitionService {
  private service: typeof webSpeechRecognition | typeof nativeSpeechRecognition
  private isNative: boolean

  constructor() {
    this.isNative = Capacitor.isNativePlatform()
    
    if (this.isNative) {
      console.log('使用原生语音识别服务')
      this.service = nativeSpeechRecognition
    } else {
      console.log('使用 Web 语音识别服务')
      this.service = webSpeechRecognition
    }
  }

  async start(lang: string = 'zh-CN') {
    return this.service.start(lang)
  }

  async stop() {
    return this.service.stop()
  }

  onResult(callback: (result: SpeechResult) => void) {
    this.service.onResult(callback)
  }

  onError(callback: (error: string) => void) {
    this.service.onError(callback)
  }

  onStart(callback: () => void) {
    this.service.onStart(callback)
  }

  onSoundStart(callback: () => void) {
    // 原生服务不支持 soundStart 事件
    if ('onSoundStart' in this.service) {
      (this.service as typeof webSpeechRecognition).onSoundStart(callback)
    }
  }

  onSoundEnd(callback: () => void) {
    // 原生服务不支持 soundEnd 事件
    if ('onSoundEnd' in this.service) {
      (this.service as typeof webSpeechRecognition).onSoundEnd(callback)
    }
  }

  isSupported(): boolean {
    return this.service.isSupported()
  }

  getServiceType(): 'web' | 'native' {
    return this.isNative ? 'native' : 'web'
  }

  async checkAvailability(): Promise<{ available: boolean; reason?: string }> {
    if (!this.isSupported()) {
      return {
        available: false,
        reason: '当前平台不支持语音识别'
      }
    }

    if (this.isNative) {
      // 检查原生服务
      try {
        const available = await (this.service as typeof nativeSpeechRecognition).isAvailable()
        if (!available) {
          return {
            available: false,
            reason: '原生语音识别服务不可用'
          }
        }

        const hasPermission = await (this.service as typeof nativeSpeechRecognition).checkPermissions()
        if (!hasPermission) {
          return {
            available: false,
            reason: '语音识别权限被拒绝'
          }
        }

        return { available: true }
      } catch (error: any) {
        return {
          available: false,
          reason: error.message || '检查服务失败'
        }
      }
    } else {
      // Web 平台
      return { available: true }
    }
  }
}

export const unifiedSpeechRecognition = new UnifiedSpeechRecognitionService()
