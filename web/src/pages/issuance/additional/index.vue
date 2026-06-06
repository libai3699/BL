<script setup>
import { computed, ref } from 'vue';
import { onLoad, onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';

import {
  addStockSubscribe,
  addStockSubscribe2,
  getStockAddList
} from '@/apis/issuance';
import { getTradeFee } from '@/apis/order';
import { useCommonStore } from '@/stores/common';
import { goBack, navigateTo } from '@/utils/navigate';
import { getAccountBalance } from '@/utils/account';

const commonStore = useCommonStore();
const { t } = useI18n();

const type = ref(2); // 1: 预售 2: 认购
const activeTab = ref('subscribing');
const list = ref([]);
const loading = ref(false);
const pageNum = ref(1);
const pageSize = ref(10);
const finished = ref(false);

const showOrderModal = ref(false);
const currentItem = ref(null);
const applyNums = ref('');
const accountInfo = ref({
  availableBalance: 0,
  currency: 'MXN',
  stockType: 'mxg'
});
const feeInfo = ref({
  buyFee: 0,
  sellFee: 0
});

const isSubscribe = computed(() => Number(type.value) === 2);
const headerTitle = computed(() => (isSubscribe.value ? t('issuance.additional') : t('issuance.presale')));
const recordTitle = computed(() => t('common.detail'));
const tabLeftText = computed(() => (isSubscribe.value ? t('issuance.subscribing') : t('issuance.preselling')));
const timeLabel = computed(() => (isSubscribe.value ? t('issuance.subscribeTime') : t('issuance.presaleTime')));
const nameLabel = computed(() => (isSubscribe.value ? t('issuance.subscribeStock') : t('issuance.presaleStock')));
const priceLabel = computed(() => (isSubscribe.value ? t('issuance.subscribePrice') : t('issuance.presalePrice')));
const minLabel = computed(() => (isSubscribe.value ? t('issuance.minSubscribeShares') : t('issuance.minPresaleShares')));
const actionText = computed(() => (isSubscribe.value ? t('issuance.subscribeNow') : t('issuance.presaleNow')));

const handleSetBack = () => {
  commonStore.fnBack = goBack;
};

const resetList = () => {
  list.value = [];
  pageNum.value = 1;
  finished.value = false;
};

const fetchBalance = async (stockType) => {
  try {
    const info = await getAccountBalance(stockType);
    accountInfo.value = info;
  } catch (error) {
    console.error('获取账户余额失败', error);
  }
};

const fetchList = async (reset = false) => {
  if (loading.value) return;
  if (reset) {
    resetList();
  }
  if (finished.value) return;

  loading.value = true;
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    zt: 1,
    isLock: 1,
    status: activeTab.value === 'subscribing' ? 1 : 2,
    type: Number(type.value)
  };

  const res = await getStockAddList(params);
  const dataList = res?.data?.list || res?.list || [];

  list.value = pageNum.value === 1 ? dataList : list.value.concat(dataList);
  if (!dataList.length || dataList.length < pageSize.value) {
    finished.value = true;
  } else {
    pageNum.value += 1;
  }
  loading.value = false;
};

const switchTab = (tab) => {
  if (activeTab.value === tab) return;
  activeTab.value = tab;
  fetchList(true);
};

const openOrder = async (item) => {
  currentItem.value = item;
  if (isSubscribe.value) {
    applyNums.value = '';
    // 根据股票类型获取对应账户余额
    await fetchBalance(item.stockType || 'mxg');

    // 获取手续费
    try {
      const gid = (item.stockType || 'mxg') + (item.code || item.stockCode);
      const feeRes = await getTradeFee({ gid });
      if (feeRes && feeRes.buyFee !== undefined) {
        feeInfo.value = {
          buyFee: feeRes.buyFee || 0,
          sellFee: feeRes.sellFee || 0
        };
      }
    } catch (error) {
      console.error('获取手续费失败', error);
      // 手续费获取失败不影响下单流程
    }

    showOrderModal.value = true;
  } else {
    submitOrder();
  }
};

const validateOrder = () => {
  if (!currentItem.value) {
    return false;
  }
  if (!isSubscribe.value) {
    return true;
  }

  const num = Number(applyNums.value);
  if (!num) {
    uni.showToast({ title: t('issuance.enterQuantity'), icon: 'none' });
    return false;
  }
  const minNumber = Number(currentItem.value?.minNumber || 0);
  if (num < minNumber) {
    uni.showToast({ title: t('issuance.quantityNotLessThan', { min: minNumber }), icon: 'none' });
    return false;
  }
  const total = Number(currentItem.value?.price || 0) * num;
  if (accountInfo.value.availableBalance < total) {
    uni.showToast({ title: t('issuance.insufficientBalance'), icon: 'none' });
    return false;
  }
  return true;
};

const submitOrder = async () => {
  if (!validateOrder()) return;
  const item = currentItem.value;
  if (!item) return;

  const payload = {
    newlistId: item.newlistId,
    newCode: item.code,
    newName: item.name,
    newType: item.stockType || 0,
    buyPrice: item.price,
    type: Number(type.value)
  };
  if (isSubscribe.value) {
    payload.applyNums = Number(applyNums.value);
  }

  const request = isSubscribe.value ? addStockSubscribe2 : addStockSubscribe;
  const res = await request(payload);
  if (res?.status === 0) {
    uni.showToast({ title: t('issuance.submitSuccess'), icon: 'success' });
    showOrderModal.value = false;
    fetchList(true);
  } else {
    uni.showToast({ title: res?.msg || t('issuance.submitFailed'), icon: 'none' });
  }
};

const goToRecords = () => {
  navigateTo(`/pages/issuance/records/index?type=${type.value}`);
};

const setPosition = (ratio) => {
  if (!currentItem.value) return;
  const price = Number(currentItem.value.price || 0);
  if (!price) return;

  // 考虑手续费的最大可买数量
  const feeRate = feeInfo.value.buyFee || 0;
  const priceWithFee = price * (1 + feeRate);
  const maxShares = Math.floor(accountInfo.value.availableBalance / priceWithFee);

  if (!maxShares) return;
  const minNumber = Number(currentItem.value.minNumber || 0) || 0;
  let candidate = Math.floor(maxShares * (ratio / 100));
  if (candidate < minNumber) {
    candidate = minNumber;
  }
  applyNums.value = String(candidate || '');
};

const getProgress = (item) => {
  const total = Number(item.orderNumber || 0) * 10000;
  const applied = Number(item.sumApplyNum || 0);
  if (!total || !applied) return 0;
  return Math.min(100, Number(((applied / total) * 100).toFixed(2)));
};

const handleLoadMore = () => {
  fetchList();
};

const handleRefresh = async () => {
  await fetchList(true);
  uni.stopPullDownRefresh();
};

onLoad((options) => {
  type.value = Number(options?.type || 2);
  fetchList(true);
});

onShow(() => {
  handleSetBack();
});

onPullDownRefresh(() => {
  handleRefresh();
});

onReachBottom(() => {
  handleLoadMore();
});
</script>

<template>
  <page-wrapper>
    <view class="issuance-page">
      <nav-bar :title="headerTitle">
        <template #right>
          <view @click="goToRecords">
            <text class="record-link">{{ recordTitle }}</text>
          </view>
        </template>
      </nav-bar>
      <view class="page-content">
        <view class="tab-container">
          <view class="tab-buttons">
            <view class="tab-button" :class="{ active: activeTab === 'subscribing' }" @click="switchTab('subscribing')">
              {{ tabLeftText }}
            </view>
            <view class="tab-button" :class="{ active: activeTab === 'waiting' }" @click="switchTab('waiting')">
              {{ $t('issuance.waitingOpen') }}
            </view>
          </view>
        </view>

        <scroll-view class="list-wrapper" scroll-y @scrolltolower="handleLoadMore">
          <view v-if="list.length" class="card-list">
            <view class="stock-card" v-for="item in list" :key="item.id">
              <view class="info-row">
                <text class="info-label">{{ nameLabel }}：</text>
                <text class="info-value primary">{{ item.name }}</text>
              </view>

              <view class="info-row">
                <text class="info-label">{{ priceLabel }}：</text>
                <text class="info-value">{{ item.price }} {{ item.stockType == 'mxg' ? 'MXN' : 'USD' }}</text>
              </view>

              <view class="info-row">
                <text class="info-label">{{ timeLabel }}：</text>
                <view class="info-value-col">
                  <text class="info-value">{{ item.subscribeStartTime || '--' }}</text>
                  <text class="info-value">{{ item.subscribeEndTime || '--' }}</text>
                </view>
              </view>

              <view class="info-row">
                <text class="info-label">{{ $t('issuance.openTradingDate') }}：</text>
                <text class="info-value">{{ item.listDate || '--' }}</text>
              </view>

              <view class="info-row">
                <text class="info-label">{{ $t('issuance.availableShares') }}：</text>
                <text class="info-value">{{ item.orderNumber || '0' }}{{ $t('issuance.tenThousandShares') }}</text>
              </view>

              <view class="info-row">
                <text class="info-label">{{ minLabel }}：</text>
                <text class="info-value">{{ item.minNumber || '0' }}{{ $t('issuance.shares') }}</text>
              </view>

              <view class="progress-section">
                <view class="progress-bar">
                  <view class="progress-fill" :style="{ width: getProgress(item) + '%' }">
                    <text class="progress-text">{{ getProgress(item) }}%</text>
                  </view>
                </view>
                <text class="progress-total">{{ item.orderNumber || '0' }}{{ $t('issuance.tenThousandShares') }}</text>
              </view>

              <view v-if="activeTab === 'subscribing'" class="action-area">
                <button class="primary-btn" @click="openOrder(item)">{{ actionText }}</button>
              </view>
            </view>

            <view v-if="loading" class="footer-tip">{{ $t('issuance.loading') }}</view>
            <view v-else-if="finished" class="footer-tip">{{ $t('issuance.noMore') }}</view>
          </view>
          <view v-else class="empty">
            <text>{{ $t('issuance.noData') }}</text>
          </view>
        </scroll-view>
      </view>

      <u-popup :show="showOrderModal" v-if="showOrderModal" mode="center" :round="24" :z-index="20"
        @close="showOrderModal = false">
        <view class="order-modal">
          <view class="modal-header">
            <text class="modal-title">{{ headerTitle }}</text>
          </view>
          <view class="modal-body">
            <view class="modal-row">
              <text class="modal-label">{{ $t('issuance.stock') }}：</text>
              <text class="modal-value">{{ currentItem?.name }}</text>
            </view>
            <view class="modal-row">
              <text class="modal-label">{{ $t('issuance.price') }}：</text>
              <text class="modal-value">{{ currentItem?.price || 0 }} {{ accountInfo.currency }}/{{
                $t('issuance.perShare')
              }}</text>
            </view>
            <view class="modal-row">
              <text class="modal-label">{{ $t('ipo.balance') }}：</text>
              <text class="modal-value">{{ accountInfo.availableBalance.toFixed(5) }} {{ accountInfo.currency }}</text>
            </view>
            <view class="modal-input-row">
              <text class="modal-label">{{ $t('issuance.subscribeQuantity') }}：</text>
              <input v-model="applyNums" class="modal-input" type="number"
                :placeholder="$t('issuance.enterQuantity')" />
            </view>
          </view>
          <view class="modal-footer">
            <button class="cancel-btn" @click="showOrderModal = false">{{ $t('issuance.cancel') }}</button>
            <button class="confirm-btn" @click="submitOrder">
              {{ $t('issuance.confirmSubmit') }}
            </button>
          </view>
        </view>
      </u-popup>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.issuance-page {
  @include hasNavBar();
}

.record-link {
  font-size: 28rpx;
  color: #00b6e6;
}

.page-content {
  padding: 20rpx 20rpx;
}

.tab-buttons {
  display: flex;
  width: 100%;
}

.tab-button {
  flex: 1;
  position: relative;
  padding: 24rpx 0;
  font-size: 28rpx;
  color: #666;
  text-align: center;
  transition: all 0.3s;

  &.active {
    color: #00b6e6;
    font-weight: 600;

    &::after {
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
  }
}

.list-wrapper {
  flex: 1;
  padding: 0;
}

.card-list {
  display: flex;
  flex-direction: column;
  gap: 0;
  margin-top: 20rpx;
}

.stock-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 20rpx;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 28rpx;
  line-height: 1.6;
  padding: 12rpx 0;
  border-bottom: 1rpx solid #f5f5f5;

  &:last-of-type {
    border-bottom: none;
  }
}

.info-label {
  color: #999;
  flex-shrink: 0;
  min-width: 200rpx;
}

.info-value {
  color: #333;
  text-align: right;
  flex: 1;

  &.primary {
    color: #666;
    font-weight: 500;
  }
}

.info-value-col {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8rpx;
  flex: 1;
}

.progress-section {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 12rpx 0;
}

.progress-bar {
  flex: 1;
  height: 24rpx;
  background: #f0f0f0;
  border-radius: 12rpx;
  overflow: visible;
  position: relative;
}

.progress-fill {
  height: 100%;
  background: #00b6e6;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 80rpx;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 20rpx;
  color: #fff;
  font-weight: 600;
}

.progress-total {
  font-size: 28rpx;
  color: #333;
  flex-shrink: 0;
}

.action-area {
  margin-top: 16rpx;
}

.primary-btn {
  width: 100%;
  height: 78rpx;
  border-radius: 16rpx;
  background: #00b6e6;
  color: #fff;
  font-size: 32rpx;
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;
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

.order-modal {
  background: #fff;
  border-radius: 24rpx;
  width: 650rpx;
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx 32rpx 32rpx;
}

.modal-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}

.modal-body {
  padding: 0 40rpx 32rpx;
  display: flex;
  flex-direction: column;
  gap: 28rpx;
}

.modal-row {
  display: flex;
  align-items: flex-start;
  font-size: 28rpx;
  line-height: 1.5;
}

.modal-label {
  color: #333;
  flex-shrink: 0;
  min-width: 140rpx;
  padding-top: 2rpx;
}

.modal-value {
  color: #666;
  flex: 1;
  word-break: break-word;
}

.modal-input-row {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 0 18rpx;
  height: 78rpx;
}

.modal-input {
  flex: 1;
  height: 100%;
  border: none;
  background: transparent;
  font-size: 28rpx;
  color: #333;
  text-align: right;
}

.modal-footer {
  display: flex;
  gap: 24rpx;
  padding: 0 40rpx 48rpx;
}

.cancel-btn {
  flex: 1;
  height: 72rpx;
  border-radius: 14rpx;
  background: #f5f5f5;
  color: #666;
  font-size: 26rpx;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.confirm-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 72rpx;
  border-radius: 14rpx;
  background: #00b6e6;
  color: #fff;
  font-size: 26rpx;
  border: none;
  padding: 0 8rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.confirm-btn:disabled {
  opacity: 0.6;
}
</style>
