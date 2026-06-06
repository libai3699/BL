<script setup>
import { ref, computed, watch, onUnmounted } from 'vue';
import { onShow, onHide, onReachBottom } from '@dcloudio/uni-app';
import { useCommonStore } from '@/stores/common';
import { useUserStore } from '@/stores/user';
import { goBack } from '@/utils/navigate';
import { getExchangeList, getStockList, getMarket, getStockKline } from '@/apis/market';
import { ensureWebSocketConnected } from '@/utils/websocket';
import StockItem from '../home/components/stock-item.vue';
import MiniChart from './components/MiniChart.vue';

const commonStore = useCommonStore();
const userStore = useUserStore();

// 当前国家类型：默认秘鲁（与 tab 顺序一致）
const currentCountryType = ref('pe'); // pe / us / mxg
// 交易所数据
const exchangeData = ref([]);
// 当前选中的交易所
const currentExchange = ref(null);
// 搜索关键词
const searchKeyword = ref('');
// 搜索防抖定时器
let searchTimer = null;
// 股票列表
const stockList = ref([]);
const currentPage = ref(1);
const pageSize = ref(20);
const hasMore = ref(true);
const isLoading = ref(false);
// 大盘指数列表
const marketList = ref([]);
// K线数据
const klineData = ref({});

// 获取当前国家数据
const getCurrentCountry = computed(() => {
  return exchangeData.value.find((item) => item.stockType === currentCountryType.value);
});

// 获取当前国家的交易所列表
const getCurrentExchanges = computed(() => {
  return getCurrentCountry.value?.exchanges || [];
});

// 获取交易所列表
const fetchExchangeList = async () => {
  const res = await getExchangeList();
  if (res && Array.isArray(res)) {
    exchangeData.value = res;
    // 只在首次加载时设置默认交易所（currentExchange为null时）
    if (!currentExchange.value) {
      const currentCountry = res.find(
        (item) => item.stockType === currentCountryType.value
      );
      if (
        currentCountry &&
        currentCountry.exchanges &&
        currentCountry.exchanges.length > 0
      ) {
        currentExchange.value = currentCountry.exchanges[0];
      }
    }
  }
};

// 获取股票列表
const fetchStockList = async (loadMore = false) => {
  if (isLoading.value) return;

  isLoading.value = true;
  const pageNum = loadMore ? currentPage.value + 1 : 1;
  const res = await getStockList({
    keyWords: searchKeyword.value,
    pageNum,
    pageSize: pageSize.value,
    stockPlate: currentExchange.value?.code || '',
    stockType: currentCountryType.value
  });

  if (res && res.list) {
    if (loadMore) {
      stockList.value = [...stockList.value, ...res.list];
    } else {
      stockList.value = res.list;
    }
    currentPage.value = pageNum;
    hasMore.value = res.hasNextPage;
  }
  isLoading.value = false;
};

// 获取大盘指数
const fetchMarket = async () => {
  const res = await getMarket();
  if (res && res.data && Array.isArray(res.data)) {
    marketList.value = res.data;
    // 获取每个指数的K线数据
    res.data.forEach((market) => {
      fetchKlineData(market.gid);
    });
  }
};

// 获取K线数据
const fetchKlineData = async (gid) => {
  const res = await getStockKline({
    gid,
    interval: 'PT5M',
    type: -1
  });
  if (res && res.data && Array.isArray(res.data)) {
    klineData.value[gid] = res.data;
  }
};

// 切换国家
const switchCountry = (type) => {
  if (currentCountryType.value === type) return;
  currentCountryType.value = type;

  // 切换时选中对应国家的第一个交易所
  const country = exchangeData.value.find((item) => item.stockType === type);
  if (country && country.exchanges && country.exchanges.length > 0) {
    currentExchange.value = country.exchanges[0];
  }

  // 重新加载股票列表
  currentPage.value = 1;
  fetchStockList();

  // 重新监听WebSocket
  listenCurrentTab();
};

// 切换交易所
const switchExchange = (exchange) => {
  if (currentExchange.value?.code === exchange.code) return;
  currentExchange.value = exchange;

  // 重新加载股票列表
  currentPage.value = 1;
  fetchStockList();
};

// 搜索防抖
watch(searchKeyword, () => {
  if (searchTimer) {
    clearTimeout(searchTimer);
  }
  searchTimer = setTimeout(() => {
    currentPage.value = 1;
    fetchStockList();
  }, 500);
});

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  fetchStockList();
};

// 加载更多
const loadMore = () => {
  if (hasMore.value) {
    fetchStockList(true);
  }
};

// 股票项点击
const handleStockClick = (item) => {
  // 只传gid到股票详情页
  uni.navigateTo({
    url: `/pages/stock-detail/index?gid=${item.gid}`
  });
};

const handleStockMessage = (data) => {
  const stockId = data.pid || data.gid;
  if (!stockId) return;

  const index = stockList.value.findIndex((item) => item.code === stockId);
  if (index !== -1) {
    const stock = stockList.value[index];
    const newPrice = data.last_numeric || stock.nowPrice;
    const newRate = data.pcp ? parseFloat(data.pcp) : stock.hcrate;
    if (newPrice !== stock.nowPrice || newRate !== stock.hcrate) {
      stockList.value[index] = {
        ...stock,
        nowPrice: newPrice,
        hcrate: newRate
      };
    }
  }
};

const listenCurrentTab = () => {
  uni.$off('ws:mexico', handleStockMessage);
  uni.$off('ws:us', handleStockMessage);
  uni.$off('ws:peru', handleStockMessage);

  let eventName;
  if (currentCountryType.value === 'mxg') eventName = 'ws:mexico';
  else if (currentCountryType.value === 'pe') eventName = 'ws:peru';
  else eventName = 'ws:us';
  uni.$on(eventName, handleStockMessage);
};

// 页面初始化
const onPageInit = async () => {
  await fetchExchangeList();
  await fetchStockList();
  await fetchMarket();
};

onShow(() => {
  commonStore.fnBack = goBack;

  // 确保 WebSocket 已连接
  ensureWebSocketConnected(userStore, 300);

  // 只在首次加载时初始化数据（exchangeData为空时）
  if (exchangeData.value.length === 0) {
    onPageInit();
  }

  listenCurrentTab();
});

// 页面隐藏时移除监听
onHide(() => {
  uni.$off('ws:mexico', handleStockMessage);
  uni.$off('ws:us', handleStockMessage);
  uni.$off('ws:peru', handleStockMessage);
});

// 组件卸载时也清理
onUnmounted(() => {
  uni.$off('ws:mexico', handleStockMessage);
  uni.$off('ws:peru', handleStockMessage);
  uni.$off('ws:us', handleStockMessage);
});

// 触底加载
onReachBottom(() => {
  loadMore();
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="m-content p-stock-market">
      <view class="market-container">
        <!-- 国家切换 Tab：顺序 秘鲁 / 美国 / 墨西哥 -->
        <view class="country-tabs">
          <view
            class="country-tab"
            :class="{ active: currentCountryType === 'pe' }"
            @click="switchCountry('pe')"
          >
            {{ exchangeData.find((item) => item.stockType === 'pe')?.countryName || '' }}
          </view>
          <view
            class="country-tab"
            :class="{ active: currentCountryType === 'us' }"
            @click="switchCountry('us')"
          >
            {{ exchangeData.find((item) => item.stockType === 'us')?.countryName || '' }}
          </view>
          <view
            class="country-tab"
            :class="{ active: currentCountryType === 'mxg' }"
            @click="switchCountry('mxg')"
          >
            {{ exchangeData.find((item) => item.stockType === 'mxg')?.countryName || '' }}
          </view>
        </view>

        <!-- 搜索框 -->
        <c-search
          class="u-search-wrap"
          v-model="searchKeyword"
          :placeholder="$t('market.searchPlaceholder')"
          @confirm="handleSearch"
        />

        <!-- 大盘指数 -->
        <view class="market-section" v-if="marketList.length > 0">
          <scroll-view class="market-scroll" scroll-x :show-scrollbar="false">
            <view class="market-list">
              <view class="market-item" v-for="market in marketList" :key="market.gid">
                <view class="market-header">
                  <image
                    v-if="market.flag == 'us'"
                    class="market-flag"
                    src="/static/images/series/default/market/usPic.png"
                    mode="aspectFit"
                  />
                  <image
                    v-if="market.flag == 'mxg'"
                    class="market-flag"
                    src="/static/images/series/default/market/mxgPic.png"
                    mode="aspectFit"
                  />
                  <image
                    v-if="market.flag == 'pe'"
                    class="market-flag"
                    src="/static/images/series/default/market/mxgPic.png"
                    mode="aspectFit"
                  />
                  <text class="market-name">{{ market.name }}</text>
                </view>
                <view class="market-price">{{ market.last }}</view>
                <view
                  class="market-change"
                  :class="{ up: market.chg >= 0, down: market.chg < 0 }"
                >
                  {{ market.chg >= 0 ? '+' : '' }}{{ market.chgPct }}%
                </view>
                <!-- K线图 -->
                <view
                  class="market-chart"
                  v-if="klineData[market.gid] && klineData[market.gid].length > 0"
                >
                  <MiniChart
                    :data="klineData[market.gid]"
                    :width="200"
                    :height="60"
                    :color="market.chg >= 0 ? '#00C087' : '#FF4D4F'"
                  />
                </view>
              </view>
            </view>
          </scroll-view>
        </view>

        <!-- 交易所切换 Tab -->
        <view class="exchange-tabs" v-if="getCurrentExchanges.length > 0">
          <view
            class="exchange-tab"
            v-for="exchange in getCurrentExchanges"
            :key="exchange.code"
            :class="{ active: currentExchange?.code === exchange.code }"
            @click="switchExchange(exchange)"
          >
            {{ exchange.code }}
          </view>
        </view>

        <!-- Loading -->
        <view class="stock-loading" v-if="isLoading && stockList.length === 0">
          <u-loading-icon mode="circle" size="40"></u-loading-icon>
        </view>

        <!-- 股票列表 -->
        <view class="m-stock-list" v-else-if="stockList.length > 0">
          <stock-item
            v-for="item in stockList"
            :key="item.gid"
            :item="item"
            :stock-type="currentCountryType"
            @click="handleStockClick(item)"
          />
        </view>

        <!-- 空数据提示 -->
        <view class="no-data" v-else>
          <text>{{ $t('market.noData') }}</text>
        </view>

        <!-- 加载状态 -->
        <view class="loading-more" v-if="isLoading && stockList.length > 0">
          <u-loading-icon mode="circle" size="30"></u-loading-icon>
          <text>{{ $t('common.loading') }}</text>
        </view>
        <view class="no-more" v-else-if="!hasMore && stockList.length > 0">
          {{ $t('common.noMore') }}
        </view>
      </view>

      <!-- 底部导航 -->
      <tab-bar :page="$page" />
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.m-content {
  @include hasBottomNav();
}

.p-stock-market {
  min-height: 100vh;
  background: var(--stock-page-bg, #e9edf5);
}

.market-container {
  padding: 0 0 32rpx;
}

.country-tabs {
  display: flex;
  background: #fff;
  padding: 0 32rpx;
  border-bottom: 2rpx solid var(--stock-line, #e5e9f2);
}

.country-tab {
  padding: 32rpx 32rpx 32rpx 0;
  font-size: 32rpx;
  color: var(--stock-ink-3, #64748b);
  position: relative;
  transition: all 0.3s;

  &.active {
    color: var(--stock-navy, #0b1e47);
    font-weight: 700;

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      right: 32rpx;
      height: 6rpx;
      background: var(--stock-gold, #d4a24c);
      border-radius: 3rpx;
    }
  }
}

// 大盘指数
.market-section {
  background: #fff;
  padding: 24rpx 0;
  margin-bottom: 16rpx;

  // 强制隐藏所有滚动条
  * {
    scrollbar-width: none;
    /* Firefox */
    -ms-overflow-style: none;
    /* IE 10+ */

    &::-webkit-scrollbar {
      display: none !important;
      width: 0 !important;
      height: 0 !important;
    }
  }
}

.market-scroll {
  white-space: nowrap;
  scrollbar-width: none !important;
  /* Firefox */
  -ms-overflow-style: none !important;
  /* IE 10+ */

  &::-webkit-scrollbar {
    display: none !important;
    width: 0 !important;
    height: 0 !important;
  }

  // 深度选择器隐藏内部滚动条
  ::v-deep * {
    scrollbar-width: none !important;
    -ms-overflow-style: none !important;

    &::-webkit-scrollbar {
      display: none !important;
      width: 0 !important;
      height: 0 !important;
    }
  }
}

.market-list {
  display: inline-flex;
  gap: 24rpx;
  padding: 0 32rpx;
}

.market-item {
  display: inline-block;
  width: 200rpx;
  min-height: 200rpx;
  padding: 24rpx;
  background: #fff;
  border: 2rpx solid var(--stock-line, #e5e9f2);
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 28rpx rgba(15, 30, 71, 0.06);
}

.market-header {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 12rpx;
}

.market-flag {
  width: 32rpx;
  height: 32rpx;
  border-radius: 50%;
}

.market-name {
  font-size: 24rpx;
  color: #666;
}

.market-price {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}

.market-change {
  font-size: 24rpx;
  margin-bottom: 16rpx;

  &.up {
    color: var(--stock-up, #10b981);
  }

  &.down {
    color: var(--stock-down, #ef4444);
  }
}

.market-chart {
  height: 60rpx;
  margin-top: 8rpx;
}

// 交易所切换 Tab
.exchange-tabs {
  display: flex;
  background: #fff;
  border-bottom: 2rpx solid #f0f0f0;
  padding: 0 32rpx;
}

.exchange-tab {
  padding: 24rpx 32rpx 24rpx 0;
  font-size: 28rpx;
  color: #666;
  position: relative;
  transition: all 0.3s;

  &.active {
    color: var(--stock-navy, #0b1e47);
    font-weight: 600;

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      width: 60rpx;
      height: 4rpx;
      background: var(--stock-gold, #d4a24c);
      border-radius: 2rpx;
    }
  }
}

.m-stock-list {
  @include stock-card;
  margin: 16rpx 24rpx 0;
  overflow: hidden;
}

// Loading
.stock-loading {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 120rpx 0;
  background: #fff;
}

// 空数据
.no-data {
  text-align: center;
  padding: 120rpx 0;
  font-size: 28rpx;
  color: #999;
  background: #fff;
}

// 股票列表
.stock-list {
  background: #fff;
  padding: 0 32rpx 32rpx;
}

// 加载状态
.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  padding: 32rpx 0;
  font-size: 28rpx;
  color: #999;
}

.no-more {
  text-align: center;
  padding: 32rpx 0;
  font-size: 28rpx;
  color: #999;
}

.u-search-wrap {
  padding: 16rpx 32rpx 24rpx;
}
</style>
