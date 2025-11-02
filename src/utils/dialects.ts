import type { Dialect } from '@/types'

export const DIALECTS: Dialect[] = [
  { code: 'mandarin', name: '普通话', lang: 'zh-CN' },
  { code: 'cantonese', name: '粤语', lang: 'zh-HK' },
  { code: 'taiwanese', name: '台湾话', lang: 'zh-TW' },
  { code: 'english', name: 'English', lang: 'en-US' },
  { code: 'japanese', name: '日本語', lang: 'ja-JP' },
  { code: 'korean', name: '한국어', lang: 'ko-KR' },
]

export function getDialectLang(code: string): string {
  return DIALECTS.find(d => d.code === code)?.lang || 'zh-CN'
}

export function getDialectName(code: string): string {
  return DIALECTS.find(d => d.code === code)?.name || '普通话'
}

