<script setup>
import { ref, computed } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';

import { useCommonStore } from '@/stores/common';
import { goBack } from '@/utils/navigate';
import FundRecordList from './components/list.vue';

const { t, locale } = useI18n();
const commonStore = useCommonStore();

const refFilters = ref({});
const showFilter = ref(false);
const summary = ref({ income: 0, expense: 0, net: 0 });

const now = new Date();
const viewYear = ref(now.getFullYear());
const viewMonth = ref(now.getMonth() + 1);

const formatDate = (date) => {
  const d = date instanceof Date ? date : new Date(date);
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
};

const monthRange = () => {
  const start = new Date(viewYear.value, viewMonth.value - 1, 1);
  const end = new Date(viewYear.value, viewMonth.value, 0);
  return {
    startTime: formatDate(start),
    endTime: formatDate(end)
  };
};

const cpdListFilters = computed(() => ({
  ...refFilters.value,
  ...monthRange()
}));

const monthLabel = computed(() => {
  const y = viewYear.value;
  const m = viewMonth.value;
  if (locale.value?.startsWith('zh')) {
    return t('fund-record.index.monthLabel', { year: y, month: m });
  }
  const date = new Date(y, m - 1, 1);
  return date.toLocaleDateString(locale.value || 'en-US', {
    year: 'numeric',
    month: 'long'
  });
});

const cpdSummaryDisplay = computed(() => {
  const fmt = (n, sign = '') => {
    const abs = Math.abs(n).toLocaleString('en-US', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
    if (sign === '+') return `+${abs}`;
    if (sign === '-') return `-${abs}`;
    const prefix = n >= 0 ? '+' : '-';
    return `${prefix}${abs}`;
  };
  return {
    income: fmt(summary.value.income, '+'),
    expense: fmt(summary.value.expense, '-'),
    net: fmt(summary.value.net)
  };
});

const shiftMonth = (delta) => {
  let m = viewMonth.value + delta;
  let y = viewYear.value;
  if (m > 12) {
    m = 1;
    y += 1;
  } else if (m < 1) {
    m = 12;
    y -= 1;
  }
  viewMonth.value = m;
  viewYear.value = y;
};

const onSelectFilter = (filters) => {
  refFilters.value = filters;
};

const onSummaryUpdate = (payload) => {
  summary.value = payload;
};

const openFilter = () => {
  showFilter.value = true;
};

const closeFilter = () => {
  showFilter.value = false;
};

onShow(() => {
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper>
    <view class="fund-page">
      <nav-bar :title="$t('fund-record.index.title')">
        <template #right>
          <text class="fund-filter-btn" @tap="openFilter">{{
            $t('fund-record.index.filter')
          }}</text>
        </template>
      </nav-bar>

      <view class="fund-month-bar">
        <text class="fund-month-bar__arrow" @tap="shiftMonth(-1)">‹</text>
        <text class="fund-month-bar__label">{{ monthLabel }}</text>
        <text class="fund-month-bar__arrow" @tap="shiftMonth(1)">›</text>
      </view>

      <view class="fund-body">
        <view class="fund-body__inner">
          <view class="fund-summary">
            <view class="fund-summary__col">
              <text class="fund-summary__val up">{{ cpdSummaryDisplay.income }}</text>
              <text class="fund-summary__lbl">{{
                $t('fund-record.index.totalIncome')
              }}</text>
            </view>
            <view class="fund-summary__col">
              <text class="fund-summary__val down">{{ cpdSummaryDisplay.expense }}</text>
              <text class="fund-summary__lbl">{{
                $t('fund-record.index.totalExpense')
              }}</text>
            </view>
            <view class="fund-summary__col">
              <text class="fund-summary__val">{{ cpdSummaryDisplay.net }}</text>
              <text class="fund-summary__lbl">{{
                $t('fund-record.index.netInflow')
              }}</text>
            </view>
          </view>

          <fund-record-list
            class="fund-list"
            :filters="cpdListFilters"
            @summary-update="onSummaryUpdate"
          />
        </view>
      </view>

      <u-popup
        :show="showFilter"
        mode="bottom"
        :round="20"
        :z-index="200"
        @close="closeFilter"
      >
        <view class="fund-filter-sheet">
          <view class="fund-filter-sheet__head">
            <text class="fund-filter-sheet__title">{{
              $t('fund-record.index.filter')
            }}</text>
            <text class="fund-filter-sheet__done" @tap="closeFilter">{{
              $t('common.confirm')
            }}</text>
          </view>
          <c-filter-bar
            v-if="showFilter"
            class="fund-filter-sheet__bar"
            :uses="[
              { name: 'amount', paramName: 'amountType', defaultValue: '' },
              'account',
              'transaction'
            ]"
            @select="onSelectFilter"
            @init="onSelectFilter"
          />
        </view>
      </u-popup>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.fund-page {
  height: 100vh;
  max-height: 100vh;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--stock-page-bg, #eef2f8);
  @include hasNavBar();
}

.fund-filter-btn {
  font-size: 26rpx;
  color: var(--stock-ink-3, #64748b);
  font-weight: 600;
  padding-right: 8rpx;
}

.fund-month-bar {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 32rpx;
  background: #fff;
  border-bottom: 1rpx solid var(--stock-line, #e5e9f2);
  font-size: 26rpx;
  font-weight: 700;
  color: var(--stock-ink, #0f172a);

  &__arrow {
    color: var(--stock-ink-3, #64748b);
    padding: 8rpx 16rpx;
    line-height: 1;
  }

  &__label {
    flex: 1;
    text-align: center;
  }
}

.fund-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  -webkit-overflow-scrolling: touch;
  overscroll-behavior: contain;
}

.fund-body__inner {
  padding: 24rpx;
  padding-bottom: 32rpx;
}

.fund-summary {
  @include stock-card;
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 12rpx;
  padding: 36rpx 24rpx;
  margin-bottom: 24rpx;
  text-align: center;
  border-radius: 28rpx;
  box-shadow: 0 8rpx 28rpx rgba(15, 30, 71, 0.07);

  &__col {
    min-width: 0;
  }

  &__val {
    display: block;
    font-size: 36rpx;
    font-weight: 800;
    color: var(--stock-ink, #0f172a);
    @include stock-tnum;

    &.up {
      color: var(--stock-green, #16a34a);
    }

    &.down {
      color: var(--stock-red, #ef4444);
    }
  }

  &__lbl {
    display: block;
    margin-top: 8rpx;
    font-size: 22rpx;
    color: var(--stock-ink-3, #64748b);
  }
}

.fund-list {
  display: block;
}

.fund-filter-sheet {
  padding: 28rpx 32rpx calc(28rpx + env(safe-area-inset-bottom));
  background: #fff;

  &__head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24rpx;
  }

  &__title {
    font-size: 30rpx;
    font-weight: 700;
    color: var(--stock-ink, #0f172a);
  }

  &__done {
    font-size: 28rpx;
    font-weight: 600;
    color: var(--stock-blue, #2563eb);
  }

  &__bar {
    :deep(.filter-bar) {
      flex-wrap: wrap;
    }
  }
}
</style>
