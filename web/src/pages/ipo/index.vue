<script setup>
import { computed, ref } from 'vue';
import dayjs from 'dayjs';
import { onLoad, onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';

import { getNewAdd, getNewGu, getSubscribeHistoryList } from '@/apis/ipo';
import { useCommonStore } from '@/stores/common';
import { goBack } from '@/utils/navigate';
import { getAccountBalance } from '@/utils/account';
import PaymentModal from '@/pages/orders/components/PaymentModal.vue';

const { t } = useI18n();
const commonStore = useCommonStore();

// 主tab
const mainTabs = [
  { key: 'subscribe', labelKey: 'home.ipo' },
  /*   { key: 'subscribe', labelKey: 'ipo.title' }, */
  { key: 'history', labelKey: 'ipo.applyRecord' }
];

// 新股申购子tab
const subscribeTabs = [
  { key: 'available', status: 1, labelKey: 'ipo.available' },
  { key: 'pending', status: 2, labelKey: 'ipo.pending' }
];

// 申购历史子tab
const historyTabs = [
  { key: 'all', status: 0, labelKey: 'order.fpoAll' },
  { key: 'pending', status: 2, labelKey: 'order.ipoPendingReview' },
  { key: 'payment', status: 3, labelKey: 'order.ipoPendingPayment' },
  { key: 'transfer', status: 4, labelKey: 'order.ipoPendingTransfer' },
  { key: 'winning', status: 5, labelKey: 'order.ipoTransferred' }
];

const activeMainTab = ref('subscribe');
const activeSubTab = ref('available');
const searchKeyword = ref('');
const list = ref([]);
const allList = ref([]); // 存储所有数据用于前端搜索
const loading = ref(false);
const finished = ref(false);
const pageNum = ref(1);
const pageSize = ref(10);

const showOrderModal = ref(false);
const currentItem = ref(null);
const applyNums = ref('');
const accountInfo = ref({
  availableBalance: 0,
  currency: 'MXN',
  stockType: 'mxg'
});

// 付款弹窗
const paymentModalVisible = ref(false);
const currentPaymentItem = ref(null);

const isSubscribeTab = computed(() => activeMainTab.value === 'subscribe');
const isAvailableTab = computed(() => activeSubTab.value === 'available');
const currentSubTabs = computed(() =>
  isSubscribeTab.value ? subscribeTabs : historyTabs
);

const setBackHandler = () => {
  commonStore.fnBack = goBack;
};

const resetList = () => {
  list.value = [];
  pageNum.value = 1;
  finished.value = false;
};

const fetchAccountInfo = async (stockType) => {
  try {
    const info = await getAccountBalance(stockType);
    accountInfo.value = info;
  } catch (error) {
    console.error('获取账户信息失败', error);
  }
};

const fetchList = async (reset = false) => {
  if (loading.value) return;
  if (reset) {
    resetList();
    searchKeyword.value = '';
  }
  if (finished.value) return;

  loading.value = true;
  const currentTab = currentSubTabs.value.find(
    (item) => item.key === activeSubTab.value
  );
  const status = currentTab?.status ?? 1;

  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    status
  };

  let res;
  // 根据主tab调用不同接口
  if (isSubscribeTab.value) {
    // 新股申购列表
    res = await getNewGu(params);
  } else {
    // 申购历史记录列表
    params.type = 1; // 类型固定为1
    res = await getSubscribeHistoryList(params);
  }

  const dataList = res?.data?.list || res?.list || [];

  if (pageNum.value === 1) {
    allList.value = dataList;
    list.value = dataList;
  } else {
    allList.value = allList.value.concat(dataList);
    list.value = allList.value;
  }

  if (!dataList.length || dataList.length < pageSize.value) {
    finished.value = true;
  } else {
    pageNum.value += 1;
  }
  loading.value = false;
};

const switchMainTab = (key) => {
  if (activeMainTab.value === key) return;
  activeMainTab.value = key;
  activeSubTab.value = key === 'subscribe' ? 'available' : 'all';
  fetchList(true);
};

const switchSubTab = (key) => {
  if (activeSubTab.value === key) return;
  activeSubTab.value = key;
  fetchList(true);
};

const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    list.value = allList.value;
    return;
  }

  const keyword = searchKeyword.value.toLowerCase().trim();
  list.value = allList.value.filter((item) => {
    const name = (item.name || '').toLowerCase();
    const code = (item.code || '').toLowerCase();
    return name.includes(keyword) || code.includes(keyword);
  });
};

const openOrder = async (item) => {
  currentItem.value = item;
  // 根据股票类型获取对应账户余额
  await fetchAccountInfo(item.stockType || 'mxg');
  const minNumber = Number(item.minNumber || 0);
  applyNums.value = minNumber ? String(minNumber) : '';
  showOrderModal.value = true;
};

const closeOrder = () => {
  showOrderModal.value = false;
  currentItem.value = null;
  applyNums.value = '';
};

const setQuickAmount = (ratio) => {
  if (!currentItem.value) return;
  const price = Number(currentItem.value.price || 0);
  if (!price) return;
  const maxShares = Math.floor(accountInfo.value.availableBalance / price);
  const totalLimit = Number(currentItem.value.orderNumber || 0) * 10000;
  const usable = totalLimit > 0 ? Math.min(maxShares, totalLimit) : maxShares;
  if (!usable) return;
  let candidate = Math.floor(usable * ratio);
  const minNumber = Number(currentItem.value.minNumber || 0) || 0;
  if (candidate < minNumber) {
    candidate = minNumber;
  }
  applyNums.value = candidate > 0 ? String(candidate) : '';
};

const validateOrder = () => {
  if (!currentItem.value) return false;
  const num = Number(applyNums.value);
  if (!num) {
    uni.showToast({ title: t('ipo.enterAmount'), icon: 'none' });
    return false;
  }
  const minNumber = Number(currentItem.value.minNumber || 0) || 0;
  if (minNumber && num < minNumber) {
    uni.showToast({ title: t('ipo.minHint', { min: minNumber }), icon: 'none' });
    return false;
  }
  const price = Number(currentItem.value.price || 0);
  const total = price * num;
  if (accountInfo.value.availableBalance && total > accountInfo.value.availableBalance) {
    uni.showToast({ title: t('ipo.balanceLack'), icon: 'none' });
    return false;
  }
  return true;
};

const submitOrder = async () => {
  if (!validateOrder()) return;
  if (!currentItem.value) return;

  const payload = {
    newCode: currentItem.value.code,
    newName: currentItem.value.name,
    newType: currentItem.value.stockType || 'mxg',
    applyNums: Number(applyNums.value),
    buyPrice: currentItem.value.price,
    type: 1
  };

  const res = await getNewAdd(payload);
  if (res?.status === 0) {
    uni.showToast({ title: t('ipo.applySuccess'), icon: 'success' });
    closeOrder();
    fetchList(true);
  } else {
    uni.showToast({ title: res?.msg || t('ipo.applyFailed'), icon: 'none' });
  }
};

const formatDate = (value) => {
  if (!value) return '--';
  return dayjs(value).format('YYYY-MM-DD');
};

// 打开付款弹窗
const handlePayment = (item, event) => {
  if (event) {
    event.stopPropagation();
  }
  currentPaymentItem.value = item;
  paymentModalVisible.value = true;
};

// 付款成功回调
const handlePaymentSuccess = () => {
  fetchList(true);
};

const handleLoadMore = () => {
  fetchList();
};

const handleRefresh = async () => {
  await fetchList(true);
  uni.stopPullDownRefresh();
};

onLoad((options) => {
  if (options?.tab === 'history') {
    activeMainTab.value = 'history';
    activeSubTab.value = 'all';
  }
  fetchList(true);
});

onShow(() => {
  setBackHandler();
});

onPullDownRefresh(() => {
  handleRefresh();
});

onReachBottom(() => {
  handleLoadMore();
});

const formatMainTabText = (text) => {
  if (!text) return '';
  const words = text.trim().split(/\s+/);
  let res = '';
  for (let i = 0; i < words.length; i++) {
    res += words[i];
    if ((i + 1) % 2 === 0 && i !== words.length - 1) {
      res += '\n';
    } else if (i !== words.length - 1) {
      res += ' ';
    }
  }
  return res;
};

const formatSubTabText = (text) => {
  if (!text) return '';
  const words = text.trim().split(/\s+/);
  if (words.length <= 1) return text;
  return words[0] + '\n' + words.slice(1).join(' ');
};
</script>

<template>
  <page-wrapper>
    <view class="ipo-page">
      <nav-bar :title="$t('ipo.recordTitle')" />

      <view class="page-content">
        <!-- 主Tab -->
        <view class="main-tab-container">
          <view v-for="tab in mainTabs" :key="tab.key" class="main-tab-item"
            :class="{ active: activeMainTab === tab.key }" @click="switchMainTab(tab.key)">
            <text class="main-tab-text">{{
              formatMainTabText(tab.labelKey ? $t(tab.labelKey) : tab.label)
            }}</text>
            <view v-if="activeMainTab === tab.key" class="main-tab-line" />
          </view>
        </view>

        <!-- 搜索框 -->
        <c-search class="u-search-wrap" v-model="searchKeyword" @input="handleSearch" />

        <!-- 子Tab -->
        <view class="sub-tab-container">
          <view v-for="tab in currentSubTabs" :key="tab.key" class="sub-tab-item"
            :class="{ active: activeSubTab === tab.key }" @click="switchSubTab(tab.key)">
            <text class="sub-tab-text">{{
              formatSubTabText(tab.labelKey ? $t(tab.labelKey) : tab.label)
            }}</text>
            <view v-if="activeSubTab === tab.key" class="sub-tab-line" />
          </view>
        </view>

        <scroll-view class="list-wrapper" scroll-y :lower-threshold="120" @scrolltolower="handleLoadMore">
          <view v-if="list.length" class="card-list">
            <!-- 新股申购列表 -->
            <view v-if="isSubscribeTab" class="stock-card" v-for="item in list" :key="item.id || item.code">
              <view class="card-header">
                <view class="title">
                  <text class="name">{{ item.name }}</text>
                  <!-- <text class="code">{{ item.code }}</text> -->
                </view>
                <view v-if="isAvailableTab" class="header-action">
                  <button class="primary-btn" @click="openOrder(item)">{{ $t('ipo.subscribeNow') }}</button>
                </view>
              </view>

              <view class="info-row">
                <text class="label">{{ $t('ipo.priceLabel') }}</text>
                <text class="value price">{{ item.price || 0 }}/{{ $t('ipo.shareUnit') }}</text>
              </view>
              <view class="info-row">
                <text class="label">{{ $t('ipo.availableShares') }}</text>
                <text class="value">{{ item.orderNumber || 0 }}{{ $t('ipo.tenThousandShare') }}</text>
              </view>
              <view v-if="isAvailableTab" class="info-row">
                <text class="label">{{ $t('order.ipoApplyTime') }}</text>
                <text class="value">{{
                  formatDate(item.subscribeStartTime || item.subscribeTime)
                }}</text>
              </view>
            </view>

            <!-- 申购历史列表 -->
            <view v-else class="history-card" v-for="item in list" :key="item.id">
              <view class="history-header">
                <view class="title">
                  <text class="name">{{ item.newName }}</text>
                  <!-- <text class="code">{{ item.newCode }}</text> -->
                </view>
                <view v-if="item.status === 2" class="status-badge pending">{{
                  $t('order.ipoPendingReview')
                }}</view>
                <view v-else-if="item.status === 3" class="status-badge payment">{{
                  $t('order.ipoPendingPayment')
                }}</view>
                <view v-else-if="item.status === 4" class="status-badge transfer">{{
                  $t('order.ipoPendingTransfer')
                }}</view>
                <view v-else-if="item.status === 5" class="status-badge winning">{{
                  $t('order.ipoTransferred')
                }}</view>
              </view>

              <view class="info-row">
                <text class="label">{{ $t('order.orderNo') }}</text>
                <text class="value">{{ item.orderNo || '--' }}</text>
              </view>
              <view class="info-row">
                <text class="label">{{ $t('order.ipoApplyTime') }}</text>
                <text class="value">{{ item.submitTime || '--' }}</text>
              </view>
              <view class="info-row">
                <text class="label">{{ $t('order.fpoIssuePrice') }}</text>
                <text class="value price">{{ item.buyPrice || 0 }}/{{ $t('ipo.shareUnit') }}</text>
              </view>
              <view class="info-row">
                <text class="label">{{ $t('order.fpoApplyNum') }}</text>
                <text class="value">{{ item.applyNums || 0 }} {{ $t('ipo.shareUnit') }}</text>
              </view>
              <view v-if="item.applyNumber > 0" class="info-row">
                <text class="label">{{ $t('order.fpoWinningNum') }}</text>
                <text class="value winning-num">{{ item.applyNumber || 0 }} {{ $t('ipo.shareUnit') }}</text>
              </view>
              <view v-if="item.bond > 0" class="info-row">
                <text class="label">{{ $t('order.ipoBond') }}</text>
                <text class="value">{{ item.bond || 0 }}</text>
              </view>
              <view v-if="item.subscriptionTime" class="info-row">
                <text class="label">{{ $t('order.fpoSubscribeDate') }}</text>
                <text class="value">{{ item.subscriptionTime }}</text>
              </view>

              <!-- 待付款状态显示付款按钮 -->
              <view v-if="item.status === 3" class="payment-action">
                <button class="payment-btn" @click="handlePayment(item, $event)">
                  {{ $t('order.payment') }}
                </button>
              </view>
            </view>

            <view class="footer-tip" v-if="loading">{{ $t('ipo.loading') }}</view>
            <view class="footer-tip" v-else-if="finished">{{ $t('common.noMore') }}</view>
          </view>

          <view v-else class="empty">{{ $t('ipo.noData') }}</view>
        </scroll-view>
      </view>

      <u-popup :show="showOrderModal" mode="bottom" :round="24" :z-index="20" @close="closeOrder">
        <view class="order-modal">
          <view class="modal-header">
            <text class="modal-title">{{ t('ipo.title') }}</text>
            <text class="modal-close" @click="closeOrder">×</text>
          </view>

          <view class="modal-body">
            <view class="row">
              <text class="row-label">{{ t('ipo.stockLabel') }}</text>
              <text class="row-value">{{ currentItem?.name }}</text>
            </view>
            <view class="row">
              <text class="row-label">{{ t('ipo.priceLabel') }}</text>
              <text class="row-value">{{ currentItem?.price || 0 }} {{ t('ipo.currencyUnit') }}/{{
                t('ipo.shareUnit')
              }}</text>
            </view>
            <view class="row">
              <text class="row-label">{{ t('ipo.balance') }}</text>
              <text class="row-value">{{ accountInfo.availableBalance.toFixed(2) }}
                {{ accountInfo.currency }}</text>
            </view>
            <view class="input-row">
              <text class="row-label">{{ t('ipo.quantity') }}</text>
              <input v-model="applyNums" class="number-input" type="number" :placeholder="t('ipo.inputPlaceholder')" />
              <text class="unit">{{ t('ipo.shareUnit') }}</text>
            </view>
            <view class="quick-actions">
              <view class="quick-btn" @click="setQuickAmount(25)">25%</view>
              <view class="quick-btn" @click="setQuickAmount(50)">50%</view>
              <view class="quick-btn" @click="setQuickAmount(75)">75%</view>
              <view class="quick-btn" @click="setQuickAmount(100)">{{
                t('ipo.full')
              }}</view>
            </view>
          </view>

          <view class="modal-footer">
            <button class="modal-btn" @click="submitOrder">
              {{ t('ipo.confirmSubmit') }}
            </button>
          </view>
        </view>
      </u-popup>

      <!-- 付款弹窗 -->
      <payment-modal v-if="paymentModalVisible" v-model:visible="paymentModalVisible" :ipo-item="currentPaymentItem"
        @success="handlePaymentSuccess" />
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.ipo-page {
  @include hasNavBar();
}

// 主Tab样式
.main-tab-container {
  display: flex;
  background: #fff;
  padding: 0 32rpx;
}

.main-tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx 0 16rpx;
  position: relative;
}

.main-tab-text {
  font-size: 32rpx;
  color: #999;
  font-weight: 400;
  white-space: pre-wrap;
  text-align: center;
  line-height: 1.2;

  .main-tab-item.active & {
    color: #00b6e6;
    font-weight: 600;
  }
}

.main-tab-line {
  position: absolute;
  bottom: 0;
  width: 48rpx;
  height: 6rpx;
  background: #00b6e6;
  border-radius: 3rpx;
}

// 子Tab样式（下划线，左对齐）
.sub-tab-container {
  display: flex;
  gap: 48rpx;
  background: #fff;
  padding: 0 32rpx;
  border-bottom: 2rpx solid #f0f0f0;
  overflow-x: auto;
  white-space: nowrap;

  &::-webkit-scrollbar {
    display: none;
  }
}

.sub-tab-item {
  display: flex;
  flex-direction: column;
  padding: 20rpx 0 16rpx;
  position: relative;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.sub-tab-text {
  font-size: 28rpx;
  color: #666;
  text-align: center;
  line-height: 1.2;
  white-space: pre-wrap;

  .sub-tab-item.active & {
    color: #00b6e6;
    font-weight: 600;
  }
}

.sub-tab-line {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4rpx;
  background: #00b6e6;
  border-radius: 2rpx;
}

.card-list {
  display: flex;
  flex-direction: column;
  padding: 24rpx 32rpx 32rpx;
  gap: 24rpx;
}

.stock-card,
.history-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 16rpx 32rpx rgba(0, 182, 230, 0.12);
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16rpx;
}

.status-badge {
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  font-weight: 600;
  flex-shrink: 0;
  white-space: nowrap;

  &.pending {
    background: rgba(255, 152, 0, 0.1);
    color: #ff9800;
  }

  &.payment {
    background: rgba(244, 67, 54, 0.1);
    color: #f44336;
  }

  &.transfer {
    background: rgba(33, 150, 243, 0.1);
    color: #2196f3;
  }

  &.winning {
    background: rgba(0, 182, 230, 0.1);
    color: #00b6e6;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16rpx;
}

.title {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  flex: 1;
}

.name {
  font-size: 32rpx;
  font-weight: 600;
  color: #111;
}

.code {
  font-size: 26rpx;
  color: #666;
}

.header-action {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.primary-btn {
  height: 60rpx;
  border-radius: 30rpx;
  background: #00b6e6;
  color: #fff;
  font-size: 28rpx;
  border: none;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 32rpx;
  white-space: nowrap;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  font-size: 26rpx;
  gap: 16rpx;

  .label {
    color: #666;
    flex: 1;
    word-break: break-word;
  }

  .value {
    color: #111;
    font-weight: 500;
    text-align: right;
    flex-shrink: 0;
    max-width: 60%;
    word-break: break-word;

    &.price {
      color: #ff3b30;
      font-weight: 600;
    }

    &.winning-num {
      color: #00b6e6;
      font-weight: 600;
    }
  }

  .column {
    display: flex;
    flex-direction: column;
    gap: 6rpx;
    align-items: flex-end;
  }

  .sub {
    font-size: 24rpx;
    color: #999;
  }
}

.footer-tip {
  text-align: center;
  font-size: 26rpx;
  color: #999;
  margin-top: 16rpx;
  padding: 16rpx 0;
}

.empty {
  margin-top: 160rpx;
  text-align: center;
  font-size: 28rpx;
  color: #999;
}

.order-modal {
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.modal-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #111;
}

.modal-close {
  font-size: 40rpx;
  color: #999;
}

.modal-body {
  padding: 32rpx;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  font-size: 26rpx;
  gap: 16rpx;

  .row-label {
    color: #666;
    flex-shrink: 0;
    max-width: 50%;
    word-break: break-word;
  }

  .row-value {
    color: #111;
    font-weight: 600;
    text-align: right;
    flex: 1;
    word-break: break-word;
  }
}

.input-row {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 16rpx;
  padding: 0 24rpx;
  height: 88rpx;
  font-size: 26rpx;
}

.number-input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 30rpx;
  color: #111;
  margin-left: 15rpx;
}

.unit {
  color: #666;
  margin-left: 8rpx;
  flex-shrink: 0;
}

.quick-actions {
  display: flex;
  gap: 16rpx;
}

.quick-btn {
  flex: 1;
  height: 64rpx;
  border-radius: 32rpx;
  background: rgba(0, 182, 230, 0.12);
  color: #00b6e6;
  font-size: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8rpx;
  white-space: normal;
  text-align: center;
  line-height: 1.3;
  word-break: break-word;
}

.modal-footer {
  padding: 0 32rpx 32rpx;
}

.modal-btn {
  width: 100%;
  height: 88rpx;
  border-radius: 44rpx;
  background: #00b6e6;
  color: #fff;
  font-size: 30rpx;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 15rpx;
}

.modal-btn:disabled {
  opacity: 0.6;
}

.u-search-wrap {
  padding: 16rpx 32rpx 24rpx;
}

// 付款按钮区域
.payment-action {
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 2rpx solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}

.payment-btn {
  min-width: 200rpx;
  padding: 0 32rpx;
  height: 64rpx;
  border-radius: 32rpx;
  background: #00b6e6;
  color: #fff;
  font-size: 28rpx;
  border: none;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
