<script setup>
import { ref, onUnmounted } from 'vue';
import { onShow, onHide } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';
import { useCommonStore } from '@/stores/common';
import { useUserStore } from '@/stores/user';
import { goBack, navigateTo } from '@/utils/navigate';
import { getPositionList, sellStock, updateProfitTarget } from '@/apis/order';
import { checkAndConnectWebSocket } from '@/utils/websocket';
import ClosePositionModal from '../components/ClosePositionModal.vue';
import ProfitStopModal from '../components/ProfitStopModal.vue';

const { t } = useI18n();
const commonStore = useCommonStore();
const userStore = useUserStore();

// 当前标签页：holding(持仓中) / history(历史)
const currentTab = ref('holding');

// 持仓列表
const positionList = ref([]);
const positionPage = ref(1);
const positionPageSize = ref(10);
const positionHasMore = ref(true);
const positionLoading = ref(false);
const refreshing = ref(false);

// 获取持仓列表
const fetchPositionList = async (loadMore = false) => {
  if (positionLoading.value) return;

  try {
    positionLoading.value = true;
    const pageNum = loadMore ? positionPage.value + 1 : 1;

    const res = await getPositionList({
      pageNum,
      pageSize: positionPageSize.value,
      state: currentTab.value === 'holding' ? 0 : 1
    });

    if (res && res.list) {
      const formattedList = res.list.map(item => {
        const currentPrice = item.now_price || item.buyOrderPrice || 0;
        const buyPrice = item.buyOrderPrice || 0;
        const quantity = item.orderNum || 0;
        const orderDirection = item.orderDirection === 0 ? 'buy' : 'sell';

        // 计算当前市值
        const currentValue = currentPrice * quantity;
        const buyTotalPrice = item.buyTotalPrice || 0;

        // 直接使用后端返回的盈亏值
        const profitAndLose = item.profitAndLose || 0;

        // 计算盈亏率：盈亏 / (买入价 * 数量)
        // 先保留4位小数，再乘100，最后结果保留2位
        const profitRate = buyTotalPrice > 0 ? (parseFloat((profitAndLose / buyTotalPrice).toFixed(4)) * 100).toFixed(2) + '%' : '--';

        return {
          ...item,
          id: item.id,
          positionSn: item.positionSn,
          stockCode: item.stockCode,
          stockName: item.stockName,
          stockSpell: item.stockSpell,
          stockPlate: item.stockPlate,
          stockType: orderDirection,
          availableNum: item.availableNum || 0,
          profitRate: profitRate,
          profitAndLose: profitAndLose,
          holdValue: buyTotalPrice,
          buyPrice: buyPrice,
          currentPrice: currentPrice,
          currentValue: currentValue,
          quantity: quantity,
          currency: item.currency || 'MXN',
          stopTarget: item.stopTargetPrice,
          gid: item.gid || item.stockCode
        };
      });

      if (loadMore) {
        positionList.value = [...positionList.value, ...formattedList];
      } else {
        positionList.value = formattedList;
      }
      positionPage.value = pageNum;
      positionHasMore.value = res.hasNextPage;

      // 获取列表后设置 WebSocket 监听
      setupWebSocketListeners();
    }
  } catch (error) {
    console.error('获取持仓列表失败', error);
    uni.showToast({
      title: error.errorDesc || error.msg || t('error.system'),
      icon: 'none'
    });
  } finally {
    positionLoading.value = false;
    refreshing.value = false;
  }
};

// 切换标签页
const switchTab = (tab) => {
  if (currentTab.value === tab) return;
  currentTab.value = tab;
  positionPage.value = 1;
  positionList.value = [];
  fetchPositionList();
};

// 下拉刷新
const onRefresh = () => {
  refreshing.value = true;
  positionPage.value = 1;
  fetchPositionList();
};

// 触底加载
const onLoadMore = () => {
  if (positionHasMore.value && !positionLoading.value) {
    fetchPositionList(true);
  }
};

// 点击持仓项
const handlePositionClick = (item) => {
  navigateTo(`/pages/orders/fpo-detail?type=position&positionSn=${item.positionSn}`);
};

// 弹框相关
const closeModalVisible = ref(false);
const profitStopModalVisible = ref(false);
const currentPosition = ref(null);

const handleStopOrLimit = (item) => {
  currentPosition.value = item;
  profitStopModalVisible.value = true;
};

const handleClosePosition = (item) => {
  currentPosition.value = item;
  closeModalVisible.value = true;
};

const handleConfirmClose = async (position) => {
  const res = await sellStock(position.positionSn);

  if (res.status == 0) {
    uni.showToast({
      title: t('order.closeSuccess'),
      icon: 'success'
    });

    closeModalVisible.value = false;
    fetchPositionList();
  } else {
    uni.showToast({
      title: res.msg || t('error.system'),
      icon: 'none'
    });
  }
};

const handleConfirmProfitStop = async (data) => {
  const res = await updateProfitTarget(data);

  if (res.status == 0) {
    uni.showToast({
      title: t('order.updateSuccess'),
      icon: 'success'
    });

    profitStopModalVisible.value = false;
    fetchPositionList();
  } else {
    uni.showToast({
      title: res.msg || t('error.system'),
      icon: 'none'
    });
  }
};

// WebSocket消息处理的节流控制
let updateTimer = null;
const pendingUpdates = new Map();

// WebSocket消息处理
const handleStockMessage = (data) => {
  // 只在持仓中tab处理消息
  if (currentTab.value !== 'holding' || positionList.value.length === 0) {
    return;
  }

  const stockId = data.pid || data.gid || data.code;
  if (!stockId) return;

  const newPrice = data.last_numeric;
  if (!newPrice) return;

  // 将更新缓存起来
  pendingUpdates.set(stockId, newPrice);

  // 使用节流，每100ms批量更新一次
  if (updateTimer) return;

  updateTimer = setTimeout(() => {
    updateTimer = null;

    // 批量处理所有待更新的股票
    pendingUpdates.forEach((price, stockId) => {
      const index = positionList.value.findIndex(
        (item) => item.stockCode === stockId || item.gid === stockId
      );

      if (index === -1) return;

      const item = positionList.value[index];

      // 如果价格没变化，跳过
      if (item.currentPrice === price) return;

      // 计算新的市值
      const currentValue = price * item.quantity;

      // 根据买卖方向计算盈亏
      // 买: (现价 - 买入价) * 数量
      // 卖: (买入价 - 现价) * 数量
      let profitAndLose = 0;
      if (item.stockType === 'buy') {
        profitAndLose = (price - item.buyPrice) * item.quantity;
      } else {
        profitAndLose = (item.buyPrice - price) * item.quantity;
      }

      // 计算盈亏率
      // 计算盈亏率：盈亏 / (买入价 * 数量)
      // 买卖都用同一个公式
      // 先保留4位小数，再乘100，最后结果保留2位
      let profitRate = '--';
      const buyTotal = item.buyPrice * item.quantity;
      profitRate = buyTotal > 0 ? (parseFloat((profitAndLose / buyTotal).toFixed(4)) * 100).toFixed(2) + '%' : '--';

      // 只更新变化的项
      positionList.value[index] = {
        ...item,
        currentPrice: price,
        currentValue: currentValue,
        profitAndLose: profitAndLose,
        profitRate: profitRate
      };
    });

    // 清空待更新列表
    pendingUpdates.clear();
  }, 100);
};

// 当前监听的 WebSocket 事件
const activeWsEvents = new Set();

// 设置 WebSocket 监听
const setupWebSocketListeners = () => {
  // 先移除所有监听
  removeWebSocketListeners();

  // 只在持仓中tab且有数据时监听
  if (currentTab.value !== 'holding' || positionList.value.length === 0) {
    return;
  }

  // 检查并连接 WebSocket，连接成功后设置监听
  checkAndConnectWebSocket(userStore, setupWebSocketListenersInternal, 1000);
};

// 内部方法：设置 WebSocket 监听
const setupWebSocketListenersInternal = () => {
  // 只在持仓中tab且有数据时监听
  if (currentTab.value !== 'holding' || positionList.value.length === 0) {
    return;
  }

  // 收集需要监听的股票类型
  const stockTypes = new Set();
  positionList.value.forEach(item => {
    // 根据 stockType 字段判断
    const stockType = item.stockType || 'mxg';
    if (stockType === 'us' || item.stockCode?.startsWith('US')) {
      stockTypes.add('us');
    } else {
      stockTypes.add('mexico');
    }
  });

  // 监听需要的 WebSocket
  stockTypes.forEach(type => {
    const eventName = type === 'us' ? 'ws:us' : 'ws:mexico';
    uni.$on(eventName, handleStockMessage);
    activeWsEvents.add(eventName);
  });

  if (activeWsEvents.size > 0) {
    console.log(`✅ 开始监听 WebSocket: ${Array.from(activeWsEvents).join(', ')}`);
  }
};

// 移除 WebSocket 监听
const removeWebSocketListeners = () => {
  activeWsEvents.forEach(eventName => {
    uni.$off(eventName, handleStockMessage);
  });
  if (activeWsEvents.size > 0) {
    console.log(`❌ 停止监听 WebSocket: ${Array.from(activeWsEvents).join(', ')}`);
  }
  activeWsEvents.clear();
};

onShow(() => {
  commonStore.fnBack = goBack;
  fetchPositionList();
});

onHide(() => {
  removeWebSocketListeners();

  // 清理定时器和待更新数据
  if (updateTimer) {
    clearTimeout(updateTimer);
    updateTimer = null;
  }
  pendingUpdates.clear();
});

onUnmounted(() => {
  removeWebSocketListeners();

  // 清理定时器和待更新数据
  if (updateTimer) {
    clearTimeout(updateTimer);
    updateTimer = null;
  }
  pendingUpdates.clear();
});
</script>

<template>
  <page-wrapper>
    <view class="m-content">
      <view class="transaction-container">
        <!-- 标题栏 -->
        <view class="header">
          <view class="back-btn" @click="goBack">
            <text class="back-icon">←</text>
          </view>
          <view class="title">{{ $t('order.transactionRecords') }}</view>
          <view class="placeholder"></view>
        </view>

        <!-- 按钮组 -->
        <view class="button-group">
          <view class="filter-btn" :class="{ active: currentTab === 'holding' }" @click="switchTab('holding')">
            {{ $t('order.holdingTab') }}
          </view>
          <view class="filter-btn" :class="{ active: currentTab === 'history' }" @click="switchTab('history')">
            {{ $t('order.historyTab') }}
          </view>
        </view>

        <!-- 列表 -->
        <scroll-view class="scroll-view" scroll-y @scrolltolower="onLoadMore" refresher-enabled
          :refresher-triggered="refreshing" @refresherrefresh="onRefresh">
          <view class="position-list" v-if="positionList.length > 0">
            <!-- 持仓中列表 -->
            <view class="position-item" v-for="item in positionList" :key="item.id" @click="handlePositionClick(item)"
              v-if="currentTab === 'holding'">
              <view class="position-header">
                <view class="stock-info">
                  <text class="stock-code">{{ item.stockName }}</text>
                </view>
                <view class="stock-info">
                  <text class="arrow">›</text>
                </view>
              </view>
              <view class="stock-name">{{ item.stockSpell }}</view>

              <view class="stock-tags">
                <view class="stock-tag">{{ item.stockPlate }}</view>
                <view class="stock-tag-type" :class="item.stockType">
                  {{ $t(`order.${item.stockType}`) }}
                </view>
              </view>

              <view class="position-details">
                <view class="detail-row">
                  <view class="detail-col">
                    <text class="label">{{ $t('order.availableNum') }}</text>
                    <view class="value" :style="{ color: item.profitAndLose >= 0 ? '#00C087' : '#FF4D4F' }">
                      <text>{{ item.profitAndLose >= 0 ? '+' : '' }}</text>
                      <c-amount :value="item.profitAndLose" />
                      <text> {{ item.currency }}</text>
                    </view>
                  </view>
                  <view class="detail-col">
                    <text class="label">{{ $t('order.profitRate') }}</text>
                    <text class="value" :style="{ color: item.profitAndLose >= 0 ? '#00C087' : '#FF4D4F' }">{{
                      item.profitRate }}</text>
                  </view>
                  <view class="detail-col">
                    <text class="label">{{ $t('order.holdValue') }}</text>
                    <view class="value">
                      <c-amount :value="item.holdValue" />
                      <text> {{ item.currency }}</text>
                    </view>
                  </view>
                </view>
                <view class="detail-row">
                  <view class="detail-col">
                    <text class="label">{{ $t('order.buyPrice') }}</text>
                    <view class="value">
                      <c-amount :value="item.buyPrice" />
                      <text> {{ item.currency }}</text>
                    </view>
                  </view>
                  <view class="detail-col">
                    <text class="label">{{ $t('order.quantity') }}</text>
                    <text class="value">{{ item.quantity }}</text>
                  </view>
                  <view class="detail-col">
                    <text class="label">{{ $t('order.currentPrice') }}</text>
                    <view class="value">
                      <c-amount :value="item.currentPrice" />
                      <text> {{ item.currency }}</text>
                    </view>
                  </view>
                </view>
              </view>

              <!-- 持仓中显示按钮 -->
              <view class="position-actions">
                <view class="action-btn secondary" @click.stop="handleStopOrLimit(item)">
                  {{ $t('order.stopOrLimit') }}
                </view>
                <view class="action-btn primary" @click.stop="handleClosePosition(item)">
                  {{ $t('order.closePosition') }}
                </view>
              </view>
            </view>

            <!-- 历史列表 -->
            <view class="history-item" v-for="item in positionList" :key="item.id" @click="handlePositionClick(item)"
              v-if="currentTab === 'history'">
              <view class="history-header">
                <view class="stock-info">
                  <text class="stock-code">{{ item.stockName }}</text>
                </view>
                <view class="stock-info">
                  <text class="arrow">›</text>
                </view>
              </view>
              <view class="stock-name">{{ item.stockSpell }}</view>

              <view class="stock-tags">
                <view class="stock-tag">{{ item.stockPlate }}</view>
                <view class="stock-tag-type" :class="item.stockType">
                  {{ $t(`order.${item.stockType}`) }}
                </view>
              </view>

              <view class="history-details">
                <view class="detail-row-3col">
                  <view class="detail-col">
                    <text class="label">{{ $t('order.holdValue') }}</text>
                    <text class="value">{{ item.holdValue.toLocaleString('en-US', {
                      minimumFractionDigits: 2,
                      maximumFractionDigits: 2
                    }) }} {{ item.currency }}</text>
                  </view>
                  <view class="detail-col">
                    <text class="label">{{ $t('order.availableNum') }}</text>
                    <text class="value" :style="{ color: item.profitAndLose >= 0 ? '#00C087' : '#FF4D4F' }">
                      {{ item.profitAndLose >= 0 ? '+' : '' }}{{ item.profitAndLose.toLocaleString('en-US', {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                      }) }} {{ item.currency }}
                    </text>
                  </view>
                  <view class="detail-col">
                    <text class="label">{{ $t('order.profitRate') }}</text>
                    <text class="value" :style="{ color: item.profitAndLose >= 0 ? '#00C087' : '#FF4D4F' }">
                      {{ item.profitRate }}
                    </text>
                  </view>
                </view>
              </view>
            </view>
          </view>

          <!-- 空数据 -->
          <view class="no-data" v-else-if="!positionLoading">
            <text>{{ $t('common.noData') }}</text>
          </view>

          <!-- 加载中 -->
          <view class="loading" v-if="positionLoading && positionList.length === 0">
            <u-loading-icon mode="circle" size="40"></u-loading-icon>
          </view>

          <!-- 加载更多 -->
          <view class="load-more" v-if="positionList.length > 0 && positionHasMore">
            <text>{{ $t('common.loadMore') }}</text>
          </view>

          <!-- 没有更多 -->
          <view class="no-more" v-if="positionList.length > 0 && !positionHasMore">
            <text>{{ $t('common.noMore') }}</text>
          </view>
        </scroll-view>
      </view>
    </view>

    <!-- 平仓弹框 -->
    <close-position-modal v-model:visible="closeModalVisible" v-if="closeModalVisible" :position="currentPosition"
      @confirm="handleConfirmClose" />

    <!-- 止盈止损弹框 -->
    <profit-stop-modal v-model:visible="profitStopModalVisible" v-if="profitStopModalVisible"
      :position="currentPosition" @confirm="handleConfirmProfitStop" />
  </page-wrapper>
</template>

<style lang="scss" scoped>
.m-content {
  height: 100vh;
  background: #f8f8f8;
}

.transaction-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

// 标题栏
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 32rpx;
  background: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.back-btn {
  width: 60rpx;
}

.back-icon {
  font-size: 40rpx;
  color: #333;
}

.title {
  font-size: 32rpx;
  font-weight: 500;
  color: #333;
}

.placeholder {
  width: 60rpx;
}

// 按钮组
.button-group {
  display: flex;
  justify-content: flex-start;
  gap: 16rpx;
  padding: 24rpx 32rpx;
  background: #fff;
}

.filter-btn {
  padding: 12rpx 32rpx;
  border-radius: 8rpx;
  font-size: 26rpx;
  color: #666;
  background: #f1f1f1;

  &.active {
    color: #fff;
    background: #333;
  }
}

// 滚动视图
.scroll-view {
  flex: 1;
  height: 0;
}

// 持仓列表
.position-list {
  padding: 24rpx;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.position-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
}

.position-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.stock-info {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.stock-code {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.arrow {
  font-size: 35rpx;
  color: #A4A4A4;
}

.stock-name {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 16rpx;
}

.stock-tags {
  display: flex;
  gap: 12rpx;
  margin-bottom: 24rpx;
}

.stock-tag {
  padding: 8rpx 10rpx;
  background: #f5f5f5;
  border-radius: 4rpx;
  font-size: 22rpx;
  color: black;
}

.stock-tag-type {
  padding: 0rpx 10rpx;
  border-radius: 4rpx;
  font-size: 22rpx;
  display: flex;
  align-items: center;

  &.buy {
    background: #009F6A33;
    color: #009F6A;
  }

  &.sell {
    background: #ff4d4f33;
    color: #ff4d4f;
  }
}

.position-details {
  margin-bottom: 24rpx;
}

.detail-row {
  display: flex;
  gap: 32rpx;
  margin-bottom: 16rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.detail-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;

  .label {
    font-size: 24rpx;
    color: #999;
  }

  .value {
    font-size: 24rpx;
    color: #333;

    &.profit {
      color: #009F6A;
    }

    &.loss {
      color: #ff4d4f;
    }
  }
}

.position-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid #f0f0f0;
}

.action-btn {
  padding: 12rpx 32rpx;
  border-radius: 8rpx;
  font-size: 26rpx;

  &.secondary {
    background: #f5f5f5;
    color: #333;
  }

  &.primary {
    background: #00bcd4;
    color: #fff;
  }
}

// 空数据
.no-data {
  text-align: center;
  padding: 120rpx 0;
  font-size: 28rpx;
  color: #999;
}

// 加载中
.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 120rpx 0;
}

// 加载更多
.load-more {
  text-align: center;
  padding: 32rpx 0;
  font-size: 24rpx;
  color: #999;
}

// 没有更多
.no-more {
  text-align: center;
  padding: 32rpx 0;
  font-size: 24rpx;
  color: #999;
}

// 历史列表样式
.history-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.history-details {
  margin-bottom: 0;
}

.detail-row-3col {
  display: flex;
  gap: 32rpx;
  margin-bottom: 0;
}
</style>
