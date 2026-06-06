<script setup>
import { computed, ref } from 'vue';
import { onLoad, onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';

import {
  getSubscribeWinRecords,
  getUserSubscribeAddRecords,
  submitSubscribeAdd
} from '@/apis/issuance';
import { useCommonStore } from '@/stores/common';
import { goBack } from '@/utils/navigate';

const commonStore = useCommonStore();
const { t } = useI18n();

const activeTab = ref('subscribe');
const records = ref([]);
const loading = ref(false);
const pageNum = ref(1);
const pageSize = ref(10);
const finished = ref(false);
const type = ref(2);

const pageTitle = computed(() => (Number(type.value) === 1 ? t('issuance.presaleRecord') : t('issuance.subscribeRecord')));

const tabItems = computed(() => [
  { key: 'subscribe', label: t('issuance.subscribeRecord') },
  { key: 'winning', label: t('issuance.winningRecord') }
]);

const emptyText = computed(() => (activeTab.value === 'subscribe' ? t('issuance.noSubscribeRecord') : t('issuance.noWinningRecord')));

const handleSetBack = () => {
  commonStore.fnBack = goBack;
};

const resetData = () => {
  records.value = [];
  pageNum.value = 1;
  finished.value = false;
};

const getStatusClass = (status) => {
  const statusMap = {
    1: 'pending',
    2: 'fail',
    3: 'success',
    4: 'success',
    5: 'success',
    6: 'fail'
  };
  return statusMap[status] || 'pending';
};

const getStatusText = (status) => {
  const statusMap = {
    1: t('issuance.waitingWinning'),
    2: t('issuance.notWinning'),
    3: t('issuance.winning'),
    4: t('issuance.paid'),
    5: t('issuance.transferred'),
    6: t('issuance.cancelled')
  };
  return statusMap[status] || t('issuance.status');
};

const fetchRecords = async () => {
  if (loading.value || finished.value) return;
  loading.value = true;
  const api = activeTab.value === 'subscribe' ? getUserSubscribeAddRecords : getSubscribeWinRecords;
  const res = await api({
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    type: Number(type.value)
  });
  const list = res?.data?.list || res?.list || [];
  records.value = pageNum.value === 1 ? list : records.value.concat(list);
  if (!list.length || list.length < pageSize.value) {
    finished.value = true;
  } else {
    pageNum.value += 1;
  }
  loading.value = false;
};

const switchTab = (key) => {
  if (activeTab.value === key) return;
  activeTab.value = key;
  resetData();
  fetchRecords();
};

const handleReachBottom = () => {
  fetchRecords();
};

const handleRefresh = async () => {
  resetData();
  await fetchRecords();
  uni.stopPullDownRefresh();
};

const handlePay = async (item) => {
  const resModal = await uni.showModal({
    title: t('issuance.pay'),
    content: t('issuance.payConfirm'),
    cancelText: t('common.cancel'),
    confirmText: t('common.confirm'),
    confirmColor: '#00B6E6'
  });

  if (!resModal?.confirm) return;

  const res = await submitSubscribeAdd({ id: item.id });
  if (res?.status === 0) {
    uni.showToast({ title: res?.msg || t('issuance.paySuccess'), icon: 'success' });
    resetData();
    fetchRecords();
  } else {
    uni.showToast({ title: res?.msg || t('issuance.payFailed'), icon: 'none' });
  }
};

onLoad((options) => {
  type.value = Number(options?.type || 2);
  fetchRecords();
});

onShow(() => {
  handleSetBack();
});

onPullDownRefresh(() => {
  handleRefresh();
});

onReachBottom(() => {
  handleReachBottom();
});
</script>

<template>
  <page-wrapper>
    <view class="records-page">
      <nav-bar :title="pageTitle" />

      <view class="tab-container">
        <view class="tab-buttons">
          <view v-for="item in tabItems" :key="item.key" class="tab-item" :class="{ active: activeTab === item.key }"
            @click="switchTab(item.key)">
            <text class="tab-text">{{ item.label }}</text>
          </view>
        </view>
      </view>

      <scroll-view class="list-container" scroll-y @scrolltolower="handleReachBottom">
        <view v-if="records.length" class="record-list">
          <view v-for="item in records" :key="item.id" class="record-card">
            <view class="card-header">
              <view class="status-badge" :class="getStatusClass(item.status)">{{ getStatusText(item.status) }}</view>
              <text class="record-time">{{ item.addTime || '--' }}</text>
            </view>

            <view class="stock-title">{{ item.newName }}</view>

            <view class="info-list">
              <view class="info-item">
                <text class="info-label">{{ $t('issuance.subscribePrice') }}：</text>
                <text class="info-value">{{ item.buyPrice || '0.00' }}{{ $t('issuance.perShare') }}</text>
              </view>
              <view class="info-item">
                <text class="info-label">{{ $t('issuance.subscribeQuantity') }}：</text>
                <text class="info-value">{{ item.applyNums || '0' }}{{ $t('issuance.shares') }}</text>
              </view>
              <view class="info-item" v-if="item.status !== 1">
                <text class="info-label">{{ $t('issuance.winningShares') }}：</text>
                <text class="info-value">{{ item.applyNumber || '0' }}{{ $t('issuance.shares') }}</text>
              </view>
              <view class="info-item">
                <text class="info-label">{{ $t('issuance.subscribeAmount') }}：</text>
                <text class="info-value">{{ ((item.buyPrice || 0) * (item.applyNums || 0)).toFixed(5) }}{{
                  $t('issuance.yuan') }}</text>
              </view>
              <view class="info-item" v-if="item.status !== 1">
                <text class="info-label">{{ $t('issuance.winningTime') }}：</text>
                <text class="info-value">{{ item.endTime || '--' }}</text>
              </view>
            </view>
          </view>

          <view class="footer-tip" v-if="loading">{{ $t('issuance.loading') }}</view>
          <view class="footer-tip" v-else-if="finished">{{ $t('issuance.noMore') }}</view>
        </view>

        <view v-else class="empty">{{ emptyText }}</view>
      </scroll-view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.records-page {
  @include hasNavBar();
}

.tab-container {
  background: #fff;
  padding: 0 32rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.tab-buttons {
  display: flex;
  width: 100%;
}

.tab-item {
  flex: 1;
  position: relative;
  padding: 24rpx 0;
  text-align: center;
}

.tab-text {
  font-size: 28rpx;
  color: #666;
  transition: all 0.3s;
}

.tab-item.active .tab-text {
  color: #00b6e6;
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 48rpx;
  height: 6rpx;
  background: #00b6e6;
  border-radius: 3rpx;
}

.record-list {
  display: flex;
  flex-direction: column;
  padding: 32rpx;
  gap: 24rpx;
}

.record-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-badge {
  padding: 8rpx 24rpx;
  border-radius: 8rpx;
  font-size: 26rpx;
  color: #fff;
  font-weight: 500;

  &.pending {
    background: #52c41a;
  }

  &.success {
    background: #52c41a;
  }

  &.fail {
    background: #ff4d4f;
  }
}

.record-time {
  font-size: 26rpx;
  color: #999;
}

.stock-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  padding: 8rpx 0;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 28rpx;
  line-height: 1.6;
  padding: 12rpx 0;
  border-bottom: 1rpx solid #f5f5f5;

  &:last-child {
    border-bottom: none;
  }
}

.info-label {
  color: #999;
  flex-shrink: 0;
}

.info-value {
  color: #333;
  text-align: right;
  margin-left: 20rpx;
}

.footer-tip {
  text-align: center;
  font-size: 26rpx;
  color: #999;
  margin-top: 16rpx;
}

.empty {
  margin-top: 120rpx;
  text-align: center;
  color: #999;
  font-size: 28rpx;
}
</style>
