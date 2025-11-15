export class AudioRecorderService {
  private mediaRecorder: MediaRecorder | null = null
  private audioChunks: Blob[] = []
  private stream: MediaStream | null = null
  private startTime: number = 0

  async start(): Promise<void> {
    try {
      // 检查 mediaDevices API 是否可用
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        throw new Error('当前环境不支持录音功能。请使用 HTTPS 或 localhost 访问，或使用 Chrome/Edge 浏览器。')
      }

      console.log('请求麦克风权限...')
      this.stream = await navigator.mediaDevices.getUserMedia({
        audio: {
          echoCancellation: true,
          noiseSuppression: true,
          autoGainControl: true,
          // 增加音频采样率和音量增益
          sampleRate: 48000,
          channelCount: 1,
          volume: 1.0,
          // 添加音频约束以提高录音质量
          latency: 0,
          // 禁用回声消除可能会提高音量，但可能引入回声
          // echoCancellation: false,
        } as MediaTrackConstraints
      })
      console.log('麦克风权限已获取')

      // 尝试调整音频轨道的音量
      const audioTrack = this.stream.getAudioTracks()[0]
      if (audioTrack) {
        const capabilities = audioTrack.getCapabilities()
        console.log('音频轨道能力:', capabilities)

        // 如果支持音量控制，设置为最大
        try {
          const constraints: any = {}
          if (capabilities.volume) {
            constraints.volume = capabilities.volume.max || 1.0
          }
          if (capabilities.sampleRate) {
            constraints.sampleRate = capabilities.sampleRate.max || 48000
          }
          if (Object.keys(constraints).length > 0) {
            await audioTrack.applyConstraints(constraints)
            console.log('已应用音频约束:', constraints)
          }
        } catch (error) {
          console.warn('应用音频约束失败:', error)
        }
      }

      this.mediaRecorder = new MediaRecorder(this.stream, {
        mimeType: 'audio/webm;codecs=opus'
      })
      console.log('MediaRecorder 已创建')

      this.audioChunks = []
      this.startTime = Date.now()

      this.mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          console.log('收到音频数据块，大小:', event.data.size)
          this.audioChunks.push(event.data)
        }
      }

      this.mediaRecorder.onstart = () => {
        console.log('MediaRecorder 已启动')
      }

      this.mediaRecorder.start(1000)
      console.log('MediaRecorder start() 调用成功')
    } catch (error: any) {
      console.error('启动录音失败:', error)

      // 提供更详细的错误信息
      if (error.message && error.message.includes('不支持录音功能')) {
        throw error
      } else if (error.name === 'NotAllowedError') {
        throw new Error('麦克风权限被拒绝，请允许访问麦克风')
      } else if (error.name === 'NotFoundError') {
        throw new Error('未找到麦克风设备，请检查设备连接')
      } else if (error.name === 'NotReadableError') {
        throw new Error('麦克风被其他应用占用，请关闭其他使用麦克风的应用')
      } else {
        throw new Error('无法访问麦克风：' + error.message)
      }
    }
  }

  async stop(): Promise<{ blob: Blob; duration: number }> {
    return new Promise((resolve, reject) => {
      if (!this.mediaRecorder) {
        console.error('录音器未初始化')
        reject(new Error('录音器未初始化'))
        return
      }

      console.log('停止 MediaRecorder...')

      this.mediaRecorder.onstop = () => {
        console.log('MediaRecorder 已停止')
        const blob = new Blob(this.audioChunks, { type: 'audio/webm' })
        const duration = Math.floor((Date.now() - this.startTime) / 1000)
        console.log('音频 Blob 大小:', blob.size, '时长:', duration)

        if (this.stream) {
          this.stream.getTracks().forEach(track => {
            console.log('停止音频轨道:', track.label)
            track.stop()
          })
        }

        resolve({ blob, duration })
      }

      this.mediaRecorder.stop()
      console.log('MediaRecorder stop() 调用成功')
    })
  }

  isRecording(): boolean {
    return this.mediaRecorder?.state === 'recording'
  }
}

export const audioRecorder = new AudioRecorderService()

