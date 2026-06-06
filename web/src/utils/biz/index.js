// 业务工具方法

import { getKeFu } from '@/apis/home';
import { navigateTo } from '../navigate';

const CUSTOMER_SERVICE_KEY = 'customerServiceUrl';

export function goService(url) {
  navigateTo(`/pages/customer-service/index?url=${encodeURIComponent(url)}`);
}

export function getCachedCustomerServiceUrl() {
  try {
    return localStorage.getItem(CUSTOMER_SERVICE_KEY) || '';
  } catch {
    return '';
  }
}

/**
 * 联系客服：优先 localStorage 缓存，无缓存再调接口
 * @param {{ onLoading?: (loading: boolean) => void, unavailableText?: string }} options
 */
export function openCustomerService(options = {}) {
  const { onLoading, unavailableText } = options;

  const cached = getCachedCustomerServiceUrl();
  if (cached) {
    goService(cached);
    return Promise.resolve(cached);
  }

  onLoading?.(true);
  return getKeFu(false)
    .then((res) => {
      const link = res?.customerLink || '';
      if (link) {
        localStorage.setItem(CUSTOMER_SERVICE_KEY, link);
        goService(link);
        return link;
      }
      if (unavailableText) {
        uni.showToast({ title: unavailableText, icon: 'none' });
      }
      return '';
    })
    .catch(() => {
      if (unavailableText) {
        uni.showToast({ title: unavailableText, icon: 'none' });
      }
      return '';
    })
    .finally(() => {
      onLoading?.(false);
    });
}

// 获取并跳转客服
export function getAndGoService() {
  return openCustomerService();
}

// 纯获取客服链接并写入缓存（App 启动预拉取等）
export function getService(callback) {
  const cached = getCachedCustomerServiceUrl();
  if (cached) {
    callback?.(cached);
    return Promise.resolve(cached);
  }
  return getKeFu(false)
    .then((res) => {
      const link = res?.customerLink || '';
      if (link) {
        localStorage.setItem(CUSTOMER_SERVICE_KEY, link);
      }
      callback?.(link);
      return link;
    })
    .catch(() => {
      callback?.('');
      return '';
    });
}
