export interface Meeting {
  id: string
  title: string
  content: string
  audioUrl?: string
  duration: number
  createdAt: number
  updatedAt: number
  dialect: string
}

export interface SpeechResult {
  text: string
  isFinal: boolean
  timestamp: number
}

export enum RecordingState {
  IDLE = 'idle',
  RECORDING = 'recording',
  PAUSED = 'paused',
  STOPPED = 'stopped'
}

export interface Dialect {
  code: string
  name: string
  lang: string
}

