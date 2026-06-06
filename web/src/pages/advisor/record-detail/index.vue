<script setup>
import { ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';

import { queryFollowRecordDetail } from '@/apis/advisor';
import { useCommonStore } from '@/stores/common';
import { goBack, navigateTo } from '@/utils/navigate';

const commonStore = useCommonStore();

const recordId = ref('');
const mentorId = ref('');
const activeTab = ref(0);

const detailData = ref({
  followNo: '',
  amount: 0,
  addAmount: 0,
  totalProfit: 0,
  totalProfitRate: 0
});

const positionList = ref([]);

const fetchDetail = async () => {
  if (!recordId.value) return;
  const res = await queryFollowRecordDetail({
    id: recordId.value,
    pageNum: 1,
    pageSize: 10
  });
  const data = res?.data || res || {};
  detailData.value = {
    followNo: data.followNo || '',
    amount: Number(data.amount || 0),
    addAmount: Number(data.addAmount || 0),
    totalProfit: Number(data.totalProfit || 0),
    totalProfitRate: Number(data.totalProfitRate || 0)
  };
  const list = data.followPositionVO?.list || [];
  positionList.value = Array.isArray(list) ? list : [];
};

const handleBack = () => {
  navigateTo(
    `/pages/advisor/advisor-record/index?activeTab=${activeTab.value}&mentorId=${mentorId.value}`
  );
};

onLoad((options) => {
  recordId.value = options?.id || '';
  mentorId.value = options?.mentorId || '';
  activeTab.value = Number(options?.activeTab || 0);
  if (!recordId.value) {
    handleBack();
    return;
  }
  fetchDetail();
});

onShow(() => {
  commonStore.fnBack = handleBack;
});
</script>

<template>
  <page-wrapper>
    <view class="detail-page">
      <nav-bar :title="$t('advisor.targetDetail')" />

      <view class="content">
        <view class="summary-card">
          <view class="row">
            <text class="label">{{ $t('advisor.orderNo') }}</text>
            <text class="value">{{ detailData.followNo || '--' }}</text>
          </view>
          <view class="grid">
            <view class="grid-item">
              <text class="grid-label">{{ $t('advisor.initialCapital') }}：</text>
              <view class="grid-value">
                <text>$ </text>
                <c-amount :value="detailData.amount" />
              </view>
            </view>
            <view class="grid-item">
              <text class="grid-label">{{ $t('advisor.appendedAmount') }}：</text>
              <view class="grid-value">
                <text>$ </text>
                <c-amount :value="detailData.addAmount" />
              </view>
            </view>
            <view class="grid-item">
              <text class="grid-label">{{ $t('advisor.totalProfitRate') }}：</text>
              <text class="grid-value"
                >{{ Number(detailData.totalProfitRate || 0).toFixed(2) }}%</text
              >
            </view>
            <view class="grid-item">
              <text class="grid-label">{{ $t('advisor.totalProfitLoss') }}：</text>
              <view class="grid-value">
                <text>$ </text>
                <c-amount :value="detailData.totalProfit" />
              </view>
            </view>
          </view>
        </view>

        <view v-if="positionList.length" class="position-list">
          <view
            class="position-card"
            v-for="item in positionList"
            :key="item.id || item.buyTime"
          >
            <view class="card-header">
              <text class="name">{{ item.stockName }}</text>
              <!-- <text class="code">({{ item.stockCode }})</text> -->
            </view>

            <view class="card-grid">
              <view class="cell">
                <text class="cell-label">{{ $t('advisor.buyPrice') }}</text>
                <text class="cell-value accent">{{ item.buyPrice || '--' }}</text>
              </view>
              <view class="cell">
                <text class="cell-label">{{ $t('advisor.buyQuantity') }}</text>
                <text class="cell-value accent">{{ item.buyQuantity || '--' }}</text>
              </view>
              <view class="cell">
                <text class="cell-label">{{ $t('advisor.buyTime') }}</text>
                <text class="cell-value">{{ item.buyTime || '--' }}</text>
              </view>
              <view class="cell">
                <text class="cell-label">{{ $t('advisor.sellPrice') }}</text>
                <text class="cell-value accent">{{
                  item.status === 2 ? item.sellPrice : '--'
                }}</text>
              </view>
              <view class="cell">
                <text class="cell-label">{{ $t('advisor.sellTime') }}</text>
                <text class="cell-value">{{
                  item.status === 2 ? item.sellTime : '--'
                }}</text>
              </view>
              <view class="cell">
                <text class="cell-label">{{ $t('advisor.profit') }}</text>
                <text
                  class="cell-value"
                  :class="{
                    positive: Number(item.profit) > 0,
                    negative: Number(item.profit) < 0
                  }"
                >
                  {{ item.status === 2 ? item.profit : '--' }}
                </text>
              </view>
              <view class="cell">
                <text class="cell-label">{{ $t('advisor.profitRate') }}</text>
                <text
                  class="cell-value"
                  :class="{
                    positive: Number(item.profitRate) > 0,
                    negative: Number(item.profitRate) < 0
                  }"
                >
                  {{ item.status === 2 ? `${item.profitRate}%` : '--' }}
                </text>
              </view>
              <view class="cell full">
                <text class="cell-value">
                  {{ $t('advisor.netProfit') }}({{
                    item.status === 2 ? item.netProfit : '--'
                  }}) = {{ $t('advisor.profit') }}({{
                    item.status === 2 ? item.profit : '--'
                  }}
                  ) {{ item.salary > 0 ? '-' : '+' }} {{ $t('advisor.profitDividend') }}
                  <text class="accent"
                    >({{ item.status === 2 ? Math.abs(item.salary) : '--' }})</text
                  >
                </text>
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

.detail-page {
  @include hasNavBar();
}

.content {
  padding: 16rpx 32rpx 64rpx;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.summary-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: 0 16rpx 32rpx rgba(0, 182, 230, 0.12);

  .row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 24rpx;

    .label {
      font-size: 28rpx;
      color: #666;
    }

    .value {
      font-size: 28rpx;
      font-weight: 600;
      color: #00b6e6;
    }
  }

  .grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20rpx;

    .grid-item {
      background: rgba(0, 182, 230, 0.08);
      border-radius: 20rpx;
      padding: 20rpx;
      text-align: center;

      .grid-label {
        font-size: 24rpx;
        color: #666;
        margin-bottom: 12rpx;
      }

      .grid-value {
        font-size: 24rpx;
        font-weight: 600;
        color: #00b6e6;
      }
    }
  }
}

.position-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.position-card {
  background: #fff;
  border-radius: 24rpx;
  border: 2rpx solid rgba(0, 182, 230, 0.3);
  box-shadow: 0 14rpx 28rpx rgba(0, 182, 230, 0.1);
  padding: 24rpx;
}

.card-header {
  display: flex;
  align-items: baseline;
  gap: 8rpx;
  margin-bottom: 20rpx;

  .name {
    font-size: 30rpx;
    font-weight: 600;
    color: #111;
  }

  .code {
    font-size: 24rpx;
    color: #666;
  }
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;

  .cell {
    background: rgba(0, 182, 230, 0.05);
    border-radius: 18rpx;
    padding: 20rpx;
    display: flex;
    flex-direction: column;
    text-align: center;

    &.full {
      grid-column: span 2;
      text-align: center;
    }

    .cell-label {
      font-size: 24rpx;
      color: #666;
      margin-bottom: 12rpx;
    }

    .cell-value {
      font-size: 28rpx;
      color: #333;

      &.accent,
      .accent {
        color: #00b6e6;
        font-weight: 600;
      }

      &.positive {
        color: #00b6e6;
        font-weight: 600;
      }

      &.negative {
        color: #ff4d4f;
        font-weight: 600;
      }
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
