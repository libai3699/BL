import path from 'path';
import { defineConfig } from 'vite';
import uni from '@dcloudio/vite-plugin-uni';
import dayjs from 'dayjs';
import SERIES from './src/configs/series/index.js';
import { CACHEABLE_SW_API_PATHS } from './src/configs/sw-cacheable-api-paths.js';
import { vitePluginSwOffline } from 'vite-plugin-sw-offline';

const BUILD_TIME = dayjs(new Date()).format('YYYY-MM-DD HH:mm:ss');
const IS_APP = process.env.IS_APP;
console.log('BUILD_TIME(打包时间)', BUILD_TIME);

// 离线页配置（从 series common config 读取）
const offlinePageConfig = SERIES.offlinePage || {};

// https://vitejs.dev/config/
export default defineConfig({
  base: IS_APP ? './' : '/',
  define: {
    'process.env': {
      APP_ENV: process.env.APP_ENV,
      IS_APP,
      IS_LOCAL_DEBUGGER: process.env.IS_LOCAL_DEBUGGER,
      BUILD_TIME
    }
  },
  plugins: [
    uni(),
    // Service Worker、离线页、默认背景图（dev 中间件 + build 写入 dist）
    vitePluginSwOffline({
      outDir: 'dist',
      offlineLogoPath: offlinePageConfig.logoSrc || '',
      offlineSkin: offlinePageConfig.skin || '',
      cacheableApiPaths: CACHEABLE_SW_API_PATHS
    })
  ],
  server: {
    host: true
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@root': path.resolve(__dirname, '.')
    }
  }
});
