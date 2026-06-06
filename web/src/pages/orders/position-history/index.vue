<script setup>
import { ref } from 'vue';
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';
import { useCommonStore } from '@/stores/common';
import { goBack, navigateTo } from '@/utils/navigate';
import { getPositionList } from '@/apis/order';

const { t } = useI18n();
const commonStore = useCommonStore();

// 持仓历史列表
const historyList = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const hasMore = ref(true);
const loading = ref(false);
const refreshing = ref(false);

// 获取持仓历史列表
const fetchHistoryList = async (loadMore = false) => {
  if (loading.value) return;

  try {
    loading.value = true;
    const currentPage = loadMore ? pageNum.value + 1 : 1;

    const res = await getPositionList({
      pageNum: currentPage,
      pageSize: pageSize.value,
      state: 1 // 1表示已平仓（历史）
    });

    const list = res?.list || [];
    const formattedList = list.map((item) => {
      const currentPrice = item.now_price || item.buyOrderPrice || 0;
      const buyPrice = item.buyOrderPrice || 0;
      const quantity = item.orderNum || 0;
      const orderDirection = item.orderDirection === 0 ? 'buy' : 'sell';

      const currentValue = currentPrice * quantity;
      const buyTotalPrice = item.buyTotalPrice || 0;
      const profitAndLose = item.profitAndLose || 0;
      const profitRate =
        buyTotalPrice > 0
          ? ((profitAndLose / buyTotalPrice) * 100).toFixed(2) + '%'
          : '--';

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
        profitRate,
        profitAndLose,
        holdValue: buyTotalPrice,
        buyPrice,
        currentPrice,
        currentValue,
        quantity,
        currency: item.currency || 'MXN',
        profitTarget: item.profitTargetPrice,
        stopTarget: item.stopTargetPrice,
        gid: item.gid || item.stockCode,
        buyOrderTime: item.buyOrderTime,
        sellTime: item.sellTime
      };
    });

    if (loadMore) {
      historyList.value = [...historyList.value, ...formattedList];
    } else {
      historyList.value = formattedList;
    }
    pageNum.value = currentPage;
    hasMore.value = Boolean(res?.hasNextPage);
  } catch (error) {
    console.error('获取持仓历史失败', error);
    if (!loadMore) {
      historyList.value = [];
    }
    hasMore.value = false;
    uni.showToast({
      title: error?.errorDesc || error?.msg || t('error.system'),
      icon: 'none'
    });
  } finally {
    loading.value = false;
    refreshing.value = false;
  }
};

// 下拉刷新
const onRefresh = async () => {
  refreshing.value = true;
  pageNum.value = 1;
  hasMore.value = true;
  await fetchHistoryList();
};

// 触底加载
const onLoadMore = () => {
  if (hasMore.value && !loading.value) {
    fetchHistoryList(true);
  }
};

// 点击历史项
const handleHistoryClick = (item) => {
  navigateTo(`/pages/orders/fpo-detail?type=position&positionSn=${item.positionSn}`);
};

onShow(() => {
  commonStore.fnBack = goBack;
  fetchHistoryList();
});

onPullDownRefresh(async () => {
  await onRefresh();
  uni.stopPullDownRefresh();
});

onReachBottom(() => {
  onLoadMore();
});
</script>

<template>
  <page-wrapper>
    <view class="position-history-page">
      <nav-bar :title="$t('order.positionHistory')" />

      <view class="page-content">
        <scroll-view
          class="scroll-view"
          scroll-y
          @scrolltolower="onLoadMore"
          refresher-enabled
          :refresher-triggered="refreshing"
          @refresherrefresh="onRefresh"
        >
          <view class="history-list" v-if="historyList.length > 0">
            <view
              class="history-item"
              v-for="item in historyList"
              :key="item.id"
              @click="handleHistoryClick(item)"
            >
              <view class="history-header">
                <view class="stock-info">
                  <text class="stock-code">{{ item.stockName }}</text>
                </view>
                <text class="arrow">›</text>
              </view>
              <view class="stock-name">{{ item.stockSpell }}</view>

              <view class="stock-tags">
                <view class="stock-tag">{{ item.stockPlate }}</view>
                <view class="stock-tag-type" :class="item.stockType">
                  {{ $t(`order.${item.stockType}`) }}
                </view>
              </view>

              <view class="history-details">
                <view class="detail-row">
                  <view class="detail-col">
                    <text class="label">{{ $t('order.holdValue') }}</text>
                    <text class="value"
                      >{{
                        item.holdValue.toLocaleString('en-US', {
                          minimumFractionDigits: 2,
                          maximumFractionDigits: 2
                        })
                      }}
                      {{ item.currency }}</text
                    >
                  </view>
                  <view class="detail-col">
                    <text class="label">{{ $t('order.profitLoss') }}</text>
                    <text
                      class="value"
                      :style="{ color: item.profitAndLose >= 0 ? '#009f6a' : '#ff4d4f' }"
                    >
                      {{ item.profitAndLose >= 0 ? '+' : ''
                      }}{{
                        item.profitAndLose.toLocaleString('en-US', {
                          minimumFractionDigits: 2,
                          maximumFractionDigits: 2
                        })
                      }}
                      {{ item.currency }}
                    </text>
                  </view>
                  <view class="detail-col">
                    <text class="label">{{ $t('order.profitRate') }}</text>
                    <text
                      class="value"
                      :style="{ color: item.profitAndLose >= 0 ? '#009f6a' : '#ff4d4f' }"
                    >
                      {{ item.profitRate }}
                    </text>
                  </view>
                </view>
                <view class="detail-row">
                  <view class="detail-col">
                    <text class="label">{{ $t('advisor.buyTime') }}</text>
                    <text class="value">{{ item.buyOrderTime || '--' }}</text>
                  </view>
                  <view class="detail-col">
                    <text class="label">{{ $t('order.sellTime') }}</text>
                    <text class="value">{{ item.sellTime || '--' }}</text>
                  </view>
                </view>
              </view>
            </view>
          </view>

          <!-- 空数据 -->
          <no-data v-else-if="!loading" />

          <!-- 加载中 -->
          <view class="loading" v-if="loading && historyList.length === 0">
            <u-loading-icon mode="circle" size="40"></u-loading-icon>
          </view>

          <!-- 加载更多 -->
          <view class="load-more" v-if="historyList.length > 0 && hasMore && loading">
            <u-loading-icon mode="circle" size="30"></u-loading-icon>
            <text>{{ $t('common.loading') }}</text>
          </view>

          <!-- 没有更多 -->
          <view class="no-more" v-if="historyList.length > 0 && !hasMore">
            <text>{{ $t('common.noMore') }}</text>
          </view>
        </scroll-view>
      </view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.position-history-page {
  @include hasNavBar();
  background: #f8f8f8;
}

.page-content {
  height: calc(100vh - 88rpx);
}

.scroll-view {
  height: 100%;
}

.history-list {
  padding: 24rpx;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

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

.stock-info {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.stock-code {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.arrow {
  font-size: 32rpx;
  color: #a4a4a4;
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
  padding: 8rpx 16rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  font-size: 22rpx;
  color: #666;
}

.stock-tag-type {
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;

  &.buy {
    background: rgba(0, 159, 106, 0.1);
    color: #009f6a;
  }

  &.sell {
    background: rgba(255, 77, 79, 0.1);
    color: #ff4d4f;
  }
}

.history-details {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.detail-row {
  display: flex;
  gap: 24rpx;
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
    font-weight: 500;

    &.profit {
      color: #009f6a;
    }

    &.loss {
      color: #ff4d4f;
    }
  }
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 120rpx 0;
}

.load-more {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16rpx;
  padding: 32rpx 0;
  font-size: 24rpx;
  color: #999;
}

.no-more {
  text-align: center;
  padding: 32rpx 0;
  font-size: 24rpx;
  color: #999;
}
</style>
