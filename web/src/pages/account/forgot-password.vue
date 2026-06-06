<script setup>
import { ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';

import { useCommonStore } from '@/stores/common';
import { goBack, redirectTo } from '@/utils/navigate';
import { getService, openCustomerService } from '@/utils/biz';
import { doCopy } from '@/utils/uniUtils';

const { t } = useI18n();
const commonStore = useCommonStore();

/** 电话、邮箱写死，不调接口 */
const SUPPORT_PHONE_DISPLAY = '+52 55 8000 8888';
const SUPPORT_PHONE_DIAL = '+525580008888';
const SUPPORT_EMAIL = 'support@artisanpartners.com';

const customerUrl = ref('');
const onlineLoading = ref(false);

function prefetchService() {
  getService((url) => {
    if (url) customerUrl.value = url;
  });
}

function onOnlineService() {
  if (onlineLoading.value) return;
  openCustomerService({
    onLoading: (loading) => {
      onlineLoading.value = loading;
    },
    unavailableText: t('account.forgotPassword.onlineUnavailable')
  }).then((link) => {
    if (link) customerUrl.value = link;
  });
}

function onPhoneService() {
  uni.makePhoneCall({
    phoneNumber: SUPPORT_PHONE_DIAL,
    fail: () => {
      doCopy(SUPPORT_PHONE_DISPLAY);
    }
  });
}

function onEmailService() {
  doCopy(SUPPORT_EMAIL);
}

const onPageInit = () => {
  prefetchService();
};

onShow(() => {
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="p-auth-forgot">
      <view class="u-bg-decor"></view>

      <view class="u-top">
        <view class="u-close" @tap="goBack">
          <svg
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <polyline points="15 18 9 12 15 6" />
          </svg>
        </view>
        <text class="u-back-login" @tap="redirectTo('/pages/account/login')">{{
          $t('account.forgotPassword.backLogin')
        }}</text>
      </view>

      <view class="u-form-card">
        <view class="u-lock-wrap">
          <view class="u-lock-icon">
            <svg
              width="38"
              height="38"
              viewBox="0 0 24 24"
              fill="none"
              stroke="#F1E0BD"
              stroke-width="1.7"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <rect x="3" y="11" width="18" height="11" rx="2" />
              <path d="M7 11V7a5 5 0 0 1 10 0v4" />
              <circle cx="12" cy="16" r="1.4" />
              <path d="M12 17.4v1.6" />
            </svg>
          </view>
        </view>

        <text class="u-title">{{ $t('account.forgotPassword.title') }}</text>

        <view class="u-desc">
          <text>{{ $t('account.forgotPassword.desc1') }}</text>
          <text class="u-desc-line2">
            {{ $t('account.forgotPassword.desc2prefix') }}
            <text class="u-highlight">{{
              $t('account.forgotPassword.desc2highlight')
            }}</text>
            {{ $t('account.forgotPassword.desc2suffix') }}
          </text>
        </view>

        <view class="u-channels">
          <view class="u-channel" @tap="onOnlineService">
            <view class="u-channel-icon green">
              <svg
                width="22"
                height="22"
                viewBox="0 0 24 24"
                fill="none"
                stroke="#fff"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path
                  d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"
                />
              </svg>
            </view>
            <view class="u-channel-body">
              <text class="u-channel-title">{{
                $t('account.forgotPassword.onlineTitle')
              }}</text>
              <text class="u-channel-sub">{{
                $t('account.forgotPassword.onlineSub')
              }}</text>
            </view>
            <view class="u-channel-right">
              <text class="u-badge">{{ $t('account.forgotPassword.recommended') }}</text>
              <up-loading-icon
                v-if="onlineLoading"
                size="28rpx"
                color="rgba(255,255,255,0.5)"
              />
              <text v-else class="u-arrow">›</text>
            </view>
          </view>

          <view class="u-channel" @tap="onPhoneService">
            <view class="u-channel-icon blue">
              <svg
                width="22"
                height="22"
                viewBox="0 0 24 24"
                fill="none"
                stroke="#fff"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path
                  d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"
                />
              </svg>
            </view>
            <view class="u-channel-body">
              <text class="u-channel-title">{{
                $t('account.forgotPassword.phoneTitle')
              }}</text>
              <text class="u-channel-sub u-tnum">{{
                $t('account.forgotPassword.phoneSub')
              }}</text>
            </view>
            <text class="u-arrow">›</text>
          </view>

          <view class="u-channel" @tap="onEmailService">
            <view class="u-channel-icon amber">
              <svg
                width="22"
                height="22"
                viewBox="0 0 24 24"
                fill="none"
                stroke="#fff"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <rect x="2" y="4" width="20" height="16" rx="2" />
                <polyline points="22,6 12,13 2,6" />
              </svg>
            </view>
            <view class="u-channel-body">
              <text class="u-channel-title">{{
                $t('account.forgotPassword.emailTitle')
              }}</text>
              <text class="u-channel-sub">{{
                $t('account.forgotPassword.emailSub')
              }}</text>
            </view>
            <text class="u-arrow">›</text>
          </view>
        </view>

        <view class="u-prepare">
          <view class="u-prepare-head">
            <svg
              width="14"
              height="14"
              viewBox="0 0 24 24"
              fill="none"
              stroke="#F1E0BD"
              stroke-width="2.2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <circle cx="12" cy="12" r="10" />
              <path d="M12 16v-4M12 8h.01" />
            </svg>
            <text>{{ $t('account.forgotPassword.prepareTitle') }}</text>
          </view>
          <view class="u-prepare-list">
            <text>{{ $t('account.forgotPassword.prepareItem1') }}</text>
            <text>{{ $t('account.forgotPassword.prepareItem2') }}</text>
            <text>{{ $t('account.forgotPassword.prepareItem3') }}</text>
            <text>{{ $t('account.forgotPassword.prepareItem4') }}</text>
          </view>
        </view>

        <view class="u-btn-back" @tap="redirectTo('/pages/account/login')">
          {{ $t('account.forgotPassword.returnLogin') }}
        </view>
      </view>

      <text class="u-footer">{{ $t('account.forgotPassword.footer') }}</text>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.p-auth-forgot {
  min-height: 100vh;
  background: linear-gradient(160deg, #0b1e47 0%, #13306e 60%, #1e407f 100%);
  color: #fff;
  padding: 0 48rpx 60rpx;
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
}

.u-bg-decor {
  position: absolute;
  width: 600rpx;
  height: 600rpx;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(212, 162, 76, 0.22), transparent 70%);
  top: -200rpx;
  right: -180rpx;
  pointer-events: none;
}

.u-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 0;
  position: relative;
  z-index: 2;
}

.u-close {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.u-back-login {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.7);
  padding: 10rpx 24rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.2);
  border-radius: 28rpx;
  max-width: 60%;
  @include ellipsis;
}

.u-form-card {
  margin-top: 8rpx;
  width: 100%;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.06);
  border: 2rpx solid rgba(255, 255, 255, 0.1);
  border-radius: 36rpx;
  padding: 36rpx;
  backdrop-filter: blur(20rpx);
  position: relative;
  z-index: 2;
}

.u-lock-wrap {
  display: flex;
  justify-content: center;
  margin-bottom: 32rpx;
}

.u-lock-icon {
  width: 156rpx;
  height: 156rpx;
  border-radius: 44rpx;
  background: linear-gradient(135deg, rgba(212, 162, 76, 0.22), rgba(212, 162, 76, 0.06));
  border: 3rpx solid rgba(212, 162, 76, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 24rpx 56rpx rgba(0, 0, 0, 0.18);
}

.u-title {
  display: block;
  font-size: 44rpx;
  font-weight: 800;
  text-align: center;
  letter-spacing: 2rpx;
  color: #fff;
}

.u-desc {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  margin-top: 20rpx;
  padding: 0 16rpx;
  font-size: 25rpx;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.7;
  text-align: center;
}

.u-desc-line2 {
  display: block;
}

.u-highlight {
  color: #f1e0bd;
  font-weight: 700;
}

.u-channels {
  margin-top: 48rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.u-channel {
  display: flex;
  align-items: center;
  gap: 28rpx;
  padding: 28rpx 32rpx;
  background: rgba(255, 255, 255, 0.07);
  border: 2rpx solid rgba(255, 255, 255, 0.12);
  border-radius: 28rpx;

  &:active {
    background: rgba(255, 255, 255, 0.1);
  }
}

.u-channel-icon {
  width: 84rpx;
  height: 84rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  &.green {
    background: linear-gradient(135deg, #10b981, #059669);
    box-shadow: 0 12rpx 28rpx rgba(16, 185, 129, 0.3);
  }

  &.blue {
    background: linear-gradient(135deg, #2563eb, #1e40af);
    box-shadow: 0 12rpx 28rpx rgba(37, 99, 235, 0.3);
  }

  &.amber {
    background: linear-gradient(135deg, #f59e0b, #d97706);
    box-shadow: 0 12rpx 28rpx rgba(245, 158, 11, 0.3);
  }
}

.u-channel-body {
  flex: 1;
  min-width: 0;
}

.u-channel-title {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: #fff;
}

.u-channel-sub {
  display: block;
  margin-top: 6rpx;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.6);
}

.u-tnum {
  @include stock-tnum;
}

.u-channel-right {
  display: flex;
  align-items: center;
  gap: 12rpx;
  flex-shrink: 0;
}

.u-badge {
  padding: 6rpx 16rpx;
  background: rgba(16, 185, 129, 0.18);
  color: #34d399;
  border-radius: 16rpx;
  font-size: 20rpx;
  font-weight: 700;
}

.u-arrow {
  color: rgba(255, 255, 255, 0.4);
  font-size: 36rpx;
  line-height: 1;
  flex-shrink: 0;
}

.u-prepare {
  margin-top: 40rpx;
  padding: 28rpx;
  background: rgba(212, 162, 76, 0.08);
  border: 2rpx solid rgba(212, 162, 76, 0.25);
  border-radius: 24rpx;
}

.u-prepare-head {
  display: flex;
  align-items: center;
  gap: 16rpx;
  font-size: 24rpx;
  font-weight: 700;
  color: #f1e0bd;
  margin-bottom: 16rpx;
}

.u-prepare-list {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
  font-size: 23rpx;
  color: rgba(255, 255, 255, 0.75);
  line-height: 1.7;
}

.u-btn-back {
  margin-top: 40rpx;
  height: 92rpx;
  border-radius: 24rpx;
  border: 3rpx solid rgba(212, 162, 76, 0.5);
  color: #f1e0bd;
  font-size: 30rpx;
  font-weight: 800;
  text-align: center;
  line-height: 86rpx;
  letter-spacing: 16rpx;

  &:active {
    background: rgba(212, 162, 76, 0.1);
  }
}

.u-footer {
  display: block;
  text-align: center;
  padding-top: 32rpx;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.4);
  position: relative;
  z-index: 2;
}
</style>
