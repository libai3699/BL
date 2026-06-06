import { createI18n } from 'vue-i18n';
import zhCN from './zh_CN/index.json';
import enUS from './en_US/index.json';
import esMX from './es_MX/index.json';
import esPE from './es_PE/index.json';
import CONFIG from '@/configs';
import { getJSONParse } from '@/utils';

// 语言列表
export const locales = [
  {
    name: '简体中文',
    code: 'zh_CN',
    locale: zhCN
  },
  {
    name: 'English',
    code: 'en_US',
    locale: enUS
  },
  {
    name: 'Español (México)',
    code: 'es_MX',
    locale: esMX
  },
  {
    name: 'Español (Perú)',
    code: 'es_PE',
    locale: esPE
  }
];

const messages = {};
for (const item of locales) {
  messages[item.code] = item.locale;
}

// 获取已设置的语言
const defaultLocale =
  getJSONParse(uni.getStorageSync('common'))?.locale || CONFIG.defaultLocale;

// 国际化
const i18nConfig = createI18n({
  legacy: false,
  locale: defaultLocale,
  messages,
  globalInjection: true
});

export default i18nConfig;
