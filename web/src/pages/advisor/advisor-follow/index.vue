<script setup>
import { computed, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';

import { getFollowIncome, getTradingRecords } from '@/apis/advisor';
import { useCommonStore } from '@/stores/common';
import { goBack, navigateTo } from '@/utils/navigate';

const commonStore = useCommonStore();

const mentorId = ref('');
const incomeLoading = ref(false);
const recordLoading = ref(false);

const profitInfo = ref({
  name: '',
  levelName: '',
  totalIncome: 0,
  todayIncome: 0,
  totalFollowNum: 0,
  todayFollowNum: 0
});

const totalProfit = ref(0);
const tradingRecords = ref([]);
const teamNoteContent = ref('');

const formattedTodayIncome = computed(() => {
  return Number(profitInfo.value.todayIncome || 0);
});

const formattedTotalIncome = computed(() => {
  return Number(profitInfo.value.totalIncome || 0);
});

const fetchIncome = async () => {
  if (incomeLoading.value) return;
  incomeLoading.value = true;
  const res = await getFollowIncome({ mentorId: mentorId.value });
  if (res) {
    profitInfo.value = {
      ...profitInfo.value,
      ...res
    };
  }
  incomeLoading.value = false;
};

const fetchTradingRecords = async () => {
  if (!mentorId.value || recordLoading.value) return;
  recordLoading.value = true;
  const res = await getTradingRecords({
    mentorId: mentorId.value,
    pageNum: 1,
    pageSize: 20
  });
  if (res) {
    totalProfit.value = Number(res.totalProfit || 0);
    tradingRecords.value = Array.isArray(res.tradeList) ? res.tradeList.slice(0, 3) : [];
  }
  recordLoading.value = false;
};

const initPage = async () => {
  await Promise.all([fetchIncome(), fetchTradingRecords()]);
};

const goApplyFollow = () => {
  const name = encodeURIComponent(profitInfo.value.name || '');
  navigateTo(
    `/pages/advisor/apply-follow/index?mentorId=${mentorId.value || ''}&name=${name}`
  );
};

const backLastPage = () => {
  navigateTo(`/pages/advisor/elite-investment-advisory/index`);
};

onLoad((options) => {
  mentorId.value = options?.mentorId || '';
  initPage();
});

onShow(() => {
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper>
    <view class="follow-page">
      <nav-bar :title="$t('advisor.follow')" @back="backLastPage" />

      <view class="page-content">
        <view class="title-section"
          >{{ $t('advisor.starAdvisor') }}：{{ profitInfo.name || '—' }}</view
        >
        <view class="title-section">{{ $t('advisor.advisorMember') }}</view>

        <view class="advisor-card">
          <view class="card-top">
            <view class="total-section">
              <text class="total-label">{{ $t('advisor.totalProfit') }}</text>
              <view class="total-value">
                <text>$ </text>
                <c-amount :value="formattedTotalIncome" />
              </view>
            </view>
          </view>

          <view class="stats-row">
            <view class="stat-item">
              <text class="stat-label">{{ $t('advisor.todayProfit') }}</text>
              <text
                class="stat-value"
                :class="{ negative: Number(profitInfo.todayIncome) < 0 }"
              >
                $ <c-amount :value="formattedTodayIncome" />
              </text>
            </view>
            <view class="stat-item">
              <text class="stat-label">{{ $t('advisor.followTotal') }}</text>
              <text class="stat-value">{{ profitInfo.totalFollowNum || 0 }}</text>
            </view>
            <view class="stat-item">
              <text class="stat-label">{{ $t('advisor.todayFollow') }}</text>
              <text class="stat-value">{{ profitInfo.todayFollowNum || 0 }}</text>
            </view>
          </view>

          <view class="button-row">
            <button class="action-btn" @click="goApplyFollow">
              {{ $t('advisor.delegateFollow') }}
            </button>
          </view>
        </view>

        <view class="title-section record_text">{{ $t('advisor.tradingRecord') }}</view>

        <view class="records-card">
          <view class="win-rate-row">
            <text class="win-rate-label">{{ $t('advisor.winRate20Days') }}</text>
            <text class="win-rate-value">{{ Number(totalProfit || 0).toFixed(2) }}%</text>
          </view>

          <view v-if="tradingRecords.length" class="record-list">
            <view
              class="record-item"
              v-for="item in tradingRecords"
              :key="item.id || item.buyTime"
            >
              <view class="record-header">
                <text class="stock-name">{{ item.stockName }}</text>
                <text
                  class="profit-rate"
                  :class="{ loss: Number(item.profitLossPercent) < 0 }"
                >
                  {{ Number(item.profitLossPercent || 0) >= 0 ? '↑' : '↓'
                  }}{{ $t('advisor.profitLossPercent') }}
                  {{ Number(item.profitLossPercent || 0) >= 0 ? '+' : ''
                  }}{{ Number(item.profitLossPercent || 0).toFixed(2) }}%
                </text>
              </view>
              <view class="record-times">
                <view class="time-item">
                  <text class="time-label">{{ $t('advisor.buyTime') }}</text>
                  <text class="time-value">{{ item.buyTime || '--' }}</text>
                </view>
                <view class="time-item">
                  <text class="time-label">{{ $t('advisor.sellTime') }}</text>
                  <text class="time-value">{{ item.sellTime || '--' }}</text>
                </view>
              </view>
            </view>
          </view>
          <no-data v-else />
        </view>

        <view v-if="teamNoteContent" class="note-card">
          <view class="note-title">{{ $t('advisor.captainNotice') }}</view>
          <rich-text :nodes="teamNoteContent" />
        </view>
      </view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.follow-page {
  @include hasNavBar();
}

.page-content {
  padding: 16rpx 32rpx 64rpx;
}

.title-section {
  font-size: 30rpx;
  color: #333;
  margin-bottom: 24rpx;
}

.record_text {
  text-align: center;
}

.advisor-card {
  background: linear-gradient(135deg, #b3e5f5 0%, #e1f5fe 100%);
  border-radius: 200rpx 200rpx 0 0;
  padding: 40rpx 32rpx;
  margin-bottom: 48rpx;
}

.card-top {
  margin-bottom: 32rpx;
  display: flex;
  justify-content: center;
}

.total-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;

  .total-label {
    font-size: 28rpx;
    color: #333;
  }

  .total-value {
    font-size: 48rpx;
    font-weight: 700;
    color: #ff4d4f;
  }
}

.stats-row {
  display: flex;
  justify-content: space-around;
  margin-bottom: 32rpx;

  .stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8rpx;

    .stat-label {
      font-size: 24rpx;
      color: #666;
    }

    .stat-value {
      font-size: 28rpx;
      font-weight: 600;
      color: #333;

      &.negative {
        color: #ff4d4f;
      }
    }
  }
}

.button-row {
  display: flex;
  gap: 24rpx;

  .action-btn {
    flex: 1;
    height: 80rpx;
    line-height: 80rpx;
    background: #00b6e6;
    color: #fff;
    font-size: 28rpx;
    border: none;
    border-radius: 40rpx;
  }
}

.records-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx 0;
}

.win-rate-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32rpx;

  .win-rate-label {
    font-size: 28rpx;
    color: #333;
  }

  .win-rate-value {
    font-size: 32rpx;
    font-weight: 700;
    color: #ff4d4f;
  }
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.record-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  border: 1rpx solid #f0f0f0;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20rpx;

  .stock-name {
    font-size: 28rpx;
    color: #333;
    font-weight: 500;
  }

  .profit-rate {
    font-size: 26rpx;
    font-weight: 600;
    color: #ff4d4f;
    text-align: right;

    &.loss {
      color: #00b6e6;
    }
  }
}

.record-times {
  display: flex;
  justify-content: space-between;
  gap: 24rpx;

  .time-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 8rpx;

    .time-label {
      font-size: 24rpx;
      color: #999;
    }

    .time-value {
      font-size: 24rpx;
      color: #666;
    }
  }
}

.empty {
  text-align: center;
  color: #999;
  font-size: 26rpx;
  padding: 40rpx 0;
}

.note-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 28rpx;
  border: 2rpx solid rgba(0, 182, 230, 0.3);

  .note-title {
    font-size: 30rpx;
    font-weight: 600;
    color: #00b6e6;
    margin-bottom: 16rpx;
  }

  :deep(p) {
    font-size: 26rpx;
    color: #444;
    line-height: 1.6;
    margin-bottom: 12rpx;
  }
}
</style>
