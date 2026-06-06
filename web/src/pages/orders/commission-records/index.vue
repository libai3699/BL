<script setup>
import { ref } from 'vue';
import { onLoad, onShow, onPullDownRefresh } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';
import { useCommonStore } from '@/stores/common';
import { goBack } from '@/utils/navigate';
import { getCommissionRecords } from '@/apis/user/commission';

const { t } = useI18n();
const commonStore = useCommonStore();

// 列表数据
const list = ref([]);
const loading = ref(false);
const refFilters = ref({});

// 筛选回调
const onSelectFilter = (filters) => {
  refFilters.value = filters;
  fetchList(true);
};

// 获取列表数据
const fetchList = async (reset = false) => {
  if (loading.value) return;
  if (reset) {
    list.value = [];
  }

  loading.value = true;
  // 构建请求参数，即使没有选择时间也要传空字符串
  const params = refFilters.value;

  const res = await getCommissionRecords(params);

  // 数据结构: [{ currency: "MXN", records: [...] }]
  const dataList = res?.data || res || [];

  // 展平数据结构，将所有币种的记录合并
  const allRecords = [];
  dataList.forEach((currencyGroup) => {
    if (currencyGroup.records && Array.isArray(currencyGroup.records)) {
      currencyGroup.records.forEach((record) => {
        allRecords.push({
          ...record,
          currency: currencyGroup.currency || record.currency
        });
      });
    }
  });

  list.value = allRecords;
  loading.value = false;
};

// 下拉刷新
const handleRefresh = async () => {
  await fetchList(true);
  uni.stopPullDownRefresh();
};

onShow(() => {
  commonStore.fnBack = goBack;
});

onPullDownRefresh(() => {
  handleRefresh();
});
</script>

<template>
  <page-wrapper>
    <view class="commission-page">
      <nav-bar :title="$t('commission.title')" />

      <view class="page-content">
        <!-- 筛选栏 -->
        <c-filter-bar class="u-cur-filter-bar" :uses="['order', 'daterange', 'amount']" @select="onSelectFilter"
          @init="onSelectFilter" />

        <!-- 列表 -->
        <scroll-view class="record-list" scroll-y>
          <view v-if="list.length > 0" class="list-content">
            <view v-for="(item, index) in list" :key="index" class="record-card">
              <!-- 时间段 -->
              <view class="card-header">
                <text class="time-period">{{ $t('commission.agentTime') }}: {{ item.timePeriod }}</text>
              </view>

              <!-- 交易类型和金额 -->
              <view class="card-main">
                <view class="main-left">
                  <text class="order-type">{{ $t('commission.tradeType') }}: {{ item.orderTypeName }}</text>
                </view>
                <view class="main-right">
                  <text class="total-amount">{{
                    item.commissionAmount.toLocaleString('en-US', {
                      minimumFractionDigits: 2,
                      maximumFractionDigits: 2
                    })
                  }}
                    {{ item.currency }}</text>
                </view>
              </view>

              <!-- 下级代理信息 -->
              <view class="agent-section">
                <view class="agent-details">
                  <view class="detail-row">
                    <text class="detail-label head_text">{{
                      $t('commission.level1Agent')
                    }}</text>
                    <text class="detail-value">{{ item.level1Count }}{{ $t('commission.people') }}</text>
                  </view>
                  <view class="detail-row">
                    <text class="detail-label">{{
                      $t('commission.commissionRate')
                    }}</text>
                    <text class="detail-value">{{ item.level1Rate }}%</text>
                  </view>
                  <view class="detail-row">
                    <text class="detail-label">{{
                      $t('commission.commissionIncome')
                    }}</text>
                    <text class="detail-value highlight">+{{
                      item.level1Amount.toLocaleString('en-US', {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                      })
                    }}</text>
                  </view>
                </view>
              </view>

              <!-- 下下级代理信息 -->
              <view class="agent-section">
                <view class="agent-details">
                  <view class="detail-row">
                    <text class="detail-label head_text">{{
                      $t('commission.level1Agent')
                    }}</text>
                    <text class="detail-value">{{ item.level2Count }}{{ $t('commission.people') }}</text>
                  </view>
                  <view class="detail-row">
                    <text class="detail-label">{{
                      $t('commission.commissionRate')
                    }}</text>
                    <text class="detail-value">{{ item.level2Rate }}%</text>
                  </view>
                  <view class="detail-row">
                    <text class="detail-label">{{
                      $t('commission.commissionIncome')
                    }}</text>
                    <text class="detail-value highlight">+{{
                      item.level2Amount.toLocaleString('en-US', {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                      })
                    }}</text>
                  </view>
                </view>
              </view>

              <!-- 下下下级代理信息 -->
              <view class="agent-section">
                <view class="agent-details">
                  <view class="detail-row">
                    <text class="detail-label head_text">{{
                      $t('commission.level1Agent')
                    }}</text>
                    <text class="detail-value">{{ item.level3Count }}{{ $t('commission.people') }}</text>
                  </view>
                  <view class="detail-row">
                    <text class="detail-label">{{
                      $t('commission.commissionRate')
                    }}</text>
                    <text class="detail-value">{{ item.level3Rate }}%</text>
                  </view>
                  <view class="detail-row">
                    <text class="detail-label">{{
                      $t('commission.commissionIncome')
                    }}</text>
                    <text class="detail-value highlight">+{{
                      item.level3Amount.toLocaleString('en-US', {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                      })
                    }}</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
          <view v-else-if="loading" class="loading-container">
            <u-loading-icon mode="circle" size="40"></u-loading-icon>
          </view>
          <no-data v-else />
        </scroll-view>
      </view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.commission-page {
  @include hasNavBar();
}

.page-content {
  display: flex;
  flex-direction: column;
  height: calc(100vh - var(--status-bar-height) - var(--nav-bar-height));
}

:deep(.u-cur-filter-bar) {
  padding: 24rpx 32rpx;
}

// 时间显示行
.time-range-display {
  padding: 0 32rpx 24rpx;
  font-size: 26rpx;
  color: #666;
}

// 时间显示行
.time-range-display {
  background: #fff;
  padding: 0 52rpx 24rpx;
  font-size: 26rpx;
  color: #666;
}

.list-content {
  padding: 24rpx 32rpx;
  padding-bottom: 32rpx;
}

// 记录卡片
.record-card {
  background: #f5f5f5;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
}

.card-header {
  margin-bottom: 20rpx;
}

.time-period {
  font-size: 26rpx;
  color: #666;
}

.card-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20rpx;
  border-bottom: 2rpx solid #f5f5f5;
  margin-bottom: 20rpx;
}

.main-left {
  flex: 1;
}

.order-type {
  font-size: 28rpx;
  color: #333;
}

.main-right {
  text-align: right;
}

.total-amount {
  font-size: 32rpx;
  color: #ff4d4f;
  font-weight: 600;
}

// 代理信息
.agent-section {
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 3rpx solid #f0f0f0;

  &:last-child {
    margin-bottom: 0;
  }
}

.agent-title {
  font-size: 26rpx;
  color: #222;
  margin-bottom: 12rpx;
}

.agent-details {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  padding-left: 24rpx;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  font-size: 24rpx;
}

.detail-label {
  color: #999;
}

.head_text {
  color: #222;
}

.detail-value {
  color: #333;
  font-weight: 500;

  &.highlight {
    color: #00c087;
    font-weight: 600;
  }
}

// 加载中
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 80rpx 0;
}
</style>
