// 导航相关工具方法
import NProgress from 'nprogress';
import pagesConfig from '@/pages.json';
import { isAppFromUniApp } from '.';
import {
  goToErrorPage,
  installNavigateInterceptors,
  isValidPagePath,
  suppressRouteCheck,
  syncH5Hash
} from './errorPage';
import { getSafePagePath } from './uniUtils';

installNavigateInterceptors();

export { goToErrorPage, isValidPagePath };

const pageScrollTop = {};
const isPreloadUrls = [];

function isHome(url) {
  return url.includes('/pages/home/index') || url.endsWith('/#/');
}

// 封装：预加载页面
function preloadPage(url, callback) {
  // 如果已经加载过，则不出现顶部进度条，并且直接走callback
  // 首页也直接callback
  if (isPreloadUrls.includes(url) || isHome(url)) {
    callback?.();
    return;
  }

  NProgress.start();
  uni.preloadPage({
    url,
    success: () => {
      callback?.();
    },
    complete: () => {
      NProgress.done();
    }
  });
}

// 封装：uni.navigateTo
export const navigateTo = (url) => {
  if (url === '' || url === null) return;

  // 判断底部导航
  if (isNavUrlAndSwitch(url)) return;

  preloadPage(url, () => {
    // 记录当前页面的垂直滚动距离
    const currentPage = getSafePagePath();
    if (currentPage) {
      pageScrollTop[currentPage] = window.scrollY;
    }
    uni.navigateTo({
      url,
      success() {
        setAppCanBack();
      }
    });
  });
};

// 封装：uni.switchTab
export const switchTab = (url) => {
  if (url === '' || url === null) return;

  // #ifdef H5
  suppressRouteCheck();
  syncH5Hash(url);
  // #endif

  preloadPage(url, () => {
    // 记录当前页面的垂直滚动距离
    const currentPage = getSafePagePath();
    if (currentPage) {
      pageScrollTop[currentPage] = window.scrollY;
    }
    // 没有包含则添加
    !isPreloadUrls.includes(url) && isPreloadUrls.push(url);
    uni.switchTab({
      url,
      success() {
        // 恢复页面的垂直滚动距离
        window.scrollTo(0, pageScrollTop[url] || 0);
        setAppCanBack();
      }
    });
  });
};

// 封装：uni.redirectTo
export const redirectTo = (url) => {
  if (url === '' || url === null) return;

  // 判断底部导航
  if (isNavUrlAndSwitch(url)) return;

  preloadPage(url, () => {
    uni.redirectTo({
      url,
      success() {
        setAppCanBack();
      }
    });
  });
};

// 封装：uni.reLaunch
export const reLaunch = (url) => {
  if (url === '' || url === null) return;

  url = decodeURIComponent(url);

  // 判断底部导航
  if (isNavUrlAndSwitch(url)) return;

  preloadPage(url, () => {
    uni.reLaunch({
      url,
      success() {
        // 恢复页面的垂直滚动距离
        window.scrollTo(0, pageScrollTop[url] || 0);
        setAppCanBack();
      }
    });
  });
};

const TAB_BAR_PATHS = pagesConfig.tabBar?.list?.map((item) => `/${item.pagePath}`) || [];

function getPagePathFromRoute(route) {
  if (!route) return '';
  const raw = String(route).startsWith('/') ? route : `/${route}`;
  return raw.split('?')[0];
}

/**
 * 从 Tab 子页推断所属 Tab（如持仓历史 -> 订单 Tab）。
 * H5 从 Tab navigateTo 子页后，栈里常只剩 1 层，navigateBack 无法回到 Tab。
 */
function inferParentTabPath(path) {
  const normalized = getPagePathFromRoute(path);
  if (!normalized || TAB_BAR_PATHS.includes(normalized)) return '';

  for (const tabPath of TAB_BAR_PATHS) {
    const modulePrefix = tabPath.replace(/\/index$/, '');
    if (normalized.startsWith(`${modulePrefix}/`)) {
      return tabPath;
    }
  }
  return '';
}

function afterBackToPath(targetPath) {
  // #ifdef H5
  if (targetPath && TAB_BAR_PATHS.includes(targetPath)) {
    suppressRouteCheck();
    syncH5Hash(targetPath);
  } else if (targetPath) {
    suppressRouteCheck(600);
    syncH5Hash(targetPath);
  }
  // #endif
  setTimeout(() => {
    try {
      window.scrollTo(0, pageScrollTop[getSafePagePath()] || 0);
      setAppCanBack();
    } catch (e) {
      console.warn('[navigate] afterBackToPath skipped:', e);
    }
  }, 50);
}

// 封装：uni.navigateBack
export const navigateBack = (params = {}) => {
  const pages = getCurrentPages();
  const currentPath = getSafePagePath();
  const prevPath =
    pages.length > 1 ? getPagePathFromRoute(pages[pages.length - 2]?.route) : '';

  // 上一页是 Tab：H5 上 navigateBack 回 Tab 不稳定，直接 switchTab
  if (prevPath && TAB_BAR_PATHS.includes(prevPath)) {
    switchTab(prevPath);
    params?.success?.({});
    return;
  }

  // 页面栈只剩一层（从 Tab 进来的子页）：回到所属 Tab
  if (pages.length <= 1) {
    const tabTarget = inferParentTabPath(currentPath);
    if (tabTarget) {
      switchTab(tabTarget);
      params?.success?.({});
      return;
    }
  }

  // #ifdef H5
  suppressRouteCheck();
  // #endif

  uni.navigateBack({
    ...params,
    success(res) {
      params?.success?.(res);
      afterBackToPath(prevPath);
    },
    fail(err) {
      params?.fail?.(err);
      const tabTarget = inferParentTabPath(currentPath);
      if (tabTarget) {
        switchTab(tabTarget);
      }
    }
  });
};

// 返回
export const goBack = () => {
  const pages = getCurrentPages();
  const len = pages.length;
  const currentPath = getSafePagePath();

  if (len > 1) {
    const prevPath = getPagePathFromRoute(pages[len - 2]?.route);
    if (prevPath && TAB_BAR_PATHS.includes(prevPath)) {
      switchTab(prevPath);
      return;
    }
    navigateBack();
    return;
  }

  // 从 Tab 子页返回（订单 -> 持仓历史 第二次返回常走此分支）
  const tabTarget = inferParentTabPath(currentPath);
  if (tabTarget) {
    switchTab(tabTarget);
    return;
  }

  // 非 Tab 子页、无栈信息时再用浏览器历史
  if (typeof history !== 'undefined' && history.length > 1) {
    // #ifdef H5
    suppressRouteCheck();
    // #endif
    history.back();
    setTimeout(() => {
      const path = getSafePagePath() || inferParentTabPath(currentPath);
      afterBackToPath(path);
    }, 100);
    return;
  }

  switchTab('/pages/home/index');
};

// 跳转回调后使用此方法进行判断：是否是首页
// 是的话，在app再按一次返回，操作返回桌面
export function setAppCanBack() {
  const curUrl = window.location.href;
  const setBackData = {
    data: isHome(curUrl),
    type: 'setCanBack'
  };
  if (isAppFromUniApp()) {
    window.uniSdk.postMessage({
      data: setBackData
    });
  }
}

// 判断url是否是底部导航，是则直接切换
export const isNavUrlAndSwitch = (url) => {
  if (url === '' || url === null) return false;
  const navUrls = pagesConfig.tabBar?.list.map((item) => `/${item.pagePath}`) || [];
  if (navUrls?.includes(url)) {
    switchTab(url);
    return true;
  }
  return false;
};
