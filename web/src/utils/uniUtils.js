// uniapp使用的工具方法

/**
 * 安全获取当前页路径（如 /pages/home/index）。
 * redirectTo / navigateBack 等过渡时 getCurrentPages() 可能暂时为空，
 * uview 原生 page() 会因此抛 Cannot read properties of undefined (reading 'route')。
 */
export function getSafePagePath() {
  const pages = getCurrentPages();
  const route = pages[pages.length - 1]?.route;
  return route ? `/${route}` : '';
}

// 获取uni存储兑现
export function getUniStorage() {
  return {
    getItem: (key) => uni.getStorageSync(key),
    setItem: (key, value) => uni.setStorageSync(key, value)
  };
}

// 返回到指定页面，如果页面历史记录中无此页面，则使用jumpType到此页面
export function popTo(url, jumpType = 'redirectTo') {
  // h5有历史记录影响，需另外考虑，先不管
  const pages = getCurrentPages();
  // 从栈顶递减判断，如果fullPath和url相同，则返回此下标
  let index = 0;
  for (let i = pages.length - 1; i >= 0; i--) {
    if (pages[i].$page.fullPath === url) {
      index = pages.length - i - 1;
      break;
    }
  }
  if (index > 0) {
    uni.navigateBack({
      delta: index
    });
  } else {
    // 使用对象映射替代动态调用，这样在打包后也能正常工作
    const navigationMap = {
      navigateTo: uni.navigateTo,
      redirectTo: uni.redirectTo,
      switchTab: uni.switchTab,
      reLaunch: uni.reLaunch
    };

    // 获取导航方法，如果不存在则使用redirectTo
    const navigateMethod = navigationMap[jumpType] || navigationMap['redirectTo'];

    // 调用导航方法
    navigateMethod({ url });
  }
}

// 封装uni.showModal
export function showModal(options = {}) {
  // 默认值
  options.showCancel = options.showCancel != null ? options.showCancel : true;
  options.cancelText =
    options.cancelText != null ? options.cancelText : uni.$t('common.cancel');
  options.confirmText =
    options.confirmText != null ? options.confirmText : uni.$t('common.ensure');

  return uni.showModal(options);
}

// 快捷显示confirm的方法
export function showConfirm(title, cb) {
  return showModal({
    title,
    success(res) {
      cb(res);
    },
    fail(res) {
      cb(res);
    }
  });
}

// 复制
export const doCopy = (data) => {
  uni.setClipboardData({
    data,
    success: function () {
      uni.showToast({
        title: uni.$t('common.copySuccess'),
        icon: 'none'
      });
    }
  });
};
