<template>
  <transition name="fade">
    <div v-if="show" class="splash-screen">
      <div class="splash-content">
        <!-- Logo -->
        <div class="logo-container">
          <div class="logo-wrapper">
            <img src="/logo.svg" alt="会议纪要" class="logo" />
          </div>
          <div class="logo-glow"></div>
        </div>
        
        <!-- App Name -->
        <h1 class="app-name">会议纪要</h1>
        <p class="app-slogan">实时语音转文字 · 智能会议记录</p>
        
        <!-- Features -->
        <div class="features">
          <div class="feature-item">
            <van-icon name="volume-o" size="20" />
            <span>实时转写</span>
          </div>
          <div class="feature-item">
            <van-icon name="chat-o" size="20" />
            <span>多种方言</span>
          </div>
          <div class="feature-item">
            <van-icon name="share-o" size="20" />
            <span>一键导出</span>
          </div>
        </div>
        
        <!-- Loading -->
        <div class="loading">
          <div class="loading-bar">
            <div class="loading-progress"></div>
          </div>
        </div>
        
        <!-- Author Info -->
        <div class="author-info">
          <div class="author-avatar">🎸</div>
          <div class="author-details">
            <p class="author-name">作者：多线程吉他</p>
            <a 
              href="https://github.com/gwj7401/meeting-minutes-h5" 
              target="_blank" 
              class="github-link"
              @click.stop
            >
              <van-icon name="star-o" size="14" />
              <span>GitHub 开源项目</span>
            </a>
          </div>
        </div>
        
        <!-- Version -->
        <p class="version">v1.0.0</p>
      </div>
      
      <!-- Background Animation -->
      <div class="bg-animation">
        <div class="circle circle-1"></div>
        <div class="circle circle-2"></div>
        <div class="circle circle-3"></div>
        <div class="wave wave-1"></div>
        <div class="wave wave-2"></div>
        <div class="wave wave-3"></div>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const show = ref(true)

onMounted(() => {
  // 2.5秒后隐藏启动页
  setTimeout(() => {
    show.value = false
  }, 2500)
})
</script>

<style scoped>
.splash-screen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.splash-content {
  position: relative;
  z-index: 2;
  text-align: center;
  padding: 20px;
  animation: slideUp 0.8s ease-out;
}

.logo-container {
  position: relative;
  margin-bottom: 30px;
}

.logo-wrapper {
  position: relative;
  z-index: 2;
  animation: float 3s ease-in-out infinite;
}

.logo {
  width: 120px;
  height: 120px;
  filter: drop-shadow(0 10px 30px rgba(0, 0, 0, 0.3));
  animation: rotate 20s linear infinite;
}

.logo-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 150px;
  height: 150px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

.app-name {
  font-size: 36px;
  font-weight: bold;
  color: white;
  margin: 0 0 10px 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  animation: fadeIn 0.8s ease-out 0.2s both;
}

.app-slogan {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0 0 30px 0;
  animation: fadeIn 0.8s ease-out 0.4s both;
}

.features {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-bottom: 40px;
  animation: fadeIn 0.8s ease-out 0.6s both;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: white;
  font-size: 12px;
}

.feature-item :deep(.van-icon) {
  background: rgba(255, 255, 255, 0.2);
  padding: 10px;
  border-radius: 50%;
  backdrop-filter: blur(10px);
}

.loading {
  margin-bottom: 40px;
  animation: fadeIn 0.8s ease-out 0.8s both;
}

.loading-bar {
  width: 200px;
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  overflow: hidden;
  margin: 0 auto;
}

.loading-progress {
  height: 100%;
  background: linear-gradient(90deg, #ffd89b, #ff6b9d, #4facfe, #00f2fe);
  background-size: 200% 100%;
  border-radius: 2px;
  animation: loading 2s ease-in-out infinite;
}

.author-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 20px;
  animation: fadeIn 0.8s ease-out 1s both;
}

.author-avatar {
  font-size: 32px;
  animation: bounce 2s ease-in-out infinite;
}

.author-details {
  text-align: left;
}

.author-name {
  font-size: 14px;
  color: white;
  margin: 0 0 4px 0;
  font-weight: 500;
}

.github-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  background: rgba(255, 255, 255, 0.15);
  padding: 4px 10px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.github-link:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
}

.version {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0;
  animation: fadeIn 0.8s ease-out 1.2s both;
}

/* Background Animation */
.bg-animation {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 100px;
  height: 100px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 10%;
  animation-delay: 1s;
}

.circle-3 {
  width: 80px;
  height: 80px;
  bottom: 15%;
  left: 15%;
  animation-delay: 2s;
}

.wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100px;
  background: rgba(255, 255, 255, 0.05);
  animation: wave 3s ease-in-out infinite;
}

.wave-1 {
  animation-delay: 0s;
}

.wave-2 {
  animation-delay: 0.5s;
  opacity: 0.7;
}

.wave-3 {
  animation-delay: 1s;
  opacity: 0.5;
}

/* Animations */
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 0.8;
    transform: translate(-50%, -50%) scale(1.1);
  }
}

@keyframes loading {
  0% {
    width: 0%;
    background-position: 0% 50%;
  }
  50% {
    width: 100%;
    background-position: 100% 50%;
  }
  100% {
    width: 100%;
    background-position: 200% 50%;
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes wave {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

