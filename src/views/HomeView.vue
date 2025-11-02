<template>
  <div class="home-view">
    <van-nav-bar title="会议纪要" fixed>
      <template #right>
        <van-icon name="setting-o" size="20" @click="showSettings = true" />
      </template>
    </van-nav-bar>

    <div class="content">
      <van-empty v-if="meetings.length === 0" description="暂无会议记录" />
      
      <van-swipe-cell v-for="meeting in meetings" :key="meeting.id">
        <van-cell
          :title="meeting.title"
          :label="`${formatDuration(meeting.duration)} · ${getDialectName(meeting.dialect)}`"
          is-link
          @click="goToDetail(meeting.id)"
          @long-press="showExportDialog(meeting)"
        >
          <template #value>
            {{ formatDate(meeting.createdAt) }}
          </template>
        </van-cell>

        <template #right>
          <van-button
            square
            type="primary"
            text="导出"
            class="export-button"
            @click="showExportDialog(meeting)"
          />
          <van-button
            square
            type="danger"
            text="删除"
            class="delete-button"
            @click="handleDelete(meeting.id)"
          />
        </template>
      </van-swipe-cell>
    </div>

    <van-floating-bubble
      icon="plus"
      @click="goToRecord"
    />

    <van-popup v-model:show="showSettings" position="bottom" :style="{ height: '30%' }">
      <van-cell-group>
        <van-cell title="清空所有记录" is-link @click="handleClearAll" />
        <van-cell title="关于" is-link @click="showAbout = true" />
      </van-cell-group>
    </van-popup>

    <!-- 关于对话框 -->
    <van-popup v-model:show="showAbout" round :style="{ padding: '30px 20px' }">
      <div class="about-dialog">
        <div class="about-logo">
          <img src="/logo.svg" alt="会议纪要" />
        </div>

        <h2 class="about-title">会议纪要</h2>
        <p class="about-version">v1.0.0</p>

        <div class="about-description">
          <p>📝 免费实时语音转文字应用</p>
          <p>🎤 支持多种方言识别</p>
          <p>📤 一键导出多种格式</p>
          <p>💾 本地存储，保护隐私</p>
        </div>

        <div class="about-tech">
          <p class="tech-label">技术栈</p>
          <div class="tech-tags">
            <span class="tech-tag">Vue 3</span>
            <span class="tech-tag">TypeScript</span>
            <span class="tech-tag">Vant 4</span>
            <span class="tech-tag">Web Speech API</span>
          </div>
        </div>

        <div class="about-author">
          <div class="author-avatar">🎸</div>
          <div class="author-info-text">
            <p class="author-name">作者：多线程吉他</p>
            <a
              href="https://github.com/gwj7401/meeting-minutes-h5"
              target="_blank"
              class="github-link"
            >
              <van-icon name="star-o" />
              <span>GitHub 开源项目</span>
            </a>
          </div>
        </div>

        <van-button
          type="primary"
          block
          round
          @click="showAbout = false"
          class="about-close-btn"
        >
          知道了
        </van-button>
      </div>
    </van-popup>

    <!-- 导出菜单 -->
    <van-action-sheet
      v-model:show="showExportMenu"
      :actions="exportActions"
      cancel-text="取消"
      close-on-click-action
      @select="onExportSelect"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMeetingStore } from '@/stores/meeting'
import { getDialectName } from '@/utils/dialects'
import { showConfirmDialog, showToast } from 'vant'

const router = useRouter()
const meetingStore = useMeetingStore()
const showSettings = ref(false)
const showAbout = ref(false)
const showExportMenu = ref(false)
const currentMeeting = ref<any>(null)

const meetings = computed(() => meetingStore.meetings)

const exportActions = [
  { name: '导出为 TXT 文本', value: 'txt' },
  { name: '导出为 Markdown', value: 'md' },
  { name: '导出为 HTML', value: 'html' },
]

function formatDate(timestamp: number): string {
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  
  return date.toLocaleDateString('zh-CN')
}

function formatDuration(seconds: number): string {
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}:${secs.toString().padStart(2, '0')}`
}

function goToRecord() {
  router.push('/record')
}

function goToDetail(id: string) {
  router.push(`/detail/${id}`)
}

async function handleDelete(id: string) {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: '删除后无法恢复，确定要删除这条记录吗？',
    })
    
    await meetingStore.deleteMeeting(id)
    showToast('删除成功')
  } catch {
    // 用户取消
  }
}

async function handleClearAll() {
  try {
    await showConfirmDialog({
      title: '确认清空',
      message: '将删除所有会议记录，此操作无法恢复！',
    })

    for (const meeting of meetings.value) {
      await meetingStore.deleteMeeting(meeting.id)
    }

    showSettings.value = false
    showToast('已清空所有记录')
  } catch {
    // 用户取消
  }
}

function showExportDialog(meeting: any) {
  currentMeeting.value = meeting
  showExportMenu.value = true
}

function onExportSelect(action: any) {
  if (!currentMeeting.value) return

  switch (action.value) {
    case 'txt':
      exportAsTxt(currentMeeting.value)
      break
    case 'md':
      exportAsMarkdown(currentMeeting.value)
      break
    case 'html':
      exportAsHtml(currentMeeting.value)
      break
  }
}

function exportAsTxt(meeting: any) {
  const content = `${meeting.title}
创建时间: ${new Date(meeting.createdAt).toLocaleString('zh-CN')}
时长: ${formatDuration(meeting.duration)}
语言: ${getDialectName(meeting.dialect)}

会议内容:
${meeting.content || '暂无内容'}
`

  downloadFile(content, `${meeting.title}.txt`, 'text/plain')
}

function exportAsMarkdown(meeting: any) {
  const content = `# ${meeting.title}

**创建时间**: ${new Date(meeting.createdAt).toLocaleString('zh-CN')}
**时长**: ${formatDuration(meeting.duration)}
**语言**: ${getDialectName(meeting.dialect)}

## 会议内容

${meeting.content || '暂无内容'}
`

  downloadFile(content, `${meeting.title}.md`, 'text/markdown')
}

function exportAsHtml(meeting: any) {
  const content = `<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${meeting.title}</title>
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
  <h1>${meeting.title}</h1>

  <div class="meta">
    <div class="meta-item">
      <span class="meta-label">创建时间:</span> ${new Date(meeting.createdAt).toLocaleString('zh-CN')}
    </div>
    <div class="meta-item">
      <span class="meta-label">时长:</span> ${formatDuration(meeting.duration)}
    </div>
    <div class="meta-item">
      <span class="meta-label">语言:</span> ${getDialectName(meeting.dialect)}
    </div>
  </div>

  <h2>会议内容</h2>
  <div class="content">${meeting.content || '暂无内容'}</div>
</body>
</html>
`

  downloadFile(content, `${meeting.title}.html`, 'text/html')
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
.home-view {
  width: 100%;
  height: 100vh;
  background: #f7f8fa;
}

.content {
  padding-top: 46px;
  height: 100%;
  overflow-y: auto;
}

.export-button {
  height: 100%;
}

.delete-button {
  height: 100%;
}

/* 关于对话框样式 */
.about-dialog {
  text-align: center;
}

.about-logo {
  margin-bottom: 20px;
}

.about-logo img {
  width: 100px;
  height: 100px;
  animation: float 3s ease-in-out infinite;
}

.about-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin: 0 0 8px 0;
}

.about-version {
  font-size: 14px;
  color: #999;
  margin: 0 0 24px 0;
}

.about-description {
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
}

.about-description p {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
  text-align: left;
}

.about-tech {
  margin-bottom: 24px;
}

.tech-label {
  font-size: 12px;
  color: #999;
  margin: 0 0 12px 0;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.tech-tag {
  display: inline-block;
  padding: 4px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  font-size: 12px;
}

.about-author {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px;
  background: #f7f8fa;
  border-radius: 12px;
  margin-bottom: 24px;
}

.author-avatar {
  font-size: 40px;
}

.author-info-text {
  text-align: left;
}

.author-name {
  font-size: 14px;
  color: #333;
  margin: 0 0 8px 0;
  font-weight: 500;
}

.github-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #667eea;
  text-decoration: none;
  background: white;
  padding: 4px 10px;
  border-radius: 12px;
  border: 1px solid #667eea;
  transition: all 0.3s ease;
}

.github-link:hover {
  background: #667eea;
  color: white;
  transform: translateY(-2px);
}

.about-close-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}
</style>

