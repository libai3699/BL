<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

import { useCommonStore } from '@/stores/common';
import { openCustomerService } from '@/utils/biz';
import {
  calcMaintainCountdown,
  fetchMaintain,
  formatMaintainDuration,
  isMaintainBypassPage,
  isMaintaining
} from '@/utils/maintain';

const { t } = useI18n();
const commonStore = useCommonStore();

const countdownText = ref('00:00:00');
const elapsedText = ref('00:00:00');
const retryLoading = ref(false);
const contactLoading = ref(false);

let remainingSeconds = 0;
let elapsedSeconds = 0;
let tickTimer = null;

const bypassMaintain = computed(() => isMaintainBypassPage());

const isLoading = computed(
  () => !bypassMaintain.value && commonStore.maintenance.needMaintenance === 'loading'
);

const isMaintenanceVisible = computed(
  () => !bypassMaintain.value && commonStore.maintenance.needMaintenance === '1'
);

const maintainData = computed(() => commonStore.maintenance.data || {});

const pageTitle = computed(() => {
  const raw = maintainData.value.maintainTitle;
  if (!raw) return t('maintenancePage.title');
  return String(raw).split(/\r?\n/)[0].trim() || t('maintenancePage.title');
});

const pageSubtitle = computed(() => {
  const raw = maintainData.value.maintainContent;
  if (!raw) return t('maintenancePage.subtitle');
  return String(raw).replace(/\r?\n/g, ' ').trim() || t('maintenancePage.subtitle');
});

const maintId = computed(
  () =>
    maintainData.value.maintainCode ||
    maintainData.value.maintainNo ||
    maintainData.value.productCode ||
    maintainData.value.code ||
    '--'
);

const startTimeText = computed(() => maintainData.value.maintainTimeStart || '--');
const endTimeText = computed(() => maintainData.value.maintainTimeEnd || '--');

const scopeText = computed(
  () =>
    maintainData.value.maintainScope ||
    maintainData.value.scope ||
    t('maintenancePage.mock.scope')
);

const statusUrl = computed(
  () => maintainData.value.statusUrl || t('maintenancePage.mock.statusUrl')
);

const hotlineText = computed(
  () => maintainData.value.hotline || t('maintenancePage.footerHotline')
);

function syncCountdownFromData() {
  const { remainingSeconds: remain, elapsedSeconds: elapsed } = calcMaintainCountdown(
    maintainData.value
  );
  remainingSeconds = remain;
  elapsedSeconds = elapsed;
  countdownText.value = formatMaintainDuration(remainingSeconds);
  elapsedText.value = formatMaintainDuration(elapsedSeconds);
}

function tickCountdown() {
  if (remainingSeconds > 0) {
    remainingSeconds -= 1;
    elapsedSeconds += 1;
  }
  countdownText.value = formatMaintainDuration(remainingSeconds);
  elapsedText.value = formatMaintainDuration(elapsedSeconds);
}

function startTickTimer() {
  stopTickTimer();
  syncCountdownFromData();
  tickTimer = setInterval(tickCountdown, 1000);
}

function stopTickTimer() {
  if (tickTimer) {
    clearInterval(tickTimer);
    tickTimer = null;
  }
}

function onContactService() {
  if (contactLoading.value) return;
  openCustomerService({
    onLoading: (loading) => {
      contactLoading.value = loading;
    },
    unavailableText: t('errorPage.contactUnavailable')
  });
}

async function onRetryCheck() {
  if (retryLoading.value) return;
  retryLoading.value = true;
  try {
    await fetchMaintain();
    syncCountdownFromData();
    if (!isMaintaining(commonStore)) {
      return;
    }
    uni.showToast({
      title: t('maintenancePage.stillMaintaining'),
      icon: 'none'
    });
  } finally {
    retryLoading.value = false;
  }
}

watch(isMaintenanceVisible, (visible) => {
  if (visible) {
    startTickTimer();
  } else {
    stopTickTimer();
  }
});

watch(
  () => commonStore.maintenance.data,
  () => {
    if (isMaintenanceVisible.value) {
      syncCountdownFromData();
    }
  },
  { deep: true }
);

onMounted(() => {
  if (isMaintenanceVisible.value) {
    startTickTimer();
  }
});

onBeforeUnmount(() => {
  stopTickTimer();
});
</script>

<template>
  <view v-if="isLoading" class="m-maintenance-loading"></view>
  <view v-else-if="isMaintenanceVisible" class="m-maintenance-box">
    <view class="u-orb u-orb-1"></view>
    <view class="u-orb u-orb-2"></view>

    <view class="u-body">
      <view class="u-icon-wrap">
        <view class="u-icon-spin">
          <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <circle cx="12" cy="12" r="3" />
            <path
              d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 1 1-4 0v-.09a1.65 1.65 0 0 0-1-1.51 1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.6 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 1 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 1 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 1 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"
            />
          </svg>
        </view>
      </view>

      <text class="u-title">{{ pageTitle }}</text>
      <text class="u-sub">{{ pageSubtitle }}</text>

      <view class="u-countdown">
        <view class="u-countdown-col">
          <text class="u-cd-label">{{ $t('maintenancePage.countdownLabel') }}</text>
          <text class="u-cd-time u-cd-time-main">{{ countdownText }}</text>
        </view>
        <view class="u-countdown-col">
          <text class="u-cd-label">{{ $t('maintenancePage.elapsedLabel') }}</text>
          <text class="u-cd-time u-cd-time-sub">{{ elapsedText }}</text>
        </view>
      </view>

      <view class="u-detail">
        <view class="u-row">
          <text class="u-row-label">{{ $t('maintenancePage.detail.maintId') }}</text>
          <text class="u-row-value">{{ maintId }}</text>
        </view>
        <view class="u-row">
          <text class="u-row-label">{{ $t('maintenancePage.detail.startTime') }}</text>
          <text class="u-row-value">{{ startTimeText }}</text>
        </view>
        <view class="u-row">
          <text class="u-row-label">{{ $t('maintenancePage.detail.endTime') }}</text>
          <text class="u-row-value">{{ endTimeText }}</text>
        </view>
        <view class="u-row">
          <text class="u-row-label">{{ $t('maintenancePage.detail.scope') }}</text>
          <text class="u-row-value">{{ scopeText }}</text>
        </view>
        <view class="u-row">
          <text class="u-row-label">{{ $t('maintenancePage.detail.status') }}</text>
          <view class="u-live">
            <view class="u-live-dot"></view>
            <text>{{ $t('maintenancePage.statusChecking') }}</text>
          </view>
        </view>
      </view>

      <view class="u-actions">
        <view
          class="u-btn outline"
          :class="{ loading: contactLoading }"
          @tap="onContactService"
        >
          <up-loading-icon v-if="contactLoading" size="28rpx" color="#fff" />
          <template v-else>
            <svg
              class="u-btn-icon"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M4 14v-2a8 8 0 0 1 16 0v2" />
              <path d="M20 14v3a2 2 0 0 1-2 2h-1v-5h3z" />
              <path d="M4 14v3a2 2 0 0 0 2 2h1v-5H4z" />
            </svg>
            <text>{{ $t('maintenancePage.contactService') }}</text>
          </template>
        </view>
        <view
          class="u-btn primary"
          :class="{ loading: retryLoading }"
          @tap="onRetryCheck"
        >
          <up-loading-icon v-if="retryLoading" size="28rpx" color="#0B1E47" />
          <template v-else>
            <svg
              class="u-btn-icon"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <polyline points="23 4 23 10 17 10" />
              <polyline points="1 20 1 14 7 14" />
              <path
                d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"
              />
            </svg>
            <text>{{ $t('maintenancePage.retryCheck') }}</text>
          </template>
        </view>
      </view>
    </view>

    <view class="u-footer">
      <text class="u-footer-line">{{ $t('maintenancePage.footerProgress') }}</text>
      <text class="u-footer-status">{{ statusUrl }}</text>
      <text class="u-footer-line u-footer-hotline">{{ hotlineText }}</text>
    </view>
  </view>
  <slot v-else />
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.m-maintenance-loading {
  min-height: 100vh;
  background: linear-gradient(160deg, #0b1e47 0%, #13306e 60%, #1e407f 100%);
}

.m-maintenance-box {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(160deg, #0b1e47 0%, #13306e 60%, #1e407f 100%);
  color: #fff;
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
}

.u-orb {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.u-orb-1 {
  width: 800rpx;
  height: 800rpx;
  background: radial-gradient(circle, rgba(212, 162, 76, 0.22), transparent 70%);
  top: -300rpx;
  right: -240rpx;
}

.u-orb-2 {
  width: 560rpx;
  height: 560rpx;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.25), transparent 70%);
  bottom: -200rpx;
  left: -160rpx;
}

.u-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48rpx 64rpx 32rpx;
  text-align: center;
  position: relative;
  z-index: 2;
}

.u-icon-wrap {
  width: 256rpx;
  height: 256rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #d4a24c, #f1e0bd);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 48rpx;
  box-shadow: 0 32rpx 80rpx rgba(212, 162, 76, 0.4);
}

.u-icon-spin {
  width: 124rpx;
  height: 124rpx;
  color: #0b1e47;
  animation: maint-spin 6s linear infinite;

  svg {
    width: 100%;
    height: 100%;
    display: block;
  }
}

@keyframes maint-spin {
  from {
    transform: rotate(0);
  }
  to {
    transform: rotate(360deg);
  }
}

.u-title {
  font-size: 48rpx;
  font-weight: 800;
  letter-spacing: 2rpx;
  background: linear-gradient(135deg, #fff, #f1e0bd);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.u-sub {
  margin-top: 20rpx;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.75);
  line-height: 1.6;
  max-width: 560rpx;
}

.u-countdown {
  margin-top: 56rpx;
  width: 100%;
  max-width: 640rpx;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.1);
  border: 2rpx solid rgba(255, 255, 255, 0.15);
  border-radius: 32rpx;
  padding: 32rpx 44rpx;
  backdrop-filter: blur(20rpx);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40rpx;
}

.u-countdown-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.u-cd-label {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.7);
}

.u-cd-time {
  margin-top: 4rpx;
  font-weight: 800;
  letter-spacing: 2rpx;
  line-height: 1.1;
  background: linear-gradient(135deg, #fff, #f1e0bd);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  @include stock-tnum;
}

.u-cd-time-main {
  font-size: 52rpx;
}

.u-cd-time-sub {
  font-size: 36rpx;
}

.u-detail {
  margin-top: 48rpx;
  width: 100%;
  max-width: 640rpx;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.06);
  border: 2rpx solid rgba(255, 255, 255, 0.1);
  border-radius: 28rpx;
  padding: 28rpx 36rpx;
}

.u-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10rpx 0;
  font-size: 23rpx;
}

.u-row-label {
  color: rgba(255, 255, 255, 0.65);
  flex-shrink: 0;
}

.u-row-value {
  color: #fff;
  font-weight: 600;
  text-align: right;
  max-width: 62%;
  @include stock-tnum;
}

.u-live {
  display: inline-flex;
  align-items: center;
  gap: 10rpx;
  color: #34d399;
  font-weight: 600;
}

.u-live-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: #34d399;
  animation: maint-blink 1.5s infinite;
}

@keyframes maint-blink {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.4;
  }
}

.u-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 64rpx;
  width: 100%;
  max-width: 640rpx;
}

.u-btn {
  flex: 1;
  min-height: 92rpx;
  border-radius: 24rpx;
  font-size: 28rpx;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  box-sizing: border-box;

  &:active {
    transform: scale(0.97);
  }

  &.outline {
    background: rgba(255, 255, 255, 0.1);
    border: 3rpx solid rgba(255, 255, 255, 0.25);
    color: #fff;
    backdrop-filter: blur(16rpx);
  }

  &.primary {
    background: linear-gradient(135deg, #d4a24c, #f1e0bd);
    color: #0b1e47;
    box-shadow: 0 16rpx 40rpx rgba(212, 162, 76, 0.35);
  }

  &.loading {
    pointer-events: none;
  }
}

.u-btn-icon {
  width: 32rpx;
  height: 32rpx;
  flex-shrink: 0;
}

.u-footer {
  flex-shrink: 0;
  padding: 0 64rpx calc(48rpx + env(safe-area-inset-bottom));
  text-align: center;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.5);
  line-height: 1.6;
  position: relative;
  z-index: 2;
}

.u-footer-line {
  display: block;
}

.u-footer-status {
  display: block;
  margin-top: 4rpx;
  color: #f1e0bd;
  font-weight: 600;
}

.u-footer-hotline {
  margin-top: 8rpx;
}
</style>
