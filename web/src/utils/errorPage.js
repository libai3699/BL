import pagesConfig from '@/pages.json';

const ERROR_PAGE_PATH = '/pages/error/index';

const REGISTERED_PATHS = new Set(
  (pagesConfig.pages || []).map((item) => `/${item.path}`)
);

const TAB_BAR_PATHS = new Set(
  (pagesConfig.tabBar?.list || []).map((item) => `/${item.pagePath}`)
);

/** H5 路由别名（Tab 根路径等） */
const ROUTE_ALIASES = {
  '/': '/pages/home/index'
};

/** 统一解析为 pages.json 路径 */
export function normalizePagePath(url) {
  const path = parsePagePath(url);
  if (!path) return '';
  return ROUTE_ALIASES[path] || path;
}

let redirectingToError = false;
let h5GuardInstalled = false;
let historyPatched = false;
/** 从错误页主动离开时，短暂跳过地址栏守卫，避免被旧非法 hash 拦回 404 */
let suppressRouteCheckUntil = 0;

/** 解析 navigate 参数中的页面路径（不含 query） */
export function parsePagePath(url) {
  if (!url) return '';
  const raw = String(url).trim();
  const path = raw.split('?')[0];
  if (!path) return '';
  return path.startsWith('/') ? path : `/${path}`;
}

/** 从完整 href 解析 /pages/... 路径 */
export function parsePathFromHref(href) {
  if (!href) return '';
  try {
    const url = new URL(href, window.location.origin);
    const hashMatch = url.hash.match(/#(\/pages\/[^?#]*)/);
    if (hashMatch) return hashMatch[1];
    const pathMatch = url.pathname.match(/(\/pages\/[^?#]*)/);
    if (pathMatch) return pathMatch[1];
  } catch {
    const hashMatch = String(href).match(/#(\/pages\/[^?#]*)/);
    if (hashMatch) return hashMatch[1];
    const pathMatch = String(href).match(/(\/pages\/[^?#]*)/);
    if (pathMatch) return pathMatch[1];
  }
  return '';
}

/** 是否为已在 pages.json 注册的页面 */
export function isValidPagePath(url) {
  const path = normalizePagePath(url);
  if (!path) return false;
  return REGISTERED_PATHS.has(path);
}

export function isTabBarPath(url) {
  const path = normalizePagePath(url);
  return path ? TAB_BAR_PATHS.has(path) : false;
}

function isH5() {
  // #ifdef H5
  return true;
  // #endif
  // #ifndef H5
  return typeof window !== 'undefined' && /uni-app|h5/i.test(navigator.userAgent);
  // #endif
}

/** 获取当前路径（综合页面栈与地址栏） */
export function getCurrentPagePath() {
  const pages = getCurrentPages();
  let stackPath = '';
  if (pages?.length) {
    const route = pages[pages.length - 1]?.route;
    if (route) {
      stackPath = normalizePagePath(route);
    }
  }

  if (typeof window === 'undefined') {
    return stackPath;
  }

  const hrefPath = normalizePagePath(parsePathFromHref(window.location.href));

  if (!hrefPath) {
    return stackPath;
  }

  if (!stackPath) {
    return hrefPath;
  }

  // 已从错误页/非法 hash 切到合法页面：以页面栈为准（修复「返回首页」仍停在 404）
  if (isValidPagePath(stackPath)) {
    if (!isValidPagePath(hrefPath) || hrefPath === ERROR_PAGE_PATH) {
      return stackPath;
    }
  }

  // 地址栏非法且与栈不一致：用户手动改了链接
  if (!isValidPagePath(hrefPath) && hrefPath !== stackPath) {
    return hrefPath;
  }

  return hrefPath;
}

function isRouteCheckSuppressed() {
  return Date.now() < suppressRouteCheckUntil;
}

export function suppressRouteCheck(ms = 1200) {
  suppressRouteCheckUntil = Date.now() + ms;
}

export function syncH5Hash(path) {
  // #ifdef H5
  if (typeof window === 'undefined' || !path) return;
  const hash = path.startsWith('/') ? `#${path}` : `#/${path}`;
  if (window.location.hash !== hash) {
    window.location.hash = hash;
  }
  // #endif
}

/** 从错误页跳转到指定合法路由（首页等） */
export function navigateFromErrorPage(url) {
  const path = parsePagePath(url);
  if (!path || !isValidPagePath(path)) {
    return;
  }

  suppressRouteCheck();
  redirectingToError = false;
  syncH5Hash(path);

  const tabPaths = pagesConfig.tabBar?.list?.map((item) => `/${item.pagePath}`) || [];

  if (tabPaths.includes(path)) {
    uni.switchTab({
      url: path,
      fail: () => {
        uni.reLaunch({ url: path });
      }
    });
    return;
  }

  uni.reLaunch({
    url: path,
    fail: () => {
      uni.redirectTo({ url: path });
    }
  });
}

/** 非法路径则跳转 404，返回是否已触发跳转 */
function validateHrefAndRedirect(href) {
  const path = normalizePagePath(parsePathFromHref(href));
  if (!path || path === ERROR_PAGE_PATH) return false;
  if (!isValidPagePath(path)) {
    goToErrorPage('404');
    return true;
  }
  return false;
}

/** 校验当前路由，非法则跳转 404 */
export function checkCurrentRoute() {
  if (isRouteCheckSuppressed()) return;

  if (typeof window !== 'undefined') {
    if (validateHrefAndRedirect(window.location.href)) return;
  }
  const path = getCurrentPagePath();
  if (!path || path === ERROR_PAGE_PATH) return;
  if (!isValidPagePath(path)) {
    goToErrorPage('404');
  }
}

/** 跳转错误页（404 / network / server） */
export function goToErrorPage(type = '404') {
  const safeType = ['404', 'network', 'server'].includes(type) ? type : '404';
  const url = `${ERROR_PAGE_PATH}?type=${safeType}`;

  if (redirectingToError) return;
  if (getCurrentPagePath() === ERROR_PAGE_PATH) return;

  redirectingToError = true;
  uni.redirectTo({
    url,
    fail: () => {
      uni.reLaunch({
        url,
        complete: () => {
          redirectingToError = false;
        }
      });
    },
    success: () => {
      redirectingToError = false;
    },
    complete: () => {
      redirectingToError = false;
    }
  });
}

function patchHistoryMethods() {
  if (historyPatched || typeof window === 'undefined') return;
  historyPatched = true;

  ['pushState', 'replaceState'].forEach((method) => {
    const raw = history[method].bind(history);
    history[method] = function patchedHistory(state, title, url) {
      const result = raw(state, title, url);
      const href =
        typeof url === 'string'
          ? url.startsWith('http')
            ? url
            : `${window.location.origin}${url.startsWith('/') ? '' : '/'}${url}`
          : window.location.href;
      queueMicrotask(() => validateHrefAndRedirect(href));
      return result;
    };
  });
}

/** 挂钩 uni-app H5 的 vue-router */
function hookVueRouter() {
  const vueApp = document.getElementById('app')?.__vue_app__;
  if (!vueApp) return false;

  const router =
    vueApp.config?.globalProperties?.$router ||
    vueApp._instance?.appContext?.config?.globalProperties?.$router;

  if (!router || router.__errorPageGuarded) return !!router?.__errorPageGuarded;

  router.__errorPageGuarded = true;
  router.afterEach((to) => {
    if (isRouteCheckSuppressed()) return;

    const path = normalizePagePath(to.path || to.fullPath);
    if (!path || path === ERROR_PAGE_PATH) return;
    // Tab 页在 H5 下常无 matched，仅以 pages.json 白名单为准（含 / → 首页）
    if (!isValidPagePath(path)) {
      goToErrorPage('404');
    }
  });
  return true;
}

/** H5：监听地址栏变化（含回车改 hash / history） */
export function setupH5RouteGuard() {
  if (!isH5() || typeof window === 'undefined' || h5GuardInstalled) return;
  h5GuardInstalled = true;

  patchHistoryMethods();

  const onLocationChange = (e) => {
    if (isRouteCheckSuppressed()) return;

    const href = e?.newURL || window.location.href;
    if (validateHrefAndRedirect(href)) return;
    queueMicrotask(() => checkCurrentRoute());
  };

  // capture：尽量早于框架把非法路由改回首页
  window.addEventListener('hashchange', onLocationChange, true);
  window.addEventListener('popstate', onLocationChange, true);
  window.addEventListener('pageshow', () => checkCurrentRoute());

  const scheduleCheck = () => queueMicrotask(() => checkCurrentRoute());

  scheduleCheck();
  [50, 150, 400, 800, 1500].forEach((ms) => setTimeout(checkCurrentRoute, ms));
  [100, 500, 1200].forEach((ms) => setTimeout(hookVueRouter, ms));
}

/** @deprecated 使用 setupH5RouteGuard */
export function guardH5RouteOnLaunch() {
  setupH5RouteGuard();
}

export function installNavigateInterceptors() {
  ['navigateTo', 'redirectTo', 'reLaunch', 'switchTab'].forEach((method) => {
    uni.addInterceptor(method, {
      invoke(args) {
        const url = args?.url;
        if (!url || isValidPagePath(url)) return;

        goToErrorPage('404');
        return false;
      }
    });
  });
}
