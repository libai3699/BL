<script setup>
import { ref, computed, onUnmounted } from 'vue';
import { onLoad, onShow, onHide } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';
import { useCommonStore } from '@/stores/common';
import { useUserStore } from '@/stores/user';
import { goBack } from '@/utils/navigate';
import { getStockKline } from '@/apis/market';
import { buyStock, getStockDetail } from '@/apis/order';
import { getCurrency } from '@/utils/account';
import { ensureWebSocketConnected } from '@/utils/websocket';
import KLineChart from './components/KLineChart.vue';
import TradeModal from './components/TradeModal.vue';

const { t } = useI18n();

const commonStore = useCommonStore();
const userStore = useUserStore();

// 股票信息
const stockInfo = ref({
  id: 0,
  name: '',
  code: '',
  gid: '',
  nowPrice: 0,
  hcrate: 0,
  high: 0,
  low: 0,
  volume: 0,
  stockType: 'mxg'
});

// 时间周期选项
const timeOptions = computed(() => [
  { label: t('stockDetail.period5m'), value: 'PT5M', interval: 'PT5M' },
  { label: t('stockDetail.period15m'), value: 'PT15M', interval: 'PT15M' },
  { label: t('stockDetail.period1d'), value: 'P1D', interval: 'P1D' },
  { label: t('stockDetail.period1w'), value: 'P1W', interval: 'P1W' },
  { label: t('stockDetail.period1m'), value: 'P1M', interval: 'P1M' }
]);

const currentTimeOption = ref('P1D');
const klineData = ref([]);
const isLoading = ref(false);

// 计算涨跌颜色
const priceColor = computed(() => {
  return stockInfo.value.hcrate >= 0 ? '#00C087' : '#FF4D4F';
});

// 获取货币单位
const currency = computed(() => getCurrency(stockInfo.value.stockType));

// 获取K线数据
const fetchKlineData = async () => {
  if (isLoading.value) return;

  try {
    isLoading.value = true;
    const res = await getStockKline({
      gid: stockInfo.value.gid,
      interval: currentTimeOption.value,
      type: 0
    });

    if (res && res.data && Array.isArray(res.data)) {
      klineData.value = res.data;
    }
  } catch (error) {
    console.error('获取K线数据失败', error);
  } finally {
    isLoading.value = false;
  }
};

// 常用指标选项
const indicators = ref([
  { label: 'MACD', value: 'MACD' },
  { label: 'KDJ', value: 'KDJ' },
  { label: 'RSI', value: 'RSI' },
  { label: 'BOLL', value: 'BOLL' },
  { label: 'Volume', value: 'VOL' }
]);

// 所有指标选项
const allIndicators = computed(() => [
  {
    label: 'MA',
    value: 'MA',
    desc: t('stockDetail.maDesc'),
    category: t('stockDetail.indicatorTrend')
  },
  {
    label: 'EMA',
    value: 'EMA',
    desc: t('stockDetail.emaDesc'),
    category: t('stockDetail.indicatorTrend')
  },
  {
    label: 'BOLL',
    value: 'BOLL',
    desc: t('stockDetail.bollDesc'),
    category: t('stockDetail.indicatorTrend')
  },
  {
    label: 'SAR',
    value: 'SAR',
    desc: t('stockDetail.sarDesc'),
    category: t('stockDetail.indicatorTrend')
  },
  {
    label: 'MACD',
    value: 'MACD',
    desc: t('stockDetail.macdDesc'),
    category: t('stockDetail.indicatorMomentum')
  },
  {
    label: 'KDJ',
    value: 'KDJ',
    desc: t('stockDetail.kdjDesc'),
    category: t('stockDetail.indicatorMomentum')
  },
  {
    label: 'RSI',
    value: 'RSI',
    desc: t('stockDetail.rsiDesc'),
    category: t('stockDetail.indicatorMomentum')
  },
  {
    label: 'CCI',
    value: 'CCI',
    desc: t('stockDetail.cciDesc'),
    category: t('stockDetail.indicatorMomentum')
  },
  {
    label: 'DMI',
    value: 'DMI',
    desc: t('stockDetail.dmiDesc'),
    category: t('stockDetail.indicatorMomentum')
  },
  {
    label: 'WR',
    value: 'WR',
    desc: t('stockDetail.wrDesc'),
    category: t('stockDetail.indicatorMomentum')
  },
  {
    label: 'MTM',
    value: 'MTM',
    desc: t('stockDetail.mtmDesc'),
    category: t('stockDetail.indicatorMomentum')
  },
  {
    label: 'VOL',
    value: 'VOL',
    desc: t('stockDetail.volDesc'),
    category: t('stockDetail.indicatorVolume')
  },
  {
    label: 'OBV',
    value: 'OBV',
    desc: t('stockDetail.obvDesc'),
    category: t('stockDetail.indicatorVolume')
  },
  {
    label: 'VR',
    value: 'VR',
    desc: t('stockDetail.vrDesc'),
    category: t('stockDetail.indicatorVolume')
  },
  {
    label: 'BIAS',
    value: 'BIAS',
    desc: t('stockDetail.biasDesc'),
    category: t('stockDetail.indicatorOther')
  },
  {
    label: 'DMA',
    value: 'DMA',
    desc: t('stockDetail.dmaDesc'),
    category: t('stockDetail.indicatorOther')
  },
  {
    label: 'TRIX',
    value: 'TRIX',
    desc: t('stockDetail.trixDesc'),
    category: t('stockDetail.indicatorOther')
  },
  {
    label: 'PSY',
    value: 'PSY',
    desc: t('stockDetail.psyDesc'),
    category: t('stockDetail.indicatorOther')
  },
  {
    label: 'ROC',
    value: 'ROC',
    desc: t('stockDetail.rocDesc'),
    category: t('stockDetail.indicatorOther')
  }
]);

const currentIndicator = ref('VOL');
const chartRef = ref(null);
const showVolume = ref(true);
const settingsVisible = ref(false);
const moreIndicatorsVisible = ref(false);

// MA均线设置
const maSettings = ref({
  enabled: true,
  periods: [5, 10, 20, 30, 60]
});

// EMA均线设置
const emaSettings = ref({
  enabled: false,
  periods: [12, 26]
});

const tempMaSettings = ref({ ...maSettings.value });
const tempEmaSettings = ref({ ...emaSettings.value });

// 切换时间周期
const switchTimeOption = (option) => {
  if (currentTimeOption.value === option.value) return;
  currentTimeOption.value = option.value;
  fetchKlineData();
};

// 切换指标
const switchIndicator = (indicator) => {
  currentIndicator.value = indicator;
  if (indicator === 'VOL') {
    showVolume.value = true;
  }
  settingsIndicator.value = indicator;
  if (chartRef.value) {
    chartRef.value.updateIndicator?.(indicator);
    chartRef.value.switchIndicator?.(indicator);
  }
};

// 显示更多指标
const showMoreIndicators = () => {
  moreIndicatorsVisible.value = true;
};

// 关闭更多指标弹框
const closeMoreIndicators = () => {
  moreIndicatorsVisible.value = false;
};

// 从更多指标中选择
const selectMoreIndicator = (indicator) => {
  switchIndicator(indicator.value);
  moreIndicatorsVisible.value = false;
};

// 按分类分组指标
const groupedIndicators = computed(() => {
  const groups = {};
  allIndicators.value.forEach((indicator) => {
    if (!groups[indicator.category]) {
      groups[indicator.category] = [];
    }
    groups[indicator.category].push(indicator);
  });
  return groups;
});

// 设置
const handleSettings = () => {
  if (settingsVisible.value) {
    settingsVisible.value = false;
    return;
  }
  tempMaSettings.value = { ...maSettings.value, periods: [...maSettings.value.periods] };
  tempEmaSettings.value = {
    ...emaSettings.value,
    periods: [...emaSettings.value.periods]
  };
  settingsVisible.value = true;
};

const closeSettings = () => {
  settingsVisible.value = false;
};

const confirmSettings = () => {
  maSettings.value = { ...tempMaSettings.value };
  emaSettings.value = { ...tempEmaSettings.value };

  // 应用均线设置到图表
  if (chartRef.value) {
    // 这里可以调用图表的方法来更新均线配置
  }

  settingsVisible.value = false;
};

// 添加/删除MA周期
const addMaPeriod = () => {
  const newPeriod = 5;
  if (!tempMaSettings.value.periods.includes(newPeriod)) {
    tempMaSettings.value.periods.push(newPeriod);
    tempMaSettings.value.periods.sort((a, b) => a - b);
  }
};

const removeMaPeriod = (index) => {
  tempMaSettings.value.periods.splice(index, 1);
};

const updateMaPeriod = (index, value) => {
  const num = parseInt(value);
  if (!isNaN(num) && num > 0) {
    tempMaSettings.value.periods[index] = num;
  }
};

// 添加/删除EMA周期
const addEmaPeriod = () => {
  const newPeriod = 12;
  if (!tempEmaSettings.value.periods.includes(newPeriod)) {
    tempEmaSettings.value.periods.push(newPeriod);
    tempEmaSettings.value.periods.sort((a, b) => a - b);
  }
};

const removeEmaPeriod = (index) => {
  tempEmaSettings.value.periods.splice(index, 1);
};

const updateEmaPeriod = (index, value) => {
  const num = parseInt(value);
  if (!isNaN(num) && num > 0) {
    tempEmaSettings.value.periods[index] = num;
  }
};

// 交易弹框
const tradeModalVisible = ref(false);
const tradeType = ref('buy'); // 'buy' or 'sell'

// 买入
const handleBuy = () => {
  tradeType.value = 'buy';
  tradeModalVisible.value = true;
};

// 卖出
const handleSell = () => {
  tradeType.value = 'sell';
  tradeModalVisible.value = true;
};

// 确认交易
const handleTradeConfirm = async (data) => {
  const res = await buyStock(data);

  if (res && res.status == 0) {
    uni.showToast({
      title: tradeType.value === 'buy' ? t('trade.buySuccess') : t('trade.sellSuccess'),
      icon: 'success'
    });

    tradeModalVisible.value = false;

    // 跳转到订单页面
    setTimeout(() => {
      uni.switchTab({
        url: '/pages/orders/index'
      });
    }, 1500);
  } else {
    uni.showToast({
      title: res.msg || t('error.system'),
      icon: 'none'
    });
  }
};

// WebSocket消息处理
const handleStockMessage = (data) => {
  const stockId = data.pid || data.gid || data.code;
  if (stockId === stockInfo.value.code || stockId === stockInfo.value.gid) {
    const newPrice = data.last_numeric || stockInfo.value.nowPrice;
    stockInfo.value.nowPrice = newPrice;
    stockInfo.value.hcrate = data.pcp ? parseFloat(data.pcp) : stockInfo.value.hcrate;

    // 更新K线图的实时价格
    if (chartRef.value && newPrice) {
      chartRef.value.updateRealtimeData?.({
        close: parseFloat(newPrice),
        timestamp: Date.now()
      });
    }
  }
};

// 获取股票详情
const fetchStockDetail = async (gid) => {
  const res = await getStockDetail({ gid: gid });
  if (res && res.gid) {
    stockInfo.value = {
      id: res.id || 0,
      name: res.name || res.stockName || '',
      code: res.code || res.stockCode || '',
      gid: res.gid || gid,
      nowPrice: res.nowPrice || res.price || 0,
      hcrate: res.hcrate || res.changeRate || 0,
      high: res.high || res.highPrice || 0,
      low: res.low || res.lowPrice || 0,
      volume: res.volume || res.tradeVolume || 0,
      stockType: res.stockType || 'mxg'
    };
    fetchKlineData();
  }
};

onLoad((options) => {
  if (options.gid) {
    fetchStockDetail(options.gid);
  }
});

onShow(() => {
  commonStore.fnBack = goBack;

  // 确保 WebSocket 已连接
  ensureWebSocketConnected(userStore, 300);

  // 监听WebSocket
  const eventName = stockInfo.value.stockType == 'mxg' ? 'ws:mexico' : 'ws:us';
  uni.$on(eventName, handleStockMessage);
});

// 页面隐藏时移除监听
onHide(() => {
  uni.$off('ws:mexico', handleStockMessage);
  uni.$off('ws:us', handleStockMessage);
});

// 组件卸载时也清理
onUnmounted(() => {
  uni.$off('ws:mexico', handleStockMessage);
  uni.$off('ws:us', handleStockMessage);
});
</script>

<template>
  <page-wrapper>
    <view class="stock-detail">
      <!-- 顶部导航 -->
      <nav-bar :title="`${stockInfo.name} `" />

      <view class="detail-container">
        <!-- 报价区域 -->
        <view class="quote-section">
          <!-- 报价标签 -->
          <view class="quote-tab">
            <text class="tab-text">{{ $t('stockDetail.quote') }}</text>
            <view class="tab-line"></view>
          </view>

          <!-- 价格和统计信息在一行 -->
          <view class="price-stats-row">
            <view class="price-left">
              <view class="current-price" :style="{ color: priceColor }">
                <c-amount :value="stockInfo.nowPrice" />
              </view>
              <text class="currency">{{ currency }}</text>
            </view>
            <view class="stats-right">
              <view class="stat-item">
                <text class="stat-label">{{ $t('stockDetail.high') }}</text>
                <view class="stat-value"><c-amount :value="stockInfo.high" /></view>
              </view>
              <view class="stat-item">
                <text class="stat-label">{{ $t('stockDetail.low') }}</text>
                <view class="stat-value"><c-amount :value="stockInfo.low" /></view>
              </view>
            </view>
          </view>

          <!-- 涨跌幅 -->
          <view class="price-change-row">
            <text class="price-change" :style="{ color: priceColor }">
              {{ stockInfo.hcrate >= 0 ? '+' : ''
              }}{{ Number(stockInfo.hcrate || 0).toFixed(2) }}%
            </text>
          </view>
        </view>

        <!-- 时间周期选择 -->
        <view class="time-period-section">
          <view class="time-options">
            <view
              class="time-option"
              v-for="option in timeOptions"
              :key="option.value"
              :class="{ active: currentTimeOption === option.value }"
              @click="switchTimeOption(option)"
            >
              {{ option.label }}
            </view>
          </view>
        </view>

        <!-- K线图区域 -->
        <view class="chart-section">
          <!-- Loading -->
          <view class="chart-loading" v-if="isLoading">
            <u-loading-icon mode="circle" size="40"></u-loading-icon>
          </view>

          <!-- K线图 -->
          <view class="chart-wrapper" v-else-if="klineData.length > 0">
            <k-line-chart
              ref="chartRef"
              :data="klineData"
              :stock-info="stockInfo"
              :period="currentTimeOption"
              :indicator="currentIndicator"
              :show-volume="showVolume"
            />
          </view>

          <!-- 空状态 -->
          <view class="chart-empty" v-else>
            <text class="empty-text">{{ $t('common.noData') }}</text>
          </view>

          <!-- 均线设置弹框 -->
          <view class="settings-modal" v-if="settingsVisible" @click="closeSettings">
            <view class="settings-modal-content" @click.stop>
              <view class="settings-modal-header">
                <text class="settings-modal-title">{{
                  $t('stockDetail.maSettings')
                }}</text>
                <text class="settings-modal-close" @click="closeSettings">×</text>
              </view>

              <scroll-view class="settings-modal-body" scroll-y>
                <!-- MA均线设置 -->
                <view class="settings-section">
                  <view class="settings-section-header">
                    <text class="settings-section-title">{{
                      $t('stockDetail.maLine')
                    }}</text>
                    <view
                      class="settings-toggle-small"
                      :class="{ active: tempMaSettings.enabled }"
                      @click="tempMaSettings.enabled = !tempMaSettings.enabled"
                    >
                      {{
                        tempMaSettings.enabled
                          ? $t('stockDetail.enabled')
                          : $t('stockDetail.disabled')
                      }}
                    </view>
                  </view>

                  <view class="settings-periods" v-if="tempMaSettings.enabled">
                    <view
                      class="period-item"
                      v-for="(period, index) in tempMaSettings.periods"
                      :key="index"
                    >
                      <input
                        class="period-input"
                        type="number"
                        :value="period"
                        @input="updateMaPeriod(index, $event.detail.value)"
                      />
                      <text
                        class="period-remove"
                        @click="removeMaPeriod(index)"
                        v-if="tempMaSettings.periods.length > 1"
                        >×</text
                      >
                    </view>
                    <view class="period-add" @click="addMaPeriod">{{
                      $t('stockDetail.addPeriod')
                    }}</view>
                  </view>
                </view>

                <!-- EMA均线设置 -->
                <view class="settings-section">
                  <view class="settings-section-header">
                    <text class="settings-section-title">{{
                      $t('stockDetail.emaLine')
                    }}</text>
                    <view
                      class="settings-toggle-small"
                      :class="{ active: tempEmaSettings.enabled }"
                      @click="tempEmaSettings.enabled = !tempEmaSettings.enabled"
                    >
                      {{
                        tempEmaSettings.enabled
                          ? $t('stockDetail.enabled')
                          : $t('stockDetail.disabled')
                      }}
                    </view>
                  </view>

                  <view class="settings-periods" v-if="tempEmaSettings.enabled">
                    <view
                      class="period-item"
                      v-for="(period, index) in tempEmaSettings.periods"
                      :key="index"
                    >
                      <input
                        class="period-input"
                        type="number"
                        :value="period"
                        @input="updateEmaPeriod(index, $event.detail.value)"
                      />
                      <text
                        class="period-remove"
                        @click="removeEmaPeriod(index)"
                        v-if="tempEmaSettings.periods.length > 1"
                        >×</text
                      >
                    </view>
                    <view class="period-add" @click="addEmaPeriod">{{
                      $t('stockDetail.addPeriod')
                    }}</view>
                  </view>
                </view>
              </scroll-view>

              <view class="settings-modal-footer">
                <view class="settings-modal-action cancel" @click="closeSettings">{{
                  $t('stockDetail.cancel')
                }}</view>
                <view class="settings-modal-action confirm" @click="confirmSettings"
                  >{{ $t('stockDetail.confirm') }}
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 指标选择 -->
        <view class="indicator-section">
          <view class="indicator-tabs">
            <text
              class="indicator-tab"
              v-for="indicator in indicators"
              :key="indicator.value"
              :class="{ active: currentIndicator === indicator.value }"
              @click="switchIndicator(indicator.value)"
            >
              {{ indicator.label }}
            </text>
            <text class="indicator-tab more" @click="showMoreIndicators">
              {{ $t('stockDetail.more') }} ▼
            </text>
            <view v-if="false" class="indicator-settings-btn" @click="handleSettings">
              <text class="settings-icon">⚙</text>
            </view>
          </view>
        </view>

        <!-- 更多指标弹框 -->
        <view
          class="indicator-modal"
          v-if="moreIndicatorsVisible"
          @click="closeMoreIndicators"
        >
          <view class="indicator-modal-content">
            <view class="indicator-modal-header">
              <text class="indicator-modal-title">{{
                $t('stockDetail.selectIndicator')
              }}</text>
              <text class="indicator-modal-close" @click="closeMoreIndicators">×</text>
            </view>

            <scroll-view class="indicator-modal-body" scroll-y>
              <view
                class="indicator-category"
                v-for="(items, category) in groupedIndicators"
                :key="category"
              >
                <text class="category-title">{{ category }}</text>
                <view class="indicator-list">
                  <view
                    class="indicator-item"
                    v-for="indicator in items"
                    :key="indicator.value"
                    :class="{ active: currentIndicator === indicator.value }"
                    @click="selectMoreIndicator(indicator)"
                  >
                    <view class="indicator-item-main">
                      <text class="indicator-item-label">{{ indicator.label }}</text>
                      <text class="indicator-item-desc">{{ indicator.desc }}</text>
                    </view>
                    <text
                      class="indicator-item-check"
                      v-if="currentIndicator === indicator.value"
                      >✓</text
                    >
                  </view>
                </view>
              </view>
            </scroll-view>
          </view>
        </view>

        <!-- 底部操作按钮 -->
        <view class="action-buttons">
          <view class="action-buttons-inner">
            <view class="action-btn buy-btn" @click="handleBuy">
              {{ $t('stockDetail.buy') }}
            </view>
            <view class="action-btn sell-btn" @click="handleSell">
              {{ $t('stockDetail.sell') }}
            </view>
          </view>
        </view>
      </view>

      <!-- 交易弹框 -->
      <trade-modal
        v-model:visible="tradeModalVisible"
        :type="tradeType"
        :stock-info="stockInfo"
        @confirm="handleTradeConfirm"
      />
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.stock-detail {
  @include hasNavBar();
  background: #f8f8f8;
}

.detail-container {
  padding: 24rpx 24rpx 200rpx;
}

// 报价区域
.quote-section {
  background: #fff;
  padding: 32rpx;
  margin-bottom: 24rpx;
  border-radius: 16rpx;
}

.quote-tab {
  margin-bottom: 24rpx;
}

.tab-text {
  font-size: 28rpx;
  color: #00bcd4;
  font-weight: 500;
}

.tab-line {
  width: 60rpx;
  height: 4rpx;
  background: #00bcd4;
  margin-top: 8rpx;
}

.price-stats-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.price-left {
  display: flex;
  align-items: baseline;
  gap: 12rpx;
}

.current-price {
  font-size: 64rpx;
  font-weight: bold;
  line-height: 1;
}

.currency {
  font-size: 24rpx;
  color: #666;
  font-weight: 500;
}

.stats-right {
  display: flex;
  gap: 32rpx;
  align-items: flex-start;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  align-items: center;
}

.stat-label {
  font-size: 22rpx;
  color: #999;
}

.stat-value {
  font-size: 26rpx;
  color: #333;
  font-weight: 500;
}

.price-change-row {
  margin-bottom: 0;
}

.price-change {
  font-size: 28rpx;
  font-weight: 500;
}

// 时间周期
.time-period-section {
  background: #fff;
  padding: 24rpx 32rpx;
  margin-bottom: 24rpx;
  border-radius: 16rpx;
}

.time-options {
  display: flex;
  gap: 32rpx;
  align-items: center;
}

.time-option {
  font-size: 26rpx;
  color: #666;
  padding: 8rpx 0;
  position: relative;

  &.active {
    color: #333;
    font-weight: 500;

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 4rpx;
      background: #333;
    }
  }
}

// K线图
.chart-section {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  overflow: hidden;
  position: relative;
}

.chart-wrapper {
  width: 100%;
  height: 800rpx;
  overflow: visible;
}

.chart-loading {
  height: 900rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-empty {
  height: 900rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

// 均线设置弹框
.settings-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  z-index: 1000;
}

.settings-modal-content {
  width: 100%;
  max-height: 70vh;
  background: #fff;
  border-radius: 32rpx 32rpx 0 0;
  padding: 24rpx 32rpx;
  display: flex;
  flex-direction: column;
}

.settings-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.settings-modal-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}

.settings-modal-close {
  font-size: 48rpx;
  color: #999;
  line-height: 1;
}

.settings-modal-body {
  flex: 1;
  overflow-y: auto;
}

.settings-section {
  margin-bottom: 32rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.settings-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.settings-section-title {
  font-size: 28rpx;
  font-weight: 500;
  color: #333;
}

.settings-toggle-small {
  padding: 8rpx 24rpx;
  background: #f5f5f5;
  border-radius: 20rpx;
  font-size: 24rpx;
  color: #666;

  &.active {
    background: #00bcd4;
    color: #fff;
  }
}

.settings-periods {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.period-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 16rpx;
  background: #f8f8f8;
  border-radius: 12rpx;
}

.period-input {
  width: 80rpx;
  height: 48rpx;
  text-align: center;
  font-size: 26rpx;
  color: #333;
  background: #fff;
  border: 1rpx solid #e0e0e0;
  border-radius: 8rpx;
}

.period-remove {
  font-size: 32rpx;
  color: #ff4d4f;
  line-height: 1;
  padding: 0 8rpx;
}

.period-add {
  padding: 12rpx 24rpx;
  background: #f0f0f0;
  border-radius: 12rpx;
  font-size: 24rpx;
  color: #666;
  border: 2rpx dashed #d0d0d0;
}

.settings-modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
  padding: 24rpx 32rpx;
  border-top: 1rpx solid #f0f0f0;
}

.settings-modal-action {
  padding: 16rpx 48rpx;
  border-radius: 12rpx;
  font-size: 28rpx;

  &.cancel {
    background: #f0f0f0;
    color: #666;
  }

  &.confirm {
    background: #00bcd4;
    color: #fff;
  }
}

// 指标选择
.indicator-section {
  background: #fff;
  padding: 24rpx 32rpx;
  margin-bottom: 24rpx;
  border-radius: 16rpx;
}

.indicator-tabs {
  display: flex;
  gap: 32rpx;
  align-items: center;
  overflow-x: auto;

  &::-webkit-scrollbar {
    display: none;
  }
}

.indicator-settings-btn {
  margin-left: auto;
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 50%;
  flex-shrink: 0;
}

.settings-icon {
  font-size: 28rpx;
  color: #666;
}

.indicator-tab {
  font-size: 26rpx;
  color: #666;
  white-space: nowrap;
  padding: 8rpx 0;

  &.active {
    color: #333;
    font-weight: 500;
    border-bottom: 4rpx solid #333;
  }

  &.more {
    color: #999;
  }
}

// 更多指标弹框
.indicator-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  z-index: 1000;
}

.indicator-modal-content {
  width: 100%;
  max-height: 80vh;
  background: #fff;
  border-radius: 32rpx 32rpx 0 0;
  padding: 24rpx 32rpx 48rpx 32rpx;
  display: flex;
  flex-direction: column;
}

.indicator-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.indicator-modal-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}

.indicator-modal-close {
  font-size: 48rpx;
  color: #999;
  line-height: 1;
}

.indicator-modal-body {
  flex: 1;
  overflow-y: auto;
}

.indicator-category {
  margin-bottom: 32rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.category-title {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 16rpx;
  display: block;
}

.indicator-list {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.indicator-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 20rpx;
  background: #f8f8f8;
  border-radius: 12rpx;

  &.active {
    background: #e6f7ff;
    border: 2rpx solid #00bcd4;
  }
}

.indicator-item-main {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.indicator-item-label {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}

.indicator-item-desc {
  font-size: 24rpx;
  color: #999;
}

.indicator-item-check {
  font-size: 32rpx;
  color: #00bcd4;
  font-weight: bold;
}

// 操作按钮
.action-buttons {
  position: fixed;
  bottom: 48rpx;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  z-index: 100;
  padding: 0 32rpx;
}

.action-buttons-inner {
  width: 100%;
  max-width: 750rpx;
  display: flex;
  gap: 24rpx;
}

.action-btn {
  flex: 1;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16rpx;
  font-size: 32rpx;
  font-weight: bold;
  color: #fff;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.15);
}

.buy-btn {
  background: #00c087;
}

.sell-btn {
  background: #ff4d4f;
}
</style>
