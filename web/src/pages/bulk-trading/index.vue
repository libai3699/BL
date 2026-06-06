<script setup>
import { computed, reactive, ref } from 'vue';
import { onLoad, onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app';
import { getDzList, buyStockDzList, buyStockDz } from '@/apis/bulk';
import { getTradeFee, getStockDetail } from '@/apis/order';
import { useCommonStore } from '@/stores/common';
import { goBack } from '@/utils/navigate';
import { getAccountBalance, getCurrency } from '@/utils/account';
import DateRangePicker from '@/components/date-range-picker/index.vue';

const commonStore = useCommonStore();

const tabs = [
  { title: 'bulk.market', value: 'market' },
  { title: 'bulk.history', value: 'history' }
];

const activeTab = ref('market');
const showDatePicker = ref(false);

// 日期范围
const dateRange = reactive({
  startTime: '',
  endTime: ''
});

// 市场列表状态
const marketState = reactive({
  list: [],
  pageNum: 1,
  pageSize: 10,
  finished: false,
  loading: false
});

// 历史列表状态
const historyState = reactive({
  list: [],
  pageNum: 1,
  pageSize: 10,
  finished: false,
  loading: false
});

// 下单弹窗
const showOrderModal = ref(false);
const currentOrderItem = ref(null);
const orderNum = ref('');
const password = ref('');
const accountInfo = ref({
  availableBalance: 0,
  currency: 'MXN',
  stockType: 'mxg'
});

// 手续费信息
const feeInfo = ref({
  buyFee: 0,
  sellFee: 0
});
const preciseUnitPrice = ref(0);

const getOrderUnitPrice = (item = currentOrderItem.value) => {
  return Number(preciseUnitPrice.value || item?.price || 0);
};

const calcDiscountedPrice = (nowPrice, discount) => {
  const price = Number(nowPrice || 0);
  const discountRate = Number(discount || 1);
  return Number((price * discountRate).toFixed(10));
};

// 计算最低股数
const minShares = computed(() => {
  if (!currentOrderItem.value?.stockNum) return 1;
  return currentOrderItem.value.stockNum;
});

// 计算手续费
const tradeFee = computed(() => {
  const shares = Number(orderNum.value || 0);
  const total = shares * getOrderUnitPrice();
  const fee = total * (feeInfo.value.buyFee || 0);
  return fee;
});

// 计算预估总金额（包含手续费）
const estimatedTotal = computed(() => {
  const shares = Number(orderNum.value || 0);
  const total = shares * getOrderUnitPrice();
  const fee = Number(tradeFee.value);
  return total + fee;
});

// 格式化日期
const formatDate = (date) => {
  // 如果是字符串，将横杠替换为斜杠，确保 iOS Safari 能正确解析
  const dateStr = typeof date === 'string' ? date.replace(/-/g, '/') : date;
  const d = new Date(dateStr);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

// 初始化日期范围（默认最近7天）
const initDateRange = () => {
  const end = new Date();
  const start = new Date();
  start.setDate(start.getDate() - 7);

  dateRange.endTime = formatDate(end);
  dateRange.startTime = formatDate(start);
};

// 格式化显示日期（用于显示）
const displayDateRange = computed(() => {
  return `${dateRange.startTime} - ${dateRange.endTime}`;
});

// 获取市场列表
const fetchMarketList = async (reset = false) => {
  if (marketState.loading) return;
  if (reset) {
    marketState.pageNum = 1;
    marketState.finished = false;
    marketState.list = [];
  }
  if (marketState.finished) return;

  marketState.loading = true;
  marketState.loading = true;
  const res = await getDzList({
    startTime: dateRange.startTime,
    endTime: dateRange.endTime,
    pageNum: marketState.pageNum,
    pageSize: marketState.pageSize
  });

  let list = res?.data?.list || res?.list || [];
  const total = res?.data?.total || res?.total || list.length;

  marketState.list = marketState.pageNum === 1 ? list : marketState.list.concat(list);
  marketState.finished = marketState.list.length >= total || list.length === 0;
  if (!marketState.finished) {
    marketState.pageNum += 1;
  }
  marketState.loading = false;
};

// 获取历史列表
const fetchHistoryList = async (reset = false) => {
  if (historyState.loading) return;
  if (reset) {
    historyState.pageNum = 1;
    historyState.finished = false;
    historyState.list = [];
  }
  if (historyState.finished) return;

  historyState.loading = true;
  historyState.loading = true;
  const res = await buyStockDzList({
    startTime: dateRange.startTime,
    endTime: dateRange.endTime,
    pageNum: historyState.pageNum,
    pageSize: historyState.pageSize
  });

  let list = res?.data?.list || res?.list || [];
  const total = res?.data?.total || res?.total || list.length;

  historyState.list = historyState.pageNum === 1 ? list : historyState.list.concat(list);
  historyState.finished = historyState.list.length >= total || list.length === 0;
  if (!historyState.finished) {
    historyState.pageNum += 1;
  }
  historyState.loading = false;
};

// 切换tab
const switchTab = (value) => {
  if (activeTab.value === value) return;
  activeTab.value = value;

  // 切换时重新加载对应数据
  if (value === 'market') {
    fetchMarketList(true);
  } else {
    fetchHistoryList(true);
  }
};

// 打开日期选择器
const openDatePicker = () => {
  showDatePicker.value = true;
};

// 日期选择确认
const handleDateConfirm = (e) => {
  console.log('日期选择确认:', e);
  const values = e.value;
  if (Array.isArray(values) && values.length === 2) {
    dateRange.startTime = values[0].split(' ')[0]; // "2025-11-05"
    dateRange.endTime = values[1].split(' ')[0]; // "2025-11-13"
  }
  showDatePicker.value = false;

  // 重新加载数据
  if (activeTab.value === 'market') {
    fetchMarketList(true);
  } else {
    fetchHistoryList(true);
  }
};

// 申请大宗交易
const handleApply = async (item) => {
  currentOrderItem.value = item;
  orderNum.value = '';
  password.value = '';
  preciseUnitPrice.value = Number(item?.price || 0);

  // 重置手续费
  feeInfo.value = {
    buyFee: 0,
    sellFee: 0
  };

  // 根据股票类型获取对应账户余额
  await fetchAccountInfo(item.stockType || 'mxg');

  // 获取手续费 - 使用 gid 或 stockCode
  const gidParam = item.stockGid;
  if (gidParam) {
    const [feeResult, stockResult] = await Promise.allSettled([
      getTradeFee({ gid: gidParam }),
      getStockDetail({ gid: gidParam })
    ]);

    if (feeResult.status === 'fulfilled') {
      const feeRes = feeResult.value;
      if (feeRes && (feeRes.gid || feeRes.buyFee !== undefined)) {
        feeInfo.value = {
          buyFee: feeRes.buyFee || 0,
          sellFee: feeRes.sellFee || 0
        };
      }
    } else {
      console.error('获取手续费失败', feeResult.reason);
    }

    if (stockResult.status === 'fulfilled') {
      const stockRes = stockResult.value;
      if (
        stockRes &&
        (stockRes.gid || stockRes.nowPrice !== undefined || stockRes.price !== undefined)
      ) {
        preciseUnitPrice.value = calcDiscountedPrice(
          stockRes.nowPrice ?? stockRes.price,
          item.discount
        );
      }
    } else {
      console.error('获取股票详情失败', stockResult.reason);
    }
  }

  showOrderModal.value = true;
};

// 查看详情
const handleViewDetail = (item) => {
  // 大宗交易也使用 positionSn
  if (item.positionSn) {
    uni.navigateTo({
      url: `/pages/orders/fpo-detail?type=bulk&positionSn=${item.positionSn}`
    });
  } else {
    // 如果没有 positionSn，使用旧方式（兼容）
    const data = encodeURIComponent(JSON.stringify(item));
    uni.navigateTo({
      url: `/pages/orders/fpo-detail?type=bulk&item=${data}`
    });
  }
};

// 设置仓位比例
const setPosition = (ratio) => {
  if (!currentOrderItem.value) return;

  const price = getOrderUnitPrice();
  const balance = Number(accountInfo.value.availableBalance || 0);
  const feeRate = Number(feeInfo.value.buyFee || 0);

  if (price <= 0) return;

  // 1. Calculate max shares that can be bought with the balance
  // Balance >= Shares * Price * (1 + FeeRate)
  // Shares <= Balance / (Price * (1 + FeeRate))
  const priceWithFee = price * (1 + feeRate);
  let maxShares = Math.floor(balance / priceWithFee);

  // 2. Apply ratio to get the target number of shares
  let targetShares = Math.floor(maxShares * ratio);

  // 3. Verify the cost for the target shares and adjust if necessary
  // This handles any precision issues or edge cases
  while (targetShares > 0) {
    const stockCost = targetShares * price;
    const fee = stockCost * feeRate;
    const totalCost = stockCost + fee;

    if (totalCost <= balance) {
      break;
    }
    targetShares--;
  }

  orderNum.value = String(targetShares);
};

// 获取账户余额
const fetchAccountInfo = async (stockType) => {
  const info = await getAccountBalance(stockType);
  accountInfo.value = info;
};

// 确认下单
const handleConfirmOrder = async () => {
  if (!orderNum.value || Number(orderNum.value) <= 0) {
    uni.showToast({ title: uni.$t('bulk.enterNum'), icon: 'none' });
    return;
  }

  const num = Number(orderNum.value);

  // 检查最低股数
  if (num < minShares.value) {
    uni.showToast({
      title: `${uni.$t('bulk.minShares')} ${minShares.value} ${uni.$t('ipo.shareUnit')}`,
      icon: 'none'
    });
    return;
  }

  // 验证余额
  const price = getOrderUnitPrice();
  const feeRate = Number(feeInfo.value.buyFee || 0);
  const totalCost = num * price * (1 + feeRate);

  if (totalCost > accountInfo.value.availableBalance) {
    uni.showToast({ title: uni.$t('issuance.insufficientBalance'), icon: 'none' });
    return;
  }

  if (!password.value) {
    uni.showToast({ title: uni.$t('bulk.passwordFormatError'), icon: 'none' });
    return;
  }
  if (!currentOrderItem.value) return;

  await buyStockDz({
    num: num,
    password: password.value,
    stockDzId: currentOrderItem.value.id
  });
  uni.showToast({ title: uni.$t('bulk.orderSuccess'), icon: 'success' });
  showOrderModal.value = false;
  // 刷新列表
  if (activeTab.value === 'history') {
    fetchHistoryList(true);
  }
};

// 下拉刷新
const handleRefresh = async () => {
  if (activeTab.value === 'market') {
    await fetchMarketList(true);
  } else {
    await fetchHistoryList(true);
  }
  uni.stopPullDownRefresh();
};

// 触底加载
const handleLoadMore = () => {
  if (activeTab.value === 'market') {
    fetchMarketList();
  } else {
    fetchHistoryList();
  }
};

// 获取状态文本和样式
const getStatusInfo = (status) => {
  const statusMap = {
    1: { text: uni.$t('bulk.pendingReview'), class: 'pending' }, // 待审核
    2: { text: uni.$t('bulk.rejected'), class: 'rejected' }, // 未通过
    5: { text: uni.$t('bulk.transferred'), class: 'success' } // 已转持仓
  };
  return statusMap[status] || { text: uni.$t('bulk.pending'), class: 'pending' };
};

// 初始化
const initPage = () => {
  initDateRange();
  fetchMarketList(true);
};

onLoad(() => {
  initPage();
});

onShow(() => {
  commonStore.fnBack = goBack;
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
    <view class="bulk-page">
      <nav-bar :title="$t('bulk.title')" />

      <view class="page-content">
        <!-- Tab切换 -->
        <view class="tabs">
          <view
            v-for="tab in tabs"
            :key="tab.value"
            class="tab-item"
            :class="{ active: activeTab === tab.value }"
            @click="switchTab(tab.value)"
          >
            {{ $t(tab.title) }}
          </view>
        </view>

        <!-- 市场列表 -->
        <view v-if="activeTab === 'market'" class="market-container">
          <!-- 表头 -->
          <view class="table-header">
            <text class="header-cell header-name">{{ $t('bulk.name') }}</text>
            <text class="header-cell">{{ $t('bulk.price') }}</text>
            <text class="header-cell">{{ $t('bulk.discount') }}</text>
            <text class="header-cell header-action"></text>
          </view>

          <!-- 股票列表 -->
          <scroll-view class="stock-list" scroll-y>
            <view v-if="marketState.list.length" class="list-content">
              <view
                v-for="item in marketState.list"
                :key="item.id"
                class="stock-item"
                @click="handleViewDetail(item)"
              >
                <view class="stock-col stock-name-col">
                  <view class="stock-name">{{ item.stockName }}</view>
                  <view class="stock-tags">
                    <text class="stock-tag">{{ item.stockPlate }}</text>
                    <!-- <text class="stock-tag">{{ item.stockType }}</text> -->
                  </view>
                </view>
                <view class="stock-col stock-price-col">
                  <text class="stock-price">{{ item.price }}</text>
                </view>
                <view class="stock-col stock-discount-col">
                  <text class="stock-discount">{{ item.discount }}</text>
                </view>
                <view class="stock-col stock-action-col">
                  <button class="apply-btn" @click.stop="handleApply(item)">
                    {{ $t('bulk.apply') }}
                  </button>
                </view>
              </view>

              <view v-if="marketState.loading" class="footer-tip">{{
                $t('common.loading')
              }}</view>
              <view v-else-if="marketState.finished" class="footer-tip">{{
                $t('common.noMore')
              }}</view>
            </view>
            <no-data v-else />
          </scroll-view>
        </view>

        <!-- 历史列表 -->
        <view v-else class="history-container">
          <!-- 时间筛选 -->
          <view class="time-filter">
            <text class="time-label">{{ $t('bulk.timeRange') }}</text>
            <view class="date-selector" @click="openDatePicker">
              <text class="date-text">{{ displayDateRange }}</text>
              <text class="arrow-icon">▼</text>
            </view>
          </view>

          <!-- 历史记录列表 -->
          <scroll-view class="history-list" scroll-y>
            <view v-if="historyState.list.length" class="list-content">
              <view
                v-for="item in historyState.list"
                :key="item.id"
                class="history-card"
                @click="handleViewDetail(item)"
              >
                <view class="card-header">
                  <view class="header-left">
                    <text class="history-stock-name">{{ item.stockName }}</text>
                    <view class="status-badge" :class="getStatusInfo(item.status).class">
                      {{ getStatusInfo(item.status).text }}
                    </view>
                  </view>
                  <view class="header-right">
                    <text class="arrow">›</text>
                  </view>
                </view>

                <view class="card-body">
                  <view class="info-row">
                    <text class="label">{{ $t('bulk.applyNum') }}</text>
                    <text class="value"
                      >{{ item.orderNum }} {{ $t('ipo.shareUnit') }}</text
                    >
                  </view>
                  <view class="info-row">
                    <text class="label">{{ $t('bulk.buyPrice') }}</text>
                    <view class="value">
                      <c-amount
                        :value="Number(item.orderNum) * Number(item.buyOrderPrice)"
                      />
                      <text>{{ getCurrency(item.stockType) }}</text>
                    </view>
                  </view>
                  <view class="info-row">
                    <text class="label">{{ $t('bulk.discountPrice') }}</text>
                    <view class="value">
                      <c-amount :value="item.buyOrderPrice" />
                      <text>{{ getCurrency(item.stockType) }}</text>
                    </view>
                  </view>
                  <view class="info-row">
                    <text class="label">{{ $t('bulk.marketPrice') }}</text>
                    <view class="value">
                      <c-amount
                        :value="Number(item.buyOrderPrice) / Number(item.buyDiscount)"
                      />
                      <text>{{ getCurrency(item.stockType) }}</text>
                    </view>
                  </view>
                </view>
              </view>

              <view v-if="historyState.loading" class="footer-tip">{{
                $t('common.loading')
              }}</view>
              <view v-else-if="historyState.finished" class="footer-tip">{{
                $t('common.noMore')
              }}</view>
            </view>
            <no-data v-else />
          </scroll-view>
        </view>
      </view>

      <!-- 日期范围选择器 -->
      <DateRangePicker
        :show="showDatePicker"
        :startDate="dateRange.startTime"
        :endDate="dateRange.endTime"
        @confirm="handleDateConfirm"
        @close="showDatePicker = false"
      />

      <!-- 下单弹窗 -->
      <u-popup
        :show="showOrderModal"
        mode="center"
        @close="showOrderModal = false"
        :round="24"
        :z-index="10"
      >
        <view class="order-modal">
          <view class="modal-header">
            <view class="header-placeholder"></view>
            <text class="modal-title">{{ $t('bulk.bulkTrading') }}</text>
            <text class="modal-close" @click="showOrderModal = false">×</text>
          </view>

          <view class="modal-body">
            <!-- 假输入框，防止自动填充 -->
            <input
              type="text"
              style="position: absolute; left: -9999px; width: 1px; height: 1px"
            />
            <input
              type="password"
              style="position: absolute; left: -9999px; width: 1px; height: 1px"
            />

            <view class="price-row">
              <text class="price-label">{{ $t('bulk.applyPrice') }}：</text>
              <text class="price-value">{{ currentOrderItem?.price || 0 }}</text>
            </view>

            <view class="info-row">
              <text class="info-label">{{ $t('bulk.availableAmount') }}：</text>
              <view class="info-value">
                <c-amount :value="accountInfo.availableBalance" />
                <text> {{ accountInfo.currency }}</text>
              </view>
            </view>

            <view class="input-row">
              <input
                v-model="orderNum"
                type="number"
                class="num-input"
                :placeholder="$t('bulk.enterNum')"
                autocomplete="off"
              />
              <text class="input-unit">{{ $t('ipo.shareUnit') }}</text>
            </view>

            <view class="min-hint" v-if="minShares > 1">
              <text
                >{{ $t('bulk.minShares') }} {{ minShares }}
                {{ $t('ipo.shareUnit') }}</text
              >
            </view>

            <view class="position-row">
              <text class="position-label">{{ $t('bulk.position') }}</text>
              <view class="position-btns">
                <view class="position-btn" @click="setPosition(0.25)">1/4</view>
                <view class="position-btn" @click="setPosition(0.33)">1/3</view>
                <view class="position-btn" @click="setPosition(0.5)">1/2</view>
                <view class="position-btn" @click="setPosition(1)">{{
                  $t('bulk.fullPosition')
                }}</view>
              </view>
            </view>

            <!-- 密码输入框 -->
            <view class="password-section">
              <input
                class="password-input"
                type="password"
                v-model="password"
                :placeholder="$t('bulk.enterPassword')"
                autocomplete="new-password"
              />
            </view>

            <view class="estimated-section">
              <view class="estimated-row">
                <text class="estimated-label">{{ $t('bulk.estimatedAmount') }}：</text>
                <view class="estimated-value"
                  ><c-amount :value="Number(orderNum || 0) * getOrderUnitPrice()"
                /></view>
              </view>
              <view class="estimated-row">
                <text class="estimated-label">{{ $t('bulk.feeLabel') }}：</text>
                <view class="estimated-value"
                  ><c-amount :value="tradeFee" :decimals="5"
                /></view>
              </view>
              <view class="estimated-row total">
                <text class="estimated-label">{{ $t('bulk.totalLabel') }}：</text>
                <view class="estimated-value"><c-amount :value="estimatedTotal" /></view>
              </view>
            </view>
          </view>

          <view class="modal-footer">
            <button class="confirm-btn" @click="handleConfirmOrder">
              {{ $t('bulk.buyIn') }}
            </button>
          </view>
        </view>
      </u-popup>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.bulk-page {
  @include hasNavBar();
}

.page-content {
  display: flex;
  flex-direction: column;
  height: calc(100vh - var(--status-bar-height) - var(--nav-bar-height));
}

// Tab样式
.tabs {
  display: flex;
  background: #fff;
  border-bottom: 2rpx solid #f0f0f0;
}

.tab-item {
  position: relative;
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  font-size: 28rpx;
  color: #666;
  transition: color 0.3s;

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
      height: 4rpx;
      background: #00b6e6;
      border-radius: 2rpx;
    }
  }
}

// 市场容器
.market-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16rpx 32rpx;
}

// 表头
.table-header {
  display: flex;
  align-items: center;
  padding: 20rpx 24rpx;
  background: #fff;
  border-radius: 16rpx 16rpx 0 0;
  margin-bottom: 2rpx;
}

.header-cell {
  flex-shrink: 0;
  font-size: 26rpx;
  color: #666;
  text-align: center;

  &.header-name {
    width: 200rpx;
    text-align: left;
  }

  &:not(.header-name):not(.header-action) {
    width: 140rpx;
  }

  &.header-action {
    width: 120rpx;
    text-align: right;
  }
}

// 股票列表
.stock-list {
  flex: 1;
  overflow: hidden;
}

.list-content {
  padding-bottom: 32rpx;
}

// 股票卡片
.stock-item {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: #fff;
  border-bottom: 2rpx solid #f5f5f5;

  &:last-child {
    border-bottom: none;
    border-radius: 0 0 16rpx 16rpx;
  }
}

.stock-col {
  display: flex;
  flex-direction: column;
  justify-content: center;
  text-align: center;
  flex-shrink: 0;

  &.stock-name-col {
    width: 200rpx;
    text-align: left;
    align-items: flex-start;
  }

  &.stock-price-col,
  &.stock-discount-col {
    width: 140rpx;
    align-items: center;
  }

  &.stock-action-col {
    width: 120rpx;
    align-items: flex-end;
  }
}

.stock-name {
  font-size: 28rpx;
  color: #333;
  font-weight: 600;
  margin-bottom: 8rpx;
  width: 200rpx;
  word-wrap: break-word;
  word-break: break-all;
}

.stock-tags {
  display: flex;
  gap: 8rpx;
}

.stock-tag {
  font-size: 20rpx;
  color: #999;
  background: #f5f5f5;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}

.stock-price {
  font-size: 28rpx;
  color: #333;
  font-weight: 600;
  width: 140rpx;
  word-wrap: break-word;
  word-break: break-all;
}

.stock-discount {
  font-size: 28rpx;
  color: #ff4d4f;
  font-weight: 600;
  width: 140rpx;
  word-wrap: break-word;
  word-break: break-all;
}

.apply-btn {
  padding: 12rpx 30rpx;
  background: #ff4d4f;
  color: #fff;
  font-size: 24rpx;
  border-radius: 32rpx;
  border: none;
  line-height: 1;
}

// 历史容器
.history-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16rpx 32rpx;
}

// 时间筛选
.time-filter {
  margin-bottom: 16rpx;
}

.time-label {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 12rpx;
}

// 日期选择器
.date-selector {
  display: flex;
  align-items: center;
  padding: 20rpx 24rpx;
  background: #fff;
  border-radius: 16rpx;
}

.date-text {
  font-size: 28rpx;
  color: #333;
}

.arrow-icon {
  font-size: 20rpx;
  color: #999;
  margin-left: 15rpx;
}

// 历史列表
.history-list {
  flex: 1;
  overflow: hidden;
}

// 历史卡片
.history-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.header-left {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12rpx;
  min-width: 0;
}

.header-right {
  display: flex;
  align-items: center;
}

.history-stock-name {
  font-size: 30rpx;
  color: #333;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-badge {
  padding: 6rpx 16rpx;
  background: #f5f5f5;
  color: #999;
  font-size: 22rpx;
  border-radius: 8rpx;
  white-space: nowrap;

  &.pending {
    background: #fff7e6;
    color: #fa8c16;
  }

  &.success {
    background: #e6f7f0;
    color: #00c087;
  }

  &.rejected {
    background: #fff1f0;
    color: #ff4d4f;
  }
}

.arrow {
  font-size: 40rpx;
  color: #999;
  line-height: 1;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.info-row {
  display: flex;
  justify-content: space-between;
  font-size: 26rpx;
}

.label {
  color: #666;
}

.value {
  color: #333;
  font-weight: 600;
}

// 底部提示
.footer-tip {
  text-align: center;
  padding: 24rpx 0;
  font-size: 26rpx;
  color: #999;
}

// 下单弹窗
// 下单弹窗
.order-modal {
  background: #fff;
  border-radius: 24rpx;
  width: 650rpx;
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.header-placeholder {
  width: 48rpx;
}

.modal-title {
  flex: 1;
  text-align: center;
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}

.modal-close {
  width: 48rpx;
  text-align: right;
  font-size: 48rpx;
  color: #999;
  line-height: 1;
}

.modal-body {
  padding: 32rpx;
}

.price-row {
  display: flex;
  align-items: center;
  margin-bottom: 32rpx;
}

.price-label {
  font-size: 28rpx;
  color: #666;
}

.price-value {
  font-size: 28rpx;
  color: #333;
  font-weight: 600;
}

.input-row {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 0 24rpx;
  margin-bottom: 24rpx;
}

.num-input {
  flex: 1;
  height: 88rpx;
  font-size: 28rpx;
  color: #333;
}

.input-unit {
  font-size: 28rpx;
  color: #666;
}

.min-hint {
  margin-bottom: 24rpx;
  padding: 12rpx 16rpx;
  background: #fff3e0;
  border-radius: 8rpx;
  font-size: 24rpx;
  color: #ff9800;
  text-align: center;
}

.position-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24rpx;
}

.position-label {
  font-size: 28rpx;
  color: #666;
}

.position-btns {
  display: flex;
  gap: 16rpx;
  align-items: center;
}

.position-btn {
  flex: 1;
  padding: 12rpx 4rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  font-size: 22rpx;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  line-height: 1.2;
  min-height: 64rpx;
}

.amount-row {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
}

.amount-label {
  font-size: 28rpx;
  color: #666;
}

.amount-value {
  font-size: 28rpx;
  color: #ff4d4f;
  font-weight: 600;
}

// 密码输入区域
.password-section {
  margin-bottom: 24rpx;
}

.password-input {
  height: 88rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #333;
  border: none;
}

.estimated-section {
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 2rpx solid #f0f0f0;
}

.estimated-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16rpx;

  &:last-child {
    margin-bottom: 0;
  }

  &.total {
    margin-top: 16rpx;
    padding-top: 16rpx;
    border-top: 2rpx solid #f0f0f0;

    .estimated-label,
    .estimated-value {
      font-size: 32rpx;
      font-weight: 700;
    }
  }
}

.estimated-label {
  font-size: 28rpx;
  color: #666;
}

.estimated-value {
  font-size: 28rpx;
  color: #333;
  font-weight: 600;
}

.modal-footer {
  padding: 0 32rpx 32rpx;
}

.confirm-btn {
  width: 100%;
  height: 72rpx;
  background: #00b6e6;
  color: #fff;
  font-size: 28rpx;
  font-weight: 600;
  border-radius: 14rpx;
  border: none;
  line-height: 72rpx;
}

// 数字键盘
.keyboard {
  background: #f5f5f5;
  padding: 16rpx;
}

.keyboard-row {
  display: flex;
  gap: 16rpx;
  margin-bottom: 16rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.key-item {
  flex: 1;
  height: 96rpx;
  background: #fff;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  color: #333;
  font-weight: 500;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
  transition: all 0.2s;

  &:active {
    background: #f0f0f0;
    transform: scale(0.95);
  }

  &.key-empty {
    background: transparent;
    box-shadow: none;
  }

  &.key-delete {
    background: #fff;
  }
}

.delete-icon {
  font-size: 40rpx;
  color: #666;
}
</style>
