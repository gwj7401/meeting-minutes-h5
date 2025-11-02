<template>
  <SplashScreen />

  <router-view v-slot="{ Component }">
    <keep-alive>
      <component :is="Component" />
    </keep-alive>
  </router-view>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useMeetingStore } from '@/stores/meeting'
import SplashScreen from '@/components/SplashScreen.vue'

const meetingStore = useMeetingStore()

onMounted(() => {
  meetingStore.loadMeetings()
})
</script>

<style>
#app {
  width: 100%;
  height: 100vh;
  overflow: hidden;
}
</style>

