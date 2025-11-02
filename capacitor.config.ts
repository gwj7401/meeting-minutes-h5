import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.meetingminutes.app',
  appName: '会议纪要',
  webDir: 'dist',
  server: {
    androidScheme: 'https',
    // 允许导航到外部链接（如 GitHub）
    allowNavigation: ['github.com']
  },
  android: {
    // 允许混合内容（HTTP 和 HTTPS）
    allowMixedContent: true,
    // 捕获返回按钮
    backgroundColor: '#667eea',
    buildOptions: {
      keystorePath: undefined,
      keystorePassword: undefined,
      keystoreAlias: undefined,
      keystoreAliasPassword: undefined,
      releaseType: 'APK'
    }
  },
  plugins: {
    // 配置状态栏
    SplashScreen: {
      launchShowDuration: 2500,
      backgroundColor: '#667eea',
      showSpinner: false
    }
  }
};

export default config;

