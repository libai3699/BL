<script setup>
import { computed, ref, watch } from 'vue';
import { onShow } from '@dcloudio/uni-app';

import { useCommonStore } from '@/stores/common';
import { useUserStore } from '@/stores/user';
import { goBack, navigateTo, reLaunch } from '@/utils/navigate';
import { showConfirm } from '@/utils/uniUtils';
import { getAndGoService } from '@/utils/biz';
import { solveNormalData } from '@/utils/solveData';
import { reqUserGetUserAccount } from '@/apis/user';

const commonStore = useCommonStore();
const userStore = useUserStore();
const user = () => userStore.userInfo.value;

const refIsInited = ref(false);
const refTabs = ref();
const refTab = ref('');
const refDataBoxDetail = ref({
  detail: null,
  isEmpty: () =>
    refDataBoxDetail.value.detail == null ||
    Object.keys(refDataBoxDetail.value.detail)?.length === 0,
  isNotGetData: () =>
    refDataBoxDetail.value.loading ||
    refDataBoxDetail.value.error ||
    refDataBoxDetail.value.isEmpty(),
  loading: true,
  error: false,
  errorText: ''
});

const cpdAccountData = computed(() => {
  return refDataBoxDetail.value.detail?.accounts?.[0] || {};
});

watch(
  () => commonStore.locale,
  () => {
    getData();
  }
);

function onChooseTab(tab) {
  refTab.value = tab.value;
  getData();
}

function getData() {
  const reqParams = {};
  if (refTab.value) {
    reqParams.type = refTab.value;
  }
  solveNormalData({
    refData: refDataBoxDetail,
    reqMethod: reqUserGetUserAccount,
    reqParams,
    onSuccess(res) {
      refTabs.value = res.accountTypes.map((item) => ({
        label: item.title,
        value: item.type
      }));
      if (refTab.value === '') {
        refTab.value = refTabs.value[0]?.value || '';
      }
    }
  });
}

function goProfile() {
  navigateTo('/pages/mine/profile/index');
}

function goMessage() {
  navigateTo('/pages/message/index');
}

function goFundDetail() {
  navigateTo('/pages/fund-record/index');
}

const payPwdMeta = computed(() =>
  user()?.hasPayPassword ? uni.$t('mine.index.meta.set') : uni.$t('mine.index.meta.unset')
);

const authMeta = computed(() => {
  const s = user()?.isActive;
  if (s === 2) return uni.$t('mine.index.meta.passed');
  if (s === 1) return uni.$t('mine.index.meta.pending');
  return uni.$t('mine.index.meta.unverified');
});

const authMetaClass = computed(() => (user()?.isActive === 2 ? 's-success' : ''));

const currencyLabel = computed(() => (refTab.value || 'mxn').toUpperCase());

const avatarLetter = computed(() => {
  const name = user()?.nickName || user()?.userName || 'A';
  return String(name).charAt(0).toUpperCase();
});

const displaySubtitle = computed(() => {
  const name = user()?.nickName || user()?.userName || '';
  const id = user()?.id;
  if (name && id) {
    return `${name} · ${uni.$t('mine.index.registerNo')} ${id}`;
  }
  return name || (id ? `${uni.$t('mine.index.registerNo')} ${id}` : '');
});

const todayPnlPercent = computed(() => {
  const total = Number(cpdAccountData.value?.totalAsset);
  const pnl = Number(cpdAccountData.value?.unrealizedPnl);
  if (!total || Number.isNaN(pnl)) return '';
  const pct = ((pnl / total) * 100).toFixed(2);
  return `(${pnl >= 0 ? '+' : ''}${pct}%)`;
});

const pnlSummaryClass = computed(() => {
  const pnl = Number(cpdAccountData.value?.unrealizedPnl);
  if (Number.isNaN(pnl) || pnl === 0) return '';
  return pnl > 0 ? 's-up' : 's-down';
});

const notifyMeta = computed(() => {
  const count = commonStore.unreadCount;
  if (count > 0) {
    return uni.$t('mine.index.meta.newMessages', { count });
  }
  return '';
});

const menuIconMap = {
  loginPwd: 'stock-lock',
  fundPwd: 'mine-menu-fund-pwd',
  notify: 'stock-mail',
  kyc: 'stock-id-card',
  coupon: 'stock-coupon',
  service: 'stock-headphone',
  setting: 'stock-settings',
  logout: 'stock-logout'
};

/** 四宫格图标与原型 pf-actions 一致 */
const quickActions = [
  {
    key: 'recharge',
    labelKey: 'mine.index.link.recharge',
    path: '/pages/mine/recharge/index',
    icon: 'mine-pf-recharge',
    iconColor: '#8B6914',
    premium: true
  },
  {
    key: 'withdraw',
    labelKey: 'mine.index.link.withdraw',
    path: '/pages/mine/withdraw/index',
    icon: 'mine-pf-withdraw',
    iconColor: '#0B1E47'
  },
  {
    key: 'transfer',
    labelKey: 'common.transfer',
    path: '/pages/transfer/index',
    icon: 'mine-pf-transfer',
    iconColor: '#0B1E47'
  },
  {
    key: 'bill',
    labelKey: 'mine.index.link.bill',
    path: '/pages/fund-record/index',
    icon: 'mine-pf-bill',
    iconColor: '#0B1E47'
  }
];

const menus1 = [
  {
    key: 'loginPwd',
    labelKey: 'mine.index.link.loginPassword',
    path: '/pages/mine/login-password/index'
  },
  {
    key: 'fundPwd',
    labelKey: 'mine.index.link.payPassword',
    path: '/pages/mine/pay-password/index'
  },
  {
    key: 'notify',
    labelKey: 'mine.index.link.notifications',
    path: '/pages/message/index'
  },
  {
    key: 'kyc',
    labelKey: 'mine.index.link.authentication',
    path: '/pages/mine/authentication/index'
  }
];

const menus2 = [
  {
    key: 'coupon',
    labelKey: 'mine.index.link.coupon',
    path: '/pages/coupon/index',
    meta: '5'
  },
  { key: 'service', labelKey: 'mine.index.link.support' },
  { key: 'setting', labelKey: 'mine.index.link.settings', path: '/pages/settings/index' },
  { key: 'logout', labelKey: 'mine.index.button.logout' }
];

function onMenuTap(item) {
  if (item.key === 'logout') {
    doLogoutConfirm();
    return;
  }
  if (item.key === 'service') {
    getAndGoService();
    return;
  }
  if (item.path) {
    navigateTo(item.path);
  }
}

function getMenuMeta(item) {
  if (item.key === 'loginPwd') return uni.$t('mine.index.meta.set');
  if (item.key === 'fundPwd') return payPwdMeta.value;
  if (item.key === 'notify') return notifyMeta.value;
  if (item.key === 'kyc') return authMeta.value;
  if (item.meta) return `${item.meta} ${uni.$t('mine.index.meta.couponUnit')}`;
  return '';
}

function getMenuMetaClass(item) {
  if (item.key === 'kyc') return authMetaClass.value;
  return '';
}

function doLogoutConfirm() {
  showConfirm(uni.$t('common.logoutConfirm'), (res) => {
    if (res.confirm) {
      doLogout();
    }
  });
}

function doLogout() {
  setTimeout(() => {
    userStore.logout();
    reLaunch('/pages/account/login');
  });
}

const onPageInit = async () => {
  if (userStore.needGetUserInfo) {
    userStore.getUserInfo();
  }
  refIsInited.value = true;
  getData();
};

onShow(() => {
  getData();
  commonStore.fnBack = goBack;
  if (!refIsInited.value) return;
  userStore.getUserInfo();
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="mine-page">
      <!-- 顶部渐变区（原型 profile-top） -->
      <view class="profile-top">
        <view class="profile-row">
          <view class="pf-avatar-wrap" @tap="goProfile">
            <c-avatar
              v-if="user()?.headImage"
              :src="user()?.headImage"
              size="lg"
              class="pf-avatar-img"
            />
            <view v-else class="pf-avatar">{{ avatarLetter }}</view>
          </view>

          <view class="pf-info" @tap="goProfile">
            <text class="pf-uid">UID {{ user()?.id }}</text>
            <text class="pf-sub">{{ displaySubtitle }}</text>
            <view class="pf-badges">
              <text
                class="pf-badge"
                :class="user()?.isActive === 2 ? 'pf-badge--verify' : 'pf-badge--muted'"
              >
                {{ user()?.isActive === 2 ? '✓ ' : ''
                }}{{ $t(`options.authentication.${user()?.isActive ?? 0}`) }}
              </text>
              <text
                class="pf-badge pf-badge--vip"
                @tap.stop="navigateTo('/pages/mine/vip/index')"
              >
                👑 {{ user()?.levelName || 'VIP' }}
              </text>
            </view>
          </view>

          <view class="pf-msg" @tap="goMessage">
            <svg-icon name="stock-mail" :size="32" color="#fff" />
            <view v-if="commonStore.unreadCount > 0" class="pf-msg-dot" />
          </view>
        </view>

        <view class="pf-asset-mini">
          <view v-if="refTabs?.length > 1" class="pf-acc-tabs">
            <view
              v-for="tab in refTabs"
              :key="tab.value"
              class="pf-acc-tab"
              :class="{ active: refTab === tab.value }"
              @tap.stop="onChooseTab(tab)"
            >
              {{ tab.label }}
            </view>
          </view>

          <view class="pf-asset-row1">
            <view>
              <view class="pf-asset-label">
                {{ $t('mine.index.amount.myAsset', { currency: currencyLabel }) }}
                <svg-icon
                  :name="commonStore.dataEye.mine ? 'eye-show' : 'eye-close'"
                  :size="26"
                  class="pf-eye"
                  @tap.stop="commonStore.dataEye.mine = !commonStore.dataEye.mine"
                />
              </view>
              <view class="pf-asset-total" translate="no">
                <template v-if="commonStore.dataEye.mine">
                  <c-amount
                    isUseCountTo
                    :loading="refDataBoxDetail.loading"
                    :value="cpdAccountData?.totalAsset"
                  />
                </template>
                <text v-else>****</text>
                <text class="pf-asset-cur">{{ currencyLabel }}</text>
              </view>
            </view>
            <text class="pf-detail-link" @tap="goFundDetail">
              {{ $t('mine.index.link.assetDetail') }} ›
            </text>
          </view>

          <view class="pf-asset-summary">
            <view class="pf-summary-item">
              <text>{{ $t('mine.index.amount.todayPnl') }} </text>
              <text class="pf-pnl" :class="pnlSummaryClass" translate="no">
                <template v-if="commonStore.dataEye.mine">
                  <text v-if="Number(cpdAccountData?.unrealizedPnl) > 0">+</text>
                  <c-amount
                    :loading="refDataBoxDetail.loading"
                    :value="cpdAccountData?.unrealizedPnl"
                  />
                  <text>{{ todayPnlPercent }}</text>
                </template>
                <text v-else>****</text>
              </text>
            </view>
            <view class="pf-summary-item">
              <text>{{ $t('mine.index.amount.available') }} </text>
              <text class="pf-available" translate="no">
                <template v-if="commonStore.dataEye.mine">
                  <c-amount
                    :loading="refDataBoxDetail.loading"
                    :value="cpdAccountData?.availableAmount"
                  />
                </template>
                <text v-else>****</text>
              </text>
            </view>
          </view>
        </view>
      </view>

      <!-- 四宫格快捷操作（原型 pf-actions） -->
      <view class="pf-actions">
        <view
          v-for="action in quickActions"
          :key="action.key"
          class="pf-action"
          :class="{ 'pf-action--deposit': action.premium }"
          @tap="navigateTo(action.path)"
        >
          <view class="pf-action-ico">
            <svg-icon :name="action.icon" :size="44" :color="action.iconColor" />
          </view>
          <text>{{ $t(action.labelKey) }}</text>
        </view>
      </view>

      <!-- 菜单组 1 -->
      <view class="menu-card">
        <view
          v-for="item in menus1"
          :key="item.key"
          class="menu-item"
          @tap="onMenuTap(item)"
        >
          <view class="menu-ico">
            <svg-icon :name="menuIconMap[item.key]" :size="36" color="#0B1E47" />
          </view>
          <text class="menu-label">{{ $t(item.labelKey) }}</text>
          <text
            v-if="getMenuMeta(item)"
            class="menu-meta"
            :class="getMenuMetaClass(item)"
            >{{ getMenuMeta(item) }}</text
          >
          <text class="menu-arrow">›</text>
        </view>
      </view>

      <!-- 菜单组 2 -->
      <view class="menu-card">
        <view
          v-for="item in menus2"
          :key="item.key"
          class="menu-item"
          @tap="onMenuTap(item)"
        >
          <view class="menu-ico">
            <svg-icon :name="menuIconMap[item.key]" :size="36" color="#0B1E47" />
          </view>
          <text class="menu-label">{{ $t(item.labelKey) }}</text>
          <text
            v-if="getMenuMeta(item)"
            class="menu-meta"
            :class="getMenuMetaClass(item)"
            >{{ getMenuMeta(item) }}</text
          >
          <text class="menu-arrow">›</text>
        </view>
      </view>

      <view class="mine-bottom-spacer" />
      <tab-bar :page="$page" />
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.mine-page {
  @include hasBottomNav();
  min-height: 100vh;
  background: var(--stock-page-bg, #eef2f8);
}

.profile-top {
  position: relative;
  padding: 32rpx 32rpx 56rpx;
  color: #fff;
  background: linear-gradient(160deg, #0b1e47 0%, #13306e 60%, #1e407f 100%);
  border-radius: 0 0 44rpx 44rpx;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    width: 560rpx;
    height: 560rpx;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(212, 162, 76, 0.18) 0%, transparent 70%);
    top: -120rpx;
    right: -180rpx;
    pointer-events: none;
  }
}

.profile-row {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: flex-start;
  gap: 24rpx;
}

.pf-avatar-wrap {
  flex-shrink: 0;
}

.pf-avatar {
  width: 108rpx;
  height: 108rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  font-weight: 800;
  color: #0b1e47;
  background: linear-gradient(135deg, #f1e0bd, #d4a24c);
  border: 4rpx solid rgba(255, 255, 255, 0.4);
}

.pf-avatar-img {
  width: 108rpx;
  height: 108rpx;
}

.pf-info {
  flex: 1;
  min-width: 0;
  padding-top: 4rpx;
}

.pf-uid {
  display: block;
  font-size: 36rpx;
  font-weight: 800;
  letter-spacing: 1rpx;
}

.pf-sub {
  display: block;
  margin-top: 4rpx;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.6);
  @include ellipsis;
}

.pf-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 12rpx;
}

.pf-badge {
  display: inline-flex;
  align-items: center;
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
  font-size: 20rpx;
  font-weight: 600;

  &--verify {
    background: rgba(52, 211, 153, 0.15);
    color: #34d399;
  }

  &--muted {
    background: rgba(255, 255, 255, 0.12);
    color: rgba(255, 255, 255, 0.75);
  }

  &--vip {
    background: linear-gradient(135deg, #d4a24c, #f1e0bd);
    color: #0b1e47;
  }
}

.pf-msg {
  position: relative;
  z-index: 2;
  flex-shrink: 0;
  width: 68rpx;
  height: 68rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.12);
}

.pf-msg-dot {
  position: absolute;
  top: 12rpx;
  right: 12rpx;
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: var(--stock-gold, #d4a24c);
}

.pf-asset-mini {
  position: relative;
  z-index: 2;
  margin-top: 36rpx;
  padding: 32rpx;
  border-radius: 32rpx;
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.1),
    rgba(255, 255, 255, 0.03)
  );
  border: 1rpx solid rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(20rpx);
}

.pf-acc-tabs {
  display: flex;
  gap: 0;
  margin-bottom: 24rpx;
  width: fit-content;
}

.pf-acc-tab {
  padding: 12rpx 32rpx;
  border-radius: 36rpx;
  font-size: 24rpx;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.75);

  &.active {
    background: rgba(255, 255, 255, 0.2);
    color: #fff;
  }
}

.pf-asset-row1 {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16rpx;
}

.pf-asset-label {
  display: flex;
  align-items: center;
  gap: 12rpx;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.65);
}

.pf-eye {
  flex-shrink: 0;
}

.pf-asset-total {
  display: flex;
  align-items: baseline;
  flex-wrap: wrap;
  gap: 8rpx;
  margin-top: 8rpx;
  font-size: 52rpx;
  font-weight: 800;
  letter-spacing: 0.6rpx;
  font-feature-settings: 'tnum';
}

.pf-asset-cur {
  font-size: 26rpx;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.7);
}

.pf-detail-link {
  flex-shrink: 0;
  padding-bottom: 8rpx;
  font-size: 24rpx;
  font-weight: 600;
  color: #f1e0bd;
}

.pf-asset-summary {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 1rpx dashed rgba(255, 255, 255, 0.15);
  font-size: 23rpx;
  color: rgba(255, 255, 255, 0.7);
  font-feature-settings: 'tnum';
}

.pf-summary-item {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 4rpx;
  min-width: 0;
}

.pf-pnl.s-up {
  color: #34d399;
  font-weight: 700;
}

.pf-pnl.s-down {
  color: #f87171;
  font-weight: 700;
}

.pf-available {
  color: #fff;
  font-weight: 700;
}

.pf-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  margin: 28rpx 24rpx 0;
  padding: 40rpx 16rpx 36rpx;
  background: #fff;
  border-radius: var(--stock-radius, 24rpx);
  box-shadow:
    0 8rpx 28rpx rgba(15, 30, 71, 0.07),
    0 2rpx 6rpx rgba(15, 30, 71, 0.04);
}

.pf-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
  font-size: 24rpx;
  font-weight: 500;
  color: var(--stock-ink, #0f172a);

  &:active .pf-action-ico {
    transform: scale(0.96);
  }
}

.pf-action-ico {
  width: 88rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 28rpx;
  background: #f4f6fb;
  border: 1rpx solid #ebeff7;
  transition: transform 0.2s;
}

.pf-action--deposit .pf-action-ico {
  background: linear-gradient(135deg, #fcf1d6 0%, #f1e0bd 100%);
  border-color: #ead89a;
}

.menu-card {
  margin: 24rpx 24rpx 0;
  background: #fff;
  border-radius: var(--stock-radius, 24rpx);
  box-shadow:
    0 8rpx 28rpx rgba(15, 30, 71, 0.07),
    0 2rpx 6rpx rgba(15, 30, 71, 0.04);
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 28rpx;
  padding: 30rpx 32rpx;
  border-bottom: 1rpx solid var(--line-color, #e5e9f2);

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: #f8fafc;
  }
}

.menu-ico {
  flex-shrink: 0;
  width: 68rpx;
  height: 68rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 22rpx;
  background: #f4f6fb;
  border: 1rpx solid #ebeff7;
}

.menu-label {
  flex: 1;
  font-size: 28rpx;
  font-weight: 500;
  color: var(--stock-ink, #0f172a);
}

.menu-meta {
  font-size: 24rpx;
  color: var(--stock-ink-3, #64748b);
  margin-right: 8rpx;

  &.s-success {
    color: var(--stock-green, #10b981);
  }
}

.menu-arrow {
  font-size: 36rpx;
  color: var(--stock-ink-3, #64748b);
}

.mine-bottom-spacer {
  height: 32rpx;
}
</style>
