<script setup>
import { computed, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';

import {
  applyMoreFollow,
  applySingleFollow,
  queryConfig,
  queryDayPackage,
  queryDayPackageInfo,
  queryMorePackage
} from '@/apis/advisor';
import { useCommonStore } from '@/stores/common';
import { goBack, navigateTo } from '@/utils/navigate';

const commonStore = useCommonStore();

const tabs = [
  { name: 'advisor.dayDelegation' },
  { name: 'advisor.periodDelegation' }
];

const activeTab = ref(0);
const mentorId = ref('');
const advisorName = ref('');

const dayPackage = ref({ id: '', minAmount: 0, maxAmount: 0, salaryRate: 0 });
const dayAmount = ref('');
const dayPackageInfo = ref([]);
const dayDetailVisible = ref(false);
const dayDetailContent = ref('');

const morePackages = ref([]);
const selectedMoreIndex = ref(0);
const moreAmount = ref('');
const moreDetailVisible = ref(false);
const moreDetailContent = ref('');

const loading = ref(false);

const selectedPeriod = computed(() => morePackages.value[selectedMoreIndex.value] || null);

const initDayPackage = async () => {
  if (!mentorId.value) return;
  const params = { mentorId: mentorId.value };
  const pkg = await queryDayPackage(params);
  if (pkg) {
    dayPackage.value = {
      id: pkg.id,
      minAmount: Number(pkg.minAmount || 0),
      maxAmount: Number(pkg.maxAmount || 0),
      salaryRate: Number(pkg.salaryRate || 0)
    };
  }
  const info = await queryDayPackageInfo(params);
  dayPackageInfo.value = Array.isArray(info) ? info : info?.list || [];
};

const initMorePackage = async () => {
  if (!mentorId.value) return;
  const list = await queryMorePackage({ mentorId: mentorId.value });
  const parsed = Array.isArray(list) ? list : list?.list || [];
  morePackages.value = parsed.map((item) => ({
    ...item,
    minAmount: Number(item.minAmount || 0),
    maxAmount: Number(item.maxAmount || 0),
    salaryRate: Number(item.salaryRate || 0)
  }));
  if (morePackages.value.length) {
    selectedMoreIndex.value = 0;
  }
};

const validateAmount = (amount, min, max) => {
  if (!amount) {
    uni.showToast({ title: uni.$t('advisor.enterAmount'), icon: 'none' });
    return false;
  }
  const numAmount = Number(amount);
  if (Number.isNaN(numAmount) || numAmount % 100 !== 0) {
    uni.showToast({ title: uni.$t('advisor.enterMultiple100'), icon: 'none' });
    return false;
  }
  if (numAmount < min) {
    uni.showToast({ title: uni.$t('advisor.amountNotLessThan').replace('{amount}', min), icon: 'none' });
    return false;
  }
  if (numAmount > max) {
    uni.showToast({ title: uni.$t('advisor.amountNotMoreThan').replace('{amount}', max), icon: 'none' });
    return false;
  }
  return true;
};

const submitDayFollow = async () => {
  if (loading.value) return;
  if (!validateAmount(dayAmount.value, dayPackage.value.minAmount, dayPackage.value.maxAmount)) {
    return;
  }
  loading.value = true;
  await applySingleFollow({
    amount: Number(dayAmount.value),
    mentorId: mentorId.value,
    stockType: 'mxg',
    packageId: dayPackage.value.id
  });
  uni.showToast({ title: uni.$t('advisor.applySuccess'), icon: 'success' });
  setTimeout(() => {
    navigateTo(`/pages/advisor/advisor-record/index?mentorId=${mentorId.value}`);
  }, 800);
  loading.value = false;
};

const submitMoreFollow = async () => {
  if (loading.value) return;
  const period = selectedPeriod.value;
  if (!period) {
    uni.showToast({ title: uni.$t('advisor.selectPeriod'), icon: 'none' });
    return;
  }
  if (!validateAmount(moreAmount.value, period.minAmount, period.maxAmount)) {
    return;
  }
  loading.value = true;
  await applyMoreFollow({
    amount: Number(moreAmount.value),
    mentorId: mentorId.value,
    stockType: 'mxg',
    packageId: period.id
  });
  uni.showToast({ title: uni.$t('advisor.applySuccess'), icon: 'success' });
  setTimeout(() => {
    navigateTo(`/pages/advisor/advisor-record/index?mentorId=${mentorId.value}`);
  }, 800);
  loading.value = false;
};

const handleTabChange = (index) => {
  activeTab.value = index;
  if (index === 1 && !morePackages.value.length) {
    initMorePackage();
  }
};

const showDayDetail = async () => {
  const res = await queryConfig({ key: '单日跟单详述' });
  dayDetailContent.value = res?.configValue || res?.data?.configValue || '';
  dayDetailVisible.value = true;
};

const showMoreDetail = async () => {
  const res = await queryConfig({ key: '多日跟单详述' });
  moreDetailContent.value = res?.configValue || res?.data?.configValue || '';
  moreDetailVisible.value = true;
};

const viewTradingRecords = () => {
  navigateTo(`/pages/advisor/trading-records/index?mentorId=${mentorId.value}&name=${advisorName}`);
};

const goToRecords = () => {
  navigateTo(`/pages/advisor/advisor-record/index?mentorId=${mentorId.value}`);
};

const backLastPage = () => {
  navigateTo(`/pages/advisor/advisor-follow/index?mentorId=${mentorId.value}`);
};

onLoad((options) => {
  console.log("options", options)
  mentorId.value = options?.mentorId || '';
  advisorName.value = decodeURIComponent(options?.name || '') || '';
  initDayPackage();
});

onShow(() => {
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper>
    <view class="apply-page">
      <nav-bar :title="$t('advisor.applyAdvisor')" @back="backLastPage" />

      <view class="content">
        <view class="tabs">
          <view v-for="(tab, index) in tabs" :key="tab.name" class="tab-item" :class="{ active: index === activeTab }"
            @click="handleTabChange(index)">
            {{ $t(tab.name) }}
          </view>
        </view>

        <view v-if="activeTab === 0" class="panel">
          <view class="card">
            <view class="card-header">
              <text class="advisor">{{ $t('advisor.advisorManager') }}：{{ advisorName || '—' }}</text>
            </view>

            <view class="stats">
              <view class="stat">
                <text class="stat-value">{{ dayPackage.minAmount || 0 }}</text>
                <text class="stat-label">{{ $t('advisor.minAmount') }}</text>
              </view>
              <view class="stat">
                <text class="stat-value">{{ dayPackage.maxAmount || 0 }}</text>
                <text class="stat-label">{{ $t('advisor.maxAmount') }}</text>
              </view>
              <view class="stat">
                <text class="stat-value">{{ dayPackage.salaryRate || 0 }}%</text>
                <text class="stat-label">{{ $t('advisor.commission') }}</text>
              </view>
            </view>

            <view class="note">
              {{ $t('advisor.amountNote') }}
              <text class="link" @click="showDayDetail">{{ $t('advisor.dayDetail') }}</text>
            </view>

            <input v-model="dayAmount" type="number" class="amount-input" :placeholder="$t('advisor.enterAmount')" />

            <button class="primary-btn" @click="submitDayFollow">{{ $t('advisor.delegateFollow') }}</button>
          </view>

          <view class="quick-links">
            <view class="link-item" @click="viewTradingRecords">{{ $t('advisor.tradingRecord') }}</view>
            <view class="link-item" @click="goToRecords">{{ $t('advisor.advisorRecord') }}</view>
          </view>

          <view class="table-card">
            <text class="table-title">{{ $t('advisor.dayDescription') }}</text>
            <view class="table table--four">
              <view class="table-header">
                <text>{{ $t('advisor.level') }}</text>
                <text>{{ $t('advisor.minAmount') }}</text>
                <text>{{ $t('advisor.maxAmount') }}</text>
                <text>{{ $t('advisor.position') }}</text>
              </view>
              <view v-for="(item, index) in dayPackageInfo" :key="index" class="table-row">
                <text>{{ item.levelName }}</text>
                <text>{{ item.minAmount }}</text>
                <text>{{ item.maxAmount }}</text>
                <text>{{ item.positionRate }}%</text>
              </view>
            </view>
          </view>
        </view>

        <view v-else class="panel">
          <view class="card">
            <view class="card-header">
              <text class="advisor">{{ $t('advisor.advisorManager') }}：{{ advisorName || '—' }}</text>
            </view>

            <view v-if="selectedPeriod" class="stats stats--four">
              <view class="stat">
                <text class="stat-value">{{ selectedPeriod.minAmount || 0 }}</text>
                <text class="stat-label">{{ $t('advisor.minAmount') }}</text>
              </view>
              <view class="stat">
                <text class="stat-value">{{ selectedPeriod.maxAmount || 0 }}</text>
                <text class="stat-label">{{ $t('advisor.maxAmount') }}</text>
              </view>
              <view class="stat">
                <text class="stat-value">{{ selectedPeriod.salaryRate || 0 }}%</text>
                <text class="stat-label">{{ $t('advisor.commission') }}</text>
              </view>
              <view class="stat">
                <text class="stat-value">{{ selectedPeriod.holdDays || 0 }}{{ $t('advisor.days') }}</text>
                <text class="stat-label">{{ $t('advisor.period') }}</text>
              </view>
            </view>

            <view class="note">
              {{ $t('advisor.amountNote') }}
              <text class="link" @click="showMoreDetail">{{ $t('advisor.periodDetail') }}</text>
            </view>

            <input v-model="moreAmount" type="number" class="amount-input" :placeholder="$t('advisor.enterAmount')" />
          </view>

          <view class="card">
            <view class="section-title">{{ $t('advisor.applyPeriod') }}</view>

            <view class="period-list">
              <view v-for="(item, index) in morePackages" :key="item.id" class="period-item"
                :class="{ selected: index === selectedMoreIndex }" @click="selectedMoreIndex = index">
                <view class="period-row">
                  <text class="period-name">{{ item.productName }}</text>
                  <text class="period-badge">{{ item.holdDays || '--' }}{{ $t('advisor.workDays') }}</text>
                </view>
                <view class="period-row">
                  <text class="period-label">{{ $t('advisor.delegateAmount') }}：</text>
                  <text class="period-value">{{ item.minAmount }}-{{ item.maxAmount }}</text>
                  <text class="period-badge-small">{{ $t('advisor.multiple100') }}</text>
                </view>
                <view class="period-row">
                  <text class="period-label">{{ $t('advisor.commissionRate') }}：</text>
                  <text class="period-value">{{ item.salaryRate }}%</text>
                  <!-- <text class="period-extra">{{ $t('advisor.periodCommission') }}：{{ item.minCommission || 15 }}% {{
                    $t('advisor.above') }}</text> -->
                </view>
              </view>
            </view>
          </view>

          <button class="floating-btn" @click="submitMoreFollow">{{ $t('advisor.delegateFollow') }}</button>

          <view class="table-card">
            <text class="table-title">{{ $t('advisor.periodDescription') }}</text>
            <view class="table table--three">
              <view class="table-header">
                <text>{{ $t('advisor.amountRange') }}</text>
                <text>{{ $t('advisor.product') }}</text>
                <text>{{ $t('advisor.commission') }}</text>
              </view>
              <view v-for="(item, index) in morePackages" :key="index" class="table-row">
                <text>{{ item.minAmount }} - {{ item.maxAmount }}</text>
                <text>{{ item.productName }}</text>
                <text>{{ item.salaryRate }}%</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <u-popup :show="dayDetailVisible" mode="center" @close="dayDetailVisible = false">
        <view class="popup-content">
          <text class="popup-title">{{ $t('advisor.dayDetail') }}</text>
          <scroll-view scroll-y class="popup-scroll">
            <rich-text :nodes="dayDetailContent" />
          </scroll-view>
          <text class="popup-title popup-btn" @click="dayDetailVisible = false">{{ $t('common.iKnow') }}</text>
        </view>
      </u-popup>

      <u-popup :show="moreDetailVisible" mode="center" @close="moreDetailVisible = false">
        <view class="popup-content">
          <text class="popup-title">{{ $t('advisor.periodDetail') }}</text>
          <scroll-view scroll-y class="popup-scroll">
            <rich-text :nodes="moreDetailContent" />
          </scroll-view>
          <text class="popup-title popup-btn" @click="moreDetailVisible = false">{{ $t('common.iKnow') }}</text>
        </view>
      </u-popup>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.apply-page {
  @include hasNavBar();
}

.content {
  padding: 0 32rpx 96rpx;
}

.tabs {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 0 62rpx;
  margin-bottom: 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.tab-item {
  position: relative;
  padding: 24rpx 32rpx;
  font-size: 30rpx;
  color: #666;
  word-break: break-word;
  text-align: center;

  &.active {
    color: #00b6e6;
    font-weight: 600;

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 60rpx;
      height: 6rpx;
      background: #00b6e6;
      border-radius: 3rpx;
    }
  }
}

.panel {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.card {
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 16rpx 32rpx rgba(0, 182, 230, 0.12);
}

.card-header {
  margin-bottom: 24rpx;

  .advisor {
    font-size: 30rpx;
    color: #111;
  }
}

.stats {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 24rpx;

  .stat {
    flex: 1;
    border-radius: 20rpx;
    padding: 24rpx 12rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8rpx;

    .stat-value {
      font-size: 28rpx;
      font-weight: 600;
      color: #00b6e6;
      display: block;
      text-align: center;
    }

    .stat-label {
      font-size: 22rpx;
      color: #666;
      display: block;
      text-align: center;
      word-spacing: 9999px;
      line-height: 1.3;
    }
  }
}

.stats--four {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 24rpx;
}

.note {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 20rpx;

  .link {
    color: #00b6e6;
    margin-left: 12rpx;
  }
}

.amount-input {
  width: 100%;
  height: 88rpx;
  border-radius: 16rpx;
  border: 2rpx solid rgba(0, 182, 230, 0.4);
  padding: 0 24rpx;
  font-size: 30rpx;
  margin-bottom: 24rpx;
  box-sizing: border-box;
}

.primary-btn,
.floating-btn {
  height: 88rpx;
  border-radius: 16rpx;
  background: #00b6e6;
  color: #fff;
  border: none;
  font-size: 30rpx;
  display: flex;
  justify-content: center;
  align-items: center;
}

.floating-btn {
  position: fixed;
  bottom: 32rpx;
  left: 32rpx;
  right: 32rpx;
  width: calc(100% - 64rpx);
  z-index: 100;
}

.quick-links {
  display: flex;
  gap: 30rpx;
  align-items: center;
  justify-content: center;
  padding: 0 30rpx;

  .link-item {
    flex: 1;
    background: #fff;
    border-radius: 10rpx;
    text-align: center;
    padding: 12rpx 16rpx;
    font-size: 28rpx;
    color: #00b6e6;
    border: 2rpx solid #00b6e6;
    word-break: keep-all;
    line-height: 1.4;
  }
}

.table-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 28rpx 28rpx 68rpx 28rpx;
  box-shadow: 0 14rpx 28rpx rgba(0, 182, 230, 0.12);
}

.table-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
}

.table {
  margin-top: 30rpx;
  width: 100%;
  border-collapse: collapse;
  background-color: #e5f5fb;
  border-radius: 5rpx;

  .table-header,
  .table-row {
    display: grid;
    gap: 8rpx;
    padding: 20rpx 12rpx;
    font-size: 24rpx;
    text-align: center;
    border-bottom: 1rpx solid #f0f0f0;
    align-items: center;
  }

  .table-header {
    font-weight: 600;
    color: #333;

    text {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 40rpx;
      line-height: 1.3;
      word-break: keep-all;
    }
  }

  .table-row {
    color: #666;

    &:last-child {
      border-bottom: none;
    }
  }
}

.table--four .table-header,
.table--four .table-row {
  grid-template-columns: 1.2fr 1fr 1fr 1fr;
}

.table--three .table-header,
.table--three .table-row {
  grid-template-columns: 1.5fr 1fr 0.8fr;
}

.period-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.period-item {
  border-radius: 24rpx;
  padding: 28rpx;
  background: #fff;
  border: 2rpx solid #e0e0e0;
  display: flex;
  flex-direction: column;
  gap: 16rpx;

  &.selected {
    background: linear-gradient(135deg, #00b6e6 0%, #00d4ff 100%);
    border-color: #00b6e6;
    box-shadow: 0 16rpx 32rpx rgba(0, 182, 230, 0.25);

    .period-name,
    .period-label,
    .period-value,
    .period-extra {
      color: #fff;
    }

    .period-badge,
    .period-badge-small {
      background: rgba(255, 255, 255, 0.2);
      color: #fff;
    }
  }

  .period-row {
    display: flex;
    align-items: center;
    gap: 8rpx;
    font-size: 26rpx;
    flex-wrap: nowrap;
  }

  .period-name {
    font-size: 32rpx;
    font-weight: 700;
    color: #111;
    flex: 1;
    white-space: nowrap;
  }

  .period-badge {
    padding: 6rpx 16rpx;
    background: #00b6e6;
    color: #fff;
    border-radius: 24rpx;
    font-size: 22rpx;
  }

  .period-badge-small {
    padding: 4rpx 12rpx;
    background: rgba(0, 182, 230, 0.1);
    color: #00b6e6;
    border-radius: 20rpx;
    font-size: 20rpx;
  }

  .period-label {
    color: #666;
    font-size: 26rpx;
  }

  .period-value {
    color: #ff4d4f;
    font-weight: 600;
    font-size: 26rpx;
  }

  .period-extra {
    color: #666;
    font-size: 24rpx;
    margin-left: auto;
  }
}

.popup-content {
  width: 600rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.popup-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #111;
  text-align: center;
}

.popup-scroll {
  max-height: 480rpx;
}

.popup-btn {
  font-size: 28rpx;
  color: #00b6e6;
  margin-top: 15rpx;
}
</style>
