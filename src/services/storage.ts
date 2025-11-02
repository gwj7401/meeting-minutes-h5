import localforage from 'localforage'
import type { Meeting } from '@/types'

const meetingStore = localforage.createInstance({
  name: 'meeting-minutes',
  storeName: 'meetings'
})

const audioStore = localforage.createInstance({
  name: 'meeting-minutes',
  storeName: 'audio'
})

export class StorageService {
  async saveMeeting(meeting: Meeting): Promise<void> {
    await meetingStore.setItem(meeting.id, meeting)
  }

  async getMeeting(id: string): Promise<Meeting | null> {
    return await meetingStore.getItem<Meeting>(id)
  }

  async getAllMeetings(): Promise<Meeting[]> {
    const meetings: Meeting[] = []
    await meetingStore.iterate<Meeting, void>((value) => {
      meetings.push(value)
    })
    return meetings.sort((a, b) => b.createdAt - a.createdAt)
  }

  async deleteMeeting(id: string): Promise<void> {
    await meetingStore.removeItem(id)
    await audioStore.removeItem(id)
  }

  async saveAudio(id: string, blob: Blob): Promise<void> {
    await audioStore.setItem(id, blob)
  }

  async getAudio(id: string): Promise<Blob | null> {
    return await audioStore.getItem<Blob>(id)
  }

  async getAudioUrl(id: string): Promise<string | null> {
    const blob = await this.getAudio(id)
    if (!blob) return null
    return URL.createObjectURL(blob)
  }

  async clearAll(): Promise<void> {
    await meetingStore.clear()
    await audioStore.clear()
  }
}

export const storage = new StorageService()

