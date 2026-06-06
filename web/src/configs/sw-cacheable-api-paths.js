/**
 * Service Worker 可缓存 API 白名单（请求 URL 的 pathname 包含其中任一项即走 SWR 缓存）
 *
 * 修改后需重新构建，使 dist/sw.js 注入生效。
 * 与 npm 包 vite-plugin-sw-offline 内置 DEFAULT 保持一致；可按业务增删。
 */
export const CACHEABLE_SW_API_PATHS = [
  // 配置类
  '/config/keFu.do',
  '/config/queryConfig.do',
  // 内容类
  '/news/getNewsList.do',
  '/site/getBannerByPlat.do',
  '/user/position/list.do',
  '/user/getUserAccount.do',
  '/stock/getHotStockList.do',
  '/stock/getExchangeList.do',
  '/stock/getStockList.do',
  '/user/getUserInfo.do'
];

export default CACHEABLE_SW_API_PATHS;
