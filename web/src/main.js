import { createSSRApp } from 'vue';
import uviewPlus from 'uview-plus';
import * as Pinia from 'pinia';
import piniaPersist from 'pinia-plugin-persist';

import App from '@/App.vue';
import i18nConfig from '@/locales/index.js';
import { initRequest } from '@/apis/request';
import { setupH5RouteGuard } from '@/utils/errorPage';
import { getSafePagePath } from '@/utils/uniUtils';

// #ifdef H5
// 开发环境显示vconsole
if (!process.env.APP_ENV.endsWith('prod')) {
  import('vconsole').then((res) => new res.default());
}
// #endif

export function createApp() {
  // #ifdef H5
  setupH5RouteGuard();
  // #endif

  const app = createSSRApp(App);

  // 状态管理工具 - pinia
  const pinia = Pinia.createPinia();
  // pinia状态持久化
  pinia.use(piniaPersist);
  app.use(pinia);

  // 国际化
  app.use(i18nConfig);

  // ui库
  app.use(uviewPlus);
  // 覆盖 uview page()：路由过渡时页面栈可能为空，原生实现会读 undefined.route 抛错
  if (uni.$u) {
    uni.$u.page = getSafePagePath;
  }

  // 引入请求封装
  initRequest(app);

  // 禁止页面滚动
  uni.$u.stopBodyScroll = () => {
    // 记录距离顶部值
    uni.$u.bodyScrollTop = document.scrollingElement.scrollTop;
    if (uni.$u.bodyScrollTop === 0) {
      // 设置一定时间防抖，避免页面闪动被用户发现
      uni.$u.throttle(
        () => {
          if (document.body.scrollHeight <= window.outerHeight) {
            document.body.style.height = window.outerHeight + 1 + 'px';
          }
          window.scrollTo(0, 1);
        },
        500,
        false
      );
    }

    document.body.style['overflow-y'] = 'hidden';
  };

  // 恢复页面滚动
  uni.$u.resumeBodyScroll = () => {
    document.scrollingElement.scrollTop = uni.$u.bodyScrollTop;
    document.body.style['overflow-y'] = 'auto';
    // 如果body设置了style.height，那body的style里必然有px，此时将此style删除
    if (document.body.style.height.includes('px')) {
      document.body.style.height = '';
    }
  };

  return {
    app,
    Pinia // 此处必须将 Pinia 返回
  };
}
