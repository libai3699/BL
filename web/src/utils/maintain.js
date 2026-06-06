import dayjs from 'dayjs';

import { reqCheckMaintain } from '@/apis/maintain';
import { useCommonStore } from '@/stores/common';

const POLL_INTERVAL_MS = 60 * 1000;

let checkPromise = null;
let pollTimer = null;

/** 维护中仍可正常访问的页面 */
export const MAINTAIN_BYPASS_PAGES = ['/pages/customer-service/index'];

export function isMaintainBypassPage(pagePath) {
  const curPage = pagePath || uni.$u.page?.() || '';
  return MAINTAIN_BYPASS_PAGES.includes(curPage);
}

export function isMaintaining(store) {
  return store.maintenance.needMaintenance === '1';
}

export function isMaintainLoading(store) {
  return store.maintenance.needMaintenance === 'loading';
}

export function applyMaintainResult(res, store) {
  const needMaintenance = Number(res?.status) === 1;
  store.maintenance.data = res || {};
  store.maintenance.needMaintenance = needMaintenance ? '1' : '0';
}

function schedulePoll() {
  clearTimeout(pollTimer);
  pollTimer = setTimeout(() => {
    fetchMaintain().finally(schedulePoll);
  }, POLL_INTERVAL_MS);
}

/** 启动后台轮询（首次检查完成后调用） */
export function startMaintainPolling() {
  if (pollTimer) return;
  schedulePoll();
}

/**
 * 首次维护检查（全局单例 Promise，需在 pinia 初始化后调用）
 * 页面渲染前应 await 此方法
 */
export function ensureMaintainChecked() {
  const store = useCommonStore();

  if (store.maintenance.needMaintenance !== 'loading') {
    return Promise.resolve();
  }

  if (checkPromise) {
    return checkPromise;
  }

  checkPromise = reqCheckMaintain()
    .then((res) => {
      applyMaintainResult(res, store);
    })
    .catch(() => {
      if (store.maintenance.needMaintenance === 'loading') {
        store.maintenance.needMaintenance = '0';
      }
    })
    .finally(() => {
      startMaintainPolling();
    });

  return checkPromise;
}

/** 手动拉取维护状态（重新检查按钮 / 轮询） */
export function fetchMaintain() {
  const store = useCommonStore();
  if (!store.canPolling) {
    return Promise.resolve();
  }
  return reqCheckMaintain()
    .then((res) => {
      applyMaintainResult(res, store);
    })
    .catch(() => {
      if (store.maintenance.needMaintenance === 'loading') {
        store.maintenance.needMaintenance = '0';
      }
    });
}

function parseMaintainTime(value) {
  if (value == null || value === '') return null;
  if (typeof value === 'number') {
    const d = dayjs(value);
    return d.isValid() ? d : null;
  }
  const raw = String(value).trim();
  if (/^\d+$/.test(raw)) {
    const d = dayjs(Number(raw));
    return d.isValid() ? d : null;
  }
  const d = dayjs(raw);
  return d.isValid() ? d : null;
}

/** 根据接口维护时间计算倒计时与已维护时长（秒） */
export function calcMaintainCountdown(data = {}) {
  const now = dayjs();
  const start = parseMaintainTime(data.maintainTimeStart);
  const end = parseMaintainTime(data.maintainTimeEnd);

  let remainingSeconds = 0;
  let elapsedSeconds = 0;

  if (end) {
    remainingSeconds = Math.max(0, end.diff(now, 'second'));
  }
  if (start) {
    elapsedSeconds = Math.max(0, now.diff(start, 'second'));
  }

  return { remainingSeconds, elapsedSeconds };
}

export function formatMaintainDuration(totalSeconds) {
  const safe = Math.max(0, totalSeconds);
  const h = Math.floor(safe / 3600);
  const m = Math.floor((safe % 3600) / 60);
  const s = safe % 60;
  return [h, m, s].map((n) => String(n).padStart(2, '0')).join(':');
}
