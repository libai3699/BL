<script setup>
import { ref, watch } from 'vue';

import { solvePagingData } from '@/utils/solveData';
import { reqUserAmountChangeListOmitInfo } from '@/apis/user/amoutChange';

const props = defineProps({
  filters: {
    type: Object,
    default: () => ({})
  }
});

const emits = defineEmits(['summary-update']);

const refDataBoxList = ref({
  details: null,
  isEmpty: () => refDataBoxList.value.details?.length === 0,
  isNotGetData: () =>
    refDataBoxList.value.loading ||
    refDataBoxList.value.error ||
    refDataBoxList.value.isEmpty(),
  loading: true,
  error: false,
  errorText: '',
  pageNum: 1,
  hasNextPage: false,
  loadmoreStatus: 'init'
});

function calcSummary(list = []) {
  let income = 0;
  let expense = 0;
  list.forEach((item) => {
    const amount = Math.abs(Number(item.amount) || 0);
    const type = Number(item.accountType);
    if (type === 1) {
      income += amount;
    } else if (type === 2) {
      expense += amount;
    } else if (Number(item.amount) >= 0) {
      income += amount;
    } else {
      expense += amount;
    }
  });
  emits('summary-update', {
    income,
    expense,
    net: income - expense
  });
}

function getRowTitle(item) {
  return item.orderTypeDesc || item.typeDesc || '--';
}

function getRowMeta(item) {
  const parts = [item.createTime, item.accountTypeDesc].filter(Boolean);
  return parts.join(' · ') || '--';
}

function isIncome(item) {
  const type = Number(item.accountType);
  if (type === 1) return true;
  if (type === 2) return false;
  return Number(item.amount) >= 0;
}

function getIconClass(item) {
  const title = (item.orderTypeDesc || '').toLowerCase();
  if (/分红|dividend/i.test(title)) return 'gold';
  return isIncome(item) ? 'up' : 'down';
}

function getIconSymbol(item) {
  const cls = getIconClass(item);
  if (cls === 'gold') return '💰';
  return isIncome(item) ? '↓' : '↑';
}

function formatRowAmount(item) {
  const num = Math.abs(Number(item.amount) || 0);
  const str = num.toLocaleString('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
  return isIncome(item) ? `+${str}` : `-${str}`;
}

function getData(isFirstPage = true, filters = {}) {
  const reqParams = { ...filters };
  solvePagingData({
    refData: refDataBoxList,
    isFirstPage,
    reqMethod: reqUserAmountChangeListOmitInfo,
    reqParams,
    onSuccess() {
      calcSummary(refDataBoxList.value.details || []);
    }
  });
}

watch(
  () => refDataBoxList.value.details,
  (list) => {
    if (list) calcSummary(list);
  }
);

watch(
  () => props.filters,
  (filters) => {
    getData(true, filters);
  },
  { deep: true, immediate: true }
);

defineExpose({
  getData
});
</script>

<template>
  <data-box
    class="m-fund-data-box"
    :loading="refDataBoxList.loading"
    :isError="refDataBoxList.error"
    :errorText="refDataBoxList.errorText"
    :isEmpty="refDataBoxList.isEmpty()"
    :loadmoreStatus="refDataBoxList.loadmoreStatus"
    @onErrorRetry="getData(true)"
    @scrolltolower="getData(false)"
    @loadmore="getData(false)"
  >
    <view class="fund-list-card">
      <view v-for="item in refDataBoxList.details" :key="item.id" class="sp-row">
        <view class="sp-soft-ico" :class="getIconClass(item)">
          <text>{{ getIconSymbol(item) }}</text>
        </view>
        <view class="sp-row__info">
          <text class="sp-row__title">{{ getRowTitle(item) }}</text>
          <text class="sp-row__meta">{{ getRowMeta(item) }}</text>
        </view>
        <text class="sp-row__amt" :class="isIncome(item) ? 'up' : 'down'">
          {{ formatRowAmount(item) }}
        </text>
      </view>
    </view>
  </data-box>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.m-fund-data-box {
  width: 100%;
}

.fund-list-card {
  @include stock-card;
  padding: 0;
  overflow: hidden;
  border-radius: 28rpx;
  box-shadow: 0 8rpx 28rpx rgba(15, 30, 71, 0.07);
}

.sp-row {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 28rpx;
  border-bottom: 1rpx solid var(--stock-line, #e5e9f2);
  background: #fff;

  &:last-child {
    border-bottom: none;
  }

  &__info {
    flex: 1;
    min-width: 0;
  }

  &__title {
    display: block;
    font-size: 26rpx;
    font-weight: 600;
    color: var(--stock-ink, #0f172a);
    @include ellipsis;
  }

  &__meta {
    display: block;
    margin-top: 4rpx;
    font-size: 21rpx;
    color: var(--stock-ink-3, #64748b);
    @include ellipsis;
  }

  &__amt {
    flex-shrink: 0;
    font-size: 28rpx;
    font-weight: 700;
    text-align: right;
    @include stock-tnum;

    &.up {
      color: var(--stock-green, #16a34a);
    }

    &.down {
      color: var(--stock-red, #ef4444);
    }
  }
}

.sp-soft-ico {
  width: 72rpx;
  height: 72rpx;
  border-radius: 20rpx;
  background: var(--stock-fill, #f1f5f9);
  border: 1rpx solid #ebeff7;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--stock-navy, #0b1e47);
  flex-shrink: 0;
  font-size: 30rpx;

  &.up {
    background: var(--stock-green-bg, #dcfce7);
    color: var(--stock-green, #16a34a);
    border-color: #ccefe0;
  }

  &.down {
    background: var(--stock-red-bg, #fee2e2);
    color: var(--stock-red, #ef4444);
    border-color: #fbd5d5;
  }

  &.gold {
    background: linear-gradient(135deg, #fcf1d6, #f1e0bd);
    color: #8b6914;
    border-color: #ead89a;
  }
}
</style>
