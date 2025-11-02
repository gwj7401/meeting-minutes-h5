<template>
  <div class="detail-view">
    <van-nav-bar
      title="会议详情"
      left-text="返回"
      left-arrow
      @click-left="router.back()"
      fixed
    >
      <template #right>
        <van-icon name="share-o" size="20" @click="showExportMenu = true" style="margin-right: 16px" />
        <van-icon name="delete-o" size="20" @click="handleDelete" />
      </template>
    </van-nav-bar>

    <!-- 导出菜单 -->
    <van-action-sheet
      v-model:show="showExportMenu"
      :actions="exportActions"
      cancel-text="取消"
      close-on-click-action
      @select="onExportSelect"
    />

    <div v-if="meeting" class="content">
      <van-cell-group>
        <van-field
          v-model="meeting.title"
          label="标题"
          placeholder="请输入标题"
          @blur="handleSave"
        />
        <van-cell title="时长" :value="formatDuration(meeting.duration)" />
        <van-cell title="语言" :value="getDialectName(meeting.dialect)" />
        <van-cell title="创建时间" :value="formatDate(meeting.createdAt)" />
      </van-cell-group>

      <div v-if="meeting.audioUrl" class="audio-player">
        <audio :src="meeting.audioUrl" controls style="width: 100%"></audio>
      </div>

      <div class="text-section">
        <div class="section-header">
          <span>会议内容</span>
          <van-button size="small" type="primary" @click="handleCopy">
            复制
          </van-button>
        </div>
        <van-field
          v-model="meeting.content"
          type="textarea"
          placeholder="暂无内容"
          rows="10"
          autosize
          @blur="handleSave"
        />
      </div>
    </div>

    <van-empty v-else description="会议记录不存在" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMeetingStore } from '@/stores/meeting'
import { getDialectName } from '@/utils/dialects'
import { showToast, showConfirmDialog } from 'vant'
import type { Meeting } from '@/types'

const router = useRouter()
const route = useRoute()
const meetingStore = useMeetingStore()

const meeting = ref<Meeting | null>(null)
const showExportMenu = ref(false)

const exportActions = [
  { name: '导出为 TXT 文本', value: 'txt' },
  { name: '导出为 Markdown', value: 'md' },
  { name: '导出为 HTML', value: 'html' },
]

onMounted(async () => {
  const id = route.params.id as string
  meeting.value = await meetingStore.getMeeting(id)
})

function formatDate(timestamp: number): string {
  return new Date(timestamp).toLocaleString('zh-CN')
}

function formatDuration(seconds: number): string {
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}:${secs.toString().padStart(2, '0')}`
}

async function handleSave() {
  if (meeting.value) {
    await meetingStore.updateMeeting(meeting.value)
    showToast('保存成功')
  }
}

async function handleCopy() {
  if (meeting.value?.content) {
    try {
      await navigator.clipboard.writeText(meeting.value.content)
      showToast('已复制到剪贴板')
    } catch {
      showToast('复制失败')
    }
  }
}

async function handleDelete() {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: '删除后无法恢复，确定要删除这条记录吗？',
    })

    if (meeting.value) {
      await meetingStore.deleteMeeting(meeting.value.id)
      showToast('删除成功')
      router.back()
    }
  } catch {
    // 用户取消
  }
}

function onExportSelect(action: any) {
  if (!meeting.value) return

  switch (action.value) {
    case 'txt':
      exportAsTxt()
      break
    case 'md':
      exportAsMarkdown()
      break
    case 'html':
      exportAsHtml()
      break
  }
}

function exportAsTxt() {
  if (!meeting.value) return

  const content = `${meeting.value.title}
创建时间: ${formatDate(meeting.value.createdAt)}
时长: ${formatDuration(meeting.value.duration)}
语言: ${getDialectName(meeting.value.dialect)}

会议内容:
${meeting.value.content || '暂无内容'}
`

  downloadFile(content, `${meeting.value.title}.txt`, 'text/plain')
}

function exportAsMarkdown() {
  if (!meeting.value) return

  const content = `# ${meeting.value.title}

**创建时间**: ${formatDate(meeting.value.createdAt)}
**时长**: ${formatDuration(meeting.value.duration)}
**语言**: ${getDialectName(meeting.value.dialect)}

## 会议内容

${meeting.value.content || '暂无内容'}
`

  downloadFile(content, `${meeting.value.title}.md`, 'text/markdown')
}

function exportAsHtml() {
  if (!meeting.value) return

  const content = `<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${meeting.value.title}</title>
  <style>
    body {
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
      max-width: 800px;
      margin: 0 auto;
      padding: 20px;
      line-height: 1.6;
      color: #333;
    }
    h1 {
      color: #7c3aed;
      border-bottom: 2px solid #7c3aed;
      padding-bottom: 10px;
    }
    .meta {
      background: #f5f5f5;
      padding: 15px;
      border-radius: 8px;
      margin: 20px 0;
    }
    .meta-item {
      margin: 5px 0;
    }
    .meta-label {
      font-weight: bold;
      color: #666;
    }
    .content {
      white-space: pre-wrap;
      background: #fafafa;
      padding: 20px;
      border-radius: 8px;
      border-left: 4px solid #7c3aed;
    }
    h2 {
      color: #555;
      margin-top: 30px;
    }
  </style>
</head>
<body>
  <h1>${meeting.value.title}</h1>

  <div class="meta">
    <div class="meta-item">
      <span class="meta-label">创建时间:</span> ${formatDate(meeting.value.createdAt)}
    </div>
    <div class="meta-item">
      <span class="meta-label">时长:</span> ${formatDuration(meeting.value.duration)}
    </div>
    <div class="meta-item">
      <span class="meta-label">语言:</span> ${getDialectName(meeting.value.dialect)}
    </div>
  </div>

  <h2>会议内容</h2>
  <div class="content">${meeting.value.content || '暂无内容'}</div>
</body>
</html>
`

  downloadFile(content, `${meeting.value.title}.html`, 'text/html')
}

function downloadFile(content: string, filename: string, mimeType: string) {
  const blob = new Blob([content], { type: `${mimeType};charset=utf-8` })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)

  showToast('导出成功')
}
</script>

<style scoped>
.detail-view {
  width: 100%;
  min-height: 100vh;
  background: #f7f8fa;
}

.content {
  padding-top: 46px;
  padding-bottom: 20px;
}

.audio-player {
  padding: 16px;
  background: white;
  margin-top: 12px;
}

.text-section {
  margin-top: 12px;
  background: white;
  padding: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-weight: bold;
}
</style>

