<script setup>
import { ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';

import { getTradingRecords } from '@/apis/advisor';
import { useCommonStore } from '@/stores/common';
import { goBack } from '@/utils/navigate';

const commonStore = useCommonStore();

const mentorId = ref('');
const totalProfit = ref(0);
const tradeList = ref([]);
const loading = ref(false);

const fetchRecords = async () => {
  if (!mentorId.value || loading.value) return;
  loading.value = true;
  const res = await getTradingRecords({
    mentorId: mentorId.value,
    pageNum: 1,
    pageSize: 20
  });
  if (res) {
    totalProfit.value = Number(res.totalProfit || 0);
    tradeList.value = Array.isArray(res.tradeList) ? res.tradeList : [];
  }
  loading.value = false;
};

onLoad((options) => {
  mentorId.value = options?.mentorId || '';
  fetchRecords();
});

onShow(() => {
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper>
    <view class="records-page">
      <nav-bar :title="$t('advisor.tradingRecord')" />

      <view class="records-content">
        <view class="summary-card">
          <text class="label">{{ $t('advisor.last20DaysProfit') }}</text>
          <text class="value">{{ Number(totalProfit || 0).toFixed(2) }}%</text>
        </view>

        <view v-if="tradeList.length" class="record-list">
          <view class="record-item" v-for="item in tradeList" :key="item.id || item.buyTime">
            <view class="item-header">
              <text class="stock">{{ item.stockName }}</text>
              <text class="rate" :class="{ loss: Number(item.profitLossPercent || 0) < 0 }">
                {{ Number(item.profitLossPercent || 0) >= 0 ? '↑' : '↓' }}
                {{ Number(item.profitLossPercent || 0).toFixed(2) }}%
              </text>
            </view>
            <view class="item-body">
              <view class="column">
                <text class="column-label">{{ $t('advisor.buyTime') }}</text>
                <text class="column-value">{{ item.buyTime || '--' }}</text>
              </view>
              <view class="column">
                <text class="column-label">{{ $t('advisor.sellTime') }}</text>
                <text class="column-value">{{ item.sellTime || '--' }}</text>
              </view>
            </view>
          </view>
        </view>
        <no-data v-else />
      </view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.records-page {
  @include hasNavBar();
  min-height: 100vh;
  background: #f5f5f5;
}

.records-content {
  padding: 16rpx 32rpx 64rpx;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.summary-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: 0 14rpx 28rpx rgba(0, 182, 230, 0.12);
  display: flex;
  justify-content: space-between;
  align-items: center;

  .label {
    font-size: 28rpx;
    color: #666;
  }

  .value {
    font-size: 40rpx;
    font-weight: 700;
    color: #00b6e6;
  }
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.record-item {
  background: #fff;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: 0 10rpx 24rpx rgba(0, 182, 230, 0.1);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;

  .stock {
    font-size: 30rpx;
    font-weight: 600;
    color: #111;
  }

  .rate {
    font-size: 30rpx;
    font-weight: 600;
    color: #00b6e6;

    &.loss {
      color: #ff4d4f;
    }
  }
}

.item-body {
  display: flex;
  justify-content: space-between;
  gap: 24rpx;

  .column {
    flex: 1;
    text-align: center;
    border-radius: 18rpx;
    padding: 20rpx 12rpx;

    .column-label {
      display: block;
      font-size: 24rpx;
      color: #666;
      margin-bottom: 12rpx;
    }

    .column-value {
      font-size: 28rpx;
      color: #333;
    }
  }
}

.empty {
  background: #fff;
  border-radius: 24rpx;
  padding: 80rpx 0;
  text-align: center;
  color: #999;
  font-size: 28rpx;
}
</style>
