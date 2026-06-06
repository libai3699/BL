import { defineStore } from 'pinia';

import { locales } from '@/locales/index.js';
import CONFIG from '@/configs';
import SERIES from '@/configs/series';
import { getUniStorage } from '@/utils/uniUtils';
import { goBack } from '@/utils/navigate';
import { testApi } from '@/utils';
import { getConfigUploadFileDest } from '@/apis/config';

export const useCommonStore = defineStore({
  id: 'common',
  state: () => ({
    // 主题
    theme: SERIES.theme,
    // 国际化
    locale: CONFIG.defaultLocale,
    // 返回方法
    fnBack: goBack,
    // 全局控制弹窗
    popup: {
      locale: {
        show: false,
        // 选择后回调
        cbChoosed: () => {}
      },
      // 普通提示弹层
      normal: {
        show: false,
        options: {}
      }
    },
    // 未读消息数量
    unreadCount: 0,
    // 数据隐藏/显示 true-显示 false-隐藏
    dataEye: {
      // 我的页
      mine: true,
      // 订单页
      orders: true
    },
    // ip地址
    ip: '',
    // 域名和ws
    storage_lines: [],
    // 当前域名
    storage_cur: {
      name: '',
      domain: CONFIG.baseURL,
      status: 'init'
    },
    // 版本号
    version: '1.0.0',
    // 维护信息（needMaintenance: loading | 0 | 1）
    maintenance: {
      needMaintenance: 'loading',
      data: {}
    },
    // 是否允许维护轮询
    canPolling: true,
    // 上传配置
    configUploadFileDest: '0'
  }),
  // 持久化
  persist: {
    enabled: true,
    strategies: [
      {
        storage: getUniStorage(),
        paths: ['locale', 'dataEye', 'storage_lines', 'storage_cur']
      }
    ]
  },

  getters: {
    getTheme(state) {
      return state.theme;
    },
    // 获取主题样式名
    getThemeClass() {
      return this.theme ? `g-theme-${this.theme}` : '';
    },
    // 获取当前语言详情
    getLocaleDetail(state) {
      return locales.find((item) => item.code === state.locale) || {};
    }
  },
  actions: {
    getIp() {
      fetch('https://api.vore.top/api/IPdata?ip=')
        .then((res) => res.json())
        .then((data) => {
          this.ip = data?.ipinfo?.text || '';
        })
        .catch((err) => {
          console.error('请求失败:', err);
        });
    },
    // 设置用户的域名和ws线路
    setApiConfig(domain, cb, errorCb) {
      const domains = domain?.split(',') || [];
      // 组装线路
      this.storage_lines = domains.map((item, index) => ({
        name: uni.$t('line.name') + (index + 1),
        domain: item
      }));
      // 检测线路，获取最快的设置
      testApi({
        lines: this.storage_lines,
        onFirstSuccess: (line) => {
          // 如果
          // 当前已经选了线路 && 当前线路不是超时/初始化 && 当前线路在线路列表里
          // 不切换
          if (
            this.storage_cur?.status !== 'bad' &&
            this.storage_cur?.status !== 'init' &&
            this.storage_cur?.domain &&
            this.storage_lines.some((l) => l.domain === this.storage_cur.domain)
          ) {
            cb?.();
            return;
          }
          this.switchApiLine(line);
          cb?.();
        },
        onAllFail: errorCb
      });
    },
    // 切换线路
    switchApiLine(line) {
      this.storage_cur = line;
      CONFIG.baseURL = line.domain;
    },
    setTheme(theme) {
      this.theme = theme;
    },
    setLocale(locale, cb = () => {}) {
      // 最终使用语言，默认为默认语言
      let finalLocale = CONFIG.defaultLocale;
      const localeCodes = locales.map((item) => item.code);
      // 用户语言必须在预设语言内
      const isInLocales = localeCodes.includes(locale);
      // 当符合条件，直接使用，否则使用默认语言
      if (locale != null && locale !== '' && isInLocales) {
        finalLocale = locale;
      }

      this.locale = finalLocale;
      /* ifdef H5 */
      document.documentElement.setAttribute('lang', finalLocale);
      /* endif */

      // 将最终语言返回，执行回调
      cb(finalLocale);
    },
    setUnreadCount(count = 0) {
      const value = Number(count);
      this.unreadCount = Number.isFinite(value) && value > 0 ? Math.floor(value) : 0;
    },

    // 打开普通提示弹层
    openNormalPopup(options = {}) {
      this.popup.normal.options = options;
      this.popup.normal.show = true;
    },
    // 关闭普通提示弹层
    closeNormalPopup() {
      this.popup.normal.show = false;
    },
    // 获取上传配置
    getAndSetConfigUploadFileDest() {
      getConfigUploadFileDest()
        .then((res) => {
          this.configUploadFileDest = Number(res);
          // 测试代码
          // this.configUploadFileDest = Number('0');
        })
        .catch((e) => {
          console.error('获取上传配置失败', e);
        });
    }
  }
});
