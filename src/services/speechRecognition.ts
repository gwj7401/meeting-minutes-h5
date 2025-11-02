import type { SpeechResult } from '@/types'

export class SpeechRecognitionService {
  private recognition: any
  private isRecognizing = false
  private resultCallback: ((result: SpeechResult) => void) | null = null
  private errorCallback: ((error: string) => void) | null = null

  constructor() {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
    
    if (!SpeechRecognition) {
      throw new Error('浏览器不支持语音识别，请使用 Chrome 或 Edge 浏览器')
    }

    this.recognition = new SpeechRecognition()
    this.setupRecognition()
  }

  private setupRecognition() {
    this.recognition.continuous = true
    this.recognition.interimResults = true
    this.recognition.maxAlternatives = 1

    this.recognition.onstart = () => {
      console.log('语音识别已启动')
    }

    this.recognition.onresult = (event: any) => {
      console.log('收到语音识别结果事件')
      const results = event.results
      const lastResult = results[results.length - 1]
      const transcript = lastResult[0].transcript
      const isFinal = lastResult.isFinal

      console.log('识别文本:', transcript, '是否最终:', isFinal)

      if (this.resultCallback) {
        this.resultCallback({
          text: transcript,
          isFinal,
          timestamp: Date.now()
        })
      }
    }

    this.recognition.onerror = (event: any) => {
      console.error('语音识别错误:', event.error, event)

      let errorMessage = '语音识别出错'
      switch (event.error) {
        case 'no-speech':
          errorMessage = '未检测到语音，请说话'
          break
        case 'audio-capture':
          errorMessage = '无法访问麦克风，请检查设备'
          break
        case 'not-allowed':
          errorMessage = '麦克风权限被拒绝，请允许访问麦克风'
          break
        case 'network':
          errorMessage = '网络错误，请检查网络连接'
          break
        case 'aborted':
          errorMessage = '语音识别被中止'
          break
      }

      if (this.errorCallback) {
        this.errorCallback(errorMessage)
      }
    }

    this.recognition.onend = () => {
      console.log('语音识别结束，isRecognizing:', this.isRecognizing)
      if (this.isRecognizing) {
        console.log('自动重启语音识别...')
        try {
          this.recognition.start()
        } catch (error) {
          console.error('重启识别失败:', error)
        }
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

  isSupported(): boolean {
    return !!(window.SpeechRecognition || window.webkitSpeechRecognition)
  }
}

export const speechRecognition = new SpeechRecognitionService()

