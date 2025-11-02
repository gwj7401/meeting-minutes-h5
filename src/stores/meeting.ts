import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Meeting } from '@/types'
import { storage } from '@/services/storage'

export const useMeetingStore = defineStore('meeting', () => {
  const meetings = ref<Meeting[]>([])
  const currentMeeting = ref<Meeting | null>(null)

  async function loadMeetings() {
    meetings.value = await storage.getAllMeetings()
  }

  async function createMeeting(dialect: string): Promise<Meeting> {
    const meeting: Meeting = {
      id: Date.now().toString(),
      title: `会议记录 ${new Date().toLocaleString('zh-CN')}`,
      content: '',
      duration: 0,
      createdAt: Date.now(),
      updatedAt: Date.now(),
      dialect
    }
    
    currentMeeting.value = meeting
    return meeting
  }

  async function saveMeeting(meeting: Meeting, audioBlob?: Blob) {
    meeting.updatedAt = Date.now()

    // 保存音频（如果有）
    if (audioBlob) {
      console.log('保存音频文件...')
      await storage.saveAudio(meeting.id, audioBlob)
      console.log('音频文件已保存')
    }

    // 保存会议记录（不包含 audioUrl，因为 Blob URL 不能序列化）
    const meetingToSave = { ...meeting }
    delete meetingToSave.audioUrl
    console.log('保存会议记录...', meetingToSave)
    await storage.saveMeeting(meetingToSave)
    console.log('会议记录已保存')

    await loadMeetings()
  }

  async function getMeeting(id: string): Promise<Meeting | null> {
    const meeting = await storage.getMeeting(id)
    if (meeting && !meeting.audioUrl) {
      meeting.audioUrl = await storage.getAudioUrl(id) || undefined
    }
    return meeting
  }

  async function updateMeeting(meeting: Meeting) {
    await storage.saveMeeting(meeting)
    await loadMeetings()
  }

  async function deleteMeeting(id: string) {
    await storage.deleteMeeting(id)
    await loadMeetings()
  }

  return {
    meetings,
    currentMeeting,
    loadMeetings,
    createMeeting,
    saveMeeting,
    getMeeting,
    updateMeeting,
    deleteMeeting
  }
})

