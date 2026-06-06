<script setup>
import { computed, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';

import { useCommonStore } from '@/stores/common';
import { goBack, switchTab } from '@/utils/navigate';
import { openCustomerService } from '@/utils/biz';
import { navigateFromErrorPage } from '@/utils/errorPage';

const { t } = useI18n();
const commonStore = useCommonStore();

const errorType = ref('404');
const onlineLoading = ref(false);

const ERROR_TYPES = ['404', 'network', 'server'];

const errorView = computed(() => {
  const type = errorType.value;
  const prefix = `errorPage.${type}`;
  return {
    code: t(`${prefix}.code`),
    title: t(`${prefix}.title`),
    desc: t(`${prefix}.desc`),
    cta: t(`${prefix}.cta`),
    metaId: t(`${prefix}.metaId`)
  };
});

const HOME_PATH = '/pages/home/index';

function onPrimaryAction() {
  if (errorType.value === '404') {
    navigateFromErrorPage(HOME_PATH);
    return;
  }

  const pages = getCurrentPages();
  if (pages.length > 1) {
    goBack();
    return;
  }

  navigateFromErrorPage(HOME_PATH);
}

function onOnlineService() {
  if (onlineLoading.value) return;
  openCustomerService({
    onLoading: (loading) => {
      onlineLoading.value = loading;
    },
    unavailableText: t('errorPage.contactUnavailable')
  });
}

onLoad((options) => {
  const type = options?.type || '404';
  errorType.value = ERROR_TYPES.includes(type) ? type : '404';
});

onShow(() => {
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper>
    <view class="p-error">
      <view class="u-orb u-orb-1"></view>
      <view class="u-orb u-orb-2"></view>

      <view class="u-header">
        <view class="u-back" @tap="goBack">
          <text class="u-back-icon">‹</text>
        </view>
        <text class="u-header-title">{{ $t('errorPage.headerTitle') }}</text>
        <view class="u-header-placeholder"></view>
      </view>

      <view class="u-body">
        <err-illustration :type="errorType" />

        <text class="u-code">{{ errorView.code }}</text>
        <text class="u-title">{{ errorView.title }}</text>
        <text class="u-desc">{{ errorView.desc }}</text>

        <view class="u-meta">
          <text>{{ $t('errorPage.metaLabel') }}</text>
          <text class="u-meta-id">{{ errorView.metaId }}</text>
        </view>

        <view class="u-actions">
          <view class="u-btn primary" @tap="onPrimaryAction">{{ errorView.cta }}</view>
          <view class="u-btn outline" @tap="onOnlineService">
            <up-loading-icon v-if="onlineLoading" size="28rpx" color="#fff" />
            <text v-else>{{ $t('errorPage.contactService') }}</text>
          </view>
        </view>

        <view class="u-quick">
          <view class="u-quick-item" @tap="navigateFromErrorPage(HOME_PATH)">
            <text>🏠 {{ $t('errorPage.quickHome') }}</text>
          </view>
          <view class="u-quick-item" @tap="switchTab('/pages/market/index')">
            <text>📊 {{ $t('errorPage.quickMarket') }}</text>
          </view>
          <view class="u-quick-item" @tap="switchTab('/pages/orders/index')">
            <text>📋 {{ $t('errorPage.quickOrders') }}</text>
          </view>
        </view>
      </view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.p-error {
  min-height: 100vh;
  background: linear-gradient(180deg, #0b1e47 0%, #13306e 50%, #1e407f 100%);
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
  width: 560rpx;
  height: 560rpx;
  background: radial-gradient(circle, rgba(212, 162, 76, 0.22), transparent 70%);
  top: -200rpx;
  right: -160rpx;
}

.u-orb-2 {
  width: 400rpx;
  height: 400rpx;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.25), transparent 70%);
  bottom: 160rpx;
  left: -160rpx;
}

.u-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 24rpx 0;
  position: relative;
  z-index: 2;
}

.u-back {
  width: 72rpx;
  height: 72rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.u-back-icon {
  font-size: 44rpx;
  line-height: 1;
  color: #fff;
  margin-top: -4rpx;
}

.u-header-title {
  font-size: 32rpx;
  font-weight: 600;
  letter-spacing: 2rpx;
  color: #fff;
  font-family:
    'Trajan Pro', 'Cinzel', 'Cormorant Garamond', 'Playfair Display', Georgia,
    'Times New Roman', serif;
}

.u-header-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.u-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx 64rpx 80rpx;
  text-align: center;
  position: relative;
  z-index: 2;
}

.u-code {
  font-size: 176rpx;
  font-weight: 300;
  line-height: 1;
  letter-spacing: 12rpx;
  margin-bottom: 12rpx;
  font-family:
    'Trajan Pro', 'Cinzel', 'Cormorant Garamond', 'Playfair Display', Georgia,
    'Times New Roman', serif;
  background: linear-gradient(135deg, #fff, #f1e0bd 50%, #d4a24c);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  @include stock-tnum;
}

.u-title {
  font-size: 44rpx;
  font-weight: 800;
  letter-spacing: 2rpx;
  color: #fff;
  font-family:
    'Trajan Pro', 'Cinzel', 'Cormorant Garamond', 'Playfair Display', Georgia,
    'Times New Roman', serif;
}

.u-desc {
  margin-top: 20rpx;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.7;
  max-width: 600rpx;
}

.u-meta {
  margin-top: 28rpx;
  display: inline-flex;
  align-items: center;
  gap: 12rpx;
  padding: 10rpx 24rpx;
  background: rgba(255, 255, 255, 0.07);
  border: 2rpx solid rgba(255, 255, 255, 0.15);
  border-radius: 24rpx;
  font-size: 21rpx;
  color: rgba(255, 255, 255, 0.6);
  @include stock-tnum;
}

.u-meta-id {
  color: #f1e0bd;
  font-weight: 700;
}

.u-actions {
  margin-top: 64rpx;
  width: 100%;
  max-width: 600rpx;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.u-btn {
  box-sizing: border-box;
  min-height: 96rpx;
  padding: 24rpx 40rpx;
  border-radius: 28rpx;
  font-size: 28rpx;
  font-weight: 800;
  letter-spacing: 2rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  &.primary {
    background: linear-gradient(135deg, #d4a24c, #f1e0bd);
    color: #0b1e47;
    box-shadow: 0 20rpx 48rpx rgba(212, 162, 76, 0.35);

    &:active {
      transform: translateY(4rpx);
      box-shadow: 0 12rpx 32rpx rgba(212, 162, 76, 0.3);
    }
  }

  &.outline {
    background: rgba(255, 255, 255, 0.06);
    border: 3rpx solid rgba(255, 255, 255, 0.2);
    color: #fff;

    &:active {
      background: rgba(255, 255, 255, 0.12);
    }
  }
}

.u-quick {
  margin-top: 64rpx;
  width: 100%;
  max-width: 600rpx;
  display: flex;
  gap: 12rpx;
  padding: 12rpx;
  background: rgba(255, 255, 255, 0.06);
  border: 2rpx solid rgba(255, 255, 255, 0.12);
  border-radius: 28rpx;
  backdrop-filter: blur(20rpx);
}

.u-quick-item {
  flex: 1;
  padding: 18rpx 20rpx;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 600;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  &:active {
    background: rgba(255, 255, 255, 0.1);
  }
}
</style>
