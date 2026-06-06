<script setup>
import { ref, computed } from 'vue';
const tab = ref('open');

const openCoupons = [
  {
    amount: '200',
    unit: 'MXN',
    type: '手续费券',
    name: 'VIP 升级礼包券',
    desc: '单笔交易满 50,000 MXN 可用',
    expire: '还剩 12 天过期',
    urgent: true,
    color: 'orange'
  },
  {
    amount: '100',
    unit: 'MXN',
    type: '现金券',
    name: '新人开户礼',
    desc: '首次充值满 1,000 MXN 自动到账',
    expire: '2026-06-30 过期',
    urgent: false,
    color: 'gold'
  },
  {
    amount: '500',
    unit: 'MXN',
    type: '体验金',
    name: 'VIP 试用 7 天 体验金',
    desc: '可用于任意股票交易,收益归用户',
    expire: '还剩 5 天过期',
    urgent: true,
    color: 'green'
  },
  {
    amount: '20%',
    unit: '',
    type: '利率折扣',
    name: '融资融券利率折扣券',
    desc: '融资融券利率 8 折,30 日内有效',
    expire: '2026-07-15 过期',
    urgent: false,
    color: 'purple'
  }
];

/** 静态演示：已使用 / 已过期 */
const usedCoupons = [
  {
    amount: '50',
    unit: 'MXN',
    type: '现金券',
    name: '交易返现券',
    desc: '已于 2026-05-10 使用',
    expire: '2026-05-10 使用',
    urgent: false,
    color: 'gold',
    used: true
  }
];

const expiredCoupons = [
  {
    amount: '30',
    unit: 'MXN',
    type: '手续费券',
    name: '限时体验券',
    desc: '已过期',
    expire: '2026-04-01 过期',
    urgent: false,
    color: 'orange',
    expired: true
  }
];

const tabs = [
  { key: 'open', labelKey: 'coupon.tabOpen' },
  { key: 'used', labelKey: 'coupon.tabUsed' },
  { key: 'exp', labelKey: 'coupon.tabExpired' }
];

const displayList = computed(() => {
  if (tab.value === 'used') return usedCoupons;
  if (tab.value === 'exp') return expiredCoupons;
  return openCoupons;
});

const onRedeem = () => {
  uni.showToast({ title: uni.$t('coupon.redeem'), icon: 'none' });
};

const onUse = (item) => {
  if (item.used || item.expired) return;
  uni.showToast({ title: uni.$t('coupon.useNow'), icon: 'none' });
};
</script>

<template>
  <page-wrapper>
    <view class="coupon-page">
      <nav-bar :title="$t('coupon.title')">
        <template #right>
          <text class="u-redeem" @tap="onRedeem">{{ $t('coupon.redeem') }}</text>
        </template>
      </nav-bar>

      <!-- 原型 sp-seg-tabs：可用 / 已使用 / 已过期 -->
      <view class="coupon-tabs">
        <view
          v-for="t in tabs"
          :key="t.key"
          class="coupon-tab"
          :class="{ active: tab === t.key }"
          @tap="tab = t.key"
        >
          {{ $t(t.labelKey) }}
        </view>
      </view>

      <view class="coupon-body">
        <view class="coupon-body__inner">
          <template v-if="displayList.length">
            <view
              v-for="(c, i) in displayList"
              :key="`${tab}-${i}`"
              class="coupon-card"
              :class="{ 'coupon-card--disabled': c.used || c.expired }"
            >
              <view class="coupon-card__left" :class="c.color">
                <text class="coupon-card__amount">
                  {{ c.amount
                  }}<text v-if="c.unit" class="coupon-card__unit">{{ c.unit }}</text>
                </text>
                <text class="coupon-card__type">{{ c.type }}</text>
              </view>
              <view class="coupon-card__right">
                <view>
                  <text class="coupon-card__name">{{ c.name }}</text>
                  <text class="coupon-card__desc">{{ c.desc }}</text>
                </view>
                <view class="coupon-card__foot">
                  <text class="coupon-card__exp" :class="{ urgent: c.urgent }">{{
                    c.expire
                  }}</text>
                  <view
                    v-if="!c.used && !c.expired"
                    class="coupon-card__btn"
                    @tap.stop="onUse(c)"
                  >
                    {{ $t('coupon.useNow') }}
                  </view>
                  <text v-else-if="c.used" class="coupon-card__tag">{{
                    $t('coupon.used')
                  }}</text>
                  <text v-else class="coupon-card__tag">{{ $t('coupon.expired') }}</text>
                </view>
              </view>
            </view>
          </template>
          <view v-else class="coupon-empty">
            <text>{{ $t('common.noData') }}</text>
          </view>
        </view>
      </view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.coupon-page {
  height: 100vh;
  max-height: 100vh;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--stock-page-bg, #eef2f8);
  @include hasNavBar();
}

.u-redeem {
  font-size: 26rpx;
  color: var(--stock-blue, #2563eb);
  font-weight: 600;
  padding-right: 8rpx;
}

.coupon-tabs {
  flex-shrink: 0;
  display: flex;
  align-items: stretch;
  background: #fff;
  padding: 0 28rpx;
  border-bottom: 1rpx solid var(--stock-line, #e5e9f2);
}

.coupon-tab {
  position: relative;
  padding: 22rpx 28rpx;
  font-size: 26rpx;
  font-weight: 500;
  color: var(--stock-ink-3, #64748b);
  white-space: nowrap;

  &.active {
    color: var(--stock-ink, #0f172a);
    font-weight: 700;

    &::after {
      content: '';
      position: absolute;
      left: 28rpx;
      right: 28rpx;
      bottom: -1rpx;
      height: 5rpx;
      background: var(--stock-gold, #d4a24c);
      border-radius: 3rpx 3rpx 0 0;
    }
  }
}

.coupon-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  -webkit-overflow-scrolling: touch;
  overscroll-behavior: contain;
}

.coupon-body__inner {
  padding: 28rpx 24rpx 32rpx;
}

.coupon-card {
  margin-bottom: 28rpx;

  &:last-child {
    margin-bottom: 0;
  }
  display: flex;
  background: #fff;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 28rpx rgba(15, 30, 71, 0.07);

  &--disabled {
    opacity: 0.72;
  }

  &__left {
    width: 216rpx;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 28rpx 12rpx;

    &.orange {
      background: linear-gradient(135deg, #ffedd5, #fed7aa);
      .coupon-card__amount {
        color: #c2410c;
      }
    }
    &.gold {
      background: linear-gradient(135deg, #fcf1d6, #f1e0bd);
      .coupon-card__amount {
        color: #8b6914;
      }
    }
    &.green {
      background: linear-gradient(135deg, #dcfce7, #86efac);
      .coupon-card__amount {
        color: #15803d;
      }
    }
    &.purple {
      background: linear-gradient(135deg, #fce7f3, #fbcfe8);
      .coupon-card__amount {
        color: #be185d;
      }
    }
  }

  &__amount {
    font-size: 56rpx;
    font-weight: 800;
    @include stock-tnum;
  }

  &__unit {
    font-size: 28rpx;
    font-weight: 600;
  }

  &__type {
    margin-top: 8rpx;
    font-size: 18rpx;
    color: var(--stock-ink-2, #475569);
    font-weight: 700;
    letter-spacing: 1rpx;
    text-transform: uppercase;
  }

  &__right {
    flex: 1;
    min-width: 0;
    padding: 28rpx;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  &__name {
    display: block;
    font-size: 26rpx;
    font-weight: 800;
    color: var(--stock-ink, #0f172a);
  }

  &__desc {
    display: block;
    font-size: 21rpx;
    color: var(--stock-ink-3, #64748b);
    margin-top: 8rpx;
    line-height: 1.5;
  }

  &__foot {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 20rpx;
    gap: 12rpx;
  }

  &__exp {
    flex: 1;
    font-size: 19rpx;
    color: var(--stock-ink-3, #64748b);
    font-weight: 600;

    &.urgent {
      color: var(--stock-red, #ef4444);
    }
  }

  &__btn {
    flex-shrink: 0;
    padding: 10rpx 24rpx;
    background: linear-gradient(
      135deg,
      var(--stock-navy, #0b1e47),
      var(--stock-blue, #2563eb)
    );
    color: #fff;
    font-size: 21rpx;
    font-weight: 700;
    border-radius: 12rpx;
  }

  &__tag {
    flex-shrink: 0;
    font-size: 21rpx;
    color: var(--stock-ink-3, #64748b);
    font-weight: 600;
  }
}

.coupon-empty {
  padding: 120rpx 0;
  text-align: center;
  font-size: 28rpx;
  color: var(--stock-ink-3, #64748b);
}
</style>
